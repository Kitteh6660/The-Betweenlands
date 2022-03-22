package thebetweenlands.api.aspect;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.registries.AspectRegistry;

public interface IAspectType {
	/**
	 * Returns the name of this aspect
	 * @return
	 */
	public String getName();

	/**
	 * Returns the type of this aspect
	 * @return
	 */
	public String getType();

	/**
	 * Returns the description of this aspect
	 * @return
	 */
	public String getDescription();

	/**
	 * Returns the aspect icon
	 * @return
	 */
	public ResourceLocation getIcon();

	/**
	 * Returns the color of the aspect
	 * @return
	 */
	public int getColor();

	/**
	 * Writes this aspect type to the specified NBT
	 * @param nbt
	 * @return
	 */
	public default CompoundNBT save(CompoundNBT nbt) {
		nbt.putString("type", this.getName());
		return nbt;
	}

	/**
	 * Reads the aspect type from the specified NBT
	 * @param nbt
	 * @return
	 */
	@Nullable
	public static IAspectType load(BlockState state, CompoundNBT nbt) {
		return AspectRegistry.getAspectTypeFromName(nbt.getString("type"));
	}
}
