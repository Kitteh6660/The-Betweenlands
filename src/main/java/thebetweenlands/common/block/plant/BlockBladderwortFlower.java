package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockBladderwortFlower extends BlockPlant {
	
	public BlockBladderwortFlower(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return state.getBlock() == BlockRegistry.BLADDERWORT_STALK;
	}

	/*@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}*/
}