package thebetweenlands.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public interface IDungeonFogBlock {
	public boolean isCreatingDungeonFog(IWorldReader world, BlockPos pos, BlockState state);
}
