package thebetweenlands.common.block.terrain;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockLogSap extends BlockLogBetweenlands {
	@Override
	public ArrayList<ItemStack> getDrops(IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(ItemRegistry.SAP_BALL, 1 + rand.nextInt(2 + fortune)));
		return drops;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		return true;
	}
}
