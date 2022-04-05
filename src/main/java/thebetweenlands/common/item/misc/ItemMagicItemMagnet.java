package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemMagicItemMagnet extends Item implements IEquippable, IAnimatorRepairable {
	public ItemMagicItemMagnet() {
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.setMaxStackSize(1);
		this.setMaxDamage(2048);
		IEquippable.addEquippedPropertyOverrides(this);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getBoolean("magnetActive") : false;
	}

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.MISC;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, PlayerEntity player, Entity target) {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack, PlayerEntity player, Entity target) {
		return player == target && EquipmentHelper.getEquipment(EnumEquipmentInventory.MISC, target, this).isEmpty();
	}

	@Override
	public boolean canUnequip(ItemStack stack, PlayerEntity player, Entity target, IInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean("magnetActive", true);
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean("magnetActive", false);
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(stack.getDamageValue() < stack.getMaxDamage()) {
			double range = 7;

			AxisAlignedBB area = new AxisAlignedBB(entity.getX(), entity.getY() + entity.height / 2, entity.getZ(), entity.getX(), entity.getY() + entity.height / 2, entity.getZ()).inflate(range);
			List<ItemEntity> entities = entity.level.getEntitiesOfClass(ItemEntity.class, area, e -> e.getDistanceSq(entity.getX(), entity.getY() + entity.height / 2, entity.getZ()) <= range*range);

			for(ItemEntity item : entities) {
				if(!item.hasNoGravity()) {
					CompoundNBT nbt = item.getEntityData();
					
					boolean isGravityCompensated = false;
					
					if(nbt.contains("thebetweenlands.item_magnet_last_gravity_update", Constants.NBT.TAG_INT) && nbt.getInt("thebetweenlands.item_magnet_last_gravity_update") == item.tickCount) {
						isGravityCompensated = true;
					}
					
					nbt.putInt("thebetweenlands.item_magnet_last_gravity_update", item.tickCount);
					
					if(!isGravityCompensated) {
						item.motionY += 0.03999999910593033D;
					}
				}
				
				double dx = entity.getX() - item.getX();
				double dy = entity.getY() + entity.height / 2 - (item.getY() + item.height / 2);
				double dz = entity.getZ() - item.getZ();
				double len = Math.sqrt(dx*dx + dy*dy + dz*dz);

				if(!entity.level.isClientSide()) {
					item.motionX += dx / len * 0.015D;
					if(item.onGround) {
						item.motionY += 0.015D;
					} else {
						item.motionY += dy / len * 0.015D;
					}
					item.motionZ += dz / len * 0.015D;
					item.velocityChanged = true;
				} else {
					this.addParticles(item);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void addParticles(ItemEntity item) {
		if(item.tickCount % 4 == 0) {
			BLParticles.CORRUPTED.spawn(item.world, item.getX(), item.getY() + item.height / 2.0f + 0.25f, item.getZ(), ParticleArgs.get().withScale(0.5f));
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the magnet break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 32;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 16;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 38;
	}

	@SubscribeEvent
	public static void onItemPickup(ItemPickupEvent event) {
		if(!event.player.level.isClientSide()) {
			ItemStack magnet = EquipmentHelper.getEquipment(EnumEquipmentInventory.MISC, event.player, ItemRegistry.MAGIC_ITEM_MAGNET);
			if(!magnet.isEmpty()) {
				//Damage magnet on pickup
				magnet.damageItem(1, event.player);
			}
		}
	}
}
