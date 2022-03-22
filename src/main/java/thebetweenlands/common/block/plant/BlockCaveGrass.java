package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockState;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockCaveGrass extends BlockPlant {
	@Override
	protected boolean canSustainBush(BlockState state) {
		return SoilHelper.canSustainPlant(state) || state.getBlock() == BlockRegistry.BETWEENSTONE || state.getBlock() == BlockRegistry.PITSTONE;
	}
}
