package thebetweenlands.common.block.plant;

import net.minecraft.block.material.Material;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockBladderwortStalk extends BlockStackablePlantUnderwater {
	
	public BlockBladderwortStalk(Properties properties) {
		super(properties);
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
			world.setBlockAndUpdate(pos.above(), BlockRegistry.BLADDERWORT_FLOWER.defaultBlockState());
		} else {
			world.setBlockAndUpdate(pos.above(), this.defaultBlockState());
		}
	}
}
