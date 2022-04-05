package thebetweenlands.common.entity.projectiles;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityPredatorArrowGuide extends Entity {
	private static final DataParameter<Integer> TARGET = EntityDataManager.defineId(EntityPredatorArrowGuide.class, DataSerializers.INT);

	private int cachedTargetId = -1;
	private Entity cachedTarget;

	public EntityPredatorArrowGuide(World world) {
		super(world);
		this.setSize(0.1F, 0.1F);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(TARGET, -1);
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public void load(CompoundNBT nbt) {

	}

	@Override
	public void save(CompoundNBT nbt) {

	}

	@Override
	public void tick() {
		super.tick();

		Entity mountedEntity = this.getRidingEntity();
		if(mountedEntity == null) {
			if(!this.level.isClientSide()) {
				this.remove();
			}
		} else {
			this.updateHomingTrajectory(mountedEntity);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		Entity ridingEntity = this.getRidingEntity();
		return ridingEntity != null ? ridingEntity.getRenderBoundingBox() : super.getRenderBoundingBox();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRender3d(double x, double y, double z) {
		Entity ridingEntity = this.getRidingEntity();
		return ridingEntity != null ? ridingEntity.isInRangeToRender3d(x, y, z) : super.isInRangeToRender3d(x, y, z);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		Entity ridingEntity = this.getRidingEntity();
		return ridingEntity != null ? ridingEntity.isInRangeToRenderDist(distance) : super.isInRangeToRenderDist(distance);
	}

	public void setTarget(@Nullable Entity target) {
		this.entityData.set(TARGET, target == null ? -1 : target.getEntityId());
	}

	public Entity getTarget() {
		int targetId = this.entityData.get(TARGET);
		if(targetId >= 0) {
			if(targetId == this.cachedTargetId) {
				return this.cachedTarget;
			} else {
				return this.cachedTarget = this.world.getEntityByID(this.cachedTargetId = targetId);
			}
		}
		this.cachedTarget = null;
		this.cachedTargetId = -1;
		return null;
	}

	protected void updateHomingTrajectory(Entity mountedEntity) {
		if(!this.level.isClientSide()) {
			Vector3d motion = new Vector3d(mountedEntity.motionX, mountedEntity.motionY, mountedEntity.motionZ);
			double speed = motion.length();

			LivingEntity bestTarget = null;

			float maxFollowReach = 24.0F;

			float maxFollowAngle = 20.0F;

			float correctionMultiplier = 0.15F;

			double distanceMovedSq = (mountedEntity.xOld - mountedEntity.getX()) * (mountedEntity.xOld - mountedEntity.getX()) + (mountedEntity.yOld - mountedEntity.getY()) * (mountedEntity.yOld - mountedEntity.getY()) + (mountedEntity.zOld - mountedEntity.getZ()) * (mountedEntity.zOld - mountedEntity.getZ());

			if(speed > 0.1D && distanceMovedSq > 0.01D && !mountedEntity.onGround) {
				Vector3d heading = motion.normalize();

				List<LivingEntity> nearbyEntities = this.world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(maxFollowReach), entity -> entity.isEntityAlive() && entity instanceof IMob);

				double bestTargetScore = Double.MAX_VALUE;

				for(LivingEntity entity : nearbyEntities) {
					double dist = entity.getDistance(mountedEntity);
					if(dist < maxFollowReach) {
						Vector3d dir = entity.getPositionEyes(1).subtract(mountedEntity.getPositionEyes(1)).normalize();

						double angle = Math.toDegrees(Math.acos(heading.dotProduct(dir)));
						if(angle < maxFollowAngle) {
							double score = dist * Math.max(angle, 0.01D);

							if(bestTarget == null || score < bestTargetScore) {
								if(entity.canSee(mountedEntity)) {
									bestTarget = entity;
									bestTargetScore = score;
								}
							}
						}
					}
				}
			}

			this.setTarget(bestTarget);

			if(bestTarget != null) {
				Vector3d newMotion = bestTarget.getPositionEyes(1).subtract(mountedEntity.getPositionEyes(1)).normalize().scale(speed * correctionMultiplier).add(motion.scale(1 - correctionMultiplier));
				mountedEntity.motionX = newMotion.x;
				mountedEntity.motionY = newMotion.y;
				mountedEntity.motionZ = newMotion.z;
				mountedEntity.velocityChanged = true;
			}
		}
	}
}
