package thebetweenlands.common.item.food;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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
	
	public ItemChiromawWing(Properties properties) {
		super(false, 0, 0, properties);
		//super(0, 0, false);
		//this.setCreativeTab(BLCreativeTabs.ITEMS);
		//this.setAlwaysEdible();
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		super.finishUsingItem(stack, world, entity);

		IFoodSicknessCapability cap = (IFoodSicknessCapability) entity.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
		if (!world.isClientSide() && cap != null) {
			if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
				cap.increaseFoodHatred(this, FoodSickness.SICK.maxHatred, FoodSickness.SICK.maxHatred);
				if (entity instanceof ServerPlayerEntity) {
					TheBetweenlands.networkWrapper.send(new MessageShowFoodSicknessLine(stack, FoodSickness.SICK), (ServerPlayerEntity) entity);
				}
			} else {
				entity.addEffect(new EffectInstance(Effects.HUNGER, 600, 2));
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			IFoodSicknessCapability cap = (IFoodSicknessCapability) player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
			if (cap != null) {
				if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
					tooltip.add(new TranslationTextComponent("tooltip.bl.chiromaw_wing.eat"));
				} else {
					tooltip.add(new TranslationTextComponent("tooltip.bl.chiromaw_wing.dont_eat"));
				}
			}
		}
	}

	@Override
	public boolean canGetSickOf(PlayerEntity player, ItemStack stack) {
		return false;
	}
}
