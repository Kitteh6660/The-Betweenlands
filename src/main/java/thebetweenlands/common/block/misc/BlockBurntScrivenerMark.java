package thebetweenlands.common.block.misc;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockBurntScrivenerMark extends BlockScrivenerMark {
	public static final BooleanProperty LINKED = BooleanProperty.create("linked");

	public BlockBurntScrivenerMark() {
		this.setHardness(0.5f);
		this.setDefaultState(this.blockState.getBaseState().setValue(LINKED, false).setValue(NORTH_SIDE, false).setValue(EAST_SIDE, false).setValue(SOUTH_SIDE, false).setValue(WEST_SIDE, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { LINKED, NORTH_SIDE, EAST_SIDE, SOUTH_SIDE, WEST_SIDE }, new IUnlistedProperty[0]));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(LINKED) ? 1 : 0;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(LINKED, meta == 1);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
