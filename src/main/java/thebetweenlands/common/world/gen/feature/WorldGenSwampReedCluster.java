package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenSwampReedCluster extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		boolean generated = false;

		for (BlockState iblockstate = world.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, world, position) || iblockstate.getBlock().isLeaves(iblockstate, world, position)) && position.getY() > 0; iblockstate = world.getBlockState(position)) {
			position = position.below();
		}

		for (int i = 0; i < 128; ++i) {
			BlockPos pos = position.add(rand.nextInt(10) - rand.nextInt(10), rand.nextInt(8) - rand.nextInt(8), rand.nextInt(10) - rand.nextInt(10));

			if (world.isBlockLoaded(pos)) {
				if (SurfaceType.WATER.matches(world, pos.above()) && world.getBlockState(pos).getBlock() == BlockRegistry.MUD && world.isEmptyBlock(pos.above(2))) {
					this.generateReedStack(world, rand, pos.above());
					generated = true;
				} else if (SurfaceType.MIXED_GROUND.matches(world, pos) && BlockRegistry.SWAMP_REED.canPlaceBlockAt(world, pos.above())) {
					this.generateReedStack(world, rand, pos.above());
					generated = true;
				}
			}
		}

		return generated;
	}

	private void generateReedStack(World world, Random rand, BlockPos pos) {
		int height = world.rand.nextInt(4) + 2;
		for(int yo = 0; yo < height; yo++) {
			BlockPos offsetPos = pos.offset(0, yo, 0);
			BlockState state = world.getBlockState(offsetPos);
			if(state.getBlock() != Blocks.AIR && !SurfaceType.WATER.matches(state)) {
				break;
			}
			if(SurfaceType.WATER.matches(state)) {
				world.setBlockState(offsetPos, BlockRegistry.SWAMP_REED_UNDERWATER.defaultBlockState(), 2 | 16);
			} else {
				world.setBlockState(offsetPos, BlockRegistry.SWAMP_REED.defaultBlockState(), 2 | 16);
			}
		}
	}
}