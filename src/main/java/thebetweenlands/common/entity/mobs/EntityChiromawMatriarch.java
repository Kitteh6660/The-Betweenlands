package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityPreventUnmount;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.entity.projectiles.EntityChiromawDroppings;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationChiromawMatriarchNest;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class EntityChiromawMatriarch extends EntityFlyingMob implements IEntityBL, IEntityPreventUnmount {
	
	private static final DataParameter<Boolean> IS_NESTING = EntityDataManager.define(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> RETURN_TO_NEST = EntityDataManager.define(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_LANDING = EntityDataManager.define(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_SPINNING = EntityDataManager.define(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	public int broodCount;
	public double pickupHeight;
	public int droppingTimer; // makes sure player is always dropped
	@Nullable
	private BlockPos boundOrigin;
	public float previousSpinAngle, spinAngle;
	public float animTime, prevAnimTime;
	public float flapSpeed = 0.5f;
	public int flapTicks;
	public int landingAbortTime;
	private boolean returnFast; //whether matriarch should return to nest fast, e.g. when player is near it

	public final ControlledAnimation landingTimer = new ControlledAnimation(10);
	public final ControlledAnimation nestingTimer = new ControlledAnimation(20);
	public final ControlledAnimation spinningTimer = new ControlledAnimation(10);

	public EntityChiromawMatriarch(World world) {
		super(world);
		setSize(1.75F, 2F);
		setIsNesting(false);
		experienceValue = 500;
		moveControl = new FlightMoveHelper(this);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_NESTING, false);
		this.entityData.define(RETURN_TO_NEST, false);
		this.entityData.define(IS_LANDING, false);
		this.entityData.define(IS_SPINNING, false);
	}

	@Override
	protected void registerGoals() {
		tasks.addGoal(0, new EntityAISwimming(this));
		tasks.addGoal(1, new EntityChiromawMatriarch.AIPickUpAndDropAttack(this, 2.0D, true));
		tasks.addGoal(2, new EntityChiromawMatriarch.AIReturnToNest(this, 1.25D, 2.15D));
		tasks.addGoal(3, new EntityChiromawMatriarch.AIMoveRandom(this));
		tasks.addGoal(4, new EntityChiromawMatriarch.AIChangeNest(this));
		tasks.addGoal(5, new EntityChiromawMatriarch.AIPoop(this));
		targetTasks.addGoal(1, new AIFindNearestTarget<PlayerEntity>(this, PlayerEntity.class, true, 16D).setUnseenMemoryTicks(160));
		targetTasks.addGoal(1, new AIFindNearestTarget<EntityVillager>(this, EntityVillager.class, true, 16D).setUnseenMemoryTicks(160));
		targetTasks.addGoal(1, new AIFindNearestTarget<EntityChiromawGreeblingRider>(this, EntityChiromawGreeblingRider.class, true, 16D).setUnseenMemoryTicks(160));
	}

	@Override
	public void tick() {
		super.tick();

		if (!level.isClientSide() && getAttackTarget() == null) {
			if (level.getDifficulty() == EnumDifficulty.PEACEFUL)
				remove();
			if (getBroodCount() <= 0) {
				setReturnToNest(!getReturnToNest());
				setBroodCount(240);
			}
			if (getBroodCount() > 0)
				setBroodCount(getBroodCount() - 1);
		}

		if (!level.isClientSide() && getAttackTarget() != null) {
			if (getIsNesting())
				setIsNesting(false);
			if (getReturnToNest())
				setReturnToNest(false);
			if(getBroodCount() < 240)
				setBroodCount(240);
		}

		if (isJumping && isInWater()) {
			getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
		}

		if(!this.level.isClientSide() && this.tickCount % 20 == 0 && this.getBoundOrigin() != null) {
			this.setBoundOrigin(this.world.getHeight(this.getBoundOrigin()));
		}

		if (!level.isClientSide() && getIsLanding()) {
			if (landingAbortTime > 0 )
				landingAbortTime--;
			if (landingAbortTime <= 0) {
				if (getIsLanding())
					setIsLanding(false);
				if (getReturnToNest())
					setReturnToNest(false);
				if (getBroodCount() < 240)
					setBroodCount(240);
			}
		}
		if (getBroodCount() > 0 && getAttackTarget() == null && getReturnToNest() && !getIsNesting()) {
			if(getNestBox() != null && getBoundingBox().intersects(getNestBox())) {
				double d0 = getBoundOrigin().getX() + 0.5D - posX;
				double d1 = getBoundOrigin().getY() - posY;
				double d2 = getBoundOrigin().getZ() + 0.5D - posZ;
				motionX += (Math.signum(d0) - motionX) * 0.0000000003D;
				motionY += (Math.signum(d1) - motionY) * 0.03125D;
				motionZ += (Math.signum(d2) - motionZ) * 0.0000000003D;

				if (getBoundingBox().minY > getNestBox().minY + 0.0625D && Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) < 0.1D) {
					if (!level.isClientSide()) {
						if (!getIsLanding()) {
							setIsLanding(true);
							landingAbortTime = 40;
						}
					}
				}
				if (getBoundingBox().minY <= getNestBox().minY + 0.0625D) {
					if (!level.isClientSide()) {
						if(getIsLanding()) {
							setIsLanding(false);
							level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_LAND, SoundCategory.HOSTILE, 0.5F, 1F + (level.rand.nextFloat() - level.rand.nextFloat()) * 0.8F);
						}
						if(!getIsNesting())
							setIsNesting(true);
						setPosition(getBoundOrigin().getX() + 0.5D, getBoundOrigin().getY(), getBoundOrigin().getZ() + 0.5D);
					}
				}
			}
		}

		if(level.getBlockState(getPosition().below()).isSideSolid(level, getPosition().below(), Direction.UP)) {
			if(!getIsLanding() && !getIsNesting())
				getMoveHelper().setMoveTo(posX, posY + 2, posZ, 1.0D);
		}

		if (getIsSpinning()) {
			if (level.isClientSide()) {
				previousSpinAngle = spinAngle;
				if (spinAngle <= 330F)
					spinAngle += 30F;
				if (spinAngle >= 360F)
					spinAngle = 0F;
			}

			//motionX *= 000001F;
			motionY += 0.05F;
			//motionZ *= 000001F;
		}

		if(this.level.isClientSide()) {
			nestingTimer.updateTimer();
			landingTimer.updateTimer();
			spinningTimer.updateTimer();

			if(this.getIsNesting()) {
				nestingTimer.increaseTimer();
				landingTimer.decreaseTimer();
				spinningTimer.decreaseTimer();
			} else if(this.getIsLanding()) {
				landingTimer.increaseTimer();
				nestingTimer.decreaseTimer();
				spinningTimer.decreaseTimer();
			} else if(this.getIsSpinning()) {
				spinningTimer.increaseTimer();
				landingTimer.decreaseTimer();
				nestingTimer.decreaseTimer();
			} else {
				landingTimer.decreaseTimer();
				nestingTimer.decreaseTimer();
				spinningTimer.decreaseTimer();
			}
		}
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		if (getIsNesting()) {
			motionX = 0;
			motionY += y;
			motionZ = 0;
			isAirBorne = false;
		} else {
			motionX += x;
			motionY += y;
			motionZ += z;
			isAirBorne = true;
		}
	}

	//moved outside of AI because it may be useful
	private boolean homeisOccupied() {
		if (getNestBox() != null) {
			Entity entity = null;
			List<Entity> entityList = level.getEntitiesWithinAABBExcludingEntity(this, getNestBox());
			if (!entityList.isEmpty())
				for (int entityCount = 0; entityCount < entityList.size(); entityCount++) {
					entity = entityList.get(entityCount);
					if (entity != null && entity instanceof EntityChiromawMatriarch)
						return true;
				}
		}
		return false;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (level.isClientSide()) {
			this.flapTicks++;

			if (!isSilent() && !getIsNesting()) {
				float flapAngle1 = MathHelper.cos(this.flapTicks * this.flapSpeed);
				float flapAngle2 = MathHelper.cos((this.flapTicks + 1) * this.flapSpeed);
				if(flapAngle1 <= 0.3f && flapAngle2 > 0.3f) {
					world.playLocalSound(posX, posY, posZ, getFlySound(), getSoundCategory(), getIsLanding() ? 0.25F : 0.5F, getIsLanding() ? 1.5F + rand.nextFloat() * 0.3F : 0.8F + rand.nextFloat() * 0.3F, false);
				}
			}

			prevAnimTime = animTime;
			float flaptimer = 0.079F;
			if (getIsLanding())
				animTime += flaptimer * 2F;
			if (getIsNesting())
				animTime = 0;
			else
				animTime += flaptimer;

			if (isAIDisabled())
				animTime = 0.5F;

		}
	}

	@Override
	protected void updateAITasks() {
		if (getIsNesting()) {
			if (!level.isClientSide()) {
				if (!level.getBlockState(new BlockPos(posX, posY - 1, posZ)).isNormalCube()) {
					setIsNesting(false);
				} else if (getAttackTarget() != null) {
					setIsNesting(false);
				}
			}
		}
	}

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof LivingEntity) {
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0.6D;
			double offSetZ = Math.cos(a) * 0.6D;
			entity.setPosition(posX - offSetX, posY - entity.height + MathHelper.sin((tickCount) * 0.5F) * 0.3F, posZ - offSetZ);
		}
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public boolean canPassengerSteer() {
		return false; //TODO different rider cases
	}

	public boolean getReturnToNest() {
		return entityData.get(RETURN_TO_NEST);
	}

	public void setReturnToNest(boolean broody) {
		entityData.set(RETURN_TO_NEST, broody);
	}

	public boolean getIsLanding() {
		return entityData.get(IS_LANDING);
	}

	public void setIsLanding(boolean landing) {
		entityData.set(IS_LANDING, landing);
	}

	public void setBroodCount(int count) {
		broodCount = count;
	}

	public int getBroodCount() {
		return broodCount;
	}

	public void setDroppingTimer(int count) {
		droppingTimer = count;
	}

	public int getDroppingTimer() {
		return droppingTimer;
	}

	public boolean getIsNesting() {
		return entityData.get(IS_NESTING);
	}

	public void setIsNesting(boolean nesting) {
		entityData.set(IS_NESTING, nesting);
	}

	public boolean getIsSpinning() {
		return entityData.get(IS_SPINNING);
	}

	public void setIsSpinning(boolean spinning) {
		entityData.set(IS_SPINNING, spinning);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CHIROMAW_MATRIARCH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CHIROMAW_MATRIARCH_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_DEATH;
	}

	protected SoundEvent getFlySound() {
		return SoundRegistry.CHIROMAW_MATRIARCH_FLAP;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(200.0D);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(64.0D);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.0D);
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.095D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return EntityChiromawMatriarch.class != entity;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return 0.5F;
	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if(!level.isClientSide())
			setBoundOrigin(getPosition());
		return livingdata;
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		super.readEntityFromNBT(compound);
		if (compound.contains("BoundX")) 
			boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
		setBroodCount(compound.getInt("BroodCount"));
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		super.writeEntityToNBT(compound);
		if (boundOrigin != null) {
			compound.putInt("BoundX", boundOrigin.getX());
			compound.putInt("BoundY", boundOrigin.getY());
			compound.putInt("BoundZ", boundOrigin.getZ());
		}
		compound.putInt("BroodCount", getBroodCount());
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN))
			return false;

		if (source instanceof EntityDamageSourceIndirect) {
			if (source.getTrueSource() == this)
				return false;
			if (source.getTrueSource() instanceof LivingEntity) {
				setRevengeTarget((LivingEntity) source.getTrueSource());
			}
		}

		if (source.getTrueSource() instanceof PlayerEntity)
			if (isBeingRidden() && isPassenger(source.getTrueSource()))
				if(!level.isClientSide())
					setDroppingTimer(0);

		return super.hurt(source, damage);
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 16384 * getRenderDistanceWeight();
	}

	@Nullable
	public BlockPos getBoundOrigin() {
		return boundOrigin;
	}

	public void setBoundOrigin(@Nullable BlockPos boundOriginIn) {
		boundOrigin = boundOriginIn;
	}

	@Nullable
	public AxisAlignedBB getNestBox() {
		return boundOrigin != null ? new AxisAlignedBB(boundOrigin, boundOrigin.above(5)).inflate(0.0625D, 0F, 0.0625D) : null;
	}

	public class AIPoop extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;

		public AIPoop(EntityChiromawMatriarch large_chiromaw) {
			setMutexBits(0);
			largeChiromaw = large_chiromaw;
		}

		@Override
		public boolean canUse() {
			return !largeChiromaw.getIsNesting() && largeChiromaw.rand.nextInt(10) == 0; // frequency of random pooping
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}

		@Override
		public void updateTask() {
			checkForPoopTarget();
		}

		private void checkForPoopTarget() {
			int distanceToSurface = MathHelper.floor(largeChiromaw.getY()) - largeChiromaw.world.getHeight(new BlockPos(largeChiromaw)).getY();
			List<BlockPos> placeToPoop = new ArrayList<>();
			if (distanceToSurface >= 16) {
				AxisAlignedBB underBox = largeChiromaw.getBoundingBox().inflate(0.625D, distanceToSurface, 0.625D).offset(0, -distanceToSurface / 2.0D, 0);
				if (!getPoopTarget(world, underBox).isEmpty()) {
					PlayerEntity player = getPoopTarget(world, underBox).get(0);
					if (player != null) {
						for (BlockPos posDrop : BlockPos.getAllInBox(player.getPosition().add(-1, 0, -1), player.getPosition().add(1, 2, 1)))
							if (world.isEmptyBlock(posDrop) && world.canSeeSky(posDrop))
								placeToPoop.add(posDrop);
						if (!placeToPoop.isEmpty()) {
							Collections.shuffle(placeToPoop);
							BlockPos posPoop = placeToPoop.get(0);
							EntityChiromawDroppings poopEntity = new EntityChiromawDroppings(world, largeChiromaw, posPoop.getX() + 0.5D, largeChiromaw.getPosition().getY(), posPoop.getZ() + 0.5D);
							world.addFreshEntity(poopEntity);
							largeChiromaw.level.playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_POOP, SoundCategory.HOSTILE, 0.5F, 1F + (largeChiromaw.level.rand.nextFloat() - largeChiromaw.level.rand.nextFloat()) * 0.8F);
						}
					}
				}
			}
		}

		public List<PlayerEntity> getPoopTarget(World world, AxisAlignedBB underBox) {
			return world.<PlayerEntity>getEntitiesOfClass(PlayerEntity.class, underBox, EntitySelectors.IS_ALIVE);
		}
	}

	class AIFindNearestTarget<T extends LivingEntity> extends EntityAINearestAttackableTarget<T> {
		protected double baseRange;
		protected double revengeRange;

		public AIFindNearestTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, double rangeIn) {
			super(creature, classTarget, checkSight);
			baseRange = revengeRange = rangeIn;
		}

		public AIFindNearestTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate <? super T > targetSelector) {
			super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
		}

		@Override
		public boolean canUse() {
			if (super.canUse()) {
				if (targetEntity != null) {
					double distance = taskOwner.getDistanceSq(targetEntity);
					if (distance <= 256.0D)
						taskOwner.level.playSound(null, taskOwner.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_ROAR, SoundCategory.HOSTILE, 2F, 1.5F);
				}
				return true;
			}
			return false;
		}

		@Override
		protected AxisAlignedBB getTargetableArea(double targetDistance) {
			return taskOwner.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
		}

		@Override
		protected double getTargetDistance() {
			if(getRevengeTarget() != null) {
				setAttackTarget(getRevengeTarget());
				revengeRange = getDistance(getRevengeTarget());
			}
			return Math.max(baseRange, revengeRange);
		}
	}

	class AIPickUpAndDropAttack extends EntityAIBase {
		World world;
		protected int attackTick;
		double speedTowardsTarget;
		boolean longMemory;
		Path path;
		private int delayCounter;
		private double targetX;
		private double targetY;
		private double targetZ;
		protected final int attackInterval = 20;
		private int failedPathFindingPenalty = 0;
		private boolean canPenalize = false;
		private final EntityChiromawMatriarch largeChiromaw;
		float rotation;

		public AIPickUpAndDropAttack(EntityChiromawMatriarch large_chiromaw, double speedIn, boolean useLongMemory) {
			largeChiromaw = large_chiromaw;
			world = largeChiromaw.world;
			speedTowardsTarget = speedIn;
			longMemory = useLongMemory;
			setMutexBits(1);
		}

		@Override
		public boolean canUse() {
			LivingEntity entitylivingbase = largeChiromaw.getAttackTarget();
			//if (largeChiromaw.getReturnToNest())
			//	return false;
			if (entitylivingbase == null)
				return false;
			else if (!entitylivingbase.isEntityAlive())
				return false;
			else {
				if (canPenalize) {
					if (--delayCounter <= 0) {
						path = largeChiromaw.getNavigation().getPathToEntityLiving(entitylivingbase);
						delayCounter = 4 + largeChiromaw.getRandom().nextInt(7);
						return path != null;
					} else
						return true;
				}
				path = largeChiromaw.getNavigation().getPathToEntityLiving(entitylivingbase);
				if (path != null)
					return true;
				else
					return getAttackReachSqr(entitylivingbase) >= largeChiromaw.getDistanceSq(entitylivingbase.getX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getZ());
			}
		}

		@Override
		public boolean canContinueToUse() {
			LivingEntity entitylivingbase = largeChiromaw.getAttackTarget();
			if (entitylivingbase == null)
				return false;
			if (largeChiromaw.isBeingRidden())
				return false;
			if (!entitylivingbase.isEntityAlive())
				return false;
			if (!longMemory)
				return !largeChiromaw.getNavigation().noPath();
			if (!largeChiromaw.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)))
				return false;
			return !(entitylivingbase instanceof PlayerEntity) || !((PlayerEntity)entitylivingbase).isSpectator() && !((PlayerEntity)entitylivingbase).isCreative();
		}

		@Override
		public void start() {
			largeChiromaw.getNavigation().setPath(path, speedTowardsTarget);
			delayCounter = 0;
		}

		@Override
		public void stop() {
			LivingEntity entitylivingbase = largeChiromaw.getAttackTarget();
			if (entitylivingbase instanceof PlayerEntity && (((PlayerEntity)entitylivingbase).isSpectator() || ((PlayerEntity)entitylivingbase).isCreative()))
				largeChiromaw.setAttackTarget((LivingEntity)null);
			largeChiromaw.getNavigation().clearPath();
			rotation = 0;
			setIsSpinning(false);
		}

		@Override
		public void updateTask() {
			LivingEntity entitylivingbase = largeChiromaw.getAttackTarget();
			if(entitylivingbase == null) {
				return;
			}
			if(!largeChiromaw.getIsSpinning())
				largeChiromaw.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
			double distToEnemySqr = largeChiromaw.getDistanceSq(entitylivingbase.getX(), entitylivingbase.getBoundingBox().minY, entitylivingbase.getZ());
			--delayCounter;
			if (!largeChiromaw.isBeingRidden()) {
				if ((longMemory || largeChiromaw.getSensing().canSee(entitylivingbase)) && delayCounter <= 0 && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D || entitylivingbase.getDistanceSq(targetX, targetY, targetZ) >= 1.0D || largeChiromaw.getRandom().nextFloat() < 0.05F)) {
					targetX = entitylivingbase.getX();
					targetY = entitylivingbase.getBoundingBox().minY;
					targetZ = entitylivingbase.getZ();
					delayCounter = 4 + largeChiromaw.getRandom().nextInt(7);

					if (canPenalize) {
						delayCounter += failedPathFindingPenalty;
						if (largeChiromaw.getNavigation().getPath() != null) {
							net.minecraft.pathfinding.PathPoint finalPathPoint = largeChiromaw.getNavigation().getPath().getFinalPathPoint();
							if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
								failedPathFindingPenalty = 0;
							else
								failedPathFindingPenalty += 10;
						}
						else
							failedPathFindingPenalty += 10;
					}
					if (distToEnemySqr > 1024.0D)
						delayCounter += 10;
					else if (distToEnemySqr > 256.0D)
						delayCounter += 5;
					if (!largeChiromaw.getNavigation().tryMoveToEntityLiving(entitylivingbase, speedTowardsTarget))
						delayCounter += 15;
				}
				if (largeChiromaw.rand.nextBoolean())
					checkAndPerformDropAttack(entitylivingbase, distToEnemySqr);
				else
					checkAndPerformSpinAttack(entitylivingbase, distToEnemySqr);
			}

			attackTick = Math.max(attackTick - 1, 0);

			if (largeChiromaw.isBeingRidden()) {
				if (entitylivingbase != null && !world.isEmptyBlock(largeChiromaw.getPosition().below(3)) || largeChiromaw.getPosition().getY() < largeChiromaw.pickupHeight + 16D) {
					largeChiromaw.getNavigation().clearPath();
					Vector3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(largeChiromaw, 16, 10, new Vector3d(largeChiromaw.getBoundOrigin().getX() + 0.5D, largeChiromaw.getBoundOrigin().getY() + 0.5D, largeChiromaw.getBoundOrigin().getZ() + 0.5D));
					if(vec3d != null)
						largeChiromaw.getNavigation().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 2D);
					largeChiromaw.motionY += 0.05D;
				}

				if (!level.isClientSide() &&  (largeChiromaw.getY() >= largeChiromaw.pickupHeight + 16D || largeChiromaw.getDroppingTimer() <= 0 || !level.isClientSide() && world.isSideSolid(new BlockPos (MathHelper.floor(largeChiromaw.getX()), MathHelper.floor(largeChiromaw.getY() + 1D), MathHelper.floor(largeChiromaw.getZ())), Direction.DOWN))) {
					largeChiromaw.setReturnToNest(true);
					largeChiromaw.removePassengers();
					largeChiromaw.level.playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_RELEASE, SoundCategory.HOSTILE, 0.5F, 1F + (largeChiromaw.level.rand.nextFloat() - largeChiromaw.level.rand.nextFloat()) * 0.8F);
				}

				if (largeChiromaw.getDroppingTimer() >= 0) {
					if(attackTick <= 0) {
						largeChiromaw.attackEntityAsMob(entitylivingbase);
						attackTick = 20;
					}
				}

				if (largeChiromaw.getDroppingTimer() >= 0)
					largeChiromaw.setDroppingTimer(getDroppingTimer() - 1);
			}

			if(largeChiromaw.getIsSpinning()) {
				rotation += 30;
				if (rotation%30 == 0) {
					Vector3d targetVector = new Vector3d(entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ());
					Vector3d chiromawVectorToTarget = new Vector3d(largeChiromaw.getX(), largeChiromaw.getY(), largeChiromaw.getZ()).subtract(targetVector);
					double height = chiromawVectorToTarget.y;
					double distance = chiromawVectorToTarget.length();
					double angle = MathHelper.atan2(height, distance) * (180D / Math.PI);
					EntityBLArrow arrow = new EntityBLArrow(world, largeChiromaw);
					arrow.setType(EnumArrowType.CHIROMAW_BARB);
					arrow.shoot(largeChiromaw, (float)angle, rotation, 1.5F, 0.5F, 0.5F);
					largeChiromaw.level.playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.HOSTILE, 0.5F, 1F + (largeChiromaw.level.rand.nextFloat() - largeChiromaw.level.rand.nextFloat()) * 0.8F);
					world.addFreshEntity(arrow);
				}
				if(rotation >= 720) {
					rotation = 0;
					setIsSpinning(false);
				}
			}
		}

		private void checkAndPerformSpinAttack(LivingEntity entitylivingbase, double distToEnemySqr) {
			if (distToEnemySqr >= 36.0D && distToEnemySqr <= 100.0D && attackTick <= 0) {
				largeChiromaw.level.playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_ROAR, SoundCategory.HOSTILE, 1F, 1F + (largeChiromaw.level.rand.nextFloat() - largeChiromaw.level.rand.nextFloat()) * 0.8F);
				attackTick = 40;
				largeChiromaw.setIsSpinning(true);
			}
			else
				return;
		}

		protected void checkAndPerformDropAttack(LivingEntity enemy, double distToEnemySqr) {
			double attackReachSq = getAttackReachSqr(enemy);
			if (distToEnemySqr <= attackReachSq && attackTick <= 0)  {
				attackTick = 20;
				largeChiromaw.jump();
				largeChiromaw.swing(Hand.MAIN_HAND);
				largeChiromaw.attackEntityAsMob(enemy);
				largeChiromaw.level.playSound(null, largeChiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_GRAB, SoundCategory.HOSTILE, 0.5F, 1F + (largeChiromaw.level.rand.nextFloat() - largeChiromaw.level.rand.nextFloat()) * 0.8F);
				enemy.startRiding(largeChiromaw, true);
				largeChiromaw.pickupHeight = largeChiromaw.getY();
				largeChiromaw.setDroppingTimer(120);
			}
		}

		protected double getAttackReachSqr(LivingEntity attackTarget)  {
			return (double)(largeChiromaw.width * 2.0F * largeChiromaw.width * 2.0F + attackTarget.width);
		}
	}

	class AIMoveRandom extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;

		public AIMoveRandom(EntityChiromawMatriarch large_chiromaw) {
			setMutexBits(1);
			largeChiromaw = large_chiromaw;
		}

		@Override
		public boolean canUse() {
			return !largeChiromaw.getReturnToNest() && !largeChiromaw.getMoveHelper().isUpdating() && largeChiromaw.rand.nextInt(10) == 0;
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}

		@Override
		public void updateTask() {
			BlockPos blockpos = largeChiromaw.getBoundOrigin();

			if (blockpos == null) {
				blockpos = new BlockPos(largeChiromaw);
			}

			for (int i = 0; i < 3; ++i) {
				BlockPos blockpos1 = blockpos.add(largeChiromaw.rand.nextInt(33) - 16, largeChiromaw.rand.nextInt(17) - 8, largeChiromaw.rand.nextInt(33) - 16);

				if (largeChiromaw.world.isEmptyBlock(blockpos1)) {
					largeChiromaw.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 3D);

					if (largeChiromaw.getAttackTarget() == null) {
						largeChiromaw.getLookHelper().setLookPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}
					break;
				}
			}
		}
	}

	class AIChangeNest extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;

		protected float maxRangeSq = 180 * 180;

		private int checkTimer = 0;

		private int idleChangeCounter = 0;

		public AIChangeNest(EntityChiromawMatriarch large_chiromaw) {
			setMutexBits(0);
			largeChiromaw = large_chiromaw;
		}

		private Vector3d getCenter(AxisAlignedBB aabb) {
			return new Vector3d(
					aabb.minX + (aabb.maxX - aabb.minX) * 0.5D,
					aabb.minY + (aabb.maxY - aabb.minY) * 0.5D,
					aabb.minZ + (aabb.maxZ - aabb.minZ) * 0.5D);
		}

		@Override
		public boolean canUse() {
			if(!largeChiromaw.getReturnToNest() && largeChiromaw.getAttackTarget() == null && checkTimer-- <= 0) {
				checkTimer = 10;

				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(largeChiromaw.world);

				List<LocationStorage> priorityNests = new ArrayList<>();
				List<LocationStorage> otherNests = new ArrayList<>();

				for(ILocalStorage localStorage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {

					if(localStorage instanceof LocationStorage  && localStorage.getBoundingBox() != null) {

						LocationStorage location = (LocationStorage) localStorage;

						Vector3d center = this.getCenter(location.getBoundingBox());

						if(center.squareDistanceTo(largeChiromaw.getX(), largeChiromaw.getY(), largeChiromaw.getZ()) < maxRangeSq && (location.getType() == EnumLocationType.FLOATING_ISLAND || location.getType() == EnumLocationType.CHIROMAW_MATRIARCH_NEST) &&
								(largeChiromaw.rand.nextInt(15) == 0 || largeChiromaw.world.getEntitiesOfClass(this.largeChiromaw.getClass(), location.getBoundingBox()).isEmpty() /*Avoid already occupied places*/)) {
							if(location instanceof LocationChiromawMatriarchNest && largeChiromaw.world.getClosestPlayer(center.x, center.y, center.z, 32, entity -> !((PlayerEntity) entity).isCreative() && !((PlayerEntity) entity).isSpectator()) != null) {
								//Prioritise nests with players nearby
								priorityNests.add(location);
							} else {
								//Prioritise nests over islands
								if(location instanceof LocationChiromawMatriarchNest || largeChiromaw.rand.nextInt(3) == 0) {
									otherNests.add(location);
								}
							}
						}
					}

				}

				LocationStorage nest = null;
				if(!priorityNests.isEmpty()) {
					Collections.shuffle(priorityNests);
					nest = priorityNests.get(0);
					largeChiromaw.returnFast = true;
				} else if(!otherNests.isEmpty() && idleChangeCounter++ >= 8) {
					idleChangeCounter = 0;
					Collections.shuffle(otherNests);
					nest = otherNests.get(0);
					largeChiromaw.returnFast = false;
				}

				if(nest != null) {
					BlockPos nestPos;
					if(nest instanceof LocationChiromawMatriarchNest) {
						nestPos = ((LocationChiromawMatriarchNest) nest).getNestPosition();
						if(nestPos == null) {
							nestPos = new BlockPos(this.getCenter(nest.getBoundingBox()));
						}
					} else {
						AxisAlignedBB locationBB = nest.getBoundingBox();
						if(locationBB != null) {
							nestPos = new BlockPos(this.getCenter(locationBB));
						} else {
							nestPos = null;
						}
					}

					if(nestPos != null) {
						nestPos = largeChiromaw.world.getHeight(nestPos);

						if(!nestPos.equals(largeChiromaw.getBoundOrigin())) {
							largeChiromaw.setBoundOrigin(nestPos);
							largeChiromaw.setIsNesting(false);
							largeChiromaw.setReturnToNest(true);
							return true;
						}
					}
				}
			}

			return false;
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}
	}

	class AIReturnToNest extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;
		protected double x;
		protected double y;
		protected double z;
		private final double speed, speedHigh;
		private int timeToRecalcPath;

		public AIReturnToNest(EntityChiromawMatriarch large_chiromaw, double speedIn, double speedHighIn) {
			largeChiromaw = large_chiromaw;
			speed = speedIn;
			speedHigh = speedHighIn;
			setMutexBits(1);
		}

		@Override
		public boolean canUse() {
			LivingEntity entitylivingbase = largeChiromaw.getAttackTarget();
			if (entitylivingbase != null)
				return false;
			if (largeChiromaw.homeisOccupied())
				return false;
			if (largeChiromaw.getReturnToNest() && !largeChiromaw.getIsNesting()) {
				Vector3d nestLocation = getNestPosition();

				if (nestLocation == null) {
					return false;
				} else {
					x = nestLocation.x;
					y = nestLocation.y;
					z = nestLocation.z;
					return true;
				}
			}
			return false;
		}

		@Nullable
		protected Vector3d getNestPosition() {
			BlockPos home = getBoundOrigin();
			home = largeChiromaw.world.getHeight(home);
			return new Vector3d(home.getX() + 0.5D, home.getY(), home.getZ() + 0.5D);
		}

		@Override
		public void start() {
			this.moveTowardsNest();
		}

		@Override
		public boolean canContinueToUse() {
			return largeChiromaw.getReturnToNest() && !largeChiromaw.getIsNesting() && largeChiromaw.getAttackTarget() == null && !largeChiromaw.homeisOccupied();
		}

		@Override
		public void updateTask() {
			if (--this.timeToRecalcPath <= 0) {
				if(!this.moveTowardsNest()) {
					this.timeToRecalcPath = 40;
				} else {
					this.timeToRecalcPath = 3;
				}
			}
		}

		private boolean moveTowardsNest() {
			if(largeChiromaw.getDistanceSq(x, y, z) < 6) {
				largeChiromaw.getMoveHelper().setMoveTo(x, y, z, speed * 0.25f);
				largeChiromaw.returnFast = false;
				return true;
			}

			PathNavigate navigator = largeChiromaw.getNavigation();
			if(!navigator.tryMoveToXYZ(x, y, z, largeChiromaw.returnFast ? speedHigh : speed)) {
				Vector3d target = new Vector3d(x, y, z);
				target = this.findNextPointTowards(12, 5, target);
				if(target != null && !navigator.tryMoveToXYZ(target.x, target.y, target.z, largeChiromaw.returnFast ? speedHigh : speed)) {
					largeChiromaw.getMoveHelper().setMoveTo(target.x, target.y, target.z, largeChiromaw.returnFast ? speedHigh : speed);
				}
				return false;
			}
			return true;
		}

		@Nullable
		private Vector3d findNextPointTowards(int xz, int y, Vector3d target) {
			Vector3d offset = target.subtract(largeChiromaw.getDeltaMovement()).normalize().scale(8 + largeChiromaw.rand.nextFloat() * 6);
			Vector3d side = offset.cross(new Vector3d(0, 1, 0)).normalize();
			Vector3d up = side.cross(offset).normalize();
			return largeChiromaw.getDeltaMovement().add(offset).add(side.scale((largeChiromaw.rand.nextFloat() - 0.5f) * 8)).add(up.scale((largeChiromaw.rand.nextFloat() - 0.5f) * 8));
		}
	}

	@Override
	public boolean isUnmountBlocked(PlayerEntity rider) {
		return !this.level.isClientSide() && this.getDroppingTimer() > 0;
	}
}
