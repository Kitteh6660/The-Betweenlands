package thebetweenlands.api.block;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import thebetweenlands.api.aspect.IAspectType;

public interface IAspectFogBlock {
	@Nullable
	public IAspectType getAspectFogType(IWorldReader world, BlockPos pos, BlockState state);
}
