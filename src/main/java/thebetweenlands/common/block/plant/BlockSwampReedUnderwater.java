package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSwampReedUnderwater extends BlockStackablePlantUnderwater {
	public BlockSwampReedUnderwater() {
		this.resetAge = true;
		this.setHardness(0.1F);
		this.setCreativeTab(null);
	}

	@Override
	protected boolean isSamePlant(BlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.SWAMP_REED;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return ItemRegistry.SWAMP_REED_ITEM;
	}


	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(ItemRegistry.SWAMP_REED_ITEM);
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}

	@Override
	protected boolean canGrowUp(World world, BlockPos pos, BlockState state, int height) {
		return world.getBlockState(pos.above()) != this && 
				(world.getBlockState(pos.above()).getMaterial() == Material.WATER || (world.getBlockState(pos).getMaterial() == Material.WATER && world.isEmptyBlock(pos.above()))) 
				&& (this.maxHeight == -1 || height < this.maxHeight);
	}

	@Override
	protected void growUp(World world, BlockPos pos) {
		if(!world.getBlockState(pos.above()).getMaterial().isLiquid()) {
			world.setBlockState(pos.above(), BlockRegistry.SWAMP_REED.defaultBlockState());
		} else {
			world.setBlockState(pos.above(), this.defaultBlockState());
		}
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}
}