package thebetweenlands.common.block.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.BeamOriginTileEntity;

public class BlockBeamOrigin extends Block {
	
	protected static final VoxelShape BOUNDING_BOX = Block.box(-0.1D, -0.5D, -0.1D, 1.1D, 1.2D, 1.1D);
	protected static final VoxelShape COLLISION_BOX = Block.box(-0.1D, -0.1D, -0.1D, 1.1D, 1.2D, 1.1D);
	
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public BlockBeamOrigin(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
		/*super(Material.ROCK);
		setHardness(10.0F);
		setResistance(2000.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public TileEntity newBlockEntity(BlockState state, IBlockReader level) {
		return new BeamOriginTileEntity();
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		if (((Boolean) state.getValue(POWERED)).booleanValue())
			meta = 1;
		return meta;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return this.defaultBlockState().setValue(POWERED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { POWERED });
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (world.getBlockEntity(pos) instanceof BeamOriginTileEntity) {
			BeamOriginTileEntity tile = (BeamOriginTileEntity) world.getBlockEntity(pos);
			tile.deactivateBlock();
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return BOUNDING_BOX;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return COLLISION_BOX;
	}
}
