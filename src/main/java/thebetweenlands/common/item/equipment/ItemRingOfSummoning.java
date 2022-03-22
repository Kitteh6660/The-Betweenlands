package thebetweenlands.common.item.equipment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.ISummoningCapability;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.entity.mobs.EntityMummyArm;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfSummoning extends ItemRing {
	public static final int MAX_USE_TIME = 100;
	public static final int USE_COOLDOWN = 120;
	public static final int MAX_ARMS = 32;

	public ItemRingOfSummoning() {
		this.setMaxDamage(256);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.ring.summoning.bonus"), 0));
		if (GuiScreen.hasShiftDown()) {
			String toolTip = I18n.get("tooltip.bl.ring.summoning", KeyBindRegistry.RADIAL_MENU.getDisplayName(), KeyBindRegistry.USE_RING.getDisplayName(), KeyBindRegistry.USE_SECONDARY_RING.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.get("tooltip.bl.press.shift"));
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.world.isClientSide() && entity instanceof PlayerEntity) {
			ISummoningCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);
			if (cap != null) {
				CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);

				if (cap.getCooldownTicks() > 0) {
					cap.setCooldownTicks(cap.getCooldownTicks() - 1);
					nbt.putBoolean("ringActive", false);
				} else {
					if (cap.isActive()) {
						cap.setActiveTicks(cap.getActiveTicks() + 1);

						nbt.putBoolean("ringActive", true);

						if (cap.getActiveTicks() > MAX_USE_TIME) {
							cap.setActive(false);
							cap.setCooldownTicks(USE_COOLDOWN);
						} else {
							int arms = entity.world.getEntitiesOfClass(EntityMummyArm.class, entity.getBoundingBox().grow(18), e -> e.getDistance(entity) <= 18.0D).size();

							if (arms < MAX_ARMS) {
								List<LivingEntity> targets = entity.world.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().grow(16), e -> e instanceof MobEntity && e.getDistance(entity) <= 16.0D && e != entity && (e instanceof EntityMob || e instanceof IMob));

								BlockPos targetPos = null;

								if (!targets.isEmpty()) {
									LivingEntity target = targets.get(entity.world.rand.nextInt(targets.size()));
									boolean isAttacked = !entity.world.getEntitiesOfClass(EntityMummyArm.class, target.getBoundingBox()).isEmpty();
									if (!isAttacked) {
										targetPos = target.getPosition();
									}
								}

								if (targetPos == null && entity.world.rand.nextInt(3) == 0) {
									targetPos = entity.getPosition().add(entity.world.rand.nextInt(16) - 8, entity.world.rand.nextInt(6) - 3, entity.world.rand.nextInt(16) - 8);
									boolean isAttacked = !entity.world.getEntitiesOfClass(EntityMummyArm.class, new AxisAlignedBB(targetPos)).isEmpty();
									if (isAttacked) {
										targetPos = null;
									}
								}

								if (targetPos != null && entity.world.getBlockState(targetPos.below()).isSideSolid(entity.world, targetPos.below(), Direction.UP)) {
									EntityMummyArm arm = new EntityMummyArm(entity.world, (PlayerEntity) entity);
									arm.moveTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, 0, 0);

									if (arm.world.getCollisionBoxes(arm, arm.getBoundingBox()).isEmpty()) {
										this.drainPower(stack, entity);
										entity.world.spawnEntity(arm);
									}
								}
							}
						}
					} else {
						nbt.putBoolean("ringActive", false);
					}
				}
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean("ringActive", false);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("ringActive");
	}

	public static boolean isRingActive(Entity entity) {
		IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {
			IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);
			boolean hasRing = false;

			for(int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack stack = inv.getItem(i);
				if(!stack.isEmpty() && stack.getItem() == ItemRegistry.RING_OF_SUMMONING && ((ItemRing) stack.getItem()).canBeUsed(stack)) {
					hasRing = true;
					break;
				}
			}

			return hasRing;
		}
		return false;
	}

	@Override
	public void onKeybindState(PlayerEntity player, ItemStack stack, IInventory inventory, boolean active) {
		ISummoningCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);

		if (cap != null) {
			if(!active && cap.isActive()) {
				cap.setActive(false);
				cap.setCooldownTicks(ItemRingOfSummoning.USE_COOLDOWN);
			} else if(active && !cap.isActive() && cap.getCooldownTicks() <= 0 && ItemRingOfSummoning.isRingActive(player)) {
				cap.setActive(true);
				cap.setActiveTicks(0);
				player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.PEAT_MUMMY_CHARGE, SoundCategory.PLAYERS, 0.4F, (player.world.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
			}
		}
	}
}
