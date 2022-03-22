package thebetweenlands.api.storage;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;

public interface IOfflinePlayerDataHandler {
	public void updateCache();
	
	/**
	 * Returns offline data specific to the player with the specified UUID.
	 * This data is always available, regardless of whether the player is on- or offline.
	 * Use {@link #setOfflinePlayerData(UUID, CompoundNBT)} after changing data to make sure it
	 * is saved.
	 * @param playerUuid
	 * @return
	 */
	@Nullable
	public CompoundNBT getOfflinePlayerData(UUID playerUuid);

	/**
	 * Sets the offline data of the player with the specified UUID.
	 * @param playerUuid
	 * @param nbt
	 */
	public void setOfflinePlayerData(UUID playerUuid, CompoundNBT nbt);

	/**
	 * Saves all offline player data to disk.
	 */
	public void saveAllOfflinePlayerData();
}
