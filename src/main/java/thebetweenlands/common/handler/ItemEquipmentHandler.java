package thebetweenlands.common.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.network.serverbound.MessageEquipItem;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemEquipmentHandler {
	private ItemEquipmentHandler() { }

	@SubscribeEvent
	public static void onLivingUpdated(LivingEvent.LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		
		if(!entity.getEntityData().getBoolean(EquipmentHelper.NBT_HAS_NO_EQUIPMENT)) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			
			if (cap != null) {
				for (EnumEquipmentInventory invType : EnumEquipmentInventory.VALUES) {
					IInventory inventory = cap.getInventory(invType);

					if (inventory instanceof ITickable) {
						((ITickable) inventory).update();
					}
				}
				
				if((entity.tickCount + entity.getEntityId()) % 100 == 0) {
					boolean hasEquipment = false;
					
					loop: for(EnumEquipmentInventory invType : EnumEquipmentInventory.VALUES) {
						IInventory inventory = cap.getInventory(invType);
						
						for(int i = 0; i < inventory.getContainerSize(); i++) {
							if(!inventory.getItem(i).isEmpty()) {
								hasEquipment = true;
								break loop;
							}
						}
					}
					
					//Put equipment ticking back to sleep until items are added again
					if(!hasEquipment) {
						entity.getEntityData().putBoolean(EquipmentHelper.NBT_HAS_NO_EQUIPMENT, true);
					}
				}
			} else {
				entity.getEntityData().putBoolean(EquipmentHelper.NBT_HAS_NO_EQUIPMENT, true);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		PlayerEntity player = event.getEntityPlayer();
		Entity target = event.getTarget();

		if(player != null && target != null && target instanceof PlayerEntity == false) {
			ItemStack heldItem = event.getItemStack();

			if(!player.isCrouching() && !heldItem.isEmpty()) {
				if(heldItem.getItem() instanceof IEquippable) {
					IEquippable equippable = (IEquippable) heldItem.getItem();

					if(equippable.canEquipOnRightClick(heldItem, player, target)) {
						ItemStack result = EquipmentHelper.equipItem(player, target, heldItem, false);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(!player.isCreative()) {
								player.setItemInHand(event.getHand(), result);
							}

							player.swingArm(event.getHand());
						}
					}
				}
			} else if(player.isCrouching() && heldItem.isEmpty()) {
				if(EquipmentHelper.tryPlayerUnequip(player, target)) {
					player.swingArm(Hand.MAIN_HAND);
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent event) {
		PlayerEntity player = event.getEntityPlayer();
		if(event instanceof PlayerInteractEvent.RightClickBlock) {
			tryEquip(player, event.getHand(), false);
		} else if(event instanceof PlayerInteractEvent.RightClickEmpty) {
			if(!tryEquip(player, Hand.MAIN_HAND, true)) {
				tryEquip(player, Hand.OFF_HAND, true);
			}
		}
	}

	private static boolean tryEquip(PlayerEntity player, Hand hand, boolean packet) {
		ItemStack heldItem = player.getItemInHand(hand);

		if(player != null && !heldItem.isEmpty() && heldItem.getItem() instanceof IEquippable) {
			IEquippable equippable = (IEquippable) heldItem.getItem();

			if(equippable.canEquipOnRightClick(heldItem, player, player)) {
				if(packet) {
					if(player.world.isClientSide()) {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, true);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(hand == Hand.OFF_HAND) {
								TheBetweenlands.networkWrapper.sendToServer(new MessageEquipItem(-1, player));

								player.swingArm(hand);

								return true;
							} else {
								int slot = player.inventory.getSlotFor(heldItem);
								if(slot >= 0) {
									TheBetweenlands.networkWrapper.sendToServer(new MessageEquipItem(slot, player));

									player.swingArm(hand);

									return true;
								}
							}
						}
					}
				} else {
					if(player.world.isClientSide()) {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, true);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							player.swingArm(hand);
							return true;
						}
					} else {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, false);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(!player.isCreative()) {
								player.setItemInHand(hand, result);
							}

							player.swingArm(hand);

							player.sendStatusMessage(new TranslationTextComponent("chat.equipment.equipped", new TranslationTextComponent(heldItem.getTranslationKey() + ".name")), true);

							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onDeathDrops(LivingDropsEvent event) {
		LivingEntity entity = event.getEntityLiving();

		if(entity != null && !entity.world.isClientSide() && !entity.world.getGameRules().getBoolean("keepInventory")) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			if(cap != null) {
				for(EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
					IInventory inv = cap.getInventory(type);

					for(int i = 0; i < inv.getContainerSize(); i++) {
						ItemStack stack = inv.getItem(i);

						if(!stack.isEmpty()) {
							if(stack.getItem() instanceof IEquippable) {
								IEquippable equippable = (IEquippable) stack.getItem();
								equippable.onUnequip(stack, entity, inv);
								if(!equippable.canDrop(stack, entity, inv)) {
									continue;
								}
							}

							ItemEntity equipmentDrop = new ItemEntity(entity.world, entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), stack.copy());
							equipmentDrop.setDefaultPickupDelay();
							event.getDrops().add(equipmentDrop);
						}
					}
					
					inv.clear();
				}
			}
		}
	}
}
