package thebetweenlands.common.block.structure;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;

import java.util.List;

/**
 * Just a vanilla C&P because it's not extensible...
 */
public class BlockWallBetweenlands extends Block {
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	protected static final AxisAlignedBB[] AABB_BY_INDEX = Block.box[] {Block.box(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), Block.box(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), Block.box(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), Block.box(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), Block.box(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), Block.box(0.3125D, 0.0D, 0.0D, 0.6875D, 0.875D, 1.0D), Block.box(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), Block.box(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D), Block.box(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D), Block.box(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), Block.box(0.0D, 0.0D, 0.3125D, 1.0D, 0.875D, 0.6875D), Block.box(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), Block.box(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), Block.box(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
	protected static final AxisAlignedBB[] CLIP_AABB_BY_INDEX = Block.box[] {AABB_BY_INDEX[0].setMaxY(1.5D), AABB_BY_INDEX[1].setMaxY(1.5D), AABB_BY_INDEX[2].setMaxY(1.5D), AABB_BY_INDEX[3].setMaxY(1.5D), AABB_BY_INDEX[4].setMaxY(1.5D), AABB_BY_INDEX[5].setMaxY(1.5D), AABB_BY_INDEX[6].setMaxY(1.5D), AABB_BY_INDEX[7].setMaxY(1.5D), AABB_BY_INDEX[8].setMaxY(1.5D), AABB_BY_INDEX[9].setMaxY(1.5D), AABB_BY_INDEX[10].setMaxY(1.5D), AABB_BY_INDEX[11].setMaxY(1.5D), AABB_BY_INDEX[12].setMaxY(1.5D), AABB_BY_INDEX[13].setMaxY(1.5D), AABB_BY_INDEX[14].setMaxY(1.5D), AABB_BY_INDEX[15].setMaxY(1.5D)};


	public BlockWallBetweenlands(BlockState state) {
		super(state.getMaterial());
		setSoundType(state.getBlock().getSoundType());
		setHardness(2.0F);
		this.setDefaultState(this.blockState.getBaseState().setValue(UP, Boolean.FALSE).setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE));
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		state = this.getActualState(state, source, pos);
		return AABB_BY_INDEX[getAABBIndex(state)];
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState) {
			state = this.getActualState(state, worldIn, pos);
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, CLIP_AABB_BY_INDEX[getAABBIndex(state)]);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		blockState = this.getActualState(blockState, worldIn, pos);
		return CLIP_AABB_BY_INDEX[getAABBIndex(blockState)];
	}

	private static int getAABBIndex(BlockState state) {
		int i = 0;

		if (state.getValue(NORTH)) {
			i |= 1 << Direction.NORTH.getHorizontalIndex();
		}

		if (state.getValue(EAST)) {
			i |= 1 << Direction.EAST.getHorizontalIndex();
		}

		if (state.getValue(SOUTH)) {
			i |= 1 << Direction.SOUTH.getHorizontalIndex();
		}

		if (state.getValue(WEST)) {
			i |= 1 << Direction.WEST.getHorizontalIndex();
		}

		return i;
	}

	@Override
	public boolean isFullCube(BlockState state)
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockReader worldIn, BlockPos pos)
	{
		return false;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	@Override
	public boolean isOpaqueCube(BlockState state)
	{
		return false;
	}

	private boolean canConnectTo(IBlockReader worldIn, BlockPos pos, Direction facing) {
		BlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, facing);
		boolean flag = blockfaceshape == BlockFaceShape.MIDDLE_POLE_THICK || blockfaceshape == BlockFaceShape.MIDDLE_POLE && (block instanceof BlockFenceGate || block instanceof BlockFenceGateBetweenlands);
		return !isExcepBlockForAttachWithPiston(block) && blockfaceshape == BlockFaceShape.SOLID || flag;
	}

	protected static boolean isExcepBlockForAttachWithPiston(Block block) {
		return Block.isExceptBlockForAttachWithPiston(block) || block == Blocks.BARRIER || block == Blocks.MELON_BLOCK || block == Blocks.PUMPKIN || block == Blocks.LIT_PUMPKIN;
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(BlockState state)
	{
		return 0;
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		return side == Direction.DOWN ? super.shouldSideBeRendered(blockState, blockAccess, pos, side) : true;
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState();
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(BlockState state) {
		return 0;
	}

	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		boolean flag =  canWallConnectTo(worldIn, pos, Direction.NORTH);
		boolean flag1 = canWallConnectTo(worldIn, pos, Direction.EAST);
		boolean flag2 = canWallConnectTo(worldIn, pos, Direction.SOUTH);
		boolean flag3 = canWallConnectTo(worldIn, pos, Direction.WEST);
		boolean flag4 = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3;
		return state.setValue(UP, !flag4 || !worldIn.isEmptyBlock(pos.above())).setValue(NORTH, flag).setValue(EAST, flag1).setValue(SOUTH, flag2).setValue(WEST, flag3);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {UP, NORTH, EAST, WEST, SOUTH});
	}
	
	@Override
	public boolean canPlaceTorchOnTop(BlockState state, IBlockReader world, BlockPos pos)
    {
		return true;
    }

    @Override
	public BlockFaceShape getBlockFaceShape(IBlockReader world, BlockState state, BlockPos pos, Direction facing) {
		return facing != Direction.UP && facing != Direction.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
	}

	/* ======================================== FORGE START ======================================== */

	@Override
	public boolean canBeConnectedTo(IBlockReader world, BlockPos pos, Direction facing) {
		Block connector = world.getBlockState(pos.offset(facing)).getBlock();
		return connector instanceof BlockWall || connector instanceof BlockFenceGate || connector instanceof BlockWallBetweenlands || connector instanceof BlockFenceGateBetweenlands;
	}

	private boolean canWallConnectTo(IBlockReader world, BlockPos pos, Direction facing) {
		BlockPos other = pos.offset(facing);
		Block block = world.getBlockState(other).getBlock();
		return block.canBeConnectedTo(world, other, facing.getOpposite()) || canConnectTo(world, other, facing.getOpposite());
	}

    /* ======================================== FORGE END ======================================== */
}