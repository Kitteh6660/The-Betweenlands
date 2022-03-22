package thebetweenlands.api.capability;

import net.minecraft.nbt.CompoundNBT;

/**
 * Capabilities can implement this interface to save/load data to/from NBT
 */
public interface ISerializableCapability {
	/**
	 * Writes the data to the nbt
	 * @param nbt
	 */
	public void save(CompoundNBT nbt);

	/**
	 * Reads the data from the nbt
	 * @param nbt
	 */
	public void load(CompoundNBT nbt);
}
