package thebetweenlands.api.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public interface ISickleHarvestable {
	/**
	 * Returns whether this block can be harvested by a sickle
	 * @param item
	 * @param world
	 * @param pos
	 * @return
	 */
	public boolean isHarvestable(ItemStack item, LevelAccessor world, BlockPos pos);

	/**
	 * Returns the drops from harvesting this block with a sickle
	 * @param item
	 * @param world
	 * @param pos
	 * @param fortune
	 * @return
	 */
	public List<ItemStack> getHarvestableDrops(ItemStack item, LevelAccessor world, BlockPos pos, int fortune);
}