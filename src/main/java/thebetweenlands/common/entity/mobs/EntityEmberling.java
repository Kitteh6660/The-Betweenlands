package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.block.misc.BlockOctine;
import thebetweenlands.common.entity.EntityTameableBL;
import thebetweenlands.common.entity.ai.EntityAIFollowOwnerBL;
import thebetweenlands.common.entity.ai.EntityAISitBL;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityEmberling extends EntityTameableBL implements IEntityMultiPart, IRingOfGatheringMinion {

	public MultiPartEntityPart[] tailPart;
	private static final DataParameter<Boolean> IS_FLAME_ATTACKING = EntityDataManager.<Boolean>createKey(EntityEmberling.class, DataSerializers.BOOLEAN);
	public float animationTicks, prevAnimationTicks;

	private EntityMoveHelper moveHelperWater;
	private EntityMoveHelper moveHelperLand;

	private PathNavigateGround pathNavigatorGround;
	private PathNavigateSwimmer pathNavigatorWater;

	public EntityEmberling(EntityType<? extends EntityTameableBL> entity, World world) {
		super(entity, world);
		setSize(0.9F, 0.85F);
		stepHeight = 1F;
		fireImmune = true;
		tailPart = new MultiPartEntityPart[] { new MultiPartEntityPart(this, "tail", 0.5F, 0.5F) };

		setPathPriority(PathNodeType.WATER, 100);

		moveHelperWater = new EntityEmberling.EmberlingMoveHelper(this);
		moveHelperLand = new EntityMoveHelper(this);

		pathNavigatorGround = new PathNavigateGround(this, world);
		pathNavigatorGround.setCanSwim(true);

		pathNavigatorWater = new PathNavigateSwimmer(this, world);

		updateMovementAndPathfinding();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_FLAME_ATTACKING, false);
	}

	public void setIsFlameAttacking(boolean flame_on) {
		dataManager.set(IS_FLAME_ATTACKING, flame_on);
	}

	public boolean getIsFlameAttacking() {
		return dataManager.get(IS_FLAME_ATTACKING);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityEmberling.EntityAIFlameBreath(this));
		this.aiSit = new EntityAISitBL(this);
		tasks.addTask(1, this.aiSit);
		tasks.addTask(2, new EntityEmberling.AIEmberlingAttack(this));
		tasks.addTask(3, new EntityAIFollowOwnerBL(this, 0.6D, 10.0F, 2.0F));
		tasks.addTask(4, new EntityAIWander(this, 0.6D));
		tasks.addTask(5, new EntityAIWatchClosest(this, PlayerEntity.class, 8.0F));
		tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityMob.class, 0, false, true, null));
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(40D);
		getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.EMBERLING_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.EMBERLING_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.EMBERLING_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.EMBERLING;
	}

	@Override
	public boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
		if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast)) {
			if (target instanceof EntityEmberling) {
				EntityEmberling emberling = (EntityEmberling) target;

				if (emberling.isTamed() && emberling.getOwner() == owner)
					return false;
			}
			if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) target))
				return false;
			else
				return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
		} else
			return false;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		boolean hitTarget = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue()));
		if (hitTarget)
			this.applyEnchantments(this, entity);
		return hitTarget;
	}

	@Override
	public boolean isNotColliding() {
		return level.getCollisionBoxes(this, getBoundingBox()).isEmpty() && level.checkNoEntityCollision(getBoundingBox(), this);
	}

	@Override
	public boolean isInWater() {
		return this.inWater = level.handleMaterialAcceleration(getBoundingBox(), Material.WATER, this);
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	protected boolean canDespawn() {
		return !isTamed();
	}

	@Override
	public boolean canBeLeashedTo(PlayerEntity player) {
		return !canDespawn() && super.canBeLeashedTo(player);
	}

	@OnlyIn(Dist.CLIENT)
	public float smoothedAngle(float partialTicks) {
		return prevAnimationTicks + (animationTicks - prevAnimationTicks) * partialTicks;
	}

	@Override
	public void tick() {
		super.tick();

		updateMovementAndPathfinding();

		if (level.getDifficulty() == EnumDifficulty.PEACEFUL)
			if (!level.isClientSide())
				if (!isTamed())
					remove();

		if (!level.isClientSide() && isSitting()) {
			if (getHealth() < getMaxHealth())
				if (tickCount % 1200 == 0)
					if (level.getBlockState(getPosition().below()).getBlock() instanceof BlockOctine) {
						heal(1); // passive heal, 1 health a minute whilst sleeping on an octine block.
						playTameEffect(true);
						world.setEntityState(this, (byte)7);
					}
		}

		if (level.isClientSide()) {
			if (!getIsFlameAttacking()) 
				if (tickCount % 5 == 0)
					if(!isSitting())
						flameParticles(level, tailPart[0].getX(), tailPart[0].getY() + 0.25, tailPart[0].getZ(), rand);
					else {
						sleepingParticles(level, tailPart[0].getX(), tailPart[0].getY() + 0.25, tailPart[0].getZ(), rand);
					}

			if (getIsFlameAttacking())
				spawnFlameBreathParticles();
		}
	}

	protected void updateMovementAndPathfinding() {
		if (isInWater())
			moveHelper = moveHelperWater;
		else
			moveHelper = moveHelperLand;

		if (isInWater() && !world.isEmptyBlock(new BlockPos(posX, getBoundingBox().maxY + 0.25D, posZ)))
			navigator = pathNavigatorWater;
		else
			navigator = pathNavigatorGround;

		//renderYawOffset = yRot;
		double a = Math.toRadians(renderYawOffset);
		double offSetX = -Math.sin(a) * (isSitting() ? -0.2D : 1.85D);
		double offSetZ = Math.cos(a) * (isSitting() ? -0.2D : 1.85D);
		tailPart[0].moveTo(posX - offSetX, posY + 0.2D, posZ - offSetZ, 0.0F, 0.0F);
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		boolean holdsEquipment = hand == Hand.MAIN_HAND && !stack.isEmpty() && (stack.getItem() instanceof IEquippable || stack.getItem() == ItemRegistry.AMULET_SLOT);
		if (holdsEquipment)
			return true;
		if (!stack.isEmpty()) {
			if (isTamed()) {
				if ((EnumItemMisc.OCTINE_NUGGET.isItemOf(stack) || stack.getItem() == ItemRegistry.OCTINE_INGOT)) {
					if (getHealth() < getMaxHealth()) {
						if (!level.isClientSide()) {
							heal(EnumItemMisc.OCTINE_NUGGET.isItemOf(stack) ? 5.0F : getMaxHealth() - getHealth());

							if (!player.isCreative()) {
								stack.shrink(1);
								if (stack.getCount() <= 0)
									player.setItemInHand(hand, ItemStack.EMPTY);
							}

							if (getHealth() == getMaxHealth()) {
								level.playSound(null, getPosition(), SoundRegistry.EMBERLING_LIVING, SoundCategory.NEUTRAL, 1.0F, 0.75F);
							}
						} else {
							playTameEffect(true);
						}

						return true;
					}
				}
			}
		}

		if (isOwner(player) && !world.isClientSide()) {
			aiSit.setSitting(!isSitting());
			isJumping = false;
			navigator.clearPath();
			setAttackTarget((LivingEntity) null);
		}

		return super.processInteract(player, hand);
	}

	@Override
	public void travel(float strafe,float up, float forward) {
		if (isServerWorld()) {
			if (isInWater()) {
				moveRelative(strafe, up, forward, 0.1F);
				move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.8999999761581421D;
				motionY *= 0.8999999761581421D;
				motionZ *= 0.8999999761581421D;

				if (getAttackTarget() == null) {
					motionY += Math.sin(this.tickCount * 0.05D) * 0.0035D - 0.0002D;
				}
			} else {
				super.travel(strafe, up, forward);
			}
		} else {
			super.travel(strafe, up, forward);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void flameParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double velX = 0.0D;
			double velY = 0.0D;
			double velZ = 0.0D;
			int motionX = rand.nextBoolean() ? 1 : - 1;
			int motionZ = rand.nextBoolean() ? 1 : - 1;
			velY = rand.nextFloat() * 0.05D;
			velZ = rand.nextFloat() * 0.025D * motionZ;
			velX = rand.nextFloat() * 0.025D * motionX;
			if(this.inWater) {
				world.spawnAlwaysVisibleParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(),  x, y, z, velX, velY, velZ);
				world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(),  x, y, z, velX, velY, velZ);
			} else {
				world.spawnAlwaysVisibleParticle(EnumParticleTypes.FLAME.getParticleID(),  x, y, z, velX, velY, velZ);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void spawnFlameBreathParticles() {
		for (int count = 0; count < 5; ++count) {
			Vector3d look = getLook(1.0F).normalize();
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 1D;
			double offSetZ = Math.cos(a) * 1D;
			int motionX = rand.nextBoolean() ? 1 : - 1;
			int motionY = rand.nextBoolean() ? 1 : - 1;
			int motionZ = rand.nextBoolean() ? 1 : - 1;
			double velX = rand.nextFloat() * 0.1D * motionX;
			double velY = rand.nextFloat() * 0.1D * motionY;
			double velZ = rand.nextFloat() * 0.1D * motionZ;

			float speed = 0.15F;
			world.spawnAlwaysVisibleParticle(EnumParticleTypes.FLAME.getParticleID(), posX + offSetX + velX, posY + getEyeHeight() * 0.75D + velY, posZ + offSetZ + velZ, look.x * speed, look.y * speed, look.z * speed);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void sleepingParticles(World world, double x, double y, double z, Random rand) {
		int motionX = rand.nextBoolean() ? 1 : -1;
		int motionZ = rand.nextBoolean() ? 1 : -1;
		double velY = rand.nextFloat() * 0.05D;
		double velZ = rand.nextFloat() * 0.025D * motionZ;
		double velX = rand.nextFloat() * 0.025D * motionX;
		world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), x, y, z, velX, velY, velZ);
	}

	@Override
	public World getWorld() {
		return level;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (isEntityInvulnerable(source) || source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN))
			return false;
		return super.attackEntityFrom(source, damage);
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		return false;
	}

	@Override
	public Entity[] getParts(){
		return tailPart;
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return 0.5F;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

	static class AIEmberlingAttack extends EntityAIAttackMelee {

		public AIEmberlingAttack(EntityEmberling emberling) {
			super(emberling, 0.65D, false);
		}

		@Override
		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}

	static class EntityAIFlameBreath extends EntityAIBase {
		EntityEmberling emberling;
		LivingEntity target;
		int missileCount;
		int shootCount;

		public EntityAIFlameBreath(EntityEmberling emberling) {
			this.emberling = emberling;
			setMutexBits(5);
		}

		@Override
		public boolean shouldExecute() {
			target = emberling.getAttackTarget();

			if (target == null || emberling.isInWater() || emberling.isSitting())
				return false;
			else {
				double distance = emberling.getDistanceSq(target);
				if (distance >= 4.0D && distance <= 25.0D) {
					if (!emberling.onGround)
						return false;
					else
						return emberling.getRNG().nextInt(8) == 0;
				} else
					return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return shootCount !=-1 && missileCount !=-1 && emberling.recentlyHit <= 40;
		}

		@Override
		public void startExecuting() {
			missileCount = 0;
			shootCount = 0;
			emberling.level.playSound(null, emberling.getPosition(), SoundRegistry.EMBERLING_FLAMES, SoundCategory.HOSTILE, 1F, 1F);
		}

		@Override
		public void resetTask() {
			shootCount = -1;
			missileCount = -1;
			if(emberling.getIsFlameAttacking())
				emberling.setIsFlameAttacking(false);
		}

		@Override
		public void updateTask() {
			if(!emberling.getIsFlameAttacking())
				emberling.setIsFlameAttacking(true);
			emberling.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			float f = (float) MathHelper.atan2(target.getZ() - emberling.getZ(), target.getX() - emberling.getX());
			int distance = MathHelper.floor(emberling.getDistance(target));
			missileCount++;
			if (missileCount %5 == 0) {
				shootCount++;
				double d2 = 1D * (double) (shootCount);
				AxisAlignedBB flameBox = new AxisAlignedBB(new BlockPos(emberling.getX() + (double) MathHelper.cos(f) * d2, emberling.getY(), emberling.getZ() + (double) MathHelper.sin(f) * d2));
				List<LivingEntity> list = emberling.level.getEntitiesOfClass(LivingEntity.class, flameBox);
				for (int entityCount = 0; entityCount < list.size(); entityCount++) {
					Entity entity = list.get(entityCount);
					if (entity != null && entity == target)
						if (entity instanceof LivingEntity)
							if(!entity.isOnFire())
								entity.setFire(5); // seems ok for time
				}
			}
			if (shootCount >= distance || shootCount >= 4)
				resetTask();
		}
	}

	static class EmberlingMoveHelper extends EntityMoveHelper {
		private final EntityEmberling emberling;

		public EmberlingMoveHelper(EntityEmberling emberling) {
			super(emberling);
			this.emberling = emberling;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (action == EntityMoveHelper.Action.MOVE_TO && !emberling.getNavigator().noPath()) {
				double d0 = posX - emberling.getX();
				double d1 = posY - emberling.getY();
				double d2 = posZ - emberling.getZ();
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				d3 = (double) MathHelper.sqrt(d3);
				d1 = d1 / d3;
				float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
				emberling.yRot = limitAngle(emberling.yRot, f, 90.0F);
				emberling.renderYawOffset = emberling.yRot;
				float f1 = (float) (speed * emberling.getEntityAttribute(Attributes.MOVEMENT_SPEED).getAttributeValue());
				emberling.setAIMoveSpeed(emberling.getAIMoveSpeed() + (f1 - emberling.getAIMoveSpeed()) * 0.125F);
				double d4 = Math.sin((double) (emberling.tickCount + emberling.getEntityId()) * 0.5D) * 0.05D;
				double d5 = Math.cos((double) (emberling.yRot * 0.017453292F));
				double d6 = Math.sin((double) (emberling.yRot * 0.017453292F));
				emberling.motionX += d4 * d5;
				emberling.motionZ += d4 * d6;
				d4 = Math.sin((double) (emberling.tickCount + emberling.getEntityId()) * 0.75D) * 0.05D;
				emberling.motionY += d4 * (d6 + d5) * 0.25D;
				if (Math.abs(emberling.motionY) < 0.35) {
					emberling.motionY += (double) emberling.getAIMoveSpeed() * d1 * 0.1D * (2 + (d1 > 0 ? 0.4 : 0) + (emberling.collidedHorizontally ? 20 : 0));
				}
				EntityLookHelper entitylookhelper = emberling.getLookHelper();
				double d7 = emberling.getX() + d0 / d3 * 2.0D;
				double d8 = (double) emberling.getEyeHeight() + emberling.getY() + d1 / d3;
				double d9 = emberling.getZ() + d2 / d3 * 2.0D;
				double d10 = entitylookhelper.getLookPosX();
				double d11 = entitylookhelper.getLookPosY();
				double d12 = entitylookhelper.getLookPosZ();

				if (!entitylookhelper.getIsLooking()) {
					d10 = d7;
					d11 = d8;
					d12 = d9;
				}

				emberling.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
			} else {
				emberling.setAIMoveSpeed(0.0F);
			}
		}
	}

	@Override
	public CompoundNBT returnToRing(UUID userId) {
		return this.save(new CompoundNBT());
	}

	@Override
	public boolean returnFromRing(Entity user, CompoundNBT nbt) {
		double prevX = this.getX();
		double prevY = this.getY();
		double prevZ = this.getZ();
		float prevYaw = this.yRot;
		float prevPitch = this.xRot;
		this.readFromNBT(nbt);
		this.moveTo(prevX, prevY, prevZ, prevYaw, prevPitch);
		if(!this.isEntityAlive()) {
			//Revivd by animator
			this.setHealth(this.getMaxHealth());
		}
		this.world.spawnEntity(this);
		return true;
	}

	@Override
	public boolean shouldReturnOnUnload(boolean isOwnerLoggedIn) {
		return IRingOfGatheringMinion.super.shouldReturnOnUnload(isOwnerLoggedIn) && !this.isSitting();
	}

	@Override
	public UUID getRingOwnerId() {
		return this.getOwnerId();
	}
}