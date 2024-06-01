package thebetweenlands.api.storage;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IChunkStorage extends ICapabilityProvider {
	/**
	 * Returns the world storage
	 * @return
	 */
	public IWorldStorage getWorldStorage();

	/**
	 * Returns the chunk of this chunk storage
	 * @return
	 */
	public LevelChunk getChunk();

	/**
	 * Called when the chunk storage is initialized before any data is loaded
	 */
	public void init();

	/**
	 * Called when the chunk storage is unloaded, after the data has been saved
	 */
	public void onUnload();

	/**
	 * Sets the default value if the chunk is new
	 */
	public void setDefaults();

	/**
	 * Reads the chunk storage data from NBT
	 * @param nbt NBT
	 * @param packet Whether the NBT is being read from a packet
	 */
	public void load(CompoundTag nbt, boolean packet);
	
	/**
	 * Reads the local storage references from NBT
	 * @param nbt
	 * @return
	 */
	public CompoundTag readLocalStorageReferences(CompoundTag nbt);

	/**
	 * Writes the chunk storage data to NBT
	 * @param nbt NBT
	 * @param packet Whether the NBT is being written to a packet
	 * @return
	 */
	public CompoundTag save(CompoundTag nbt, boolean packet);
	
	/**
	 * Writes the local storage references to NBT
	 * @param nbt
	 * @return
	 */
	public CompoundTag writeLocalStorageReferences(CompoundTag nbt);

	/**
	 * Adds a watcher
	 * @param player
	 * @return True if the player wasn't watching yet
	 */
	public boolean addWatcher(ServerPlayer player);

	/**
	 * Removes a watcher
	 * @param player
	 * @return True if the player was watching
	 */
	public boolean removeWatcher(ServerPlayer player);

	/**
	 * Returns an unmodifiable list of all current watching players
	 * @return
	 */
	public Collection<ServerPlayer> getWatchers();

	/**
	 * Marks the chunk storage and the chunk as dirty
	 */
	public void setChanged();

	/**
	 * Sets whether the chunk storage is dirty.
	 * If dirty is true the chunk is also marked dirty
	 * @param dirty
	 */
	public void setDirty(boolean dirty);

	/**
	 * Returns whether the chunk storage data is dirty
	 * @return
	 */
	public boolean isDirty();

	/**
	 * Returns the reference with the specified ID
	 * @param id
	 * @return
	 */
	@Nullable
	public LocalStorageReference getReference(StorageID id);

	/**
	 * Unlinks this chunk from the specified local storage
	 * @param storage
	 * @return True if it was successfully unlinked
	 */
	public boolean unlinkLocalStorage(ILocalStorage storage);

	/**
	 * Links this chunk with the specified local storage
	 * @param storage
	 * @return
	 */
	public boolean linkLocalStorage(ILocalStorage storage);

	/**
	 * Returns a list of all local storage references
	 * @return
	 */
	public Collection<LocalStorageReference> getLocalStorageReferences();
}
