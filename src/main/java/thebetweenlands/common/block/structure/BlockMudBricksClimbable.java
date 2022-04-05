package thebetweenlands.common.block.structure;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMudBricksClimbable extends Block {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    protected static final VoxelShape LADDER_EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 0.99, 1.0D, 1.0D);
    protected static final VoxelShape LADDER_WEST_AABB = Block.box(0.01D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final VoxelShape LADDER_SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.99D);
    protected static final VoxelShape LADDER_NORTH_AABB = Block.box(0.0D, 0.0D, 0.01D, 1.0D, 1.0D, 1.0D);

	public BlockMudBricksClimbable(Properties properties) {
		super(properties);
		/*super(material);
		setHardness(0.4f);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setLightOpacity(191);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		switch (state.getValue(FACING)) {
		case NORTH:
			return LADDER_NORTH_AABB;
		case SOUTH:
			return LADDER_SOUTH_AABB;
		case WEST:
			return LADDER_WEST_AABB;
		case EAST:
		default:
			return LADDER_EAST_AABB;
		}
	}
	
	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean whatIsThis) {
		state = state.getActualState(worldIn, pos);
		switch (state.getValue(FACING)) {
			default:
			case EAST:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, LADDER_EAST_AABB);
				break;
			case WEST:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, LADDER_WEST_AABB);
				break;
			case SOUTH:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, LADDER_SOUTH_AABB);
				break;
			case NORTH:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, LADDER_NORTH_AABB);
				break;
		}
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
        entity.motionX = MathHelper.clamp(entity.motionX, -0.15000000596046448D, 0.15000000596046448D);
        entity.motionZ = MathHelper.clamp(entity.motionZ, -0.15000000596046448D, 0.15000000596046448D);
        entity.fallDistance = 0.0F;

        if (entity.motionY < -0.15D)
        	entity.motionY = -0.15D;

        if (entity.motionY < 0.0D)
        	entity.motionY = 0.0D;

        if (entity.collidedHorizontally)
        	entity.motionY = 0.2D;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta); // Using this instead of 'byHorizontalIndex' because the ids don't match and previous was release
		return defaultBlockState().setValue(FACING, facing.getAxis().isHorizontal() ? facing: Direction.NORTH);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		meta = meta | state.getValue(FACING).getIndex();
		return meta;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader world, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
}
