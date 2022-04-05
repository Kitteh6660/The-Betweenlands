package thebetweenlands.common.entity.projectiles;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.entity.mobs.EntityTarminion;

public class EntityThrownTarminion extends EntityThrowable {
	private UUID ownerUUID = null;

	public EntityThrownTarminion(World world) {
		super(world);
	}

	public EntityThrownTarminion(World world, LivingEntity entity) {
		super(world, entity);
		this.ownerUUID = entity.getUUID();
	}

	public EntityThrownTarminion(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putUUID("owner", this.ownerUUID);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		this.ownerUUID = nbt.getUUID("owner");
	}

	@Override
	public void tick() {
		super.tick();
		if(!this.onGround) {
			if (this.level.isClientSide()) {
				BLParticles.TAR_BEAST_DRIP.spawn(this.world, this.getX(), this.getY(), this.getZ());
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit != null && result.entityHit instanceof LivingEntity) {
			if(!(result.entityHit instanceof EntityTarminion)) {
				if(!this.level.isClientSide()) {
					result.entityHit.hurt(DamageSource.causeThrownDamage(this, getThrower()), 2);

					if (this.isOnFire() && !(result.entityHit instanceof EntityEnderman)) {
						result.entityHit.setFire(5);
					}
				}

				if(this.level.isClientSide()) {
					for (int i = 0; i < 8; i++) {
						BLParticles.SPLASH_TAR.spawn(this.world, this.getX(), this.getY(), this.getZ());
					}
				}
			}

			if (!this.level.isClientSide()) {
				EntityTarminion tarminion = spawnTarminion();
				if(result.entityHit instanceof EntityMob) {
					tarminion.setAttackTarget((LivingEntity) result.entityHit);
				}
			}
		} else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			if (!this.level.isClientSide()) {
				spawnTarminion();
			}
		}
	}

	private EntityTarminion spawnTarminion() {
		if (!this.level.isClientSide()) {
			this.remove();
			EntityTarminion tarminion = new EntityTarminion(this.world);
			tarminion.setTamed(true);
			if(this.ownerUUID != null) {
				tarminion.setOwnerId(this.ownerUUID);
			}
			Vector3d motionVec = new Vector3d(this.motionX, this.motionY, this.motionZ);
			motionVec = motionVec.normalize();
			double speed = 0.25D;
			tarminion.motionX = motionVec.x * speed;
			tarminion.motionY = motionVec.y * speed;
			tarminion.motionZ = motionVec.z * speed;
			tarminion.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
			this.world.addFreshEntity(tarminion);
			return tarminion;
		}
		return null;
	}
}