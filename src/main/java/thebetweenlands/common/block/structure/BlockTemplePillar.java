package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import thebetweenlands.client.tab.BLCreativeTabs;
import net.minecraft.block.WallBlock;

public class BlockTemplePillar extends RotatedPillarBlock {
	
	public static final VoxelShape AABB_Y = Block.box(0.0625f, 0f, 0.0625f, .9375f, 1f, .9375f);
	public static final VoxelShape AABB_X = Block.box(0f, 0.0625f, 0.0625f, 1f, .9375f, .9375f);
	public static final VoxelShape AABB_Z = Block.box(0.0625f, 0.0625f, 0f, .9375f, .9375f, 1f);

	public BlockTemplePillar(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext context) {
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

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return side.getAxis() == base_state.getValue(AXIS);
	}
}
