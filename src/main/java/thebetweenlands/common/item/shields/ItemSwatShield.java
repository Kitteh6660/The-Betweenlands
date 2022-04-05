package thebetweenlands.common.item.shields;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

import java.util.List;

public class ItemSwatShield extends ItemBLShield {
	public ItemSwatShield(ToolMaterial material) {
		super(material);

		this.addPropertyOverride(new ResourceLocation("charging"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack && (getRemainingChargeTicks(stack, entityIn) > 0 || isPreparingCharge(stack, entityIn)) ? 1.0F : 0.0F);
	}

	/**
	 * Sets whether the specified user is preparing for a charge attack
	 * @param stack
	 * @param user
	 * @param charging
	 */
	public void setPreparingCharge(ItemStack stack, LivingEntity user, boolean charging) {
		user.getEntityData().putBoolean("thebetweenlands.shield.charging", charging);
	}

	/**
	 * Returns whether the specified user is preparing for a charge attack
	 * @param stack
	 * @param user
	 * @return
	 */
	public boolean isPreparingCharge(ItemStack stack, LivingEntity user) {
		return user.getEntityData().getBoolean("thebetweenlands.shield.charging");
	}

	/**
	 * Sets how long the specified user has been preparing for a charge attack
	 * @param stack
	 * @param user
	 * @param ticks
	 */
	public void setPreparingChargeTicks(ItemStack stack, LivingEntity user, int ticks) {
		user.getEntityData().putInt("thebetweenlands.shield.chargingTicks", ticks);
	}

	/**
	 * Returns how long the specified user has been preparing for a charge attack
	 * @param stack
	 * @param user
	 * @return
	 */
	public int getPreparingChargeTicks(ItemStack stack, LivingEntity user) {
		return user.getEntityData().getInt("thebetweenlands.shield.chargingTicks");
	}

	/**
	 * Sets for how much longer the specified user can charge
	 * @param stack
	 * @param user
	 * @param ticks
	 */
	public void setRemainingChargeTicks(ItemStack stack, LivingEntity user, int ticks) {
		user.getEntityData().putInt("thebetweenlands.shield.remainingRunningTicks", ticks);
	}

	/**
	 * Returns for how much longer the specified user can charge
	 * @param stack
	 * @param user
	 * @return
	 */
	public int getRemainingChargeTicks(ItemStack stack, LivingEntity user) {
		return user.getEntityData().getInt("thebetweenlands.shield.remainingRunningTicks");
	}

	/**
	 * Returns for how many ticks the user can charge for the specified preparation ticks
	 * @param stack
	 * @param user
	 * @param preparingTicks
	 * @return
	 */
	public int getChargeTime(ItemStack stack, LivingEntity user, int preparingTicks) {
		float strength = MathHelper.clamp(this.getPreparingChargeTicks(stack, user) / 20.0F - 0.2F, 0, 1);
		return (int)(strength * strength * this.getMaxChargeTime(stack, user));
	}

	/**
	 * Returns the maximum charge ticks
	 * @param stack
	 * @param user
	 * @return
	 */
	public int getMaxChargeTime(ItemStack stack, LivingEntity user) {
		return 80;
	}

	/**
	 * Called when an enemy is rammed
	 * @param stack
	 * @param user
	 * @param enemy
	 * @param rammingDir
	 */
	public void onEnemyRammed(ItemStack stack, LivingEntity user, LivingEntity enemy, Vector3d rammingDir) {
		boolean attacked = false;
		
		if(user instanceof PlayerEntity) {
			attacked = enemy.hurt(DamageSource.causePlayerDamage((PlayerEntity)user), 10.0F);
			
			if (user instanceof ServerPlayerEntity)
				AdvancementCriterionRegistry.SWAT_SHIELD.trigger((ServerPlayerEntity) user, enemy);
		} else {
			attacked = enemy.hurt(DamageSource.causeMobDamage(user), 10.0F);
		}
		
		if(attacked) {
			enemy.knockBack(user, 2.0F, -rammingDir.x, -rammingDir.z);
		}
	}

	/**
	 * Called every tick when charging
	 * @param stack
	 * @param user
	 */
	public void onChargingUpdate(ItemStack stack, LivingEntity user) {
		if(user.onGround && !user.isCrouching()) {
			Vector3d dir = user.getLookVec();
			dir = new Vector3d(dir.x, 0, dir.z).normalize();
			
			double speed = user instanceof PlayerEntity ? 0.35D : 0.2D;
			
			user.motionX += dir.x * speed;
			user.motionZ += dir.z * speed;

			if(user instanceof PlayerEntity) {
				((PlayerEntity) user).getFoodData().causeFoodExhaustion(0.15F);
			}
		}
		
		if(Math.sqrt(user.motionX*user.motionX + user.motionZ*user.motionZ) > 0.2D) {
			Vector3d moveDir = new Vector3d(user.motionX, user.motionY, user.motionZ).normalize();
			
			List<LivingEntity> targets = user.world.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(1), e -> e != user);
			
			for(LivingEntity target : targets) {
				Vector3d dir = target.getDeltaMovement().subtract(user.getDeltaMovement()).normalize();
				
				//45° angle range
				if(target.canBeCollidedWith() && Math.toDegrees(Math.acos(moveDir.dotProduct(dir))) < 45) {
					this.onEnemyRammed(stack, user, target, moveDir);
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		playerIn.setActiveHand(handIn);
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity user, int count) {
		boolean preparing = this.isPreparingCharge(stack, user);

		int runningTicks = this.getRemainingChargeTicks(stack, user);

		if(preparing && runningTicks <= 0) {
			this.setPreparingChargeTicks(stack, user, this.getPreparingChargeTicks(stack, user) + 1);
		}

		if(preparing && !user.isCrouching() && runningTicks <= 0) {
			this.setRemainingChargeTicks(stack, user, this.getChargeTime(stack, user, this.getPreparingChargeTicks(stack, user)));
			this.setPreparingChargeTicks(stack, user, 0);
			this.setPreparingCharge(stack, user, false);
			
			this.onStartedCharging(stack, user);
		} else if(!preparing && user.isCrouching()) {
			this.setRemainingChargeTicks(stack, user, 0);
			this.setPreparingChargeTicks(stack, user, 0);
			this.setPreparingCharge(stack, user, true);
		}

		if(runningTicks > 0) {
			this.onChargingUpdate(stack, user);
			this.setRemainingChargeTicks(stack, user, --runningTicks);
			if(runningTicks <= 0) {
				user.stopActiveHand();
				this.onStoppedCharging(stack, user);
			}
		}

		super.onUsingTick(stack, user, count);
	}

	protected void onStartedCharging(ItemStack stack, LivingEntity user) {
		
	}
	
	protected void onStoppedCharging(ItemStack stack, LivingEntity user) {
		if(!user.level.isClientSide() && user instanceof PlayerEntity) {
			((PlayerEntity) user).getCooldownTracker().setCooldown(this, 8 * 20);
		}
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
		
		if(this.getRemainingChargeTicks(stack, entityLiving) > 0) {
			this.onStoppedCharging(stack, entityLiving);
		}
		
		this.setPreparingChargeTicks(stack, entityLiving, 0);
		this.setRemainingChargeTicks(stack, entityLiving, 0);
		this.setPreparingCharge(stack, entityLiving, false);
		
		if (entityLiving instanceof ServerPlayerEntity)
			AdvancementCriterionRegistry.SWAT_SHIELD.revert((ServerPlayerEntity) entityLiving);
	}

	@Override
	public boolean canBlockDamageSource(ItemStack stack, LivingEntity attacked, Hand hand, DamageSource source) {
		if(this.getRemainingChargeTicks(stack, attacked) > 0 && source.getImmediateSource() != null) {
			return true;
		}
		return super.canBlockDamageSource(stack, attacked, hand, source);
	}

	@Override
	public float getBlockedDamage(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		if(this.getRemainingChargeTicks(stack, attacked) > 0) {
			return 0;
		}
		return super.getBlockedDamage(stack, attacked, damage, source);
	}

	@Override
	public float getDefenderKnockbackMultiplier(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		if(this.getRemainingChargeTicks(stack, attacked) > 0) {
			return 0;
		}
		return super.getDefenderKnockbackMultiplier(stack, attacked, damage, source);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onUpdateFov(FOVUpdateEvent event) {
		ItemStack activeItem = event.getEntity().getActiveItemStack();
		ItemSwatShield swatShield;
		if(!activeItem.isEmpty() && activeItem.getItem() instanceof ItemSwatShield && (swatShield = (ItemSwatShield) activeItem.getItem()).isPreparingCharge(activeItem, event.getEntity())) {
			int preparingTicks = swatShield.getPreparingChargeTicks(activeItem, event.getEntity());
			float progress = Math.min(swatShield.getChargeTime(activeItem, event.getEntity(), preparingTicks) / (float)swatShield.getMaxChargeTime(activeItem, event.getEntity()), 1);
			event.setNewfov(1.0F - progress * 0.25F);
		}
	}
}
