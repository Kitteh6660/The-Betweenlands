package thebetweenlands.common.item.food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemSpiritFruit extends BLFoodItem {
	public ItemSpiritFruit(Properties properties) {
		super(false, 0, 0, properties);
		//this.setAlwaysEdible();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, PlayerEntity player) {
		if (!worldIn.isClientSide()) {
			player.addEffect(new EffectInstance(Effects.REGENERATION, 100, 1));
			player.addEffect(new EffectInstance(Effects.ABSORPTION, 2400, 0));
		}
	}
}