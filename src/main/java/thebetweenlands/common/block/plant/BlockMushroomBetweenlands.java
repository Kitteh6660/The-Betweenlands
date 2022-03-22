package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockState;

public class BlockMushroomBetweenlands extends BlockPlant {
	@Override
	protected boolean canSustainBush(BlockState state) {
		return state.isOpaqueCube();
	}
}
