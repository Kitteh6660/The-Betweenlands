package thebetweenlands.api.storage;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import thebetweenlands.api.network.IGenericDataManagerAccess;

public interface ILocalStorage extends ICapabilityProvider {
	/**
	 * Returns the world storage
	 * @return
	 */
	public IWorldStorage getWorldStorage();

	/**
	 * Returns the bounds of the local storage. May be null
	 * @return
	 */
	@Nullable
	public AABB getBoundingBox();

	/**
	 * Returns whether the local storage is loaded
	 * @return
	 */
	public boolean isLoaded();

	/**
	 * Returns the storage ID
	 * @return
	 */
	public StorageID getID();

	/**
	 * Returns the storage region
	 * @return
	 */
	@Nullable
	public LocalRegion getRegion();

	/**
	 * Reads the local storage data from NBT.
	 * {@link #getID()} and {@link #getRegion()} are already read automatically
	 * @param nbt
	 */
	public void load(CompoundTag nbt);

	/**
	 * Writes the local storage data to NBT.
	 * {@link #getID()} and {@link #getRegion()} are already written automatically
	 * @param nbt
	 * @return
	 */
	public CompoundTag save(CompoundTag nbt);
	
	/**
	 * Reads the initial data that is sent the first time
	 * @param nbt
	 */
	public void readInitialPacket(CompoundTag nbt);
	
	/**
	 * Writes the initial data that is sent the first time
	 * @param nbt
	 * @return
	 */
	public CompoundTag writeInitialPacket(CompoundTag nbt);

	/**
	 * Marks the local storage as dirty
	 */
	public void setChanged();

	/**
	 * Sets whether the local storage is dirty
	 * @param dirty
	 */
	public void setDirty(boolean dirty);

	/**
	 * Returns whether the local storage data is dirty
	 * @return
	 */
	public boolean isDirty();

	/**
	 * Returns an unmodifiable list of all linked chunks
	 * @return
	 */
	public List<ChunkPos> getLinkedChunks();

	/**
	 * Sets the linked chunks. Only for use on client side for syncing
	 * @param linkedChunks New linked chunks
	 */
	@OnlyIn(Dist.CLIENT)
	public void setLinkedChunks(List<ChunkPos> linkedChunks);

	/**
	 * Called once when the local storage is initially added to the world
	 */
	public default void onAdded() {
		
	}
	
	/**
	 * Called when the local storage is loaded
	 */
	public void onLoaded();

	/**
	 * Called when the local storage is unloaded
	 */
	public void onUnloaded();

	/**
	 * Called when the local storage has been removed
	 */
	public void onRemoved();
	
	/**
	 * Called before the local storage is being removed
	 */
	public default void onRemoving() {
		
	}

	/**
	 * Returns a list of all currently loaded references
	 * @return
	 */
	public Collection<LocalStorageReference> getLoadedReferences();

	/**
	 * Loads a reference
	 * @param reference
	 * @return True if the reference wasn't loaded yet
	 */
	public boolean loadReference(LocalStorageReference reference);

	/**
	 * Unloads a reference
	 * @param reference
	 * @return True if the reference was loaded
	 */
	public boolean unloadReference(LocalStorageReference reference);

	/**
	 * Adds a watcher of the specified chunk storage.
	 * May be called multiple times with the same player but from
	 * a different chunk storage
	 * @param chunkStorage
	 * @param player
	 * @return True if the player wasn't watching yet
	 */
	public boolean addWatcher(IChunkStorage chunkStorage, ServerPlayer player);

	/**
	 * Removes a watcher of the specified chunk storage.
	 * May be called multiple times with the same player but from
	 * a different chunk storage
	 * @param chunkStorage
	 * @param player
	 * @return True if the player was watching
	 */
	public boolean removeWatcher(IChunkStorage chunkStorage, ServerPlayer player);

	/**
	 * Returns an unmodifiable list of all current watching players
	 * @return
	 */
	public Collection<ServerPlayer> getWatchers();

	/**
	 * Unlinks all chunks from this local storage.
	 * Do not use this to remove local storage since the
	 * file won't be deleted. To remove a local storage
	 * use {@link ILocalStorageHandler#removeLocalStorage(ILocalStorage)} instead
	 * @return True if all chunks were successfully unlinked
	 */
	public boolean unlinkAllChunks();

	/**
	 * Links the specified chunk to this local storage
	 * @param chunk
	 * @return True if the chunk was linked successfully
	 */
	public boolean linkChunk(LevelChunk chunk);
	
	/**
	 * Links the specified chunk to this local storage using a deferred
	 * storage operation. The link will be completed once the chunk is loaded
	 * @param chunk
	 */
	public default void linkChunkDeferred(ChunkPos chunk) {
		
	}
	
	/**
	 * Links the specified chunk to this local storage in a safe manner,
	 * i.e. calls {@link #linkChunk(LevelChunk)} if the chunk already exists and is loaded,
	 * and {@link #linkChunkDeferred(ChunkPos)} if the chunk does not yet exist
	 * or is not loaded.
	 * @param chunk
	 */
	public default void linkChunkSafely(ChunkPos chunk) {
		LevelChunk instance = this.getWorldStorage().getLevel().getChunkSource().getChunkNow(chunk.x, chunk.z);
		if(instance != null) {
			this.linkChunk(instance);
		} else {
			this.linkChunkDeferred(chunk);
		}
	}

	/**
	 * Unlinks the specified chunk from this local storage
	 * Do not use this to remove local storage since the
	 * file won't be deleted. To remove a local storage
	 * use {@link ILocalStorageHandler#removeLocalStorage(ILocalStorage)} instead
	 * @param chunk
	 * @return True if the chunk was unlinked successfully
	 */
	public boolean unlinkChunk(LevelChunk chunk);

	/**
	 * Returns the data manager used to sync data. <p><b>Only storages that implement {@link ITickable}
	 * will automatically update the data manager!</b>
	 * @return
	 */
	@Nullable
	public IGenericDataManagerAccess getDataManager();
}
