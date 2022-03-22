package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockHollowLog;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenSmallHollowLog extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		int len = rand.nextInt(3) + 3;
		int offsetX = rand.nextInt(2), offsetZ = 1 - offsetX;

		for (int a = 0; a < len; a++)
			if (!world.isEmptyBlock(pos.offset(offsetX * a, 0, offsetZ * a)) || world.isEmptyBlock(pos.offset(offsetX * a, -1, offsetZ * a)))
				return false;

		boolean wasLastBranch = false;
		for (int a = 0; a < len; a++) {
			BlockState state = BlockRegistry.HOLLOW_LOG.defaultBlockState();
			if(offsetX != 0)
				state = state.setValue(BlockHollowLog.FACING, Direction.EAST);
			else if(offsetZ != 0)
				state = state.setValue(BlockHollowLog.FACING, Direction.SOUTH);
			this.setBlockAndNotifyAdequately(world, pos.offset(offsetX * a, 0, offsetZ * a), state);
			if(wasLastBranch) {
				wasLastBranch = false;
			} else if(rand.nextInt(6) == 0) {
				wasLastBranch = true;
				BlockState state2 = BlockRegistry.HOLLOW_LOG.defaultBlockState();
				if(offsetX != 0)
					state2 = state.setValue(BlockHollowLog.FACING, Direction.SOUTH);
				else if(offsetZ != 0)
					state2 = state.setValue(BlockHollowLog.FACING, Direction.EAST);
				BlockPos newPos = pos.offset(offsetX * a, 0, offsetZ * a);
				if(rand.nextInt(2) == 0)
					newPos = newPos.add(offsetZ != 0 ? 1 : 0, 0, offsetX != 0 ? 1 : 0);
				else
					newPos = newPos.add(offsetZ != 0 ? -1 : 0, 0, offsetX != 0 ? -1 : 0);
				if(world.isEmptyBlock(newPos)) {
					this.setBlockAndNotifyAdequately(world, newPos, state2);
				}
			}
		}
		return true;
	}
}
