package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockDungeonWallCandle extends HorizontalFaceBlock {

	protected static final VoxelShape CANDLE_WEST_AABB = Block.box(0.25D, 0D, 0.25D, 1, 0.875D, 0.75D);
	protected static final VoxelShape CANDLE_EAST_AABB = Block.box(0D, 0D, 0.25D, 0.75D, 0.875D, 0.75D);
	protected static final VoxelShape CANDLE_SOUTH_AABB = Block.box(0.25D, 0D, 0D, 0.75D, 0.875D, 0.75D);
	protected static final VoxelShape CANDLE_NORTH_AABB = Block.box(0.25D, 0D, 0.25D, 0.75D, 0.875D, 1D);
	public static final BooleanProperty LIT = BooleanProperty.create("lit");

	public BlockDungeonWallCandle(Properties properties) {
		super(properties);
		/*super(material);
		setHardness(0.1F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		state = state.getActualState(source, pos);
		switch ((Direction) state.getValue(FACING)) {
		default:
		case EAST:
			return CANDLE_EAST_AABB;
		case WEST:
			return CANDLE_WEST_AABB;
		case SOUTH:
			return CANDLE_SOUTH_AABB;
		case NORTH:
			return CANDLE_NORTH_AABB;
		}
		
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World world, BlockPos pos) {
		state = state.getActualState(world, pos);
		switch ((Direction) state.getValue(FACING)) {
		default:
		case EAST:
			return CANDLE_EAST_AABB.offset(pos);
		case WEST:
			return CANDLE_WEST_AABB.offset(pos);
		case SOUTH:
			return CANDLE_SOUTH_AABB.offset(pos);
		case NORTH:
			return CANDLE_NORTH_AABB.offset(pos);
		}
	}

	@Nullable
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return NULL_AABB;
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
	public boolean isPassable(IBlockReader worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		if (canPlaceAt(world, pos, facing))
			return this.defaultBlockState().setValue(FACING, facing).setValue(LIT, false);
		return this.defaultBlockState();
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, BlockRayTraceResult hitResult) {
		if (level.isClientSide()) {
			return true;
		} else {
			state = state.cycle(LIT);
			world.setBlockState(pos, state, 3);
			if(state.getValue(LIT))
				world.playLocalSound((PlayerEntity)null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.05F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			else
				world.playLocalSound((PlayerEntity)null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1F, 2F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			return true;
		}
	}

	@Override
	public int getLightValue(BlockState state) {
		return state.getValue(LIT) ? 13 : 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if(state.getValue(LIT)) {
			double offSetX = 0D;
			double offSetZ = 0D;
			double offSetWaxX = 0D + rand.nextDouble() * 0.125D - rand.nextDouble() * 0.125D;
			double offSetWaxZ = 0D + rand.nextDouble() * 0.125D - rand.nextDouble() * 0.125D;
			if (state.getValue(FACING) == Direction.WEST)
				offSetX = 0.09375;
			if (state.getValue(FACING) == Direction.EAST)
				offSetX = -0.09375;
			if (state.getValue(FACING) == Direction.NORTH)
				offSetZ = 0.09375;
			if (state.getValue(FACING) == Direction.SOUTH)
				offSetZ = -0.09375;

			double x = (double)pos.getX() + 0.5D;
			double y = (double)pos.getY() + 0.9375D;
			double z = (double)pos.getZ() + 0.5D;

			world.addParticle(ParticleTypes.SMOKE_NORMAL, x + offSetX, y, z + offSetZ, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, x + offSetX, y, z + offSetZ, 0.0D, 0.0D, 0.0D);
			if (rand.nextInt(10) == 0)
				BLParticles.TAR_BEAST_DRIP.spawn(world , x + offSetX + offSetWaxX, y - 0.938D, z + offSetZ +offSetWaxZ).setRBGColorF(1F, 1F, 1F);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		for (Direction Direction : FACING.getAllowedValues()) {
			if (canPlaceAt(world, pos, Direction))
				return true;
		}
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
		Direction facing = Direction.byIndex(meta & 0b111);
		if(facing.getAxis() == Direction.Axis.Y) {
			facing = Direction.NORTH;
		}
		return defaultBlockState().setValue(FACING, facing).setValue(LIT, (meta & 0b1000) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = state.getValue(FACING).getIndex();

		if(state.getValue(LIT)) {
			meta |= 0b1000;
		}

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
		return new BlockStateContainer(this, new IProperty[] { FACING, LIT });
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
