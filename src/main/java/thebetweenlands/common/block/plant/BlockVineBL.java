package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockVineBL extends BlockVine implements ISickleHarvestable, IShearable {
	public BlockVineBL(){
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.2F);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}
	
	@Override
	public boolean canAttachTo(World world, BlockPos pos, Direction face) {
		Block block = world.getBlockState(pos.above()).getBlock();
        return this.isAcceptableNeighbor(world, pos.offset(face.getOpposite()), face) && (block == Blocks.AIR || block == this || this.isAcceptableNeighbor(world, pos.above(), Direction.UP));
    }

	private boolean isAcceptableNeighbor(World world, BlockPos pos, Direction face) {
        BlockState iblockstate = world.getBlockState(pos);
        return iblockstate.getBlockFaceShape(world, pos, face) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(iblockstate.getBlock());
    }
	
	@Override
	public boolean isLadder(BlockState state, IBlockReader world, BlockPos pos, LivingEntity entity) {
		return false;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockReader world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}
}
