//TODO: Remove this file.
/*package thebetweenlands.common.item.food;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.item.farming.ItemPlantableFood;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemBulbCappedMushroom extends ItemPlantableFood {
	public ItemBulbCappedMushroom() {
		super(3, 0.6F);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player) {
		super.onFoodEaten(stack, world, player);
		if (player != null) {
			player.addEffect(new EffectInstance(Effects.NAUSEA, 200, 1));
			player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 200, 1));
		}
	}

	@Override
	protected Block getBlock(ItemStack stack, PlayerEntity playerIn, World worldIn, BlockPos pos) {
		return BlockRegistry.BULB_CAPPED_MUSHROOM;
	}
}
*/