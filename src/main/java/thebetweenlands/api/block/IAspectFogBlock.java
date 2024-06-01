package thebetweenlands.api.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.IAspectType;

public interface IAspectFogBlock {
	@Nullable
	public IAspectType getAspectFogType(LevelAccessor world, BlockPos pos, BlockState state);
}
