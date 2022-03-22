package thebetweenlands.common.block.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.IConnectedTextureBlock;

public class BlockAmatePaperPane extends BlockPaneBetweenlands implements IConnectedTextureBlock {
	protected static final AxisAlignedBB[] AABB_BY_INDEX = Block.box[] {Block.box(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), Block.box(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), Block.box(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), Block.box(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), Block.box(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), Block.box(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), Block.box(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), Block.box(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), Block.box(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), Block.box(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), Block.box(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), Block.box(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), Block.box(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), Block.box(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

	public BlockAmatePaperPane() {
		super(Material.CLOTH, true);
		this.setSoundType(SoundType.CLOTH);
		this.setHardness(0.3F);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockState getExtendedState(BlockState oldState, IBlockReader world, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;
		IConnectionRules connectionState = new IConnectionRules() {
			@Override
			public boolean canConnectTo(IBlockReader world, BlockPos pos, Direction face, BlockPos.Mutable to) {
				return Math.abs(to.getX() - pos.getX() - face.getStepX()) + Math.abs(to.getY() - pos.getY() - face.getStepY()) + Math.abs(to.getZ() - pos.getZ() - face.getStepZ()) != 1 && world.getBlockState(to).getBlock() == BlockAmatePaperPane.this;
			}

			@Override
			public boolean canConnectThrough(IBlockReader world, BlockPos pos, Direction face, BlockPos.Mutable to) {
				Axis axis = face.getAxis();
				if((axis == Axis.X && to.getX() - pos.getX() != 0) || (axis == Axis.Y && to.getY() - pos.getY() != 0) || (axis == Axis.Z && to.getZ() - pos.getZ() != 0)) {
					return true;
				}
				return false;
			}
		};
		return this.getExtendedConnectedTextureState(state, world, pos, connectionState);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[]{NORTH, EAST, WEST, SOUTH}, new IUnlistedProperty[0]));
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState)
		{
			state = this.getActualState(state, worldIn, pos);
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[0]);

		if (((Boolean)state.getValue(NORTH)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(Direction.NORTH)]);
		}

		if (((Boolean)state.getValue(SOUTH)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(Direction.SOUTH)]);
		}

		if (((Boolean)state.getValue(EAST)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(Direction.EAST)]);
		}

		if (((Boolean)state.getValue(WEST)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(Direction.WEST)]);
		}
	}

	private static int getBoundingBoxIndex(Direction facing) {
		return 1 << facing.getHorizontalIndex();
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		state = this.getActualState(state, source, pos);
		return AABB_BY_INDEX[getBoundingBoxIndex(state)];
	}

	private static int getBoundingBoxIndex(BlockState state)
	{
		int i = 0;

		if (((Boolean)state.getValue(NORTH)).booleanValue())
		{
			i |= getBoundingBoxIndex(Direction.NORTH);
		}

		if (((Boolean)state.getValue(EAST)).booleanValue())
		{
			i |= getBoundingBoxIndex(Direction.EAST);
		}

		if (((Boolean)state.getValue(SOUTH)).booleanValue())
		{
			i |= getBoundingBoxIndex(Direction.SOUTH);
		}

		if (((Boolean)state.getValue(WEST)).booleanValue())
		{
			i |= getBoundingBoxIndex(Direction.WEST);
		}

		return i;
	}

	@Override
	public boolean canPlaceTorchOnTop(BlockState state, IBlockReader world, BlockPos pos) {
		return true;
    }
}
