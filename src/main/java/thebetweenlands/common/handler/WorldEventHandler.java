package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.WorldStorageImpl;

public final class WorldEventHandler {
	public static final String CHUNK_NBT_TAG = ModInfo.ID + ":chunk_data";

	private static final Map<Chunk, IWorldStorage> UNLOAD_QUEUE = new HashMap<>();

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
		if(cap != null) {
			cap.loadChunk(event.getChunk());
			
			if(!event.getWorld().isClientSide()) {
				IChunkStorage chunkStorage = cap.getChunkStorage(event.getChunk());
				if(chunkStorage != null) {
					cap.getLocalStorageHandler().loadDeferredOperations(chunkStorage);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onChunkRead(ChunkDataEvent.Load event) {
		if(event.getData().contains(CHUNK_NBT_TAG, Constants.NBT.TAG_COMPOUND)) {
			IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
			if(cap != null) {
				cap.readAndLoadChunk(event.getChunk(), event.getData().getCompound(CHUNK_NBT_TAG));
			}
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
		if(cap != null) {
			if(event.getWorld().isClientSide()) {
				//Unload immediately on client side
				cap.unloadChunk(event.getChunk());
			} else {
				//Queue chunk to be unloaded later because there's no way to know
				//whether chunk will be saved to disk or not
				UNLOAD_QUEUE.put(event.getChunk(), cap);
			}
		}
	}

	@SubscribeEvent
	public static void onChunkSave(ChunkDataEvent.Save event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
		if(cap != null) {
			CompoundNBT nbt = cap.saveChunk(event.getChunk());
			if(nbt != null) {
				event.getData().put(CHUNK_NBT_TAG, nbt);
			}
			if(!event.getChunk().isLoaded()) {
				//Unload immediately after saving
				cap.unloadChunk(event.getChunk());
				UNLOAD_QUEUE.remove(event.getChunk());
			}
		}
	}

	@SubscribeEvent
	public static void onWatchChunk(ChunkWatchEvent.Watch event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
		if(cap != null) {
			cap.watchChunk(event.getPos(), event.getPlayer());
		}
	}

	@SubscribeEvent
	public static void onUnwatchChunk(ChunkWatchEvent.UnWatch event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
		if(cap != null) {
			cap.unwatchChunk(event.getPos(), event.getPlayer());
		}
	}

	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event) {
		IWorldStorage worldStorage = WorldStorageImpl.getCapability(event.getWorld());
		
		worldStorage.getLocalStorageHandler().saveAll();
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			//Unload queued chunks that weren't saved to disk
			for(Entry<Chunk, IWorldStorage> entry : UNLOAD_QUEUE.entrySet()) {
				Chunk chunk = entry.getKey();
				if(!chunk.isLoaded()) {
					entry.getValue().unloadChunk(chunk);
				}
			}
			UNLOAD_QUEUE.clear();
		}
	}

	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if(event.phase == Phase.END && !event.world.isClientSide()) {
			tickWorld(event.world);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			World world = Minecraft.getInstance().level;
			if(world != null && !Minecraft.getInstance().isPaused()) {
				tickWorld(world);
			}
		}
	}

	private static void tickWorld(World world) {
		IWorldStorage cap = WorldStorageImpl.getCapability(world);
		if(cap != null) {
			cap.tick();
		}
	}
}
