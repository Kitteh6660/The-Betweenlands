package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockStalactite extends Block implements IWaterLoggable {
	
	public static final BooleanProperty NO_BOTTOM = BooleanProperty.create("no_bottom");
	public static final BooleanProperty NO_TOP = BooleanProperty.create("no_top");
	public static final IntegerProperty DIST_UP = IntegerProperty.create("dist_up", 0, 3);
	public static final IntegerProperty DIST_DOWN = IntegerProperty.create("dist_down", 0, 3);
	public static final IntegerProperty POS_X = IntegerProperty.create("pos_x", 0, 3);
	public static final IntegerProperty POS_Y = IntegerProperty.create("pos_x", 0, 3);
	public static final IntegerProperty POS_Z = IntegerProperty.create("pos_z", 0, 3);

	public BlockStalactite(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {POS_X, POS_Y, POS_Z, NO_BOTTOM, NO_TOP, DIST_UP, DIST_DOWN});
	}

	@Override
	public boolean isBlockNormalCube(BlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState blockState) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return null;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}
}
