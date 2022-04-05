package thebetweenlands.api.storage;

import javax.annotation.Nullable;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.api.entity.spawning.IBiomeSpawnEntriesData;

public interface IWorldStorage {
	/**
	 * Writes the world data to the nbt
	 * @param nbt
	 */
	public void save(CompoundNBT nbt);

	/**
	 * Reads the world data from the nbt
	 * @param nbt
	 */
	public void load(CompoundNBT nbt);

	/**
	 * Returns the world instance
	 * @return
	 */
	public World getWorld();

	/**
	 * Called when a chunk storage needs to be read from the specified NBT and loaded
	 * @param chunk
	 * @param nbt
	 */
	public void readAndLoadChunk(Chunk chunk, CompoundNBT nbt);

	/**
	 * Called when a new chunk is loaded without any NBT data
	 * @param chunk
	 */
	public void loadChunk(Chunk chunk);

	/**
	 * Saves the chunk storage data to NBT. May return
	 * null if no data needs to be saved
	 * @param chunk
	 * @return
	 */
	@Nullable
	public CompoundNBT saveChunk(Chunk chunk);

	/**
	 * Called when a chunk is unloaded
	 * @param chunk
	 */
	public void unloadChunk(Chunk chunk);

	/**
	 * Called when a player starts watching the specified chunk
	 * @param pos
	 * @param player
	 */
	public void watchChunk(ChunkPos pos, ServerPlayerEntity player);

	/**
	 * Called when a player stops watching the specified chunk
	 * @param pos
	 * @param player
	 */
	public void unwatchChunk(ChunkPos pos, ServerPlayerEntity player);

	/**
	 * Returns the chunk storage of the specified chunk
	 * @param pos
	 * @return
	 */
	public IChunkStorage getChunkStorage(Chunk chunk);

	/**
	 * Returns the local storage handler responsible for loading and
	 * saving local storage from/to files and keeping track
	 * the local storage instances
	 * @return
	 */
	public ILocalStorageHandler getLocalStorageHandler();

	/**
	 * Ticks the world storage
	 */
	public void tick();
	
	/**
	 * Returns the persistent biome spawn entries data for the specified biome
	 * @param biome
	 * @return
	 */
	@Nullable
	public IBiomeSpawnEntriesData getBiomeSpawnEntriesData(Biome biome);
}
