package thebetweenlands.common.block.misc;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.block.IConnectedTextureBlock;

public class BlockScrivenerMark extends Block implements IConnectedTextureBlock {
	
	public static final BooleanProperty NORTH_SIDE = BooleanProperty.create("north_side");
	public static final BooleanProperty EAST_SIDE = BooleanProperty.create("east_side");
	public static final BooleanProperty SOUTH_SIDE = BooleanProperty.create("south_side");
	public static final BooleanProperty WEST_SIDE = BooleanProperty.create("west_side");
	
	public BlockScrivenerMark(Properties properties) {
		super(properties);
		/*super(Material.CIRCUITS);
		this.setItemDropped(() -> Items.AIR);
		this.setLightOpacity(0);*/
		this.registerDefaultState(this.defaultBlockState().setValue(NORTH_SIDE, false).setValue(EAST_SIDE, false).setValue(SOUTH_SIDE, false).setValue(WEST_SIDE, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return Block.box(0.0D, 0.0D, 0.0D, 1.0D, 0.1D, 1.0D);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { NORTH_SIDE, EAST_SIDE, SOUTH_SIDE, WEST_SIDE }, new IUnlistedProperty[0]));
	}

	//TODO: remove this code.
	/*@Override
	public int getMetaFromState(BlockState state) {
		return 0;
	}*/

	@Override
	public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;
		return this.getExtendedConnectedTextureState(state, worldIn, pos, p -> worldIn.getBlockState(p).getBlock() instanceof BlockScrivenerMark, false);
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		state = state.setValue(WEST_SIDE, this.shouldAttachUp(worldIn, pos, Direction.WEST));
		state = state.setValue(EAST_SIDE, this.shouldAttachUp(worldIn, pos, Direction.EAST));
		state = state.setValue(NORTH_SIDE, this.shouldAttachUp(worldIn, pos, Direction.NORTH));
		state = state.setValue(SOUTH_SIDE, this.shouldAttachUp(worldIn, pos, Direction.SOUTH));
		return state;
	}

	private boolean shouldAttachUp(IBlockReader worldIn, BlockPos pos, Direction direction) {
		BlockPos offsetPos = pos.offset(direction);
		BlockState offsetState = worldIn.getBlockState(offsetPos);

		if(!canConnectTo(worldIn.getBlockState(offsetPos), direction, worldIn, offsetPos) && (offsetState.isNormalCube() || !canConnectUpwardsTo(worldIn, offsetPos.below()))) {
			if(worldIn.getBlockState(offsetPos).isSideSolid(worldIn, offsetPos, Direction.UP) && canConnectUpwardsTo(worldIn, offsetPos.above()) && offsetState.isBlockNormalCube()) {
				return true;
			}
		}

		return false;
	}

	protected static boolean canConnectUpwardsTo(IBlockReader worldIn, BlockPos pos) {
		return canConnectTo(worldIn.getBlockState(pos), null, worldIn, pos);
	}

	protected static boolean canConnectTo(BlockState blockState, @Nullable Direction side, IBlockReader world, BlockPos pos) {
		return blockState.getBlock() instanceof BlockScrivenerMark;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(BlockState state) {
		return false;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		BlockState downState = worldIn.getBlockState(pos.below());
		return downState.isTopSolid() || downState.getBlockFaceShape(worldIn, pos.below(), Direction.UP) == BlockFaceShape.SOLID;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isClientSide()) {
			if(!this.canPlaceBlockAt(worldIn, pos)) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, 0);
	}
}
