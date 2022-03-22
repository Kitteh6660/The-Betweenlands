package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenRootsCluster extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		boolean generated = false;

		for (BlockState iblockstate = world.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, world, position) || iblockstate.getBlock().isLeaves(iblockstate, world, position)) && position.getY() > 0; iblockstate = world.getBlockState(position)) {
			position = position.below();
		}

		for (int i = 0; i < 128; ++i) {
			BlockPos pos = position.add(rand.nextInt(10) - rand.nextInt(10), rand.nextInt(8) - rand.nextInt(8), rand.nextInt(10) - rand.nextInt(10));

			if(SurfaceType.MIXED_GROUND.matches(world, pos)) {
				if(this.generateRootsStack(world, rand, pos.above()))
					generated = true;
			}
		}

		return generated;
	}

	private boolean generateRootsStack(World world, Random rand, BlockPos pos) {
		int height = 6;
		BlockPos.Mutable checkPos = new BlockPos.Mutable();
		for(int yo = 0; yo < 6; yo++) {
			checkPos.setPos(pos.getX(), pos.getY() + yo, pos.getZ());
			if(!world.isEmptyBlock(checkPos)) {
				height = yo;
				break;
			}
		}
		if(height < 2)
			return false;
		height = rand.nextInt(height) + 1 + rand.nextInt(4);
		for(int yo = 0; yo < height; yo++) {
			BlockPos offsetPos = pos.offset(0, yo, 0);
			world.setBlockState(offsetPos, BlockRegistry.ROOT.defaultBlockState(), 2 | 16);
		}
		return true;
	}
}