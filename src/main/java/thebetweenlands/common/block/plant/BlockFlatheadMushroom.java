package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockFlatheadMushroom extends BlockMushroomBetweenlands {
	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}
}
