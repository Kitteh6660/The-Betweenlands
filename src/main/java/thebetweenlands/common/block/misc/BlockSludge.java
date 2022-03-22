package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSludge extends Block {
	private static final AxisAlignedBB BOUNDS = Block.box(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);

	public BlockSludge() {
		super(BLMaterialRegistry.SLUDGE);
		this.setHardness(0.1F);
		this.setSoundType(SoundType.GROUND);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setTranslationKey("thebetweenlands.sludge");
		this.setHarvestLevel("shovel", 0);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return BOUNDS;
	}

	public void generateBlockTemporary(World world, BlockPos pos) {
		world.setBlockState(pos, this.defaultBlockState());
		world.scheduleBlockUpdate(pos, this, 20 * 60, 0);
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random rnd) {
		world.setBlockState(pos, Blocks.AIR.defaultBlockState());
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!(entity instanceof IEntityBL) && entity.onGround) {
			entity.setInWeb();
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldSideBeRendered(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockAccess.getBlockState(pos) != this && super.shouldSideBeRendered(state, blockAccess, pos, side);
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rnd, int fortune) {
		return ItemRegistry.SLUDGE_BALL;
	}

	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(BlockState state) {
        return false;
    }
	
	@Override
	public boolean isFullCube(BlockState state) {
        return false;
    }
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return world.isSideSolid(pos.below(), Direction.UP);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, Direction side) {
		return this.canPlaceBlockAt(world, pos);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if (!this.canPlaceBlockAt(worldIn, pos)) {
			worldIn.setBlockState(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public boolean isSideSolid(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}