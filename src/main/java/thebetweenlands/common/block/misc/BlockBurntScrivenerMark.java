package thebetweenlands.common.block.misc;

import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;

public class BlockBurntScrivenerMark extends BlockScrivenerMark {
	
	public static final BooleanProperty LINKED = BooleanProperty.create("linked");

	public BlockBurntScrivenerMark(Properties properties) {
		super(properties);
		//this.setHardness(0.5f);
		this.registerDefaultState(this.defaultBlockState().setValue(LINKED, false).setValue(NORTH_SIDE, false).setValue(EAST_SIDE, false).setValue(SOUTH_SIDE, false).setValue(WEST_SIDE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { LINKED, NORTH_SIDE, EAST_SIDE, SOUTH_SIDE, WEST_SIDE }, new IUnlistedProperty[0]));
	}

	//TODO: Remove this.
	/*@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(LINKED) ? 1 : 0;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(LINKED, meta == 1);
	}*/

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
