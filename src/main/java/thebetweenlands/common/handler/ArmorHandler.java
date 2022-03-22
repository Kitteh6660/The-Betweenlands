package thebetweenlands.common.handler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.common.item.armor.ItemLurkerSkinArmor;
import thebetweenlands.common.item.armor.ItemSyrmoriteArmor;

public class ArmorHandler {
	private ArmorHandler() { }

	@SubscribeEvent
	public static void onEntityOnFire(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		LivingEntity entityLiving = event.getEntityLiving();

		if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.LAVA) {
			float damageMultiplier = 1;
			Iterable<ItemStack> armorStacks = entityLiving.getArmorSlots();
			float reductionAmount = 0.25F;
			for(ItemStack stack : armorStacks) {
				if (!stack.isEmpty() && stack.getItem() instanceof ItemSyrmoriteArmor) {
					damageMultiplier -= reductionAmount;
				}
			}
			if (damageMultiplier < 0.001F) {
				event.setAmount(0.01F); //Set to tiny amount so armor still takes damage
				entityLiving.clearFire();
			} else {
				event.setAmount(event.getAmount() * damageMultiplier);
			}
		}
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		PlayerEntity player = event.getPlayer();

		if(player.isInWater()) {
			BlockState blockState = player.level.getBlockState(new BlockPos(player.getX(), player.getBoundingBox().maxY + 0.1D, player.getZ()));
			boolean fullyInWater = blockState.getMaterial().isLiquid();

			if(fullyInWater) {
				NonNullList<ItemStack> armor = player.inventory.armor;
				int pieces = 0;
				for (int i = 0; i < armor.size(); i++) {
					if (!armor.get(i).isEmpty() && armor.get(i).getItem() instanceof ItemLurkerSkinArmor) {
						pieces++;
					}
				}
				if(pieces != 0) {
					event.setNewSpeed(event.getNewSpeed() * (5.0F * (player.isOnGround() ? 1.0F : 5.0F) / 4.0F * pieces));
				}
			}
		}
	}
}