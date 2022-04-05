package thebetweenlands.common.block.misc;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.terrain.BlockRubberLog;
import thebetweenlands.common.item.tools.ItemBLBucket;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityRubberTap;
import thebetweenlands.util.StatePropertyHelper;

public class BlockRubberTap extends HorizontalFaceBlock {
	
	public static final IntegerProperty AMOUNT = IntegerProperty.create("amount", 0, 15);

	protected static final VoxelShape TAP_WEST_AABB = Block.box(0.4D, 0.0D, 0.15D, 1.0D, 1.0D, 0.85D);
	protected static final VoxelShape TAP_EAST_AABB = Block.box(0.0D, 0.0D, 0.15D, 0.6D, 1.0D, 0.85D);
	protected static final VoxelShape TAP_SOUTH_AABB = Block.box(0.15D, 0.0D, 0.0D, 0.85D, 1.0D, 0.6D);
	protected static final VoxelShape TAP_NORTH_AABB = Block.box(0.15D, 0.0D, 0.4D, 0.85D, 1.0D, 1.0D);

	/**
	 * The number of ticks it requires to fill up to the next step (15 steps in total)
	 */
	public final int ticksPerStep;

	public BlockRubberTap(int ticksPerStep, Properties properties) {
		super(properties);
		/*super(material.getMaterial());
		this.registerDefaultState(this.stateDefinition.any().setValue(AMOUNT, 0));
		this.setSoundType(material.getBlock().getSoundType());
		this.setHardness(2.0F);
		this.setCreativeTab(null);*/
		this.ticksPerStep = ticksPerStep;
	}

	@Override
	public TileEntity newBlockEntity(BlockState state, IBlockReader level) {
		return new TileEntityRubberTap();
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, World worldIn, BlockPos pos) {
		return 0.075F; //breaking speed shouldn't depend on tool
    }

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		if(!worldIn.isClientSide() && te instanceof TileEntityRubberTap) {
			player.awardStat(StatList.getBlockStats(this));
			player.causeFoodExhaustion(0.025F);

			TileEntityRubberTap tap = (TileEntityRubberTap) te;

			FluidStack drained = tap.drain(Fluid.BUCKET_VOLUME, false);

			if(drained != null && drained.amount == Fluid.BUCKET_VOLUME) {
				spawnAsEntity(worldIn, pos, getBucket(true));
			} else {
				spawnAsEntity(worldIn, pos, getBucket(false));
			}
		}
	}

	@Override
	public boolean canHarvestBlock(IBlockReader world, BlockPos pos, PlayerEntity player) {
		return true; //shouldn't depend on tool
    }
	
	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		if (this.canPlaceAt(world, pos, facing)) {
			return this.defaultBlockState().setValue(FACING, facing);
		} else {
			for (Direction Direction : FACING.getAllowedValues()) {
				if(this.canPlaceAt(world, pos, Direction))
					return this.defaultBlockState().setValue(FACING, Direction);
			}
			return this.defaultBlockState();
		}
	}

	@Override
	public void onPlace(World worldIn, BlockPos pos, BlockState state) {
		this.checkForDrop(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		for (Direction Direction : FACING.getAllowedValues()) {
			if (this.canPlaceAt(worldIn, pos, Direction)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (this.checkForDrop(world, pos, world.getBlockState(pos))) {
			Direction facing = (Direction)state.getValue(FACING);
			Direction.Axis axis = facing.getAxis();
			Direction oppositeFacing = facing.getOpposite();
			if (axis.isVertical() || !this.canPlaceOn(world, pos.offset(oppositeFacing))) {
				this.dropBlockAsItem(world, pos, state, 0);
				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
		}
	}

	protected boolean checkForDrop(World worldIn, BlockPos pos, BlockState state) {
		if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, (Direction)state.getValue(FACING))) {
			return true;
		} else {
			if (worldIn.getBlockState(pos).getBlock() == this) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
			return false;
		}
	}

	private boolean canPlaceAt(World worldIn, BlockPos pos, Direction facing) {
		BlockPos blockPos = pos.offset(facing.getOpposite());
		boolean isHorizontal = facing.getAxis().isHorizontal();
		return isHorizontal && this.canPlaceOn(worldIn, blockPos);
	}

	private boolean canPlaceOn(World worldIn, BlockPos pos) {
		BlockState state = worldIn.getBlockState(pos);
		return state.getBlock() == BlockRegistry.LOG_RUBBER && state.getValue(BlockRubberLog.NATURAL);
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((Direction)state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] {FACING, AMOUNT});
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((Direction)state.getValue(FACING)).getHorizontalIndex();
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		TileEntityRubberTap te = StatePropertyHelper.getTileEntityThreadSafe(worldIn, pos, TileEntityRubberTap.class);
		if(te != null) {
			FluidStack drained = ((TileEntityRubberTap)te).drain(Fluid.BUCKET_VOLUME, false);
			if(drained != null) {
				int amount = (int)((float)drained.amount / (float)Fluid.BUCKET_VOLUME * 15.0F);
				state = state.setValue(AMOUNT, amount);
			} else {
				state = state.setValue(AMOUNT, 0);
			}
		}
		return state;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new TileEntityRubberTap();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		switch ((Direction)state.getValue(FACING)) {
		default:
		case EAST:
			return TAP_EAST_AABB;
		case WEST:
			return TAP_WEST_AABB;
		case SOUTH:
			return TAP_SOUTH_AABB;
		case NORTH:
			return TAP_NORTH_AABB;
		}
	}

	@Nullable
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return this.getBoundingBox(state, level, pos);
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		drops.add(getBucket(false));
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return getBucket(false);
	}

    protected ItemStack getBucket(boolean withRubber) {
    	if (this == BlockRegistry.SYRMORITE_RUBBER_TAP.get()) {
    		return new ItemStack(withRubber ? ItemRegistry.BL_SYRMORITE_BUCKET_RUBBER.get() : ItemRegistry.SYRMORITE_BUCKET.get());
    	}
    	else {
    		return new ItemStack(withRubber ? ItemRegistry.BL_WEEDWOOD_BUCKET_RUBBER.get() : ItemRegistry.WEEDWOOD_BUCKET.get());
    	}
    }
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}
	

}
