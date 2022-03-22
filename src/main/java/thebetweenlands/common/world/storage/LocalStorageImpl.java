package thebetweenlands.common.world.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.event.AttachLocalStorageCapabilitiesEvent;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.LocalStorageReference;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageAddLocalStorage;
import thebetweenlands.common.network.clientbound.MessageRemoveLocalStorage;
import thebetweenlands.common.world.storage.operation.DeferredLinkOperation;

public abstract class LocalStorageImpl implements ILocalStorage {
	private final IWorldStorage worldStorage;
	private final LocalRegion region;
	private final StorageID id;

	private CapabilityDispatcher capabilities;
	private boolean dirty;

	private final List<ChunkPos> linkedChunks = new ArrayList<>();
	private final List<LocalStorageReference> loadedReferences = new ArrayList<>();

	//protected boolean requiresSync = false;

	private final List<ServerPlayerEntity> watchers = new ArrayList<>();
	private final List<ServerPlayerEntity> duplicateWatchers = new ArrayList<>();

	private boolean loaded = false;

	public LocalStorageImpl(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		this.worldStorage = worldStorage;
		this.id = id;
		this.region = region;

		//Gather capabilities
		AttachLocalStorageCapabilitiesEvent event = new AttachLocalStorageCapabilitiesEvent(this);
		MinecraftForge.EVENT_BUS.post(event);
		this.capabilities = event.getCapabilities().size() > 0 ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		return this.capabilities == null ? false : this.capabilities.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		return this.capabilities == null ? null : this.capabilities.getCapability(capability, facing);
	}

	@Override
	public IWorldStorage getWorldStorage() {
		return this.worldStorage;
	}

	@Override
	public StorageID getID() {
		return this.id;
	}

