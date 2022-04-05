package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
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
import thebetweenlands.api.entity.IPullerEntity;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.entity.projectiles.EntityBetweenstonePebble;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawGreeblingRider extends EntityChiromaw {
	private static final DataParameter<Boolean> IS_SHOOTING = EntityDataManager.defineId(EntityChiromawGreeblingRider.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> RELOAD_TIMER = EntityDataManager.defineId(EntityChiromawGreeblingRider.class, DataSerializers.INT);
	public boolean playPullSound;
	
	public EntityChiromawGreeblingRider(World world) {
		super(world);
		setSize(0.7F, 0.9F);
		setIsHanging(false);

		this.moveControl = new FlightMoveHelper(this);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
	}

	@Override
	protected void registerGoals() {
		tasks.addGoal(0, new EntityAISwimming(this));
		tasks.addGoal(1, new EntityChiromawGreeblingRider.EntityAISlingshotAttack(this));
		tasks.addGoal(2, new EntityChiromawGreeblingRider.EntityAIMoveTowardsTargetWithDistance(this, 1.5D, 8, 128));
		tasks.addGoal(3, new EntityAIFlyingWander(this, 0.75D, 5));
        targetTasks.addGoal(1, new EntityAIHurtByTarget(this, false));
		targetTasks.addGoal(2, new EntityAIFindNearestTarget<LivingEntity>(this, LivingEntity.class, 10, true, false, e -> e instanceof IPullerEntity, 6).setUnseenMemoryTicks(160));
		targetTasks.addGoal(3, new EntityAIFindNearestTarget<LivingEntity>(this, LivingEntity.class, 10, true, false, e -> e instanceof EntityChiromawMatriarch, 0).setUnseenMemoryTicks(160));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		this.entityData.define(IS_SHOOTING, false);
		this.entityData.define(RELOAD_TIMER, 0);
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	@Override
	public void tick() {
		super.tick();
		// WIP Temp
		if (!level.isClientSide()) {
			if (getAttackTarget() != null) {
				if (getReloadTimer() < 90 && !getIsShooting()) {
					setReloadTimer(Math.min(90, getReloadTimer() + 2));
				}
				if (getReloadTimer() >= 90 && getIsShooting() && getReloadTimer() < 100) {
					setReloadTimer(Math.min(102, getReloadTimer() + 4));
				}
			} else {
				if (getReloadTimer() > 0 && !getIsShooting()) {
					setReloadTimer(Math.max(0, getReloadTimer() - 2));
				}
			}

			if (getReloadTimer() <= 0)
				playPullSound = true;

			if (isPulling())
				if (playPullSound) {
					level.playSound(null, getPosition(), SoundRegistry.SLINGSHOT_CHARGE, SoundCategory.HOSTILE, 1F, 1F);
					playPullSound = false;
				}
		}
	}
	
	@Override
	protected void onDeathUpdate() {
		super.onDeathUpdate();
		
		if(!this.level.isClientSide() && this.isDead) {
			EntityGreeblingVolarpadFloater floater = new EntityGreeblingVolarpadFloater(this.world, posX, posY, posZ);
			this.world.addFreshEntity(floater);
		}
	}
	
	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		if (getIsHanging())
			if (!this.level.isClientSide())
				setIsHanging(false);
	}

	public boolean getIsShooting() {
		return entityData.get(IS_SHOOTING);
	}

	public void setIsShooting(boolean shooting) {
		entityData.set(IS_SHOOTING, shooting);
	}
	
	public int getReloadTimer() {
		return entityData.get(RELOAD_TIMER);
	}

	public void setReloadTimer(int timer) {
		entityData.set(RELOAD_TIMER, timer);
	}

	public boolean isPulling() {
		return entityData.get(RELOAD_TIMER) > 0 && entityData.get(RELOAD_TIMER) < 90 ;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CHIROMAW;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CHIROMAW_GREEBLING_RIDER_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundRegistry.FLYING_FIEND_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FLYING_FIEND_DEATH;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(10.0D);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30.0D);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.095D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
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
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(ItemRegistry.SIMPLE_SLINGSHOT));
		return livingdata;
	}

	static class EntityAIFindNearestTarget<T extends LivingEntity> extends EntityAINearestAttackableTarget<T> {

		protected double minHeight;
		
		public EntityAIFindNearestTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, double minHeight) {
			super(creature, classTarget, checkSight);
			this.minHeight = minHeight;
		}

		public EntityAIFindNearestTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate <? super T > targetSelector, double minHeight) {
			super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
			this.minHeight = minHeight;
		}

		@Override
		public boolean canUse() {
			if (super.canUse()) {
				if (targetEntity != null) {
					if(minHeight > 0) {
						Entity checkEntity = targetEntity;
						
						if(checkEntity instanceof IPullerEntity) {
							EntityDraeton carriage = ((IPullerEntity) checkEntity).getCarriage();
							if(carriage != null) {
								checkEntity = carriage;
							}
						}
						
						BlockPos surface = taskOwner.world.getHeight(new BlockPos(checkEntity));
						if(checkEntity.getY() - surface.getY() < minHeight) {
							targetEntity = null;
							return false;
						}
					}
					
					double distance = taskOwner.getDistanceSq(targetEntity);
					
					if (distance <= 576.0D) {
						taskOwner.level.playSound(null, taskOwner.getPosition(), SoundRegistry.GREEBLING_HEY, SoundCategory.HOSTILE, 0.5F, 1F);
					}
				}
				
				return true;
			}
			return false;
		}

		@Override
	    protected AxisAlignedBB getTargetableArea(double targetDistance) {
	        return this.taskOwner.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
	    }

		@Override
	    protected double getTargetDistance() {
	        return 90; //because softcoding is for wimps :P
	    }

    }

    static class EntityAISlingshotAttack extends EntityAIBase {
    	EntityChiromawGreeblingRider chiromawRider;
		LivingEntity target;
		
		public EntityAISlingshotAttack(EntityChiromawGreeblingRider chiromawRider) {
			this.chiromawRider = chiromawRider;
			this.setMutexBits(5);
		}

		@Override
		public boolean canUse() {
			target = chiromawRider.getAttackTarget();

			if (target == null)
				return false;
			else {
				double distance = chiromawRider.getDistanceSq(target);
				if (distance >= 36.0D && distance <= 576.0D && chiromawRider.getReloadTimer() >= 90) {
						return true;
				} else
					return false;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return target != null && chiromawRider.recentlyHit <= 40 && chiromawRider.getReloadTimer() >= 90;
		}

		@Override
		public void start() {
			//chiromawRider.level.playSound(null, chiromawRider.getPosition(), SoundRegistry.GREEBLING_HEY, SoundCategory.HOSTILE, 1F, 1F);
		}

		@Override
		public void updateTask() {
			if(!chiromawRider.getIsShooting())
				chiromawRider.setIsShooting(true);
			if(target != null) {
				chiromawRider.faceEntity(target, 30F, 30F);
				chiromawRider.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
				if (chiromawRider.getReloadTimer() == 90) {
					double targetX = target.getX() - chiromawRider.getX();
					double targetY = target.getBoundingBox().minY + (double) (target.height / 2.0F) - (chiromawRider.getY() + (double) (chiromawRider.height / 2.0F));
					double targetZ = target.getZ() - chiromawRider.getZ();
					double targetDistance = (double) MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
					EntityBetweenstonePebble pebble = new EntityBetweenstonePebble(chiromawRider.level, chiromawRider);
					pebble.setDamage(2D);
					pebble.shoot(targetX, targetY + targetDistance * 0.10000000298023224D, targetZ, 1.6F, 0F);
					chiromawRider.level.addFreshEntity(pebble);
					chiromawRider.level.playSound(null, chiromawRider.getPosition(), SoundRegistry.SLINGSHOT_SHOOT, SoundCategory.HOSTILE, 1F, 1F + (chiromawRider.level.rand.nextFloat() - chiromawRider.level.rand.nextFloat()) * 0.8F);
					chiromawRider.playPullSound = true;
				}
			}
			if (chiromawRider.getReloadTimer() == 102) {
				if (chiromawRider.getIsShooting()) {
					chiromawRider.setIsShooting(false);
					chiromawRider.setReloadTimer(0);
					chiromawRider.playPullSound = true;
				}
				stop();
			}
		}

		@Override
	    public void stop() {
	        target = null;
	    }
	}

    static class EntityAIMoveTowardsTargetWithDistance extends EntityAIBase {
    	EntityChiromawGreeblingRider chiromawRider;
		LivingEntity target;
	    double movePosX;
	    double movePosY;
	    double movePosZ;
	    double speed;
	    float minTargetDistance;
	    float maxTargetDistance;
	    
		public EntityAIMoveTowardsTargetWithDistance(EntityChiromawGreeblingRider chiromawRider, double speedIn,  float targetMinDistance, float targetMaxDistance) {
			this.chiromawRider = chiromawRider;
	        this.speed = speedIn;
	        this.minTargetDistance = targetMinDistance;
	        this.maxTargetDistance = targetMaxDistance;
	        this.setMutexBits(1);
		}

		@Override
		public boolean canUse() {
			target = chiromawRider.getAttackTarget();

			if (target == null) {
				return false;
			} else if (chiromawRider.getDistanceSq(target) > (double) (maxTargetDistance * maxTargetDistance)) {
				return false;
			} else if (chiromawRider.getDistanceSq(target) > (double) (minTargetDistance * minTargetDistance) && chiromawRider.getDistanceSq(target) <= (double) (maxTargetDistance * maxTargetDistance)) {
				Vector3d vec3d = findNextPointTowards(8, 3, new Vector3d(target.getX(), target.getY(), target.getZ()));
				if (vec3d == null) {
					return false;
				} else {
					flyToLocation(vec3d);
					return true;
				}
			} else if (chiromawRider.getDistanceSq(target) <= (double) (minTargetDistance * minTargetDistance)) {
				 Vector3d vec3d = findNextPointAway(16, 2, new Vector3d(target.getX(), target.getY(), target.getZ()));
				if (vec3d == null) {
					return false;
				} else {
					flyToLocation(vec3d);
					return true;
				}
			}
			else
				return false;
		}
		
		@Nullable
		private Vector3d findNextPointTowards(int xz, int y, Vector3d target) {
			Vector3d newTarget = RandomPositionGenerator.findRandomTargetBlockTowards(this.chiromawRider, xz, y, target);
			if(newTarget == null) {
				return null;
			}
			if(target.y < this.chiromawRider.getY()) {
				newTarget = new Vector3d(newTarget.x, this.chiromawRider.getY() - Math.abs(newTarget.y - this.chiromawRider.getY()), newTarget.z);
			} else {
				newTarget = new Vector3d(newTarget.x, this.chiromawRider.getY() + Math.abs(newTarget.y - this.chiromawRider.getY()), newTarget.z);
			}
			return newTarget;
		}
		
		@Nullable
		private Vector3d findNextPointAway(int xz, int y, Vector3d target) {
			Vector3d newTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.chiromawRider, xz, y, target);
			if(newTarget == null) {
				return null;
			}
			if(target.y > this.chiromawRider.getY()) {
				newTarget = new Vector3d(newTarget.x, this.chiromawRider.getY() - Math.abs(newTarget.y - this.chiromawRider.getY()), newTarget.z);
			} else {
				newTarget = new Vector3d(newTarget.x, this.chiromawRider.getY() + Math.abs(newTarget.y - this.chiromawRider.getY()), newTarget.z);
			}
			return newTarget;
		}

		public void flyToLocation(Vector3d vec3d) {
			this.movePosX = vec3d.x;
			this.movePosY = vec3d.y;
			this.movePosZ = vec3d.z;
		}

		@Override
	    public boolean canContinueToUse() {
	        return !chiromawRider.getNavigation().noPath() && target.isEntityAlive() && target.getDistanceSq(chiromawRider) <= (double)(maxTargetDistance * maxTargetDistance);
	    }

		@Override
	    public void stop() {
	        target = null;
	    }
		
		@Override
	    public void start() {
			chiromawRider.getNavigation().tryMoveToXYZ(movePosX, movePosY, movePosZ, speed);
	    }
    }
}