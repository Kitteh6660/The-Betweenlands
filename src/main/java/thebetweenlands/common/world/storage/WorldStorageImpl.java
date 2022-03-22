package thebetweenlands.common.world.storage;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class WorldStorageImpl implements IWorldStorage {
	////////////// Handler //////////////////
	public static class Handler {
		private Handler() {}

		@SubscribeEvent
		public static void onWorldCapability(final AttachCapabilitiesEvent<World> event) {
			event.addCapability(new ResourceLocation(ModInfo.ID, "world_data"), new ICapabilitySerializable<CompoundNBT>() {
				private IWorldStorage instance = this.getNewInstance();

				private IWorldStorage getNewInstance() {
					WorldStorageImpl instance = new BetweenlandsWorldStorage();
					instance.setWorld(event.getObject());
					instance.init();
					return instance;
				}

				@Override
				public boolean hasCapability(Capability<?> capability, Direction facing) {
					return capability == CAPABILITY_INSTANCE;
				}

				@SuppressWarnings("unchecked")
				@Override
				public <T> T getCapability(Capability<T> capability, Direction facing) {
					return capability == CAPABILITY_INSTANCE ? (T)this.instance : null;
				}

				@Override
				public CompoundNBT serializeNBT() {
					return (CompoundNBT) CAPABILITY_INSTANCE.getStorage().writeNBT(CAPABILITY_INSTANCE, this.instance, null);
				}

				@Override
				public void deserializeNBT(CompoundNBT nbt) {
					CAPABILITY_INSTANCE.getStorage().readNBT(CAPABILITY_INSTANCE, this.instance, null, nbt);					
				}
			});
		}
	}

	/**
	 * Returns the world capability for the specified world
	 * @param world
	 * @return
	 */
	@Nullable
	public static IWorldStorage getCapability(World world) {
		return world.getCapability(CAPABILITY_INSTANCE, null);
	}

	/**
	 * Called after the world is set
	 */
	protected void init() {

	}

	/**
	 * Registers the capability and event handler
	 */
	public static void register() {
		CapabilityManager.INSTANCE.register(IWorldStorage.class, new IStorage<IWorldStorage>() {
			@Override
			public INBT writeNBT(Capability<IWorldStorage> capability, IWorldStorage instance, Direction side) {
				CompoundNBT nbt = new CompoundNBT();
				instance.save(nbt);
				return nbt;
			}

			@Override
			public void readNBT(Capability<IWorldStorage> capability, IWorldStorage instance, Direction side, INBT nbt) {
				if(nbt instanceof CompoundNBT) {
					instance.readFromNBT((CompoundNBT)nbt);
				}
			}
		}, new Callable<IWorldStorage>() {
			@Override
			public IWorldStorage call() throws Exception {
				return new BetweenlandsWorldStorage();
			}
		});

		MinecraftForge.EVENT_BUS.register(Handler.class);
	}
	////////////// End Handler //////////////////





	@CapabilityInject(IWorldStorage.class)
	public static final Capability<IWorldStorage> CAPABILITY_INSTANCE = null;

	private Map<ChunkPos, ChunkStorageImpl> storageMap = new HashMap<>();
	private List<ITickable> tickableStorages = new ArrayList<>();

	private World world;

	private ILocalStorageHandler localStorageHandler;

	/**
	 * Sets the capability's world
	 * @param world
	 */
	private void setWorld(World world) {
		this.world = world;
		this.localStorageHandler = new LocalStorageHandlerImpl(this);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public void save(CompoundNBT nbt) {

	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {

	}

	@Override
	public void loadChunk(Chunk chunk) {
		if(!this.storageMap.containsKey(chunk.getPos())) {
			try {
				ChunkStorageImpl storage = new BetweenlandsChunkStorage(this, chunk);
				storage.init();
				storage.setDefaults();
				this.storageMap.put(chunk.getPos(), storage);

				if(storage instanceof ITickable) {
					this.tickableStorages.add((ITickable) storage);
				}

				//Makes sure that the default values are saved
				chunk.setModified(true);
			} catch(Exception ex) {
				TheBetweenlands.logger.error(String.format("Failed creating chunk storage at %s", "[x=" + chunk.x + ", z=" + chunk.z + "]"), ex);
			}
		}
	}

	@Override
	public void readAndLoadChunk(Chunk chunk, CompoundNBT nbt) {
		if(this.storageMap.containsKey(chunk.getPos())) {
			if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.warn(String.format("Reading chunk storage at %s, but chunk storage is already loaded!", "[x=" + chunk.x + ", z=" + chunk.z + "]"));
		} else {
			try {
				ChunkStorageImpl storage = new BetweenlandsChunkStorage(this, chunk);
				storage.init();
				storage.readFromNBT(nbt, false);
				this.storageMap.put(chunk.getPos(), storage);

				if(storage instanceof ITickable) {
					this.tickableStorages.add((ITickable) storage);
				}
			} catch(Exception ex) {
				TheBetweenlands.logger.error(String.format("Failed reading chunk storage at %s", "[x=" + chunk.x + ", z=" + chunk.z + "]"), ex);
			}
		}
	}

	@Override
	public void unloadChunk(Chunk chunk) {
		if(!this.storageMap.containsKey(chunk.getPos())) {
			if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.warn(String.format("Unloading chunk storage at %s, but chunk storage is not loaded!", "[x=" + chunk.x + ", z=" + chunk.z + "]"));
		} else {
			ChunkStorageImpl storage = this.storageMap.remove(chunk.getPos());
			if(storage instanceof ITickable) {
				this.tickableStorages.remove((ITickable) storage);
			}
			storage.onUnload();
		}
	}

	@Override
	public CompoundNBT saveChunk(Chunk chunk) {
		if(!this.storageMap.containsKey(chunk.getPos())) {
			if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.warn(String.format("Saving chunk storage at %s, but chunk storage is not loaded!", "[x=" + chunk.x + ", z=" + chunk.z + "]"));
		} else {
			try {
				ChunkStorageImpl storage = this.storageMap.get(chunk.getPos());
				CompoundNBT nbt = storage.save(new CompoundNBT(), false);
				storage.setDirty(false);
				return nbt;
			} catch(Exception ex) {
				TheBetweenlands.logger.error(String.format("Failed saving chunk storage at %s", "[x=" + chunk.x + ", z=" + chunk.z + "]"), ex);
			}
		}
		return null;
	}

	@Override
	public void watchChunk(ChunkPos pos, ServerPlayerEntity player) {
		ChunkStorageImpl storage = this.storageMap.get(pos);
		if(storage != null) {
			storage.addWatcher(player);
		}
	}

	@Override
	public void unwatchChunk(ChunkPos pos, ServerPlayerEntity player) {
		ChunkStorageImpl storage = this.storageMap.get(pos);
		if(storage != null) {
			storage.removeWatcher(player);
		}
	}

	@Override
	public ChunkStorageImpl getChunkStorage(Chunk chunk) {
		return this.storageMap.get(chunk.getPos());
	}

	@Override
	public ILocalStorageHandler getLocalStorageHandler() {
		return this.localStorageHandler;
	}

	@Override
	public void tick() {
		this.localStorageHandler.update();

		for(int i = 0; i < this.tickableStorages.size(); i++) {
			ITickable tickable = this.tickableStorages.get(i);
			tickable.update();
		}
	}
}
