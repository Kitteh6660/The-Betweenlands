package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockLeavesSpiritTree extends BlockLeavesBetweenlands {
	public static final AxisAlignedBB AABB = Block.box(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);

	public static enum Type {
		TOP, MIDDLE, BOTTOM
	}

	public final Type type;

	public BlockLeavesSpiritTree(Type type) {
		this.type = type;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).setValue(DECAYABLE, false);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos, world.getBlockState(pos));
	}

	public boolean canBlockStay(World world, BlockPos pos, BlockState state) {
		switch(this.type) {
		case MIDDLE:
		case BOTTOM:
			BlockState above = world.getBlockState(pos.above());
			if(this.type == Type.MIDDLE) {
				return above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_TOP || above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE;
			} else {
				return above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE;
			}
		default:
			return true;
		}
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if(this.type == Type.TOP) {
			super.updateTick(worldIn, pos, state, rand);
		}
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		Random rand = world instanceof World ? ((World)world).rand : new Random();
		if(rand.nextInt(140) == 0) {
			drops.add(new ItemStack(ItemRegistry.SPIRIT_FRUIT));
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		return Block.box(0.4D, 0.4D, 0.4D, 0.6D, 0.6D, 0.6D);
	}

	@Override
	public boolean isLadder(BlockState state, IBlockReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
		if(rand.nextInt(100) == 0) {
			double px = (double)pos.getX() + rand.nextDouble() * 0.5D;
			double py = (double)pos.getY() + rand.nextDouble() * 0.5D;
			double pz = (double)pos.getZ() + rand.nextDouble() * 0.5D;
			BLParticles.SPIRIT_BUTTERFLY.spawn(world, px, py, pz);
		}
	}
}