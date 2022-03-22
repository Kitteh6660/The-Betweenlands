package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockTemplePillar extends BlockRotatedPillar {
	public static final AxisAlignedBB AABB_Y = Block.box(0.0625f, 0f, 0.0625f, .9375f, 1f, .9375f);
	public static final AxisAlignedBB AABB_X = Block.box(0f, 0.0625f, 0.0625f, 1f, .9375f, .9375f);
	public static final AxisAlignedBB AABB_Z = Block.box(0.0625f, 0.0625f, 0f, .9375f, .9375f, 1f);

	public BlockTemplePillar() {
		super(Material.ROCK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().setValue(AXIS, Direction.Axis.Y));
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		switch (state.getValue(AXIS)) {
		case Y:
			return AABB_Y;
		case X:
			return AABB_X;
		default:
			return AABB_Z;
		}
	}

	@Override
	public boolean isBlockNormalCube(BlockState worldIn) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return super.shouldSideBeRendered(blockState, blockAccess, pos, side) && (blockAccess.getBlockState(pos.offset(side)).getBlock() != this
				|| (blockState.getValue(AXIS) != blockAccess.getBlockState(pos.offset(side)).getValue(AXIS) || side.getAxis() != blockState.getValue(AXIS)));
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return side.getAxis() == base_state.getValue(AXIS);
	}
}
