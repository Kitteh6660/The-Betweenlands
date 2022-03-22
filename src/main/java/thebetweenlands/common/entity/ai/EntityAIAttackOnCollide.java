package thebetweenlands.common.entity.ai;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase {
	protected World worldObj;
	protected MobEntity attacker;

	/** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
	protected int attackTick;

	protected boolean useStandardAttack;

	public EntityAIAttackOnCollide(MobEntity entity) {
		this(entity, false);
	}

	public EntityAIAttackOnCollide(MobEntity entity, boolean useStandardAttack) {
		this.attacker = entity;
		this.worldObj = entity.world;
		this.useStandardAttack = useStandardAttack;
	}

	@Override
	public boolean shouldExecute() {
		return this.attacker.getAttackTarget() != null && this.attacker.getAttackTarget().isEntityAlive();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.attacker.getAttackTarget() != null && this.attacker.getAttackTarget().isEntityAlive();
	}

	@Override
	public void updateTask() {
		LivingEntity target = this.attacker.getAttackTarget();
		if(target != null) {
			this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			double distSq = this.attacker.getDistanceSq(target.getX(), target.getBoundingBox().minY, target.getZ());
			this.attackTick = Math.max(this.attackTick - 1, 0);
			this.attackEntity(target, distSq);
		}
	}

	protected void attackEntity(LivingEntity target, double distSq) {
		double d0 = this.getAttackReachSqr(target);

		if (distSq <= d0 && this.attackTick <= 0) {
			this.attackTick = 20;
			this.attacker.swingArm(Hand.MAIN_HAND);
			if(this.useStandardAttack) {
				useStandardAttack(this.attacker, target);
			} else {
				this.attacker.attackEntityAsMob(target);
			}
		}
	}

	protected double getAttackReachSqr(LivingEntity attackTarget) {
		return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
	}

	/**
	 * See {@link #useStandardAttack(MobEntity, Entity, float)}
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static boolean useStandardAttack(MobEntity attacker, Entity target) {
		return useStandardAttack(attacker, target, true);
	}
	
	/**
	 * See {@link #useStandardAttack(MobEntity, Entity, float)}
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static boolean useStandardAttack(MobEntity attacker, Entity target, boolean knockback) {
		float attackDamage;
		
		if(attacker.getEntityAttribute(Attributes.ATTACK_DAMAGE) != null) {
			attackDamage = (float)attacker.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue();
		} else {
			attackDamage = 2.0F;
		}
		
		return useStandardAttack(attacker, target, attackDamage, knockback);
	}
	
	/**
	 * Attacks the target with the standard attack implementation of {@link EntityMob#attackEntityAsMob(Entity)}
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static boolean useStandardAttack(MobEntity attacker, Entity target, float attackDamage, boolean knockback) {
		int knockBackModifier = 0;

		if (target instanceof LivingEntity) {
			attackDamage += EnchantmentHelper.getModifierForCreature(attacker.getMainHandItem(), ((LivingEntity)target).getCreatureAttribute());
			knockBackModifier += EnchantmentHelper.getKnockbackModifier(attacker);
		}

		double prevMotionX = target.motionX;
		double prevMotionY = target.motionY;
		double prevMotionZ = target.motionZ;
		
		boolean attacked = target.attackEntityFrom(DamageSource.causeMobDamage(attacker), attackDamage);

		if(!knockback) {
			target.motionX = prevMotionX;
			target.motionY = prevMotionY;
			target.motionZ = prevMotionZ;
		}
		
		if (attacked) {
			if (knockback && knockBackModifier > 0 && target instanceof LivingEntity) {
				((LivingEntity)target).knockBack(attacker, (float)knockBackModifier * 0.5F, (double)MathHelper.sin(attacker.yRot * 0.017453292F), (double)(-MathHelper.cos(attacker.yRot * 0.017453292F)));
				attacker.motionX *= 0.6D;
				attacker.motionZ *= 0.6D;
			}

			int fireAspectModifier = EnchantmentHelper.getFireAspectModifier(attacker);

			if (fireAspectModifier > 0) {
				target.setFire(fireAspectModifier * 4);
			}

			if (target instanceof PlayerEntity) {
				PlayerEntity entityplayer = (PlayerEntity)target;
				ItemStack attackerItem = attacker.getMainHandItem();
				ItemStack defenderItem = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

				if (!attackerItem.isEmpty() && !defenderItem.isEmpty() && attackerItem.getItem().canDisableShield(attackerItem, defenderItem, entityplayer, attacker) && defenderItem.getItem().isShield(defenderItem, entityplayer)) {
					float efficiencyModifier = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(attacker) * 0.05F;

					if (attacker.world.rand.nextFloat() < efficiencyModifier) {
						entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
						attacker.world.setEntityState(entityplayer, (byte)30);
					}
				}
			}
		}

		return attacked;
	}
}