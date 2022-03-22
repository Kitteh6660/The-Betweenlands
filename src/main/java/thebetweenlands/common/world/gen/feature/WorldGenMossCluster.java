package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockMoss;

public class WorldGenMossCluster extends WorldGenerator {
	private final BlockState blockState;
	private final Block block;
	private final int offset;
	private final int attempts;

	public WorldGenMossCluster(BlockState blockState, int offset, int attempts) {
		this.blockState = blockState;
		this.block = blockState.getBlock();
		this.offset = offset;
		this.attempts = attempts;
	}

	public WorldGenMossCluster(BlockState blockState) {
		this(blockState, 8, 256);
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		boolean generated = false;

		for (BlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
			position = position.below();
		}

		PooledMutableBlockPos mutablePos = PooledMutableBlockPos.retain();
		for (int i = 0; i < this.attempts; ++i) {
			mutablePos.setPos(position.getX() + rand.nextInt(this.offset) - rand.nextInt(this.offset), position.getY() + rand.nextInt(this.offset/2+1) - rand.nextInt(this.offset/2+1), position.getZ() + rand.nextInt(this.offset) - rand.nextInt(this.offset));

			if (worldIn.isAreaLoaded(mutablePos, 1) && worldIn.isEmptyBlock(mutablePos) && this.block.canPlaceBlockAt(worldIn, mutablePos)) {
				Direction facing = Direction.byIndex(rand.nextInt(Direction.VALUES.length));
				Direction.Axis axis = facing.getAxis();
				Direction oppositeFacing = facing.getOpposite();
				boolean isInvalid = false;
				if (axis.isHorizontal() && !worldIn.isSideSolid(mutablePos.offset(oppositeFacing), facing, true)) {
					isInvalid = true;
				} else if (axis.isVertical() && !this.canPlaceOn(worldIn, mutablePos.offset(oppositeFacing))) {
					isInvalid = true;
				}
				if (!isInvalid) {
					BlockState state = this.blockState.setValue(BlockMoss.FACING, facing);
					this.setBlockAndNotifyAdequately(worldIn, mutablePos.toImmutable(), state);
					generated = true;
				}
			}
		}
		mutablePos.release();

		return generated;
	}

	private boolean canPlaceOn(World worldIn, BlockPos pos) {
		BlockState state = worldIn.getBlockState(pos);
		if (state.isSideSolid(worldIn, pos, Direction.UP)) {
			return true;
		} else {
			return false;
		}
	}
}