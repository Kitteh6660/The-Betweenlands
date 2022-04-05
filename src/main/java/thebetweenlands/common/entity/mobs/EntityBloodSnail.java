package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.ai.attributes.Attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBloodSnail extends EntityMob implements IEntityBL {
	public static final IAttribute RANGED_ATTACK_MIN_DIST_ATTRIB = (new RangedAttribute(null, "bl.rangedAttackMinDist", 3.0D, 0, Double.MAX_VALUE)).setDescription("Minimum range at which the ranged attack is used");
	public static final IAttribute RANGED_ATTACK_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.rangedAttackCooldown", 50, 0, Integer.MAX_VALUE)).setDescription("Ranged attack cooldown in ticks");

	protected int rangedAttackTimer = 0;

	public EntityBloodSnail(World world) {
		super(world);
		setSize(0.7F, 0.5F);
		stepHeight = 0.0F;
	}

	@Override
	protected void registerGoals() {
		tasks.addGoal(0, new EntityAISwimming(this));
		tasks.addGoal(1, new EntityAIAttackMelee(this, 1D, false));
		tasks.addGoal(2, new EntityAIWander(this, 1D));
		tasks.addGoal(3, new EntityAIWatchClosest(this, PlayerEntity.class, 6.0F));
		tasks.addGoal(4, new EntityAILookIdle(this));
		targetTasks.addGoal(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addGoal(1, new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, false, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(5.0D);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getAttributeMap().registerAttribute(RANGED_ATTACK_MIN_DIST_ATTRIB);
		getAttributeMap().registerAttribute(RANGED_ATTACK_COOLDOWN_ATTRIB);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.BLOOD_SNAIL;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SNAIL_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SNAIL_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SNAIL_DEATH;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			if (entity instanceof MobEntity) {
				byte duration = 0;
				if (world.getDifficulty() == EnumDifficulty.NORMAL)
					duration = 7;
				else if (world.getDifficulty() == EnumDifficulty.HARD)
					duration = 15;

				if (duration > 0) {
					((MobEntity) entity).addEffect(new EffectInstance(Effects.POISON, duration * 20, 0));
					((MobEntity) entity).addEffect(new EffectInstance(Effects.NAUSEA, duration * 20, 0));
				}
			}
			return true;
		} else
			return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (getAttackTarget() != null && this.isEntityAlive()) {
			float distance = (float) getDistance(getAttackTarget().getX(), getAttackTarget().getBoundingBox().minY, getAttackTarget().getZ());
			double minDist = this.getAttribute(RANGED_ATTACK_MIN_DIST_ATTRIB).getValue();

			if(distance > minDist) {
				int cooldown = (int) this.getAttribute(RANGED_ATTACK_COOLDOWN_ATTRIB).getValue();

				if (getRangeAttackTimer() < cooldown) {
					setRangeAttackTimer(getRangeAttackTimer() + 1);
				} else if (getRangeAttackTimer() >= cooldown) {
					shootMissile(getAttackTarget(), distance);
				}
			}
		}
	}

	public void shootMissile(LivingEntity entity, float distance) {
		setRangeAttackTimer(0);
		if (canSee(entity)) {
			EntityThrowable missile = new EntitySnailPoisonJet(world, this);
			missile.moveTo(this.getX(), this.getY(), this.getZ(), 0, 0);
			missile.xRot -= -20.0F;
			double targetX = entity.getX() + entity.motionX - this.getX();
			double targetY = entity.getY() + entity.getEyeHeight() / 2.0D - this.getY();
			double targetZ = entity.getZ() + entity.motionZ - this.getZ();
			float target = MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
			missile.shoot(targetX, targetY + target * 0.1F, targetZ, 0.75F, 8.0F);
			world.addFreshEntity(missile);
		}
	}

	public int getRangeAttackTimer() {
		return rangedAttackTimer;
	}

	public void setRangeAttackTimer(int size) {
		rangedAttackTimer = size;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
}
