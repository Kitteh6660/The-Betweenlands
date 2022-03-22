package thebetweenlands.common.item.equipment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.Attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.entity.mobs.EntityFortressBossBlockade;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfRecruitment extends ItemRing {
	public static final String NBT_UUID = "ring_of_recruitment.uuid";

	public ItemRingOfRecruitment() {
		this.setMaxDamage(100);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.ring.recruitment.bonus"), 0));
		if (GuiScreen.hasShiftDown()) {
			String toolTip = I18n.get("tooltip.bl.ring.recruitment", KeyBindRegistry.RADIAL_MENU.getDisplayName(), Minecraft.getInstance().gameSettings.keyBindUseItem.getDisplayName(), KeyBindRegistry.USE_RING.getDisplayName(), KeyBindRegistry.USE_SECONDARY_RING.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.get("tooltip.bl.press.shift"));
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.world.isClientSide() && entity instanceof PlayerEntity) {
			IPuppeteerCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);

			if(cap != null) {
				int puppets = cap.getPuppets().size();
				CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);

				if(puppets == 0) {
					nbt.putBoolean("ringActive", false);
				} else {
					nbt.putBoolean("ringActive", true);
				}
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean("ringActive", false);

		//Reset recruitment points
		stack.setItemDamage(0);
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
		//Set new ring UUID so that previously recruited but unloaded entities will be unlinked when they're loaded again
		this.setRingUuid(stack, UUID.randomUUID());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("ringActive");
	}
	
	@Override
	public void onKeybindState(PlayerEntity player, ItemStack stack, IInventory inventory, boolean active) {
		if(!player.world.isClientSide() && active && !player.getCooldownTracker().hasCooldown(ItemRegistry.RING_OF_RECRUITMENT)) {
			IPuppeteerCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
			
			if(cap != null && cap.getShield() != null) {
				List<Entity> targets = cap.getPuppets();
				
				Set<Entity> spawned = new HashSet<>();
				
				for(Entity target : targets) {
					IPuppetCapability targetCap = target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
					if(targetCap != null && target.onGround && ((!targetCap.getStay() && !targetCap.getGuard()) || target.getDistance(player) < 6)) {
						List<EntityFortressBossBlockade> collidingEntities = target.world.getEntitiesOfClass(EntityFortressBossBlockade.class, target.getBoundingBox().grow(0.5D));
						for(EntityFortressBossBlockade collidingEntity : collidingEntities) {
							if(!spawned.contains(collidingEntity)) {
								collidingEntity.remove();
							}
						}
						
						EntityFortressBossBlockade blockade = new EntityFortressBossBlockade(target.world, player);
						blockade.moveTo(target.getX(), target.getY() - 0.15f, target.getZ(), target.world.rand.nextFloat() * 360.0f, 0);
						blockade.setMaxDespawnTicks(30 + target.world.rand.nextInt(20));
						blockade.setTriangleSize(0.75f + target.width * 0.5f);
						
						spawned.add(blockade);
						
						target.world.spawnEntity(blockade);
					}
				}
				
				if(!spawned.isEmpty()) {
					player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES, SoundCategory.HOSTILE, 0.8f, 0.9f + player.world.rand.nextFloat() * 0.15f);
				
					player.getCooldownTracker().setCooldown(ItemRegistry.RING_OF_RECRUITMENT, 40);
				}
			}
		}
	}

	@Nullable
	public UUID getRingUuid(ItemStack stack) {
		if(stack.hasTag() && stack.getTag().hasUUID(NBT_UUID)) {
			return stack.getTag().getUUID(NBT_UUID);
		}
		return null;
	}

	public void setRingUuid(ItemStack stack, UUID uuid) {
		CompoundNBT nbt = stack.getTag();
		if(nbt == null) {
			nbt = new CompoundNBT();
		}
		nbt.putUUID(NBT_UUID, uuid);
		stack.setTag(nbt);
	}

	public int getRecruitmentCost(LivingEntity target) {
		float damageMultiplier = 0.5f;

		ModifiableAttributeInstance damageAttrib = target.getEntityAttribute(Attributes.ATTACK_DAMAGE);
		if(damageAttrib != null) {
			damageMultiplier = 1.0f + Math.min((float)(damageAttrib.getAttributeValue() - 2.0f) / 10.0f, 0.5f);
		}

		return Math.min(60, Math.max(MathHelper.floor(target.getMaxHealth() / 2.0f * damageMultiplier), 10));
	}

	public static boolean isRingActive(Entity user, @Nullable IPuppetCapability recruited) {
		return !getActiveRing(user, recruited).isEmpty();
	}

	@Nullable
	public static ItemStack getActiveRing(Entity user, @Nullable IPuppetCapability recruited) {
		if(user instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) user;

			if (player.experienceTotal <= 0 && player.experienceLevel <= 0 && player.experience <= 0) {
				return ItemStack.EMPTY;
			}
		}

		ItemStack ring = EquipmentHelper.getEquipment(EnumEquipmentInventory.RING, user, ItemRegistry.RING_OF_RECRUITMENT);

		if(!ring.isEmpty()) {
			UUID ringUuid = ((ItemRingOfRecruitment) ring.getItem()).getRingUuid(ring);

			if(recruited != null && ringUuid != null) {
				UUID recruitedRingUuid = recruited.getRingUuid();

				if(recruitedRingUuid != null && !ringUuid.equals(recruitedRingUuid)) {
					return ItemStack.EMPTY;
				}
			}

			return ring;
		}

		return ItemStack.EMPTY;
	}
}
