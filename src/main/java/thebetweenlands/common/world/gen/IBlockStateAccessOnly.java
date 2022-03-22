package thebetweenlands.common.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public interface IBlockStateAccessOnly {
	public boolean isEmptyBlock(BlockPos pos);

	public BlockState getBlockState(BlockPos pos);

	public void setBlockState(BlockPos pos, BlockState state);

	public void setBlockState(BlockPos pos, BlockState state, int  flags);

	public static IBlockStateAccessOnly from(final World world) {
		return new IBlockStateAccessOnly() {
			@Override
			public boolean isEmptyBlock(BlockPos pos) {
				return world.isEmptyBlock(pos);
			}

			@Override
			public BlockState getBlockState(BlockPos pos) {
				return world.getBlockState(pos);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state) {
				world.setBlockState(pos, state);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state, int flags) {
				world.setBlockState(pos, state, flags);
			}
		};
	}

	public static IBlockStateAccessOnly from(final int chunkX, final int chunkZ, final ChunkPrimer primer) {
		return new IBlockStateAccessOnly() {
			@Override
			public boolean isEmptyBlock(BlockPos pos) {
				return primer.getBlockState(pos.getX() - chunkX * 16, pos.getY(), pos.getZ() - chunkZ * 16) == Blocks.AIR.defaultBlockState();
			}

			@Override
			public BlockState getBlockState(BlockPos pos) {
				return primer.getBlockState(pos.getX() - chunkX * 16, pos.getY(), pos.getZ() - chunkZ * 16);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state) {
				primer.setBlockState(pos.getX() - chunkX * 16, pos.getY(), pos.getZ() - chunkZ * 16, state);
			}

			@Override
			public void setBlockState(BlockPos pos, BlockState state, int flags) {
				primer.setBlockState(pos.getX() - chunkX * 16, pos.getY(), pos.getZ() - chunkZ * 16, state);
			}
		};
	}
}
