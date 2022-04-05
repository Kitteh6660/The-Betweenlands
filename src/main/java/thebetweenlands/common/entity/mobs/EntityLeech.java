package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIBLAvoidEntity;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class EntityLeech extends EntityMob implements IEntityBL {
	private static final int MAX_BLOOD_LEVEL = 5;

	private static final int TIME_TO_FLEE = 600;

	public int attackCountDown = 20;

	public int hungerCoolDown;

	private int drainage;

	public float moveProgress;

	public boolean firstTickCheck;

	public int fleeingTick;

	AnimationMathHelper mathSucking = new AnimationMathHelper();

	private EntityAINearestAttackableTarget<LivingEntity> aiAttackTarget;
	private EntityAIWander aiWander;
	private EntityAIAttackMelee aiAttackOnCollide;
	private EntityAIBLAvoidEntity aiAvoidHarmer;

	private static final DataParameter<Byte> BLOOD_CONSUMED = EntityDataManager.defineId(EntityLeech.class, DataSerializers.BYTE);

	public EntityLeech(World worldIn) {
		super(worldIn);
		setSize(0.7F, 0.3F);
		stepHeight = 0;
		moveProgress = 0;
		firstTickCheck = false;
		drainage = 0;
		setBloodConsumed(0);
	}


	@Override
	protected void registerGoals() {
		this.aiAttackTarget = new EntityAINearestAttackableTarget<LivingEntity>(this, LivingEntity.class, true, true);
		this.aiWander = new EntityAIWander(this, 0.8D);
		this.aiAttackOnCollide = new EntityAIAttackMelee(this, 1.0D, false);
		this.aiAvoidHarmer = new EntityAIBLAvoidEntity(this, PlayerEntity.class, 6, 0.5D, 0.6D);

		tasks.addGoal(0, aiAttackOnCollide);
		tasks.addGoal(1, aiWander);
		tasks.addGoal(2, new EntityAILookIdle(this));

		targetTasks.addGoal(0, aiAttackTarget);
		targetTasks.addGoal(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(BLOOD_CONSUMED, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.LEECH_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.LEECH_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.LEECH_DEATH;
	}

	public void onCollideWithEntity(LivingEntity entity) {
		if (!level.isClientSide()) {
			if (!entity.isBeingRidden() && getBloodConsumed() <= 0) {
				startRiding(entity);
				this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(entity));
				targetTasks.removeTask(aiAttackTarget);
				tasks.removeTask(aiAttackOnCollide);
			}
		}
	}

	@Override
	public void tick() {
		if (!level.isClientSide()) {
			if (fleeingTick == 0 && getAttackTarget() != null && getDistance(getAttackTarget()) < 2) {
				onCollideWithEntity(getAttackTarget());
			}
			if (getRidingEntity() != null) {
				setRotation(getRidingEntity().yRot, getRidingEntity().xRot);
				if (getRidingEntity().isOnFire() && getRidingEntity() instanceof LivingEntity) {
					PotionEffect stomachContents = new EffectInstance(Effects.POISON, 120 + getBloodConsumed() * 200 / MAX_BLOOD_LEVEL, 0);
					((LivingEntity) getRidingEntity()).addEffect(stomachContents);
					setBloodConsumed(0);
					Entity mount = this.getRidingEntity();
					dismountEntity(mount);
					flee();
					dismountRidingEntity();
					this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(mount));
				}
			}
			if (getBloodConsumed() == MAX_BLOOD_LEVEL && getRidingEntity() != null) {
				Entity mount = this.getRidingEntity();
				dismountEntity(getRidingEntity());
				dismountRidingEntity();
				this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(mount));
			}
			if (fleeingTick > 0) {
				fleeingTick--;
				if (fleeingTick == 0) {
					stopFleeing();
				}
			}
		}

		if (!firstTickCheck) {
			dismountRidingEntity();
			firstTickCheck = true;
		}
		if (--hungerCoolDown == 0) {
			if (getBloodConsumed() > 0) {
				setBloodConsumed(getBloodConsumed() - 1);
			}
		}
		if (getRidingEntity() != null) {
			moveProgress = 1 + mathSucking.swing(1, 0.15F, false);
			if (rand.nextInt(10) == 0) {
				for (int i = 0; i < 8; i++) {
					world.addParticle(ParticleTypes.REDSTONE, posX + (rand.nextFloat() - rand.nextFloat()), posY + rand.nextFloat(), posZ + (rand.nextFloat() - rand.nextFloat()), 0, 0, 0);
				}
			}
		} else if (!level.isClientSide()) {
			moveProgress = 0F + mathSucking.swing(1, 0.15F, false);
		}

		if (getRidingEntity() != null && getRidingEntity() instanceof LivingEntity && getBloodConsumed() < MAX_BLOOD_LEVEL) {
			drainage++;
			if (drainage >= attackCountDown && this.deathTime == 0) {
				getRidingEntity().hurt(DamageSource.causeMobDamage(this), (int)getAttribute(Attributes.ATTACK_DAMAGE).getValue());
				drainage = 0;
				setBloodConsumed(getBloodConsumed() + 1);
			}
		}
		super.tick();
	}

	private void flee() {
		fleeingTick = TIME_TO_FLEE;
		if(!this.level.isClientSide()) {
			aiAvoidHarmer.setTargetEntityClass(getRidingEntity().getClass());
			tasks.addGoal(0, aiAvoidHarmer);
			targetTasks.removeTask(aiAttackTarget);
			tasks.removeTask(aiAttackOnCollide);
		}
	}

	private void stopFleeing() {
		if(!this.level.isClientSide()) {
			tasks.removeTask(aiAvoidHarmer);
			targetTasks.addGoal(0, aiAttackTarget);
			tasks.addGoal(0, aiAttackOnCollide);
		}
	}

	@Override
	public double getStepY() {
		if (getRidingEntity() != null && getRidingEntity() instanceof PlayerEntity)
			return getRidingEntity().height -2.25F;
		else if (getRidingEntity() != null)
			return getRidingEntity().height * 0.5D - 1.25D;
		else
			return super.getStepY();
	}

	@Override
    public boolean canRiderInteract() {
        return true;
    }

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return entity != EntityLeech.class && (entity == PlayerEntity.class || entity == ServerPlayerEntity.class || entity == EntitySwampHag.class);
	}

	public int getBloodConsumed() {
		return entityData.get(BLOOD_CONSUMED) & 0xFF;
	}

	public void setBloodConsumed(int amount) {
		hungerCoolDown = 500;
		entityData.set(BLOOD_CONSUMED, Byte.valueOf((byte) amount));
		if (amount == 0 && getRidingEntity() == null && !this.level.isClientSide()) {
			targetTasks.addGoal(0, aiAttackTarget);
			tasks.addGoal(0, aiAttackOnCollide);
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.LEECH;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.putInt("bloodLevel", getBloodConsumed());
		nbttagcompound.putInt("fleeingTick", fleeingTick);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		if(nbttagcompound.contains("bloodLevel")) {
			setBloodConsumed(nbttagcompound.getInt("bloodLevel"));
		}
		if(nbttagcompound.contains("fleeingTick")) {
			fleeingTick = nbttagcompound.getInt("fleeingTick");
		}
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
