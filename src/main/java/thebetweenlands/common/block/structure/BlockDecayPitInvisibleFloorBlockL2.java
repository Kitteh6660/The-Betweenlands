package thebetweenlands.common.block.structure;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockDecayPitInvisibleFloorBlockL2 extends HorizontalFaceBlock implements ICustomItemBlock {

		public BlockDecayPitInvisibleFloorBlockL2(Properties properties) {
			super(properties);
			/*super(Material.ROCK);
			setHardness(10.0F);
			setResistance(2000.0F);
			setSoundType(SoundType.STONE);
			setCreativeTab(BLCreativeTabs.BLOCKS);*/
			this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
		public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
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
		public BlockState getStateFromMeta(int meta) {
			// S = 0, W = 1, N = 2, E = 3
			return defaultBlockState().setValue(FACING, Direction.byIndex(meta));
		}

		@Override
		public int getMetaFromState(BlockState state) {
			// S = 0, W = 1, N = 2, E = 3
			int meta = 0;
			meta = meta | ((Direction) state.getValue(FACING)).getIndex();
			return meta;
		}

		@Override
		 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
			return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
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
			return new BlockStateContainer(this, new IProperty[] {FACING});
		}

    public static final VoxelShape BOX_S1 = Block.box(0D, 0D, 0D, 0.25D, 1D, 0.875D);
    public static final VoxelShape BOX_S2 = Block.box(0.25D, 0D, 0D, 0.5D, 1D, 0.75D);
    public static final VoxelShape BOX_S3 = Block.box(0.5D, 0D, 0D, 0.75D, 1D, 0.625D);
    public static final VoxelShape BOX_S4 = Block.box(0.75D, 0D, 0D, 1D, 1D, 0.5D);

    public static final VoxelShape BOX_N1 = Block.box(0D, 0D, 0.5D, 0.25D, 1D, 1D);
    public static final VoxelShape BOX_N2 = Block.box(0.25D, 0D, 0.3125D, 0.5D, 1D, 1D);
    public static final VoxelShape BOX_N3 = Block.box(0.5D, 0D, 0.25D, 0.75D, 1D, 1D);
    public static final VoxelShape BOX_N4 = Block.box(0.75D, 0D, 0.125D, 1D, 1D, 1D);

    public static final VoxelShape BOX_E1 = Block.box(0D, 0D, 0D, 0.5D, 1D, 0.25D );
    public static final VoxelShape BOX_E2 = Block.box(0D, 0D, 0.25D, 0.625D, 1D, 0.5D);
    public static final VoxelShape BOX_E3 = Block.box(0D, 0D, 0.5D, 0.75D, 1D, 0.75D);
    public static final VoxelShape BOX_E4 = Block.box(0D, 0D, 0.75D, 0.875D, 1D, 1D);

    public static final VoxelShape BOX_W1 = Block.box(0.125D, 0D, 0D, 1D, 1D, 0.25D);
    public static final VoxelShape BOX_W2 = Block.box(0.25D, 0D, 0.25D, 1D, 1D, 0.5D);
    public static final VoxelShape BOX_W3 = Block.box(0.375D, 0D, 0.5D, 1D, 1D, 0.75D);
    public static final VoxelShape BOX_W4 = Block.box( 0.5D, 0D, 0.75D, 1D, 1D, 1D);

    @Override
	public void addCollisionBoxToList(BlockState state, World world, BlockPos pos, VoxelShape entityBox, List<VoxelShape> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState)
			state = state.getActualState(world, pos);

		if (state.getValue(FACING) == Direction.NORTH) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_N1);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_N2);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_N3);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_N4);
		}

		if (state.getValue(FACING) == Direction.SOUTH) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_S1);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_S2);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_S3);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_S4);
		}

		if (state.getValue(FACING) == Direction.EAST) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_E1);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_E2);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_E3);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_E4);
		}

		if (state.getValue(FACING) == Direction.WEST) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_W1);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_W2);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_W3);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_W4);
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