package thebetweenlands.common.block.plant;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.IConnectedTextureBlock;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.item.ItemWaterPlaceable;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockAlgae extends BlockPlant implements IConnectedTextureBlock, ICustomItemBlock {
	
	protected static final VoxelShape ALGAE_AABB = Block.box(0.0D, 0.0D, 0.0D, 1D, 0.08D, 1D);

	public BlockAlgae(Properties properties) {
		super(properties);
		this.setReplaceable(true);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if(entityIn instanceof EntityWeedwoodRowboat == false) {
			entityIn.motionX *= 0.95D;
			entityIn.motionZ *= 0.95D;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return ALGAE_AABB;
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		//No solid collision
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return state.getMaterial() == Material.WATER;
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			BlockState iblockstate = worldIn.getBlockState(pos.below());
			Material material = iblockstate.getMaterial();
			return material == Material.WATER && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0;
		} else {
			return false;
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState s) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader iblockaccess, BlockPos pos, Direction side) {
		Block block = iblockaccess.getBlockState(pos.offset(side)).getBlock();
		return block != BlockRegistry.ALGAE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[0]));
	}

	@Override
	public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;
		return this.getExtendedConnectedTextureState(state, worldIn, pos, p -> {
			return p.getY() <= pos.getY() && (worldIn.getBlockState(p).getBlock() == this || worldIn.getBlockState(p.below()).isFullCube());
		}, false);
	}

	@Override
	public boolean isFaceConnectedTexture(Direction face) {
		return face == Direction.UP;
	}

	@Override
	public BlockItem getItemBlock() {
		return new ItemWaterPlaceable(this);
	}
}
