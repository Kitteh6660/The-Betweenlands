package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenWaterRootsCluster extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		boolean generated = false;

		for (BlockState iblockstate = world.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, world, position) || iblockstate.getBlock().isLeaves(iblockstate, world, position)) && position.getY() > 0; iblockstate = world.getBlockState(position)) {
			position = position.below();
		}

		for (int i = 0; i < 128; ++i) {
			BlockPos pos = position.add(rand.nextInt(10) - rand.nextInt(10), rand.nextInt(8) - rand.nextInt(8), rand.nextInt(10) - rand.nextInt(10));

			if(SurfaceType.WATER.matches(world, pos.above()) && SurfaceType.MIXED_GROUND.matches(world, pos)) {
				if(this.generateWaterRootsStack(world, rand, pos.above()))
					generated = true;
			}
		}

		return generated;
	}

	private boolean generateWaterRootsStack(World world, Random rand, BlockPos pos) {
		int height = 0;
		BlockPos.Mutable checkPos = new BlockPos.Mutable();
		for(int yo = 0; yo < 128; yo++) {
			checkPos.setPos(pos.getX(), pos.getY() + yo, pos.getZ());
			if(!world.isEmptyBlock(checkPos) && !SurfaceType.WATER.matches(world, checkPos))
				return false;
			if(world.isEmptyBlock(checkPos) && world.isEmptyBlock(checkPos.setPos(pos.getX(), pos.getY() + yo + 1, pos.getZ()))) {
				height = yo;
				break;
			}
		}
		if(height < 1)
			return false;
		height = rand.nextInt(height) + 1 + rand.nextInt(4);
		for(int yo = 0; yo < height; yo++) {
			BlockPos offsetPos = pos.offset(0, yo, 0);
			BlockState state = world.getBlockState(offsetPos);
			if(!SurfaceType.WATER.matches(state)) {
				if(state.getBlock() != Blocks.AIR)
					break;
				world.setBlockState(offsetPos, BlockRegistry.ROOT.defaultBlockState(), 2 | 16);
			} else {
				world.setBlockState(offsetPos, BlockRegistry.ROOT_UNDERWATER.defaultBlockState(), 2 | 16);
			}
		}
		return true;
	}
}