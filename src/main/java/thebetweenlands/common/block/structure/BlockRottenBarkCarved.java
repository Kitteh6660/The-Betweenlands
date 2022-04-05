package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockRottenBarkCarved extends Block {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	
	public BlockRottenBarkCarved(Properties properties) {
		super(properties);
		/*super(material);
		setHardness(2.0F);
		setHarvestLevel("axe", 0);
		setCreativeTab(BLCreativeTabs.PLANTS);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setSoundType(SoundType.WOOD);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public boolean isWood(IBlockReader world, BlockPos pos) {
		return true;
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta); // Using this instead of 'byHorizontalIndex' because the ids don't match and previous was release
		return defaultBlockState().setValue(FACING,facing.getAxis().isHorizontal() ? facing: Direction.NORTH);
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

}