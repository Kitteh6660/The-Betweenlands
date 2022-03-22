package thebetweenlands.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public interface ITintedBlock {
	int getColorMultiplier(BlockState state, @Nullable IWorldReader worldIn, @Nullable BlockPos pos, int tintIndex);
}
