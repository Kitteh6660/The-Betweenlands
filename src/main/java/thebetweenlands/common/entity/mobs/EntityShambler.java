package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.PlayerUtil;

public class EntityShambler extends EntityMob implements IEntityMultiPart, IEntityBL {

	private static final DataParameter<Boolean> JAWS_OPEN = EntityDataManager.defineId(EntityShambler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TONGUE_EXTEND = EntityDataManager.defineId(EntityShambler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> JAW_ANGLE = EntityDataManager.defineId(EntityShambler.class, DataSerializers.INT);
	private static final DataParameter<Integer> TONGUE_LENGTH = EntityDataManager.defineId(EntityShambler.class, DataSerializers.INT);

	private int prevJawAngle;
	private int prevTongueLength;
	
	public MultiPartEntityPart[] tongue_array; // we may want to make more tongue parts
	public MultiPartEntityPart tongue_end = new MultiPartEntityPart(this, "tongue_end", 0.5F, 0.5F);

	public EntityShambler(World world) {
		super(world);
		this.setSize(0.95F, 1.25F);
		tongue_array = new MultiPartEntityPart[16];
		for(int i = 0; i < tongue_array.length - 1; i++) {
			tongue_array[i] = new MultiPartEntityPart(this, "tongue_" + i, 0.125F, 0.125F);
		}
		tongue_array[tongue_array.length - 1] = tongue_end;
	}

	@Override
	public World getWorld() {
		return level;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new EntityAISwimming(this));
		this.goalSelector.addGoal(1, new EntityAIAttackMelee(this, 0.8D, true));
		this.goalSelector.addGoal(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.goalSelector.addGoal(3, new EntityAIWander(this, 0.75D));
		this.goalSelector.addGoal(4, new EntityAIWatchClosest(this, PlayerEntity.class, 8.0F));
		this.goalSelector.addGoal(5, new EntityAILookIdle(this));

		this.targetSelector.addGoal(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetSelector.addGoal(1, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(2, new EntityAINearestAttackableTarget<>(this, EntityFrog.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(3, new EntityAINearestAttackableTarget<>(this, EntityChiromaw.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(4, new EntityAINearestAttackableTarget<>(this, EntityMireSnail.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(5, new EntityAINearestAttackableTarget<>(this, EntityBloodSnail.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(6, new EntityAINearestAttackableTarget<>(this, EntityDragonFly.class, 3, true, true, null).setUnseenMemoryTicks(120));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(JAWS_OPEN, false);
		this.entityData.define(TONGUE_EXTEND, false);
		this.entityData.define(JAW_ANGLE, 0);
		this.entityData.define(TONGUE_LENGTH, 0);
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SHAMBLER;
	}

	public boolean jawsAreOpen() {
		return entityData.get(JAWS_OPEN);
	}

	public void setOpenJaws(boolean jawState) {
		entityData.set(JAWS_OPEN, jawState);
	}

	public boolean isExtendingTongue() {
		return entityData.get(TONGUE_EXTEND);
	}

	public void setExtendingTongue(boolean tongueState) {
		entityData.set(TONGUE_EXTEND, tongueState);
	}

	public void setJawAngle(int count) {
		entityData.set(JAW_ANGLE, count);
	}

	public void setJawAnglePrev(int count) {
		prevJawAngle = count;
	}

	public void setTongueLength(int count) {
		entityData.set(TONGUE_LENGTH, count);
	}

	public void setTongueLengthPrev(int count) {
		prevTongueLength = count;
	}

	public int getJawAngle() {
		return entityData.get(JAW_ANGLE);
	}

	public int getJawAnglePrev() {
		return prevJawAngle;
	}

	public int getTongueLength() {
		return entityData.get(TONGUE_LENGTH);
	}

	public int getTongueLengthPrev() {
		return prevTongueLength;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}
	
	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() * 1.5F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SHAMBLER_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SHAMBLER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SHAMBLER_DEATH;
	}

	@Override
	public void onLivingUpdate() {
		setJawAnglePrev(getJawAngle());
		setTongueLengthPrev(getTongueLength());
		
		if (!level.isClientSide()) {
			if (getAttackTarget() != null && canSee(getAttackTarget())) {
				faceEntity(getAttackTarget(), 10.0F, 20.0F);
				double distance = getDistance(getAttackTarget().getX(), getAttackTarget().getBoundingBox().minY, getAttackTarget().getZ());

				if (distance > 5.0D) {
					if(jawsAreOpen()) {
						setOpenJaws(false);
						if (isExtendingTongue())
							setExtendingTongue(false);
					}
				}

				if (distance <= 5.0D && distance >= 1) {
					if (!jawsAreOpen()) {
						setOpenJaws(true);
						if (!isExtendingTongue()) {
							setExtendingTongue(true);
							playSound(SoundRegistry.SHAMBLER_LICK, 1F, 1F + this.random.nextFloat() * 0.3F);
						}
					}
				}
			}

			if (getAttackTarget() == null) {
				if(jawsAreOpen())
					setOpenJaws(false);
				if (isExtendingTongue())
					setExtendingTongue(false);
			}

			if (getJawAngle() > 0 && !jawsAreOpen())
				setJawAngle(getJawAngle() - 1);

			if (jawsAreOpen() && getJawAngle() <= 10)
				setJawAngle(getJawAngle() + 1);

			if (getJawAngle() < 0 && !jawsAreOpen())
				setJawAngle(0);

			if (jawsAreOpen() && getJawAngle() > 10)
				setJawAngle(10);

			if (getTongueLength() > 0 && !isExtendingTongue())
				setTongueLength(getTongueLength() - 1);

			if (isExtendingTongue() && getTongueLength() <= 9)
				setTongueLength(getTongueLength() + 1);

			if (getTongueLength() < 0 && !isExtendingTongue())
				setTongueLength(0);

			if (isExtendingTongue() && getTongueLength() > 9) {
				setTongueLength(9);
				setExtendingTongue(false);
			}
		}
		super.onLivingUpdate();
	}

	@Override
    public void tick() {
		super.tick();
		
		Vector3d vector = getLook(1);
		
		double offSetX = vector.x * -0.25D;
		double offsetY = vector.y * -0.25D;
		double offSetZ = vector.z * -0.25D;
		
		double lengthIncrement = 0.5D / tongue_array.length;
		
		double tongueLength = lengthIncrement;
		
		for(MultiPartEntityPart part : tongue_array) {
			part.prevRotationYaw = part.yRot;
			part.prevRotationPitch = part.xRot;
			
			part.lastTickPosX = part.xOld = part.getX();
			part.lastTickPosY = part.yOld = part.getY();
			part.lastTickPosZ = part.zOld = part.getZ();
			
			part.setPosition(posX + offSetX + ((double) vector.x * getTongueLength() * tongueLength), posY + this.getEyeHeight() - 0.32 + offsetY + ((double) vector.y * getTongueLength() * tongueLength), posZ + offSetZ + ((double) vector.z * getTongueLength() * tongueLength));
			part.yRot = this.yRot;
			part.xRot = this.xRot;
		
			tongueLength += lengthIncrement;
		}
		
		checkCollision();
    }
	
	@Override
	public void updatePassenger(Entity entity) {
		PlayerUtil.resetFloating(entity);
		
		if (entity instanceof LivingEntity) {
			double a = Math.toRadians(yRot);
			double offSetX = Math.sin(a) * getTongueLength() > 0 ? -0.125D : -0.35D;
			double offSetZ = -Math.cos(a) * getTongueLength() > 0 ? -0.125D : -0.35D;
			entity.setPosition(tongue_end.getX() + offSetX, tongue_end.getY() - entity.height * 0.3D, tongue_end.getZ() + offSetZ);
			if (entity.isCrouching())
				entity.setSneaking(false);
		}
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	protected Entity checkCollision() {
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, tongue_end.getBoundingBox());
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null && entity == getAttackTarget() && !(entity instanceof IEntityMultiPart) && !(entity instanceof MultiPartEntityPart)) {
				if (entity instanceof LivingEntity)
					if (!isBeingRidden()) {
						entity.startRiding(this, true);
						if (!level.isClientSide())
							if (isExtendingTongue())
								setExtendingTongue(false); //eeeeeh!
					}
			}
		}
		return null;
	}

    @OnlyIn(Dist.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return getJawAnglePrev() + (getJawAngle() - getJawAnglePrev()) * partialTicks;
    }

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		return canSee(entity) ? super.attackEntityAsMob(entity) : false;
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float dmg) {
		damageShambler(source, dmg); // we may want seperate tongue damage - dunno
		return true;
	}

	protected boolean damageShambler(DamageSource source, float ammount) {
		return super.hurt(source, ammount);
	}
}
