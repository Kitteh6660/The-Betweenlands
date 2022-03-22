package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
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
import thebetweenlands.api.capability.ISwarmedCapability;
import thebetweenlands.client.audio.EntitySound;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySwarm extends EntityClimberBase implements IMob {
	public static final DataParameter<Float> SWARM_SIZE = EntityDataManager.createKey(EntitySwarm.class, DataSerializers.FLOAT);

	@OnlyIn(Dist.CLIENT)
	private ISound idleSound;

	public EntitySwarm(World world) {
		this(world, 1);
	}

	public EntitySwarm(World world, float swarmSize) {
		super(world);
		this.setSwarmSize(swarmSize);
		this.experienceValue = 5;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SWARM_SIZE, 1.0f);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new AIMerge(this, 50, 1.0D));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, 1, false, false, null));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE);

		this.getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
		this.getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(24.0D);
		this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	public float getSwarmSize() {
		return this.dataManager.get(SWARM_SIZE);
	}

	public void setSwarmSize(float swarmSize) {
		this.dataManager.set(SWARM_SIZE, swarmSize);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		super.readEntityFromNBT(compound);

		this.setSwarmSize(compound.getFloat("SwarmSize"));
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		super.writeEntityToNBT(compound);

		compound.setFloat("SwarmSize", this.getSwarmSize());
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return false;
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && super.getCanSpawnHere();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) { }

	@Override
	public void tick() {
		super.tick();

		if(!this.level.isClientSide()) {
			if(world.getDifficulty() == EnumDifficulty.PEACEFUL)
				remove();
				
			if(this.isOnFire() || this.isInWater()) {
				if(this.getSwarmSize() > 0.1f) {
					this.setSwarmSize(Math.max(0.1f, this.getSwarmSize() - 0.005f));
				}

				if(this.isOnFire() && this.random.nextInt(10) == 0) {
					List<EntitySwarm> swarms = this.world.getEntitiesOfClass(EntitySwarm.class, this.getBoundingBox().grow(1), s -> !s.isOnFire());

					for(EntitySwarm swarm : swarms) {
						swarm.setFire(2);
					}
				}
			}

			float range = 3.25f;

			List<PlayerEntity> players = this.world.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().grow(range));

			for(PlayerEntity player : players) {
				double dst = player.getDistance(this);

				if(dst < range && canEntityBeSeen(player)) {
					ISwarmedCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_SWARMED, null);

					if(cap != null) {
						cap.setSwarmedStrength(cap.getSwarmedStrength() + (1.0f - (float) dst / range) * 0.025f * MathHelper.clamp(this.getSwarmSize() * 1.75f, 0, 1));

						cap.setDamage((float) this.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue());
					}
				}
			}
		} else {
			this.updateClient();
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.isFireDamage()) {
			amount *= 2;
		}

		boolean attacked = super.attackEntityFrom(source, amount);

		if(this.isEntityAlive() && attacked && amount > 2 && (this.random.nextFloat() * 16 < amount || this.getHealth() < this.getMaxHealth() * 0.25f)) {
			this.split();
		}

		return attacked;
	}

	protected boolean split() {
		float swarmSize = this.getSwarmSize();

		if(swarmSize > 0.3f) {
			float initialSwarmSize = swarmSize;

			float fraction = initialSwarmSize * 0.25f + initialSwarmSize * (this.random.nextFloat() - 0.5f) * 0.05f;
			this.setSwarmSize(fraction);
			swarmSize -= fraction;

			for(int i = 0; i < 3; i++) {
				fraction = i == 2 ? swarmSize : (initialSwarmSize * 0.25f + initialSwarmSize * (this.random.nextFloat() - 0.5f) * 0.05f);
				EntitySwarm swarm = new EntitySwarm(this.world, fraction);
				swarmSize -= fraction;

				swarm.setHealth(this.getHealth() * 0.66f);
				swarm.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);

				if(this.isOnFire()) {
					swarm.setFire(40);
				}

				float mx = this.random.nextFloat() - 0.5f;
				float mz = this.random.nextFloat() - 0.5f;

				float len = MathHelper.sqrt(mx * mx + mz * mz);

				mx /= len;
				mz /= len;

				swarm.motionX = mx * 0.5f;
				swarm.motionY = 0.3f;
				swarm.motionZ = mz * 0.5f;

				this.world.spawnEntity(swarm);
			}

			this.setHealth(this.getHealth() * 0.5f);

			return true;
		}

		return false;
	}

	protected void mergeInto(EntitySwarm swarm) {
		swarm.setSwarmSize(swarm.getSwarmSize() + this.getSwarmSize());

		if(this.getHealth() < swarm.getHealth()) {
			swarm.setHealth((this.getHealth() / 0.66f + swarm.getHealth()) * 0.5f);
		}

		if(this.isOnFire()) {
			swarm.setFire(2);
		}

		this.remove();
	}

	@OnlyIn(Dist.CLIENT)
	protected void updateClient() {
		Entity view = Minecraft.getInstance().getRenderViewEntity();

		if(view != null && view.getDistance(this) < 16 && !this.isInWater()) {
			SoundHandler handler = Minecraft.getInstance().getSoundHandler();
			if(this.idleSound == null || !handler.isSoundPlaying(this.idleSound)) {
				this.idleSound = new EntitySound<Entity>(SoundRegistry.SWARM_IDLE, SoundCategory.HOSTILE, this, e -> e.isEntityAlive(), 0.8f);
				handler.playSound(this.idleSound);
			}

			List<AxisAlignedBB> collisionBoxes = new ArrayList<>();

			for(BlockPos offsetPos : BlockPos.getAllInBoxMutable(
					new BlockPos(MathHelper.floor(this.getX() - this.width - 2), MathHelper.floor(this.getY() - 2), MathHelper.floor(this.getZ() - this.width - 2)),
					new BlockPos(MathHelper.floor(this.getX() + this.width + 2), MathHelper.floor(this.getY() + this.height + 2), MathHelper.floor(this.getZ() + this.width + 2)))) {
				BlockState state = this.world.getBlockState(offsetPos);

				if(state.isFullCube()) {
					collisionBoxes.add(new AxisAlignedBB(offsetPos));
				}
			}

			float swarmSize = this.getSwarmSize();

			for(int i = 0; i < Math.max(2 - Minecraft.getInstance().gameSettings.particleSetting, 1) * swarmSize + 1; i++) {
				float rx = (this.world.rand.nextFloat() - 0.5f) * this.width;
				float ry = (this.world.rand.nextFloat() - 0.5f) * this.height;
				float rz = (this.world.rand.nextFloat() - 0.5f) * this.width;

				float len = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);

				rx /= len;
				ry /= len;
				rz /= len;

				len = 0.333f + this.world.rand.nextFloat() * 0.666f;

				double x = this.getX() + this.motionX * 5 + rx * len * (this.width + 0.3f) * swarmSize * 0.5f;
				double y = this.getY() + this.motionY * 5 - 0.15f * swarmSize + (this.height + 0.3f) * swarmSize * 0.5f + ry * len * (this.height + 0.3f) * swarmSize;
				double z = this.getZ() + this.motionZ * 5 + rz * len * (this.width + 0.3f) * swarmSize * 0.5f;

				if(this.isOnFire() && this.random.nextInt(3) == 0) {
					this.world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, 0, 0, 0);
				}

				if(this.random.nextInt(8) == 0) {
					if(this.random.nextInt(3) == 0) {
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.FLYING_SWARM_EMISSIVE.create(this.world, x, y, z));
					} else {
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.FLY.create(world, x, y, z, ParticleArgs.get().withScale(0.15F * world.rand.nextFloat() + 0.25F).withData(40, 0.01F, 0.0025F, false)));
					}
				} else {
					AxisAlignedBB particle = new AxisAlignedBB(x - 0.01f, y - 0.01f, z - 0.01f, x + 0.01f, y + 0.01f, z + 0.01f);

					double closestDst = 1;
					double closestDX = 0;
					double closestDY = 0;
					double closestDZ = 0;

					for(AxisAlignedBB box : collisionBoxes) {
						double dx1 = box.calculateXOffset(particle, -1);
						double dy1 = box.calculateYOffset(particle, -1);
						double dz1 = box.calculateZOffset(particle, -1);
						double dx2 = box.calculateXOffset(particle, 1);
						double dy2 = box.calculateYOffset(particle, 1);
						double dz2 = box.calculateZOffset(particle, 1);

						if(Math.abs(dx1) < closestDst) {
							closestDst = Math.abs(dx1);
							closestDX = dx1;
							closestDY = 0;
							closestDZ = 0;
						}

						if(Math.abs(dy1) < closestDst) {
							closestDst = Math.abs(dy1);
							closestDX = 0;
							closestDY = dy1;
							closestDZ = 0;
						}

						if(Math.abs(dz1) < closestDst) {
							closestDst = Math.abs(dz1);
							closestDX = 0;
							closestDY = 0;
							closestDZ = dz1;
						}

						if(Math.abs(dx2) < closestDst) {
							closestDst = Math.abs(dx2);
							closestDX = dx2;
							closestDY = 0;
							closestDZ = 0;
						}

						if(Math.abs(dy2) < closestDst) {
							closestDst = Math.abs(dy2);
							closestDX = 0;
							closestDY = dy2;
							closestDZ = 0;
						}

						if(Math.abs(dz2) < closestDst) {
							closestDst = Math.abs(dz2);
							closestDX = 0;
							closestDY = 0;
							closestDZ = dz2;
						}
					}

					if(closestDst < 1) {
						x += closestDX - Math.signum(closestDX) * 0.01f;
						y += closestDY - Math.signum(closestDY) * 0.01f;
						z += closestDZ - Math.signum(closestDZ) * 0.01f;

						double ox = 1 - Math.abs(Math.signum(closestDX));
						double oy = 1 - Math.abs(Math.signum(closestDY));
						double oz = 1 - Math.abs(Math.signum(closestDZ));

						BLParticles variant;
						if(this.random.nextInt(6) == 0) {
							variant = BLParticles.SWARM_EMISSIVE;
						} else {
							variant = BLParticles.SWARM;
						}

						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, variant.create(this.world, x, y, z, 
								ParticleArgs.get()
								.withMotion((this.world.rand.nextFloat() - 0.5f) * 0.05f * ox, (this.world.rand.nextFloat() - 0.5f) * 0.05f * oy, (this.world.rand.nextFloat() - 0.5f) * 0.05f * oz)
								.withScale(0.25f)
								.withData(Direction.getNearest((float) -closestDX, (float) -closestDY, (float) -closestDZ), 40, this.getPositionVector(), (Supplier<Vector3d>) () -> this.getPositionVector())));
					}
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	public static class AIMerge extends EntityAIBase {
		private final EntitySwarm entity;
		private int delay;
		private double speedTowardsTarget;

		private EntitySwarm leader;
		private int delayCounter;
		private int failedPathFindingPenalty = 0;

		public AIMerge(EntitySwarm entity, int delay, double speed) {
			this.entity = entity;
			this.delay = delay;
			this.speedTowardsTarget = speed;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.getSwarmSize() < 0.9f && this.delay-- <= 0) {
				EntitySwarm leader = this.findLeader();

				if(leader != null && leader != this.entity) {
					this.leader = leader;
					return true;
				}
			}
			return false;
		}

		@Nullable
		private EntitySwarm findLeader() {
			List<EntitySwarm> swarms = this.entity.world.getEntitiesOfClass(EntitySwarm.class, this.entity.getBoundingBox().grow(8));

			int minId = Integer.MAX_VALUE;
			EntitySwarm leader = null;

			for(EntitySwarm swarm : swarms) {
				if(swarm.getEntityId() < minId && swarm.getSwarmSize() + this.entity.getSwarmSize() <= 1) {
					minId = swarm.getEntityId();
					leader = swarm;
				}
			}

			return leader;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.leader != null && this.leader.isEntityAlive() && this.leader.getSwarmSize() + this.entity.getSwarmSize() <= 1;
		}

		@Override
		public void resetTask() {
			this.leader = null;
			this.delayCounter = 0;
		}

		@Override
		public void updateTask() {
			if(this.leader != null) {
				if(this.leader.getDistance(this.entity) < 1) {
					this.entity.mergeInto(this.leader);
				} else if(--this.delayCounter <= 0) {
					this.delayCounter = 4 + this.entity.getRNG().nextInt(7);

					double dstSq = this.entity.getDistanceSq(this.leader.getX(), this.leader.getBoundingBox().minY, this.leader.getZ());

					if(dstSq > 1024.0D) {
						this.delayCounter += 10;
					} else if (dstSq > 256.0D) {
						this.delayCounter += 5;
					}

					if(!this.entity.getNavigator().tryMoveToEntityLiving(this.leader, this.speedTowardsTarget)) {
						this.delayCounter += 15;
					}
				}
			}
		}
	}
}