	@Override
	public LocalRegion getRegion() {
		return this.region;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		if(this.capabilities != null && nbt.contains("ForgeCaps")) {
			this.capabilities.deserializeNBT(nbt.getCompoundTag("ForgeCaps"));
		}

		this.readReferenceChunks(nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		if(this.capabilities != null) {
			CompoundNBT caps = this.capabilities.serializeNBT();
			if(caps.getSize() > 0) {
				nbt.setTag("ForgeCaps", caps);
			}
		}

		this.writeReferenceChunks(nbt);

		return nbt;
	}

	@Override
	public void readInitialPacket(CompoundNBT nbt) {
		this.readReferenceChunks(nbt);
	}

	@Override
	public CompoundNBT writeInitialPacket(CompoundNBT nbt) {
		this.writeReferenceChunks(nbt);
		return nbt;
	}

	protected final void writeReferenceChunks(CompoundNBT nbt) {
		ListNBT referenceChunkList = new ListNBT();
		for(ChunkPos referenceChunk : this.linkedChunks) {
			CompoundNBT referenceChunkNbt = new CompoundNBT();
			referenceChunkNbt.putInt("x", referenceChunk.x);
			referenceChunkNbt.putInt("z", referenceChunk.z);
			referenceChunkList.appendTag(referenceChunkNbt);
		}
		nbt.setTag("ReferenceChunks", referenceChunkList);
	}

	protected final void readReferenceChunks(CompoundNBT nbt) {
		this.linkedChunks.clear();
		ListNBT referenceChunkList = nbt.getList("ReferenceChunks", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < referenceChunkList.size(); i++) {
			CompoundNBT referenceChunkNbt = referenceChunkList.getCompound(i);
			this.linkedChunks.add(new ChunkPos(referenceChunkNbt.getInt("x"), referenceChunkNbt.getInt("z")));
		}
	}

	@Override
	public void setChanged() {
		this.setDirty(true);
	}

	/**
	 * Sets whether the data is dirty and needs to be saved to the file
	 * @param dirty
	 * @return
	 */
	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public List<ChunkPos> getLinkedChunks() {
		return Collections.unmodifiableList(this.linkedChunks);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setLinkedChunks(List<ChunkPos> linkedChunks) {
		this.linkedChunks.clear();
		this.linkedChunks.addAll(linkedChunks);
	}

	@Override
	public void onLoaded() {
		this.loaded = true;
	}

	@Override
	public void onUnloaded() {
		this.loaded = false;
	}

	@Override
	public void onRemoved() {

	}

	@Override
	public void onRemoving() {
		//Notify clients when shared storage is removed.
		//This is done before onRemoved so that the list of watchers is not yet empty.
		if(!this.getWorldStorage().getWorld().isClientSide()) {
			if (!this.getWatchers().isEmpty()) {
				this.sendMessageToAllWatchers(new MessageRemoveLocalStorage(this.getID()));
			}
		}
	}

	@Override
	public Collection<LocalStorageReference> getLoadedReferences() {
		return Collections.unmodifiableCollection(this.loadedReferences);
	}

	@Override
	public boolean loadReference(LocalStorageReference reference) {
		if(!this.loadedReferences.contains(reference)) {
			return this.loadedReferences.add(reference);
		}
		return false;
	}

	@Override
	public boolean unloadReference(LocalStorageReference reference) {
		return this.loadedReferences.remove(reference);
	}

	@Override
	public boolean addWatcher(IChunkStorage chunkStorage, ServerPlayerEntity player) {
		boolean contained = this.duplicateWatchers.contains(player);
		this.duplicateWatchers.add(player);
		if(!contained) {
			this.watchers.add(player);
			this.onWatched(player);
		}
		return !contained;
	}

	/**
	 * Called when a new watcher is added
	 * @param player
	 */
	protected void onWatched(ServerPlayerEntity player) {
		this.sendDataToPlayer(new MessageAddLocalStorage(this), player);
	}

	@Override
	public boolean removeWatcher(IChunkStorage chunkStorage, ServerPlayerEntity player) {
		boolean contained = this.duplicateWatchers.remove(player);
		if(contained && !this.duplicateWatchers.contains(player)) {
			this.watchers.remove(player);
			this.onUnwatched(player);
		}
		return contained;
	}

	/**
	 * Called when a player stops watching this local storage
	 * @param player
	 */
	protected void onUnwatched(ServerPlayerEntity player) {

	}

	@Override
	public Collection<ServerPlayerEntity> getWatchers() {
		return Collections.unmodifiableCollection(this.watchers);
	}

	@Override
	public boolean unlinkAllChunks() {
		boolean changed = false;
		boolean allUnlinked = true;
		List<ChunkPos> chunks = new ArrayList<>(this.linkedChunks.size());
		chunks.addAll(this.linkedChunks);
		Iterator<ChunkPos> it = chunks.iterator();
		ChunkPos pos = null;
		while(it.hasNext()) {
			pos = it.next();
			Chunk chunk = this.worldStorage.getWorld().getChunk(pos.x, pos.z);
			IChunkStorage chunkData = this.worldStorage.getChunkStorage(chunk);
			if(chunkData == null || !chunkData.unlinkLocalStorage(this)) {
				allUnlinked = false;
			} else if(chunkData != null) {
				changed = true;
			}
		}
		if(changed) {
			this.setDirty(true);
		}
		this.linkedChunks.clear();
		return allUnlinked;
	}

	@Override
	public boolean linkChunk(Chunk chunk) {
		ChunkPos chunkPos = new ChunkPos(chunk.x, chunk.z);
		if(!this.linkedChunks.contains(chunkPos)) {
			IChunkStorage chunkData = this.worldStorage.getChunkStorage(chunk);
			if(chunkData != null && chunkData.linkLocalStorage(this)) {
				if(this.linkedChunks.add(chunkPos)) {
					//TODO Send packet
					this.setDirty(true);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void linkChunkDeferred(ChunkPos chunk) {
		if(!this.linkedChunks.contains(chunk) && this.linkedChunks.add(chunk)) {
			this.setDirty(true);
			this.worldStorage.getLocalStorageHandler().queueDeferredOperation(chunk, new DeferredLinkOperation(new LocalStorageReference(chunk, this.getID(), this.getRegion())));
		}
	}

	@Override
	public boolean unlinkChunk(Chunk chunk) {
		ChunkPos chunkPos = new ChunkPos(chunk.x, chunk.z);
		if(this.linkedChunks.contains(chunkPos)) {
			IChunkStorage chunkData = this.worldStorage.getChunkStorage(chunk);
			if(chunkData != null) {
				chunkData.unlinkLocalStorage(this);
				if(this.linkedChunks.remove(chunkPos)) {
					//TODO Send packet
					this.setDirty(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sends the message to all watching players
	 */
	protected void sendMessageToAllWatchers(IMessage message) {
		for (ServerPlayerEntity watcher : this.getWatchers()) {
			this.sendDataToPlayer(message, watcher);
		}
	}

	/**
	 * Sends the message to a player
	 * @param player
	 */
	protected void sendDataToPlayer(IMessage message, ServerPlayerEntity player) {
		TheBetweenlands.networkWrapper.sendTo(message, player);
	}

	@Override
	public boolean isLoaded() {
		return this.loaded;
	}
}
