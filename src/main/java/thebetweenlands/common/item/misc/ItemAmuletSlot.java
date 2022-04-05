package thebetweenlands.common.item.misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemAmuletSlot extends Item {
	public ItemAmuletSlot() {
		this.setMaxStackSize(1);
		this.setMaxDamage(30);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide()) {
			if(player.isCrouching() && player.isCreative()) {
				removeAmuletSlot(player);
			} else {
				addAmuletSlot(player, stack, player);
			}
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
		if(!player.level.isClientSide()) {
			if(ItemAmulet.canPlayerAddAmulet(player, target) || player.isCreative()) {
				if(player.isCrouching() && player.isCreative()) {
					removeAmuletSlot(target);
				} else {
					if(addAmuletSlot(player, stack, target)) {
						player.swing(hand);
					}
				}
			}
		}
		return true;
	}

	public static boolean addAmuletSlot(PlayerEntity player, ItemStack stack, LivingEntity entity) {
		IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {

			if(cap.getAmuletSlots() < 3 || player.isCreative()) {
				cap.setAmuletSlots(cap.getAmuletSlots() + 1);

				if(!player.isCreative()) {
					if(entity instanceof PlayerEntity) {
						stack.damageItem(5, player);
					} else {
						stack.damageItem(2, player);
					}
				}

				player.displayClientMessage(new TranslationTextComponent("chat.amulet.slot.added"), true);

				return true;
			} else {
				player.displayClientMessage(new TranslationTextComponent("chat.amulet.slot.full"), true);
			}
		}
		return false;
	}

	public static void removeAmuletSlot(LivingEntity entity) {
		IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {
			if(cap.getAmuletSlots() > 1) {
				cap.setAmuletSlots(cap.getAmuletSlots() - 1);
			}
		}
	}
}
