package thebetweenlands.common.block.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockWalkway extends Block implements IStateMappedBlock {
	
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;
	public static final BooleanProperty STANDS = BooleanProperty.create("has_stands");

	protected static final VoxelShape AABB = Block.box(0, 0.0F, 0, 1.0F, 0.6F, 1.0F);

	public BlockWalkway(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(1.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(STANDS, true));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return AABB;
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.setValue(STANDS, worldIn.getBlockState(pos.below()).isSideSolid(worldIn, pos.below(), Direction.UP));
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 2);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction Direction = Direction.byIndex(meta);

		if (Direction.getAxis() == Axis.Y) {
			Direction = Direction.NORTH;
		}

		return defaultBlockState().setValue(FACING, Direction);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((Direction) state.getValue(FACING)).getIndex();
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
		return new BlockStateContainer(this, new IProperty[] { FACING, STANDS });
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(Builder builder) {
		builder.withPropertySuffixFalse(STANDS, "no_stands").ignore(STANDS);
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
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if(entityIn instanceof LivingEntity) {
			((LivingEntity)entityIn).addEffect(new EffectInstance(Effects.SPEED, 1, 1, false, false));
		}
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
