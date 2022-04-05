package thebetweenlands.common.block.structure;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockWoodenSupportBeam extends HorizontalFaceBlock {
	
	private static final VoxelShape[] SELECTION_AABB = {
		Block.box(0.28D, 0, 0, 0.72D, 1, 1), //north/south
		Block.box(0, 0, 0.28D, 1, 1, 0.72D)  //east/west
	};
	
	public static final BooleanProperty TOP = BooleanProperty.create("top");

	public BlockWoodenSupportBeam(Properties properties) {
		super(properties);
		/*super(material);
		setHardness(2.0F);
		setHarvestLevel("axe", 0);
		setCreativeTab(BLCreativeTabs.PLANTS);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setSoundType(SoundType.WOOD);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TOP, false));
	}

	@Override
	public boolean isWood(IBlockReader world, BlockPos pos) {
		return true;
	}
	
    @Override
	@Nullable
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
    	Direction facing = state.getValue(FACING);
    	return SELECTION_AABB[facing.getAxis() == Axis.Z ? 0 : 1];
    }
    
    // Entities should be able to path and walk through this otherwise it blocks the dungeon in some areas.
    @Override
    @Nullable
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos) {
        return isOnFire(world, pos) ? PathNodeType.DAMAGE_FIRE : PathNodeType.OPEN;
    }

    @Override
    public boolean isPassable(IBlockReader world, BlockPos pos) {
        return true;
    }

    @Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
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
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		BlockState state = defaultBlockState().setValue(FACING, facing);
		if (this.canPlaceAt(world, pos, facing)) {
			return facing != Direction.DOWN && (facing == Direction.UP || (double)hitY <= 0.5D) ? state.setValue(TOP, false) : state.setValue(TOP, true);
		}
		return this.defaultBlockState();
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, Direction side) {
		return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && this.canPlaceAt(worldIn, pos, side);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return false;
	}

	private boolean canPlaceAt(World world, BlockPos pos, Direction facing) {
		BlockPos blockpos = pos.offset(facing.getOpposite());
		boolean isSide = facing.getAxis().isHorizontal();
		return isSide && world.getBlockState(blockpos).isSideSolid(world, blockpos, facing);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		Direction facing = world.getBlockState(pos).getValue(FACING);
    	if(!canPlaceAt((World) world, pos, facing)) {
            this.dropBlockAsItem((World) world, pos, world.getBlockState(pos), 0);
            ((World) world).setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byHorizontalIndex(meta & 0b11);
		if (facing.getAxis() == Direction.Axis.Y)
			facing = Direction.NORTH;
		return defaultBlockState().setValue(FACING, facing).setValue(TOP, Boolean.valueOf((meta & 0b100) > 0));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		meta = meta | ((Direction) state.getValue(FACING)).getHorizontalIndex();

		if (((Boolean) state.getValue(TOP)).booleanValue())
			meta |= 0b100;

		return meta;
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate((Direction) state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((Direction) state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { FACING, TOP });
	}
}
