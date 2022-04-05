package thebetweenlands.common.item.tools;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.event.ArmSwingSpeedEvent;
import thebetweenlands.api.item.IExtendedReach;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemGreatsword extends BLSwordItem implements IExtendedReach {
	
	protected static final String NBT_SWING_START_COOLDOWN = "swingStartCooldownState";
	protected static final String NBT_HIT_COOLDOWN = "hitCooldownState";
	protected static final String NBT_SWING_START_TICKS = "swingStartTicks";
	protected static final String NBT_LONG_SWING_STATE = "longSwingState";

	public ItemGreatsword(IItemTier itemTier, int damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
		//super(mat);
		//setCreativeTab(BLCreativeTabs.GEARS);
	}

	/*public ItemGreatsword() {
		this(BLMaterialRegistry.TOOL_VALONITE);
	}*/

	protected double getAoEReach(LivingEntity entityLiving, ItemStack stack) {
		return 2.8D;
	}

	protected double getAoEHalfAngle(LivingEntity entityLiving, ItemStack stack) {
		return 45.0D;
	}

	@Override
	public void onLeftClick(PlayerEntity player, ItemStack stack) {
		boolean enemiesInReach = false;

		if(!player.level.isClientSide() && !player.swinging) {
			stack.addTagElement(NBT_SWING_START_COOLDOWN, FloatNBT.valueOf(player.getAttackStrengthScale(0)));
			stack.addTagElement(NBT_SWING_START_TICKS, IntNBT.valueOf(player.tickCount));
			stack.addTagElement(NBT_LONG_SWING_STATE, ByteNBT.valueOf((byte) 1));
		}

		double aoeReach = this.getAoEReach(player, stack);
		double aoeHalfAngle = this.getAoEHalfAngle(player, stack);

		//oof
		ModifiableAttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
		double baseAttackSpeed = attackSpeed.getBaseValue();

		Collection<AttributeModifier> attackSpeedModifiers = attackSpeed.getModifiers();
		for(AttributeModifier modifier : attackSpeedModifiers) {
			attackSpeed.removeModifier(modifier);
		}

		float initialAttackStrength = Math.max(player.getAttackStrengthScale(0.5F), NBTHelper.getStackNBTSafe(stack).getFloat(NBT_HIT_COOLDOWN));

		List<LivingEntity> others = player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(aoeReach));
		for(LivingEntity target : others) {
			if(target != player) {
				Entity[] parts = target.getParts();

				for(int i = 0; i < 1 + (parts != null ? parts.length : 0); i++) {
					Entity part;
					if(i == 0) {
						part = target;
					} else {
						part = parts[i - 1];
					}

					double dist = part.distanceToSqr(player);

					if(dist < aoeReach) {
						double angle = Math.min(
								Math.toDegrees(Math.acos(part.getDeltaMovement().subtract(player.getDeltaMovement()).normalize().dotProduct(player.getLookVec()))),
								Math.min(
										Math.toDegrees(Math.acos(part.getDeltaMovement().subtract(player.getPositionEyes(1)).normalize().dotProduct(player.getLookVec()))),
										Math.toDegrees(Math.acos(part.getDeltaMovement().add(0, part.height / 2, 0).subtract(player.getPositionEyes(1)).normalize().dotProduct(player.getLookVec())))
										)
								);

						if(angle < aoeHalfAngle) {
							double distXZ = Math.sqrt((part.getX() - player.getX())*(part.getX() - player.getX()) + (part.getZ() - player.getZ())*(part.getZ() - player.getZ()));

							double hitY = player.getY() + player.getEyeHeight() + player.getLookVec().y / Math.sqrt(Math.pow(player.getLookVec().x, 2) + Math.pow(player.getLookVec().z, 2) + 0.1D) * distXZ;

							if(hitY > part.getBoundingBox().minY - 0.25D && hitY < part.getBoundingBox().maxY + 0.25D) {
								if(player.level.rayTraceBlocks(player.getDeltaMovement().add(0, player.getEyeHeight(), 0), part.getDeltaMovement().add(0, part.height / 2, 0), false, true, false) == null) {
									if(!player.level.isClientSide()) {
										//yikes
										//Adjust attack speed such that the current attack strength becomes the same as the initial attack strength
										player.resetAttackStrengthTicker();
										attackSpeed.setBaseValue(20 * initialAttackStrength / 0.5f);

										player.attackTargetEntityWithCurrentItem(target);
									}

									enemiesInReach = true;

									break;
								}
							}
						}
					}
				}
			}
		}

		//oof
		attackSpeed.setBaseValue(baseAttackSpeed);
		for(AttributeModifier modifier : attackSpeedModifiers) {
			if(!attackSpeed.hasModifier(modifier)) {
				attackSpeed.addModifier(modifier);
			}
		}

		if(player.level.isClientSide() && (!player.isSwingInProgress || player.swingProgressInt >= player.getArmSwingAnimationEnd() / 2 || player.swingProgressInt < 0)) {
			this.playSwingSound(player, stack);

			if(enemiesInReach) {
				this.playSliceSound(player, stack);
			}
		}

		stack.addTagElement(NBT_HIT_COOLDOWN, FloatNBT.valueOf(0));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity holder, int slot, boolean isHeldItem) {
		super.onUpdate(stack, world, holder, slot, isHeldItem);

		if(!level.isClientSide()) {
			boolean swingInProgress = this.isLongSwingInProgress(stack);
			boolean newSwingInProgress = false;

			if(holder instanceof LivingEntity && ((LivingEntity) holder).getMainHandItem() == stack) {
				int ticksElapsed = holder.tickCount - this.getSwingStartTicks(stack);
				newSwingInProgress = ticksElapsed >= 0 && ticksElapsed < this.getLongSwingDuration((LivingEntity) holder, stack);
			}

			if(swingInProgress != newSwingInProgress) {
				stack.addTagElement(NBT_LONG_SWING_STATE, ByteNBT.valueOf(newSwingInProgress ? (byte) 1 : (byte) 0));
			}
		}
	}

	protected float getSwingStartCooledAttackStrength(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if(tag != null && tag.contains(NBT_SWING_START_COOLDOWN, Constants.NBT.TAG_FLOAT)) {
			return tag.getFloat(NBT_SWING_START_COOLDOWN);
		}
		return 0.0f;
	}

	protected int getSwingStartTicks(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if(tag != null && tag.contains(NBT_SWING_START_TICKS, Constants.NBT.TAG_INT)) {
			return tag.getInt(NBT_SWING_START_TICKS);
		}
		return 0;
	}

	protected boolean isLongSwingInProgress(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if(tag != null && tag.contains(NBT_LONG_SWING_STATE, Constants.NBT.TAG_BYTE)) {
			return tag.getBoolean(NBT_LONG_SWING_STATE);
		}
		return false;
	}

	protected float getLongSwingDuration(LivingEntity entity, ItemStack stack) {
		return entity.getArmSwingAnimationEnd() / 3.0f / this.getSwingSpeedMultiplier(entity, stack);
	}

	protected void playSwingSound(PlayerEntity player, ItemStack stack) {
		player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundRegistry.LONG_SWING, SoundCategory.PLAYERS, 1.2F, 0.925F * ((0.65F + this.getSwingSpeedMultiplier(player, stack)) * 0.66F + 0.33F) + player.level.random.nextFloat() * 0.15F);
	}

	protected void playSliceSound(PlayerEntity player, ItemStack stack) {
		player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundRegistry.LONG_SLICE, SoundCategory.PLAYERS, 1.2F, 0.925F * ((0.65F + this.getSwingSpeedMultiplier(player, stack)) * 0.66F + 0.33F) + player.level.random.nextFloat() * 0.15F);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		target.knockback(0.8F, (double) MathHelper.sin(attacker.yRot * 0.017453292F), (double)(-MathHelper.cos(attacker.yRot * 0.017453292F)));
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
		if (slot == EquipmentSlotType.MAINHAND) {
			modifiers.removeAll(Attributes.ATTACK_SPEED.getRegistryName());
			modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", -2.5D, Operation.ADDITION));
		}
		return modifiers;
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(BLMaterialRegistry.TOOL_LEGEND);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(BLMaterialRegistry.TOOL_LEGEND);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(BLMaterialRegistry.TOOL_LEGEND);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(BLMaterialRegistry.TOOL_LEGEND);
	}

	@Override
	public double getReach() {
		return 5.5;
	}

	protected float getSwingSpeedMultiplier(LivingEntity entity, ItemStack stack) {
		return 0.35F;
	}

	protected boolean doesBlockShieldUse(LivingEntity entity, ItemStack stack) {
		return true;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}

	@SubscribeEvent
	public static void onAttack(AttackEntityEvent event) {
		PlayerEntity player = event.getPlayer();
		ItemStack stack = player.getMainHandItem();

		if(!stack.isEmpty() && stack.getItem() instanceof ItemGreatsword) {
			stack.addTagElement(NBT_HIT_COOLDOWN, FloatNBT.valueOf(Math.max(NBTHelper.getStackNBTSafe(stack).getFloat(NBT_HIT_COOLDOWN), player.getAttackStrengthScale(0.5f))));
		}
	}

	private static boolean renderingHand = false;

	@SubscribeEvent
	public static void onStartUsingItem(LivingEntityUseItemEvent.Start event) {
		if(handleItemUse(event.getEntityLiving(), event.getItem())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onStartUsingItem(PlayerInteractEvent.RightClickItem event) {
		if(handleItemUse(event.getEntityLiving(), event.getEntityLiving().getItemInHand(event.getHand()))) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onItemUsing(LivingEntityUseItemEvent.Tick event) {
		if(handleItemUse(event.getEntityLiving(), event.getItem())) {
			event.setCanceled(true);
		}
	}

	private static boolean handleItemUse(LivingEntity entity, ItemStack useStack) {
		if(!useStack.isEmpty() && useStack.getItem().isShield(useStack, entity)) {
			for(Hand hand : Hand.values()) {
				ItemStack stack = entity.getItemInHand(hand);

				if(!stack.isEmpty() && stack.getItem() instanceof ItemGreatsword && ((ItemGreatsword) stack.getItem()).doesBlockShieldUse(entity, stack)) {
					return true;
				}
			}
		}

		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onArmSwingSpeed(ArmSwingSpeedEvent event) {
		LivingEntity entity = event.getEntityLiving();

		if(entity.swinging && entity.swingingArm != null) {
			ItemStack stack = entity.getItemInHand(entity.swingingArm);

			if(!stack.isEmpty() && stack.getItem() instanceof ItemGreatsword) {
				event.setSpeed(event.getSpeed() * ((ItemGreatsword) stack.getItem()).getSwingSpeedMultiplier(entity, stack));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRenderHand(RenderHandEvent event) {
		if(!renderingHand) {
			ItemStack stack = event.getItemStack();

			if(!stack.isEmpty() && stack.getItem() instanceof ItemGreatsword) {
				event.setCanceled(true);

				renderingHand = true;
				MatrixStack matrix = event.getMatrixStack();
				try {
					matrix.pushPose();

					float drive = event.getSwingProgress();

					float driveScale = 0.05f;
					float drivePow = 2f;

					drive = (float)(1 - Math.pow((1 - drive) * driveScale, drivePow) / Math.pow(driveScale, drivePow));

					float xOff = -0.65f;
					float yOff = 1f;
					float zOff = 0.85f;

					float leftMove = (float) Math.sin(drive * Math.PI);

					float roll = (float) Math.sin(Math.min(drive * Math.PI * 2, Math.PI / 2));
					float roll2 = (drive > 0.75F ? (float) Math.pow(Math.sin((drive - 0.75F) * Math.PI * 2), 3) : 0);
					float yaw = (float) Math.sin(drive * Math.PI);

					matrix.translate(leftMove * -1.2f, leftMove * 0.7f - event.getEquipProgress() * 0.2f, 0);

					matrix.translate(-xOff, -yOff, -zOff);
					matrix.rotate(roll * -90, 0, 0, 1);
					matrix.rotate(yaw * -190, 1, 0, 0);
					matrix.rotate(roll2 * 90, 0, 0, 1);
					matrix.translate(xOff, yOff, zOff);
					
					float equipProg = 0 /*event.getEquipProgress()*/;
					float swingProg = 0 /*event.getSwingProgress()*/;

					//Give other listeners the chance to render their own custom hand or item (e.g. decay renderer)
					if(!ForgeHooksClient.renderSpecificFirstPersonHand(event.getHand(), event.getPartialTicks(), event.getInterpolatedPitch(), swingProg, equipProg, event.getItemStack())) {
						Minecraft mc = Minecraft.getInstance();
						mc.getItemRenderer().renderItemInFirstPerson(mc.player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), swingProg, event.getItemStack(), equipProg);
					}

					matrix.popPose();
				} finally {
					renderingHand = false;
				}
			}
		}
	}
}
