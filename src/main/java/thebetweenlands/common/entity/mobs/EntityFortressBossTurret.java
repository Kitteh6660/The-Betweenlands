package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntityFortressBossTurret extends EntityMob implements IEntityBL {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossTurret.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Optional<UUID>> TARGET = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossTurret.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Boolean> DEFLECTION_STATE = EntityDataManager.<Boolean>createKey(EntityFortressBossTurret.class, DataSerializers.BOOLEAN);

	private Entity cachedOwner;
	private Entity cachedTarget;	

	private boolean particlesSpawned = false;
	private int attackTicks = 0;
	private int attackDelay = 40;

	public EntityFortressBossTurret(World world) {
		super(world);
		float width = 0.4F;
		float height = 0.4F;
		this.setSize(width, height);
	}

	public EntityFortressBossTurret(World world, Entity source) {
		super(world);
		this.setOwner(source);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getDataManager().register(OWNER, Optional.absent());
		this.getDataManager().register(TARGET, Optional.absent());
		this.getDataManager().register(DEFLECTION_STATE, false);
	}

	public void setDeflectable(boolean deflectable) {
		this.getDataManager().set(DEFLECTION_STATE, deflectable);
	}

	public boolean isDeflectable() {
		return this.getDataManager().get(DEFLECTION_STATE);
	}

	public void setOwner(@Nullable Entity entity) {
		this.getDataManager().set(OWNER, entity == null ? Optional.absent() : Optional.of(entity.getUUID()));
	}

	@Nullable
	public UUID getOwnerUUID() {
		Optional<UUID> uuid = this.getDataManager().get(OWNER);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getOwner() {
		UUID uuid = this.getOwnerUUID();
		if(uuid == null) {
			this.cachedOwner = null;
		} else if(this.cachedOwner == null || !this.cachedOwner.isEntityAlive() || !this.cachedOwner.getUUID().equals(uuid)) {
			this.cachedOwner = null;
			for(Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUUID().equals(uuid)) {
					this.cachedOwner = entity;
					break;
				}
			}
		}
		return this.cachedOwner;
	}

	public void setTarget(@Nullable Entity entity) {
		this.getDataManager().set(TARGET, entity == null ? Optional.absent() : Optional.of(entity.getUUID()));
	}

	@Nullable
	public UUID getTargetUUID() {
		Optional<UUID> uuid = this.getDataManager().get(TARGET);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getTarget() {
		UUID uuid = this.getTargetUUID();
		if(uuid == null) {
			this.cachedTarget = null;
		} else if(this.cachedTarget == null || !this.cachedTarget.isEntityAlive() || !this.cachedTarget.getUUID().equals(uuid)) {
			this.cachedTarget = null;
			for(Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUUID().equals(uuid)) {
					this.cachedTarget = entity;
					break;
				}
			}
		}
		return this.cachedTarget;
	}

	public int getAttackDelay() {
		return this.attackDelay;
	}

	public void setAttackDelay(int delay) {
		this.attackDelay = delay;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putInt("attackDelay", this.attackDelay);
		nbt.putBoolean("deflectable", this.isDeflectable());
		if(this.getOwnerUUID() != null) {
			nbt.putUUID("owner", this.getOwnerUUID());
		}
		if(this.getTargetUUID() != null) {
			nbt.putUUID("target", this.getTargetUUID());
		}
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.attackDelay = nbt.getInt("attackDelay");
		this.setDeflectable(nbt.getBoolean("deflectable"));
		if(nbt.hasUUID("owner")) {
			this.getDataManager().set(OWNER, Optional.of(nbt.getUUID("owner")));
		} else {
			this.getDataManager().set(OWNER, Optional.absent());
		}
		if(nbt.hasUUID("target")) {
			this.getDataManager().set(TARGET, Optional.of(nbt.getUUID("target")));
		} else {
			this.getDataManager().set(TARGET, Optional.absent());
		}
	}

	@Override
	public void tick() {
		if(!this.level.isClientSide() && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.remove();
			return;
		}
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		super.tick();

		if(this.level.isClientSide()) {
			if(!this.particlesSpawned) {
				this.particlesSpawned = true;
				for(int i = 0; i < 6; i++) {
					this.spawnVolatileParticles();
				}
			}
			if(this.world.rand.nextInt(6) == 0) {
				this.spawnFlameParticles();
			}
		}

		if(this.getTarget() == null) {
			AxisAlignedBB searchBB = this.getBoundingBox().grow(16, 16, 16);
			List<PlayerEntity> eligiblePlayers = this.world.getEntitiesOfClass(PlayerEntity.class, searchBB);
			PlayerEntity closest = null;
			for(PlayerEntity player : eligiblePlayers) {
				if(closest == null || closest.getDistance(this) > player.getDistance(this))
					closest = player;
			}
			if(closest != null) 
				this.setTarget(closest);
		}

		if(this.getTarget() != null) {
			this.faceEntity(this.getTarget(), 360.0F, 360.0F);
			this.attackTicks++;

			if(this.attackTicks > this.attackDelay) {
				if(!this.level.isClientSide()) {
					if(!this.isObstructedByBoss()) {
						Vector3d diff = new Vector3d(this.getX(), this.getY(), this.getZ())
								.subtract(new Vector3d(this.getTarget().getBoundingBox().minX + (this.getTarget().getBoundingBox().maxX - this.getTarget().getBoundingBox().minX) / 2.0D,
										this.getTarget().getBoundingBox().minY + (this.getTarget().getBoundingBox().maxY - this.getTarget().getBoundingBox().minY) / 2.0D,
										this.getTarget().getBoundingBox().minZ + (this.getTarget().getBoundingBox().maxZ - this.getTarget().getBoundingBox().minZ) / 2.0D)).normalize();
						EntityFortressBossProjectile bullet = new EntityFortressBossProjectile(this.world, this.getOwner());
						bullet.setDeflectable(this.isDeflectable());
						bullet.moveTo(this.getX(), this.getY(), this.getZ(), 0, 0);
						float speed = 0.5F;
						bullet.shoot(-diff.x, -diff.y, -diff.z, speed, 0.0F);
						this.world.spawnEntity(bullet);
					}
				} else {
					for(int i = 0; i < 6; i++)
						this.spawnVolatileParticles();
				}
				this.remove();
			}
		} else {
			this.attackTicks = 0;
		}
	}

	public boolean isObstructedByBoss() {
		Vector3d ray = this.getLookVec().normalize();
		Vector3d currentPos = new Vector3d(this.getX(), this.getY(), this.getZ());
		Vector3d nextPos = currentPos.add(ray.x * 64.0D, ray.y * 64.0D, ray.z * 64.0D);
		Entity hitEntity = null;
		List<Entity> hitEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(64, 64, 64));
		double minDist = 0.0D;
		for (int i = 0; i < hitEntities.size(); ++i) {
			Entity entity = (Entity)hitEntities.get(i);
			if (entity.canBeCollidedWith()) {
				float f = 0.65F / 2.0F + 0.1F + 0.1F;
				AxisAlignedBB entityBB = entity.getBoundingBox().grow((double)f, (double)f, (double)f);
				RayTraceResult result = entityBB.calculateIntercept(currentPos, nextPos);
				if (result != null) {
					double dst = currentPos.distanceTo(result.hitVec);
					if (dst < minDist || minDist == 0.0D) {
						hitEntity = entity;
						minDist = dst;
					}
				}
			}
		}
		if(hitEntity == null || (hitEntity instanceof EntityFortressBoss == false && hitEntity != this.getOwner())) {
			return false;
		}
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnFlameParticles() {
		BLParticles.GREEN_FLAME.spawn(this.world, this.getX(), this.getY() + 0.2F, this.getZ(), ParticleArgs.get().withMotion((this.world.rand.nextFloat() - 0.5F) / 5.0F, (this.world.rand.nextFloat() - 0.5F) / 5.0F, (this.world.rand.nextFloat() - 0.5F) / 5.0F));
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnVolatileParticles() {
		final double radius = 0.3F;
		final double cx = this.getX();
		final double cy = this.getY() + 0.35D;
		final double cz = this.getZ();
		for(int i = 0; i < 8; i++) {
			double px = this.world.rand.nextFloat() * 0.7F;
			double py = this.world.rand.nextFloat() * 0.7F;
			double pz = this.world.rand.nextFloat() * 0.7F;
			Vector3d vec = new Vector3d(px, py, pz).subtract(new Vector3d(0.35F, 0.35F, 0.35F)).normalize();
			px = cx + vec.x * radius;
			py = cy + vec.y * radius;
			pz = cz + vec.z * radius;
			BLParticles.STEAM_PURIFIER.spawn(this.world, px, py, pz);
		}
	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if (this.isInWater()) {
			this.moveRelative(strafe, up, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else {
			float friction = 0.91F;

			if (this.onGround) {
				friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()))).getBlock().slipperiness * 0.91F;
			}

			float groundFriction = 0.16277136F / (friction * friction * friction);
			this.moveRelative(strafe, up,  forward, this.onGround ? 0.1F * groundFriction : 0.02F);
			friction = 0.91F;

			if (this.onGround) {
				friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()))).getBlock().slipperiness * 0.91F;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)friction;
			this.motionY *= (double)friction;
			this.motionZ *= (double)friction;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double dx = this.getX() - this.xOld;
		double dz = this.getZ() - this.zOld;
		float distanceMoved = MathHelper.sqrt(dx * dx + dz * dz) * 4.0F;

		if (distanceMoved > 1.0F) {
			distanceMoved = 1.0F;
		}

		this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}
}
