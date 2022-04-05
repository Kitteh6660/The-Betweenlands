package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockState;

public class BlockMushroomBetweenlands extends BlockPlant {
	
	public BlockMushroomBetweenlands(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return state.canOcclude();
	}
}
