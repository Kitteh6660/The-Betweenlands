package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockState;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockCaveGrass extends BlockPlant {
	
	public BlockCaveGrass(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return SoilHelper.canSustainPlant(state) || state.getBlock() == BlockRegistry.BETWEENSTONE.get() || state.getBlock() == BlockRegistry.PITSTONE.get();
	}
}
