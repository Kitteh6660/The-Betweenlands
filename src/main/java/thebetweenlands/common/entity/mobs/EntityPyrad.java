package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.ai.attributes.Attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.ai.EntityAIMoveToDirect;
import thebetweenlands.common.entity.attributes.BooleanAttribute;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.entity.projectiles.EntityPyradFlame;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityPyrad extends EntityFlyingMob implements IEntityBL {
	public static final IAttribute FLAMES_PER_ATTACK = (new RangedAttribute(null, "bl.flamesPerAttack", 6.0D, 1.0D, 64.0D)).setDescription("Number range of flames per attack");
	public static final IAttribute AGRESSIVE = (new BooleanAttribute(null, "bl.pyradAgressive", false)).setDescription("Whether the Pyrad is agressive and doesn't go inactive");

	private static final DataParameter<Boolean> CHARGING = EntityDataManager.<Boolean>createKey(EntityPyrad.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ACTIVE = EntityDataManager.<Boolean>createKey(EntityPyrad.class, DataSerializers.BOOLEAN);

	private List<EntityAIBase> activeTasks;
	private List<EntityAIBase> activeTargetTasks;

	private int glowTicks = 0;
	private int prevGlowTicks = 0;

	private int activeTicks = 0;
	private int prevActiveTicks = 0;

	private int hitTicks = 0;
	private int prevHitTicks = 0;

	private int deathTicks = 0;

	public EntityPyrad(World worldIn) {
		super(worldIn);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.LAVA, 8.0F);
		this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.fireImmune = true;
		this.experienceValue = 10;
		this.moveHelper = new FlightMoveHelper(this);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CHARGING, false);
		this.entityData.define(ACTIVE, false);
	}

	@Override
	protected void initEntityAI() {
		this.activeTasks = new ArrayList<EntityAIBase>();
		this.activeTargetTasks = new ArrayList<EntityAIBase>();

		this.activeTasks.add(new EntityAIMoveToDirect<EntityPyrad>(this, 1.0D) {
			@Override
			protected Vector3d getTarget() {
				LivingEntity target = this.entity.getAttackTarget();
				if(target != null) {
					BlockPos pos = new BlockPos(this.entity);
					int groundHeight = FlightMoveHelper.getGroundHeight(this.entity.world, pos, 16, pos).getY();
					Vector3d dir = new Vector3d(target.getX() - this.entity.getX(), target.getY() + 1 - this.entity.rand.nextFloat() * 0.3 - this.entity.getY() - 1, target.getZ() - this.entity.getZ());
					double dst = dir.length();
					if(dst > 10) {
						dir = dir.normalize();
						this.setSpeed(0.75D);
						return new Vector3d(this.entity.getX() + dir.x * (dst - 10), Math.min(this.entity.getY() + dir.y * (dst - 10), Math.max(groundHeight + 2, target.getY() + 2)), this.entity.getZ() + dir.z * (dst - 10));
					} else if(dst < 5) {
						dir = dir.normalize();
						this.setSpeed(1.0D);
						return new Vector3d(this.entity.getX() - dir.x * 2, Math.min(this.entity.getY() - dir.y * 2, Math.max(groundHeight + (this.entity.isCharging() ? 6 : 2), target.getY() + 2)), this.entity.getZ() - dir.z * 2);
					}
				}
				return null;
			}
		});
		this.activeTasks.add(new EntityAIFlyRandomly<EntityPyrad>(this) {
			@Override
			protected double getTargetX(Random rand, double distanceMultiplier) {
				return this.entity.getX() + (double)((rand.nextFloat() * 2.0F - 1.0F) * 10.0F * distanceMultiplier);
			}

			@Override
			protected double getTargetY(Random rand, double distanceMultiplier) {
				return this.entity.getY() + (rand.nextFloat() * 1.45D - 1.0D) * 4.0D * distanceMultiplier;
			}

			@Override
			protected double getTargetZ(Random rand, double distanceMultiplier) {
				return this.entity.getZ() + (double)((rand.nextFloat() * 2.0F - 1.0F) * 10.0F * distanceMultiplier);
			}

			@Override
			protected double getFlightSpeed() {
				return 0.5D;
			}
		});
		this.activeTasks.add(new EntityPyrad.AIPyradAttack(this));
		this.activeTasks.add(new EntityAIMoveTowardsRestriction(this, 0.04D));
		this.activeTasks.add(new EntityAIWatchClosest(this, PlayerEntity.class, 8.0F));
		this.activeTasks.add(new EntityAILookIdle(this));

		this.activeTargetTasks.add(new EntityAIHurtByTarget(this, true));
		this.activeTargetTasks.add(new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(60.0D);;
		this.getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0D);
		this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1D);
		this.getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(28.0D);
		this.getAttributeMap().registerAttribute(FLAMES_PER_ATTACK);
		this.getAttributeMap().registerAttribute(AGRESSIVE);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if(!this.isActive()) {
			return null;
		}
		return SoundRegistry.PYRAD_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.PYRAD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.PYRAD_DEATH;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		nbt.putBoolean("active", this.isActive());

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		if(nbt.contains("active")) {
			this.setActive(nbt.getBoolean("active"));
		}

		super.readEntityFromNBT(nbt);
	}

	@Override
	public void onLivingUpdate() {
		if (!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.6D;
		}

		if(!this.level.isClientSide()) {
			boolean day = this.world.provider.getSunBrightnessFactor(1) >= 0.5F;
			
			if(this.isEntityAlive() && (this.getEntityAttribute(AGRESSIVE).getAttributeValue() == 1 || !day || this.isInWater()) && !this.isActive()) {
				this.setActive(true);
			}

			if(this.getEntityAttribute(AGRESSIVE).getAttributeValue() == 0 && this.isEntityAlive() && this.isActive() && this.getAttackTarget() == null && this.random.nextInt(800) == 0) {
				this.setActive(false);
			}

			if(this.isInWater() && this.isActive()) {
				this.moveHelper.setMoveTo(this.getX(), this.getY() + 1.0D, this.getZ(), 1.0D);
			}
		}

		this.prevGlowTicks = this.glowTicks;
		if(this.isCharging() && this.glowTicks < 10) {
			this.glowTicks++;
		} else if(!this.isCharging() && this.glowTicks > 0) {
			this.glowTicks--;
		}

		this.prevActiveTicks = this.activeTicks;
		if(this.isActive() && this.activeTicks < 60) {
			this.activeTicks++;
		} else if(!this.isActive() && this.activeTicks > 0 && this.onGround) {
			this.activeTicks--;
		}

		this.prevHitTicks = this.hitTicks;
		if(this.hitTicks > 0) {
			this.hitTicks--;
		}

		if(!this.isActive() && this.activeTicks == 0) {
			this.setSize(0.7F, 1.2F);
			this.rotationYawHead = (float) (this.interpTargetYaw = this.yRot);
		} else {
			this.setSize(0.7F, 2F);
		}

		if (this.level.isClientSide() && this.isActive()) {
			if(this.random.nextInt(4) == 0) {
				ParticleArgs<?> args = ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
				if(this.isCharging()) {
					args.withColor(0.9F, 0.35F, 0.1F, 1);
				} else {
					args.withColor(1F, 0.65F, 0.25F, 1);
				}
				BLParticles.LEAF_SWIRL.spawn(this.world, this.getX(), this.getY(), this.getZ(), args);
			}
			if(this.isCharging() || this.random.nextInt(10) == 0) {
				ParticleArgs<?> args = ParticleArgs.get().withMotion((this.random.nextFloat() - 0.5F) / 4.0F, (this.random.nextFloat() - 0.5F) / 4.0F, (this.random.nextFloat() - 0.5F) / 4.0F);
				if(this.isCharging()) {
					args.withColor(0.9F, 0.35F, 0.1F, 1);
					args.withData(60);
				} else {
					args.withColor(1F, 0.65F, 0.25F, 1);
				}
				BLParticles.WEEDWOOD_LEAF.spawn(this.world, this.getX(), this.getY() + this.getEyeHeight(), this.getZ(), args);
			}
		}

		super.onLivingUpdate();
	}

	public float getGlowTicks(float partialTicks) {
		return this.prevGlowTicks + (this.glowTicks - this.prevGlowTicks) * partialTicks;
	}

	public float getActiveTicks(float partialTicks) {
		return this.prevActiveTicks + (this.activeTicks - this.prevActiveTicks) * partialTicks;
	}

	public float getHitTicks(float partialTicks) {
		return this.prevHitTicks + (this.hitTicks - this.prevHitTicks) * partialTicks;
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
	}

	/*@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}*/

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return !this.isActive() && this.activeTicks == 0 ? entity.getBoundingBox() : super.getCollisionBox(entity);
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox() {
		return !this.isActive() && this.activeTicks == 0 ? this.getBoundingBox() : super.getCollisionBoundingBox();
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		if(!this.isActive()) {
			this.motionX = 0;
			this.motionZ = 0;
			this.motionY -= 0.1D;
		}

		super.travel(strafe, vertical, forward);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(!this.isActive() && this.isEntityAlive()) {
			if(this.hitTicks <= 0) {
				if(!this.level.isClientSide() && (this.random.nextInt(12) == 0 || amount > 3.0F)) {
					this.setActive(true);
					return super.attackEntityFrom(source, amount);
				}
				for(int i = 0; i < 10; i++) {
					if(this.level.isClientSide()) {
						ParticleArgs<?> args = ParticleArgs.get().withMotion((this.random.nextFloat() - 0.5F) / 2.0F, (this.random.nextFloat() - 0.5F) / 2.0F, (this.random.nextFloat() - 0.5F) / 2.0F).withColor(1F, 0.65F, 0.25F, 1);
						BLParticles.WEEDWOOD_LEAF.spawn(this.world, this.getX(), this.getY() + 0.8D, this.getZ(), args);
					}
					this.playSound(SoundRegistry.PYRAD_HURT, 0.1F, 0.3F + this.random.nextFloat() * 0.3F);
				}
				this.hitTicks = 20;
			}
			return false;
		} else {
			return super.attackEntityFrom(source, amount);
		}
	}

	@Override
	public void knockBack(Entity entityIn, float strenght, double xRatio, double zRatio) {
		if(this.isActive()) {
			super.knockBack(entityIn, strenght, xRatio, zRatio);
		}
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed() && this.isActive();
	}

	@Override
	protected void onDeathUpdate() {
		if(this.isActive()) {
			this.setActive(false);
		} else if(this.onGround && this.activeTicks < 20) {
			this.deathTicks++;
			if(this.deathTicks > 10) {
				if(this.level.isClientSide()) {
					for(int i = 0; i < 10; i++) {
						ParticleArgs<?> args = ParticleArgs.get().withMotion((this.random.nextFloat() - 0.5F) / 2.0F, (this.random.nextFloat() - 0.5F) / 2.0F, (this.random.nextFloat() - 0.5F) / 2.0F).withScale(2.0F);
						args.withColor(1F, 0.25F + this.random.nextFloat() * 0.5F, 0.05F + this.random.nextFloat() * 0.25F, 1);
						BLParticles.WEEDWOOD_LEAF.spawn(this.world, this.getX(), this.getY() + 0.8D, this.getZ(), args);
						args = ParticleArgs.get().withMotion((this.random.nextFloat() - 0.5F) / 2.0F, (this.random.nextFloat() - 0.5F) / 2.0F, (this.random.nextFloat() - 0.5F) / 2.0F);
						BLParticles.SWAMP_SMOKE.spawn(this.world, this.getX(), this.getY() + 0.8D, this.getZ(), args);
					}
				}
				if(this.deathTicks > 30 && !this.level.isClientSide()) {
					for(int i = 0; i < 10; i++) {
						this.playSound(SoundRegistry.PYRAD_HURT, 0.18F, 0.1F + this.random.nextFloat() * 0.2F);
						this.playSound(SoundRegistry.PYRAD_DEATH, 0.08F, 0.1F + this.random.nextFloat() * 0.2F);
					}
					this.remove();
				}
			}
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.PYRAD;
	}

	public boolean isCharging() {
		return this.getDataManager().get(CHARGING);
	}

	public void setCharging(boolean charging) {
		this.getDataManager().set(CHARGING, charging);
	}

	public boolean isActive() {
		return this.getDataManager().get(ACTIVE);
	}

	public void setActive(boolean active) {
		this.getDataManager().set(ACTIVE, active);

		if(!this.level.isClientSide()) {
			if(active) {
				for(int i = 0; i < this.activeTasks.size(); i++) {
					this.tasks.addTask(i, this.activeTasks.get(i));
				}
				for(int i = 0; i < this.activeTargetTasks.size(); i++) {
					this.targetTasks.addTask(i, this.activeTargetTasks.get(i));
				}
			} else {
				for(EntityAIBase ai : this.activeTasks) {
					this.tasks.removeTask(ai);
				}
				for(EntityAIBase ai : this.activeTargetTasks) {
					this.targetTasks.removeTask(ai);
				}
			}
		}
	}

	static class AIPyradAttack extends EntityAIBase {
		private final EntityPyrad pyrad;
		private int attackStep;
		private int attackTime;

		public AIPyradAttack(EntityPyrad pyrad) {
			this.pyrad = pyrad;
		}

		@Override
		public boolean shouldExecute() {
			LivingEntity target = this.pyrad.getAttackTarget();
			return target != null && target.isEntityAlive();
		}

		@Override
		public void startExecuting() {
			this.attackStep = 0;
		}

		@Override
		public void resetTask() {
			this.pyrad.setCharging(false);
		}

		@Override
		public void updateTask() {
			--this.attackTime;
			LivingEntity target = this.pyrad.getAttackTarget();
			
			if(target != null) {
				double distSq = this.pyrad.getDistanceSq(target);
	
				if (distSq < 4.0D) {
					if (this.attackTime <= 0) {
						this.attackTime = 20;
						this.pyrad.attackEntityAsMob(target);
					}
	
					this.pyrad.getMoveHelper().setMoveTo(target.getX(), target.getY(), target.getZ(), 1.0D);
				} else if (distSq < 256.0D) {
					double dx = target.getX() - this.pyrad.getX();
					double dy = target.getBoundingBox().minY + (double)(target.height / 2.0F) - (this.pyrad.getY() + (double)(this.pyrad.height / 2.0F));
					double dz = target.getZ() - this.pyrad.getZ();
	
					if (this.attackTime <= 0) {
						++this.attackStep;
	
						if (this.attackStep == 1) {
							this.attackTime = 20 + this.pyrad.rand.nextInt(40);
							this.pyrad.setCharging(true);
						} else if (this.attackStep <= 4) {
							this.attackTime = 6;
						} else {
							this.attackTime = 60 + this.pyrad.rand.nextInt(40);
							this.attackStep = 0;
							this.pyrad.setCharging(false);
						}
	
						if (this.attackStep > 1) {
							float f = MathHelper.sqrt(MathHelper.sqrt(distSq)) * 0.8F;
							this.pyrad.world.playEvent((PlayerEntity)null, 1018, new BlockPos((int)this.pyrad.getX(), (int)this.pyrad.getY(), (int)this.pyrad.getZ()), 0);
	
							int numberFlames = (int)this.pyrad.getEntityAttribute(FLAMES_PER_ATTACK).getAttributeValue();
	
							for (int i = 0; i < (numberFlames > 1 ? this.pyrad.rand.nextInt(numberFlames) : 0) + 1; ++i) {
								EntityPyradFlame flame = new EntityPyradFlame(this.pyrad.world, this.pyrad, dx + this.pyrad.getRNG().nextGaussian() * (double)f, dy, dz + this.pyrad.getRNG().nextGaussian() * (double)f);
								flame.getY() = this.pyrad.getY() + (double)(this.pyrad.height / 2.0F) + 0.5D;
								this.pyrad.world.spawnEntity(flame);
							}
						}
					}
	
					this.pyrad.getLookHelper().setLookPositionWithEntity(target, 10.0F, 10.0F);
				}
			}

			super.updateTask();
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