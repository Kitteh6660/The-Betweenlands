//TODO: Remove this file.

/*package thebetweenlands.common.item.food;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.item.farming.ItemPlantableFood;
import thebetweenlands.common.registries.BlockRegistry;


public class ItemFlatHeadMushroom extends ItemPlantableFood {
	public ItemFlatHeadMushroom() {
		super(3, 0.6F);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player) {
		super.onFoodEaten(stack, world, player);
		player.addEffect(new EffectInstance(Effects.HUNGER, 500, 1));
	}

	@Override
	protected Block getBlock(ItemStack stack, PlayerEntity playerIn, World worldIn, BlockPos pos) {
		return BlockRegistry.FLAT_HEAD_MUSHROOM;
	}

	@Override
	public boolean canGetSickOf(PlayerEntity player, ItemStack stack) {
		return false;
	}
}*/
