package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import thebetweenlands.common.block.terrain.BlockLogBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenRottenLogs extends Feature<IFeatureConfig> {

	private int length = -1;
	private int baseRadius = -1;
	private byte direction;
	private BlockState log;

	public WorldGenRottenLogs(int length, int baseRadius, byte direction) {
		this.length = length;
		this.baseRadius = baseRadius;
		this.direction = direction;
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, IFeatureConfig config) {
		
		this.log = BlockRegistry.ROTTEN_WOOD.get().defaultBlockState();

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		// Trunk N/S
		if (direction == 1) {
			for (int xx = x - baseRadius; baseRadius + x >= xx; xx++) {
				for (int zz = z - length; length + z - 1 >= zz; zz++) {
					for (int yy = y + 1; yy <= y + baseRadius * 2; yy++) {
						BlockPos offsetPos = new BlockPos(xx, yy, zz);
						if (!world.isBlockLoaded(offsetPos) || !world.getBlockState(offsetPos).getBlock().isReplaceable(world, offsetPos)) {
							return false;
						}
					}
				}
			}

			for (int zz = z - length; length + z - 1 >= zz; zz++)
				for (int i = baseRadius * -1; i <= baseRadius; ++i)
					for (int j = baseRadius * -1; j <= baseRadius; ++j) {
						double dSq = i * i + j * j;
						BlockPos offsetPos = new BlockPos(x + i, y + j + baseRadius, zz);
						if (Math.round(Math.sqrt(dSq)) == baseRadius) {
							world.setBlock(offsetPos, log, 2 | 16);
							if (random.nextInt(12) == 0)
								world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 2 | 16);
							if (zz == z - length && random.nextInt(2) == 0 || zz == z + length - 1 && random.nextInt(2) == 0)
								world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 2 | 16);
						} else
							world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 2 | 16);
					}

		} else {

			// Trunk E/W
			for (int xx = x - length; length + x - 1 >= xx; xx++) {
				for (int zz = z - baseRadius; baseRadius + z >= zz; zz++) {
					for (int yy = y + 1; yy <= y + baseRadius * 2; yy++) {
						BlockPos offsetPos = new BlockPos(xx, yy, zz);
						if (!world.isBlockLoaded(offsetPos) || !world.getBlockState(offsetPos).getBlock().isReplaceable(world, offsetPos)) {
							return false;
						}
					}
				}
			}

			for (int xx = x - length; length + x - 1 >= xx; xx++)
				for (int i = baseRadius * -1; i <= baseRadius; ++i)
					for (int j = baseRadius * -1; j <= baseRadius; ++j) {
						double dSq = i * i + j * j;
						BlockPos offsetPos = new BlockPos(xx, y + j + baseRadius, z + i);
						if (Math.round(Math.sqrt(dSq)) == baseRadius) {
							world.setBlock(offsetPos, log, 2 | 16);
							if (random.nextInt(12) == 0)
								world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 2 | 16);
							if (xx == x - length && random.nextInt(2) == 0 || xx == x + length - 1 && random.nextInt(2) == 0)
								world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 2 | 16);
						} else
							world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 2 | 16);
					}
		}
		return true;
	}

}
