package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockBulbCappedMushroom extends BlockMushroomBetweenlands {
	public BlockBulbCappedMushroom() {
		this.setLightLevel(1.0F);
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
