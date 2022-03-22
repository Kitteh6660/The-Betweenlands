package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenSwampKelpCluster extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		boolean generated = false;

		for (BlockState iblockstate = world.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, world, position) || iblockstate.getBlock().isLeaves(iblockstate, world, position)) && position.getY() > 0; iblockstate = world.getBlockState(position)) {
			position = position.below();
		}

		for (int i = 0; i < 128; ++i) {
			BlockPos pos = position.add(rand.nextInt(10) - rand.nextInt(10), rand.nextInt(8) - rand.nextInt(8), rand.nextInt(10) - rand.nextInt(10));

			if(SurfaceType.WATER.matches(world, pos.above()) && SurfaceType.DIRT.matches(world, pos)) {
				if(this.generateSwampKelpStack(world, rand, pos.above()))
					generated = true;
			}
		}

		return generated;
	}

	private boolean generateSwampKelpStack(World world, Random rand, BlockPos pos) {
		int height = 0;
		BlockPos.Mutable checkPos = new BlockPos.Mutable();
		for(int yo = 0; yo < 128; yo++) {
			checkPos.setPos(pos.getX(), pos.getY() + yo, pos.getZ());
			if(!SurfaceType.WATER.matches(world, checkPos)) {
				height = yo;
				break;
			}
		}
		if(height < 2)
			return false;
		height = Math.min(height, 7);
		height = rand.nextInt(height) + 1;
		for(int yo = 0; yo < height; yo++) {
			BlockPos offsetPos = pos.offset(0, yo, 0);
			BlockState state = world.getBlockState(offsetPos);
			if(SurfaceType.WATER.matches(state)) {
				world.setBlockState(offsetPos, BlockRegistry.SWAMP_KELP.defaultBlockState(), 2 | 16);
				//this.setBlockAndNotifyAdequately(world, offsetPos, BlockRegistry.SWAMP_KELP.defaultBlockState());
			}
		}
		return true;
	}
}