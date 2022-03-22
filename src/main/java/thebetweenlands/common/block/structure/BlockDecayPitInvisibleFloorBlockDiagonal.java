package thebetweenlands.common.block.structure;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockDecayPitInvisibleFloorBlockDiagonal extends HorizontalFaceBlock implements ICustomItemBlock {
	public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");
		public BlockDecayPitInvisibleFloorBlockDiagonal() {
			super(Material.ROCK);
			setHardness(10.0F);
			setResistance(2000.0F);
			setSoundType(SoundType.STONE);
			setCreativeTab(BLCreativeTabs.BLOCKS);
			this.registerDefaultState(this.stateDefinition.any().setValue(FLIPPED, false));
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

		@Nullable
		@Override
		public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
			return NULL_AABB;
		}

		@Override
		public boolean isPassable(IBlockReader worldIn, BlockPos pos) {
			return false;
		}

		@Override
		public BlockRenderType getRenderShape(BlockState state) {
			return BlockRenderType.INVISIBLE;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public BlockRenderLayer getRenderLayer() {
			return BlockRenderLayer.CUTOUT;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
			return FULL_BLOCK_AABB.offset(pos);
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
		protected BlockStateContainer createBlockState() {
			return new BlockStateContainer(this, new IProperty[] { FLIPPED });
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
	public BlockFaceShape getBlockFaceShape(IBlockReader world, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public BlockItem getItemBlock() {
        return null;
    }
}