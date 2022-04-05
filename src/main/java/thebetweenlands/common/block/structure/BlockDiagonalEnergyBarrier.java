package thebetweenlands.common.block.structure;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.IConnectedTextureBlock;

public class BlockDiagonalEnergyBarrier extends Block implements IConnectedTextureBlock {
	
	public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");

	public BlockDiagonalEnergyBarrier(Properties properties) {
		super(properties);
		/*super(Material.GLASS);
		setSoundType(SoundType.GLASS);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setBlockUnbreakable();
		setResistance(6000000.0F);
		setLightLevel(0.8F);*/
		setDefaultState(getBlockState().getBaseState().setValue(FLIPPED, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockAccess.getBlockState(pos.offset(side)).getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Override
    public boolean causesSuffocation(BlockState state) {
    	return false;
    }

	@Override
    public boolean isFullCube(BlockState state){
        return false;
    }

	@Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		
	}

	@Nullable
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public VoxelShape getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
		return VoxelShapes.block().move(pos);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState().setValue(FLIPPED, Boolean.valueOf(meta > 0));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FLIPPED) ? 1 : 0;
	}
	
	@Override
	 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return defaultBlockState().setValue(FLIPPED, getFacingFromEntity(pos, placer));
	}

	public static boolean getFacingFromEntity(BlockPos pos, LivingEntity entity) {
		Direction facing = entity.getDirection();
		if (facing == Direction.EAST || facing == Direction.WEST)
			return true;
		return false;
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { FLIPPED }, new IUnlistedProperty[0]));
	}
	
	@Override
	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
		final boolean flipped = state.getValue(FLIPPED);
		return this.getExtendedConnectedTextureState((IExtendedBlockState) state, world, pos, p -> {
			int xzSteps = Math.abs(p.getX() - pos.getX()) + Math.abs(p.getZ() - pos.getZ());
			BlockState otherState = world.getBlockState(p);
			
			//Only connect up/down or diagonals
			if((p.getY() != pos.getY() && xzSteps == 0) || xzSteps > 1) {
				return otherState.getBlock() instanceof BlockDiagonalEnergyBarrier && otherState.getValue(FLIPPED) == flipped;
			}
			
			return false;
		}, false);
	}

    public static final AxisAlignedBB CORNER_NW_AABB = Block.box(0D, 0D, 0D, 0.25D, 1D, 0.25D);
    public static final AxisAlignedBB CORNER_SW_AABB = Block.box(0D, 0D, 0.75D, 0.25D, 1D, 1D);
    public static final AxisAlignedBB CORNER_NE_AABB = Block.box(0.75D, 0D, 0D, 1D, 1D, 0.25D);
    public static final AxisAlignedBB CORNER_SE_AABB = Block.box(0.75D, 0D, 0.75D, 1D, 1D, 1D);
    
    public static final AxisAlignedBB MID_NW_AABB = Block.box(0.25D, 0D, 0.25D, 0.5D, 1D, 0.5D);
    public static final AxisAlignedBB MID_SW_AABB = Block.box(0.25D, 0D, 0.5D, 0.5D, 1D, 0.75D);
    public static final AxisAlignedBB MID_NE_AABB = Block.box(0.5D, 0D, 0.25D, 0.75D, 1D, 0.5D);
    public static final AxisAlignedBB MID_SE_AABB = Block.box(0.5D, 0D, 0.5D, 0.75D, 1D, 0.75D);

    @Override
	public void addCollisionBoxToList(BlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState)
			state = state.getActualState(world, pos);

		if (state.getValue(FLIPPED)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_NW_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_NW_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_SE_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_SE_AABB);
		}

		if (!state.getValue(FLIPPED)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_NE_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_NE_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_SW_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_SW_AABB);
		}
	}
    
    @Override
    public RayTraceResult collisionRayTrace(BlockState blockState, World worldIn, BlockPos pos, Vector3d start, Vector3d end) {
    	RayTraceResult result = super.collisionRayTrace(blockState, worldIn, pos, start, end);
    	
    	if(result != null) {
    		//Got intersection with full AABB, now check for intersection with
    		//plane
    		
    		Vector3d diff = end.subtract(start);
    		Vector3d dir = diff.normalize();
    		
    		Vector3d p0 = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    		Vector3d n = blockState.getValue(FLIPPED) ? new Vector3d(0.70710678118D, 0, -0.70710678118D) : new Vector3d(0.70710678118D, 0, 0.70710678118D);
    		
    		double d = p0.subtract(start).dotProduct(n) / (dir.dotProduct(n));
    		
    		Vector3d intercept = start.add(dir.scale(d));
    		
    		if(intercept.x >= pos.getX() && intercept.x <= pos.getX() + 1 &&
    				intercept.y >= pos.getY() && intercept.y <= pos.getY() + 1 &&
    				intercept.z >= pos.getZ() && intercept.z <= pos.getZ() + 1) {
    			return new RayTraceResult(intercept, result.sideHit, result.getBlockPos());
    		}
    	}
    	
    	return null;
    }
    
	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		/*if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (!player.isSpectator()) {
				entity.hurt(DamageSource.MAGIC, 1);
				double dx = (entity.getX() - (pos.getX())) * 2 - 1;
				double dz = (entity.getZ() - (pos.getZ())) * 2 - 1;
				if (Math.abs(dx) > Math.abs(dz))
					dz = 0;
				else
					dx = 0;
				dx = (int) dx;
				dz = (int) dz;
				entity.addVelocity(dx * 0.85D, 0.08D, dz * 0.85D);
				entity.playSound(SoundRegistry.REJECTED, 0.5F, 1F);
			}
		}*/
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}