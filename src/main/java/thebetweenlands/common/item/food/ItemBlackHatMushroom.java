package thebetweenlands.common.item.food;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.item.farming.ItemPlantableFood;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemBlackHatMushroom extends ItemPlantableFood {
	public ItemBlackHatMushroom() {
		super(3, 0.6f);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player) {
		super.onFoodEaten(stack, world, player);
		if (player != null) {
			player.addEffect(new EffectInstance(Effects.HUNGER, 500, 1));
		}
	}

	@Override
	protected Block getBlock(ItemStack stack, PlayerEntity playerIn, World worldIn, BlockPos pos) {
		return BlockRegistry.BLACK_HAT_MUSHROOM;
	}

	@Override
	public boolean canGetSickOf(PlayerEntity player, ItemStack stack) {
		return false;
	}
}
