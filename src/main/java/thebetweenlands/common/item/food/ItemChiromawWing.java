package thebetweenlands.common.item.food;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.network.clientbound.MessageShowFoodSicknessLine;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;
import java.util.List;


public class ItemChiromawWing extends BLFoodItem {
	public ItemChiromawWing() {
		super(0, 0, false);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.setAlwaysEdible();
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player) {
		super.onFoodEaten(stack, world, player);

		IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
		if (!world.isClientSide() && cap != null) {
			if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
				cap.increaseFoodHatred(this, FoodSickness.SICK.maxHatred, FoodSickness.SICK.maxHatred);
				if(player instanceof ServerPlayerEntity) {
					TheBetweenlands.networkWrapper.sendTo(new MessageShowFoodSicknessLine(stack, FoodSickness.SICK), (ServerPlayerEntity) player);
				}
			} else {
				player.addEffect(new EffectInstance(Effects.HUNGER, 600, 2));
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		PlayerEntity player = FMLClientHandler.instance().getClientPlayerEntity();
		if (player != null) {
			IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
			if (cap != null) {
				if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
					tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.chiromaw_wing.eat"));
				} else {
					tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.chiromaw_wing.dont_eat"));
				}
			}
		}
	}

	@Override
	public boolean canGetSickOf(PlayerEntity player, ItemStack stack) {
		return false;
	}
}
