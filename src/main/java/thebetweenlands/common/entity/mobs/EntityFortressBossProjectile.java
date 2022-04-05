package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityFortressBossProjectile extends Entity implements IProjectile {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossProjectile.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Boolean> DEFLECTION_STATE = EntityDataManager.<Boolean>createKey(EntityFortressBossProjectile.class, DataSerializers.BOOLEAN);

	private UUID throwerUUID;
	private int ticksInAir = 0;
	private boolean canDismount = false;

	private Entity cachedOwner;
	private Entity cachedThrower;	

	public EntityFortressBossProjectile(World world) {
		super(world);
		this.setSize(0.65F, 0.65F);
		this.noClip = true;
	}

	public EntityFortressBossProjectile(World world, Entity source) {
		this(world);
		this.setOwner(source);
		this.setThrower(source);
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().register(OWNER, Optional.absent());
		this.getEntityData().register(DEFLECTION_STATE, false);
	}

	public void setDeflectable(boolean deflectable) {
		this.getEntityData().set(DEFLECTION_STATE, deflectable);
	}

	public boolean isDeflectable() {
		return this.getEntityData().get(DEFLECTION_STATE);
	}

	public void setOwner(@Nullable Entity entity) {
		this.getEntityData().set(OWNER, entity == null ? Optional.absent() : Optional.of(entity.getUUID()));
	}

	@Nullable
	public UUID getOwnerUUID() {
		Optional<UUID> uuid = this.getEntityData().get(OWNER);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getOwner() {
		UUID uuid = this.getOwnerUUID();
		if(uuid == null) {
			this.cachedOwner = null;
		} else if(this.cachedOwner == null || !this.cachedOwner.isEntityAlive() || !this.cachedOwner.getUUID().equals(uuid)) {
			this.cachedOwner = null;
			for(Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(64.0D, 64.0D, 64.0D))) {
				if(entity.getUUID().equals(uuid)) {
					this.cachedOwner = entity;
					break;
				}
			}
		}
		return this.cachedOwner;
	}

	public void setThrower(@Nullable Entity entity) {
		this.throwerUUID = entity != null ? entity.getUUID() : null;
	}

	@Nullable
	public UUID getThrowerUUID() {
		return this.throwerUUID;
	}

	@Nullable
	public Entity getThrower() {
		UUID uuid = this.getThrowerUUID();
		if(uuid == null) {
			this.cachedThrower = null;
		} else if(this.cachedThrower == null || !this.cachedThrower.isEntityAlive() || !this.cachedThrower.getUUID().equals(uuid)) {
			this.cachedThrower = null;
			for(Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(64.0D, 64.0D, 64.0D))) {
				if(entity.getUUID().equals(uuid)) {
					this.cachedThrower = entity;
					break;
				}
			}
		}
		return this.cachedThrower;
	}

	protected void onImpact(RayTraceResult target) {
		if(target.entityHit instanceof EntityFortressBossProjectile == false) {
			if (target.entityHit != null && target.entityHit instanceof LivingEntity) {
				if(target.entityHit instanceof EntityFortressBoss) {
					EntityFortressBoss boss = (EntityFortressBoss) target.entityHit;
					Vector3d ray = new Vector3d(this.motionX, this.motionY, this.motionZ);
					ray = ray.normalize().scale(64.0D);
					int shieldHit = EntityFortressBoss.rayTraceShield(boss.shield, new Vector3d(boss.getX() + EntityFortressBoss.SHIELD_OFFSET_X, boss.getY() + EntityFortressBoss.SHIELD_OFFSET_Y, boss.getZ() + EntityFortressBoss.SHIELD_OFFSET_Z), boss.getShieldRotationYaw(1), boss.getShieldRotationPitch(1), boss.getShieldRotationYaw(1), boss.getShieldExplosion(1), new Vector3d(this.getX(), this.getY(), this.getZ()), ray, false);
					if(shieldHit >= 0) {
						if(!this.level.isClientSide()) {
							boss.shield.setActive(shieldHit, false);

							this.world.playLocalSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.FORTRESS_BOSS_SHIELD_DOWN, SoundCategory.HOSTILE, 1.0F, 1.0F);

							double angle = Math.PI * 2.0D / 18;
							for(int i = 0; i < 18; i++) {
								Vector3d dir = new Vector3d(Math.sin(angle * i), 0, Math.cos(angle * i));
								dir = dir.normalize();
								float speed = 0.8F;
								EntityFortressBossProjectile bullet = new EntityFortressBossProjectile(this.world, this.getOwner());
								bullet.moveTo(boss.getX(), boss.getY(), boss.getZ(), 0, 0);
								bullet.shoot(dir.x, dir.y, dir.z, speed, 0.0F);
								this.world.addFreshEntity(bullet);
							}
						}
					} else {
						boss.hurt(DamageSource.GENERIC, 10);
					}

					if(!this.level.isClientSide()) {
						boss.setFloating(false);
					}
				} else {
					target.entityHit.hurt(DamageSource.causeIndirectMagicDamage(this, this.getOwner()), 2);
				}

				if(!this.level.isClientSide()) {
					this.remove();
				}
			} else if(target.typeOfHit == RayTraceResult.Type.BLOCK) {
				this.remove();
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else {
			if(this.isDeflectable()) {
				this.markVelocityChanged();
				if (source.getTrueSource() instanceof PlayerEntity) {
					ItemStack heldItem = ((PlayerEntity)source.getTrueSource()).getItemInHand(Hand.MAIN_HAND);
					if(heldItem != null && heldItem.getItem() instanceof ItemSword) {
						if(!this.level.isClientSide() && source.getTrueSource().getPassengers().isEmpty()) {
							this.startRiding(source.getTrueSource(), true);
							this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(source.getTrueSource()));
							return true;
						}
					}
				}
			} else {
				if(!this.level.isClientSide()) {
					this.remove();
				}
			}
			return false;
		}
	}

	@Override
	public void tick() {
		if(!this.level.isClientSide() && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.remove();
			return;
		}

		if(!this.isDead) {
			if(this.getRidingEntity() == null) {
				this.ticksInAir++;

				if(this.ticksInAir > 200) {
					this.remove();
				}

				Vector3d currentPos = new Vector3d(this.getX(), this.getY(), this.getZ());
				Vector3d nextPos = new Vector3d(this.getX() + this.motionX, this.getY() + this.motionY, this.getZ() + this.motionZ);
				RayTraceResult hitObject = this.world.rayTraceBlocks(currentPos, nextPos);
				currentPos = new Vector3d(this.getX(), this.getY(), this.getZ());
				nextPos = new Vector3d(this.getX() + this.motionX, this.getY() + this.motionY, this.getZ() + this.motionZ);

				if (hitObject != null) {
					nextPos = new Vector3d(hitObject.hitVec.x, hitObject.hitVec.y, hitObject.hitVec.z);
				}

				Entity hitEntity = null;
				List<Entity> hitEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().inflate(this.motionX, this.motionY, this.motionZ).inflate(0.1D, 0.1D, 0.1D));
				double minDist = 0.0D;

				for (int i = 0; i < hitEntities.size(); ++i) {
					Entity entity = (Entity)hitEntities.get(i);
					if (entity.canBeCollidedWith() && entity != this.getThrower() && entity != this) {
						float f = 0.1F;
						AxisAlignedBB axisalignedbb = entity.getBoundingBox().inflate((double)f, (double)f, (double)f);
						RayTraceResult movingobjectposition1 = axisalignedbb.calculateIntercept(currentPos, nextPos);
						if (movingobjectposition1 != null) {
							double d1 = currentPos.distanceTo(movingobjectposition1.hitVec);
							if (d1 < minDist || minDist == 0.0D) {
								hitEntity = entity;
								minDist = d1;
							}
						}
					}
				}

				if (hitEntity != null) {
					hitObject = new RayTraceResult(hitEntity);
				}

				if (hitObject != null && hitObject.entityHit != this.getThrower()) {
					this.onImpact(hitObject);
				}
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			} else {
				if(this.getRidingEntity() instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) this.getRidingEntity();
					ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
					if(!this.isDeflectable() || heldItem == null || heldItem.getItem() instanceof ItemSword == false) {
						if(!this.level.isClientSide()) {
							this.remove();
						}
					} else {
						player.setInWeb();
						player.motionY -= 1.5D;
						if(player.isSwingInProgress) {
							if(this.canDismount) {
								Vector3d look = this.getRidingEntity().getLookVec();
								look.normalize();
								this.shoot(look.x, look.y, look.z, 0.5F, 0.0F);
								this.dismountRidingEntity();
								this.setThrower(player);
							}
						} else {
							this.canDismount = true;
						}
					}
				}
			}
		}

		super.tick();
	}

	@Override
	public void shoot(double x, double y, double z, float speed, float randMotion) {
		float f2 = MathHelper.sqrt(x * x + y * y + z * z);
		x /= (double)f2;
		y /= (double)f2;
		z /= (double)f2;
		x += this.random.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		y += this.random.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		z += this.random.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		x *= (double)speed;
		y *= (double)speed;
		z *= (double)speed;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f3 = MathHelper.sqrt(x * x + z * z);
		this.prevRotationYaw = this.yRot = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.xRot = (float)(Math.atan2(y, (double)f3) * 180.0D / Math.PI);
		this.velocityChanged = true;
	}

	@Override
	public void load(CompoundNBT nbt) {
		if(nbt.hasUUID("owner")) {
			this.getEntityData().set(OWNER, Optional.of(nbt.getUUID("owner")));
		} else {
			this.getEntityData().set(OWNER, Optional.absent());
		}
		if(nbt.hasUUID("thrower")) {
			this.throwerUUID = nbt.getUUID("thrower");
		} else {
			this.throwerUUID = null;
		}
		this.ticksInAir = nbt.getInt("ticksInAir");
		this.canDismount = nbt.getBoolean("canDismount");
		this.setDeflectable(nbt.getBoolean("deflectable"));
	}

	@Override
	public void save(CompoundNBT nbt) {
		if(this.getOwnerUUID() != null) {
			nbt.putUUID("owner", this.getOwnerUUID());
		}
		if(this.getThrowerUUID() != null) {
			nbt.putUUID("thrower", this.getThrowerUUID());
		}
		nbt.putInt("ticksInAir", this.ticksInAir);
		nbt.putBoolean("canDismount", this.canDismount);
		nbt.putBoolean("deflectable", this.isDeflectable());
	}
}