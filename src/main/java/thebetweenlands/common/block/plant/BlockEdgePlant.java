package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockEdgePlant extends BlockSludgeDungeonPlant implements ICustomItemBlock {
	
    public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;
    
    protected static final VoxelShape PLANT_AABB_NORTH = Block.box(0D, 0D, 0.5D, 1D, 0.25D, 1D);
    protected static final VoxelShape PLANT_AABB_SOUTH = Block.box(0D, 0D, 0D, 1D, 0.25D, 0.5D);
    protected static final VoxelShape PLANT_AABB_EAST = Block.box(0.0D, 0D, 0D, 0.5D, 0.25D, 1D);
    protected static final VoxelShape PLANT_AABB_WEST = Block.box(0.5D, 0D, 0D, 1D, 0.25D, 1D);
    
    public BlockEdgePlant(Properties properties) {
    	super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        /*setSoundType(SoundType.PLANT);
        setHardness(0.1F);
        setCreativeTab(BLCreativeTabs.PLANTS);*/
    }

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		switch (state.getValue(FACING)) {
			case SOUTH:
				return PLANT_AABB_SOUTH;
			case EAST:
				return PLANT_AABB_EAST;
			case WEST:
				return PLANT_AABB_WEST;
			default:
				return PLANT_AABB_NORTH;
		}
	}

	@Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
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
    public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
        return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
    }

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
		boolean hasSupportBlock;
		
		if(state.getBlock() instanceof BlockEdgePlant == false) {
			//Block is air during placement
			hasSupportBlock = true;
		} else {
			Direction facing = state.getValue(FACING);
			hasSupportBlock = this.hasSupportBlock(worldIn, pos, facing);
		}
		
		return hasSupportBlock && super.canBlockStay(worldIn, pos, state);
	}
	
	protected boolean hasSupportBlock(World world, BlockPos pos, Direction facing) {
		BlockPos supportPos = pos.offset(facing.getOpposite());
		return world.getBlockState(supportPos).isSideSolid(world, supportPos, facing);
	}
	
	@Override
	public boolean canSpreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		return world.isEmptyBlock(targetPos) && this.hasSupportBlock(world, targetPos, state.getValue(FACING));
	}
	
	@Override
	public void spreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		world.setBlockState(targetPos, this.defaultBlockState().setValue(FACING, state.getValue(FACING)));
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getAverageGrassColor(worldIn, pos) : FoliageColors.getDefaultColor();
	}

	@Override
	public EnumOffsetType getOffsetType() {
		return EnumOffsetType.NONE;
	}
	
	@Override
	public BlockItem getItemBlock() {
		return new ItemBlockEdgePlant(this);
	}
	
	//why does this need a custom item class
	private static class ItemBlockEdgePlant extends BlockItem {
		private final BlockEdgePlant block;
		
		public ItemBlockEdgePlant(BlockEdgePlant block) {
			super(block);
			this.block = block;
		}
		
		@Override
		public boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side, BlockRayTraceResult hitResult, BlockState newState) {
			return this.block.canBlockStay(world, pos, newState) && super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		}
	}
}