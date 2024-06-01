package thebetweenlands.api.storage;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import thebetweenlands.api.entity.spawning.IBiomeSpawnEntriesData;

public interface IWorldStorage {
	/**
	 * Writes the world data to the nbt
	 * @param nbt
	 */
	public void save(CompoundTag nbt);

	/**
	 * Reads the world data from the nbt
	 * @param nbt
	 */
	public void load(CompoundTag nbt);

	/**
	 * Returns the world instance
	 * @return
	 */
	public Level getLevel();

	/**
	 * Called when a chunk storage needs to be read from the specified NBT and loaded
	 * @param chunk
	 * @param nbt
	 */
	public void readAndLoadChunk(LevelChunk chunk, CompoundTag nbt);

	/**
	 * Called when a new chunk is loaded without any NBT data
	 * @param chunk
	 */
	public void loadChunk(LevelChunk chunk);

	/**
	 * Saves the chunk storage data to NBT. May return
	 * null if no data needs to be saved
	 * @param chunk
	 * @return
	 */
	@Nullable
	public CompoundTag saveChunk(LevelChunk chunk);

	/**
	 * Called when a chunk is unloaded
	 * @param chunk
	 */
	public void unloadChunk(LevelChunk chunk);

	/**
	 * Called when a player starts watching the specified chunk
	 * @param pos
	 * @param player
	 */
	public void watchChunk(ChunkPos pos, ServerPlayer player);

	/**
	 * Called when a player stops watching the specified chunk
	 * @param pos
	 * @param player
	 */
	public void unwatchChunk(ChunkPos pos, ServerPlayer player);

	/**
	 * Returns the chunk storage of the specified chunk
	 * @param pos
	 * @return
	 */
	public IChunkStorage getChunkStorage(LevelChunk chunk);

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
