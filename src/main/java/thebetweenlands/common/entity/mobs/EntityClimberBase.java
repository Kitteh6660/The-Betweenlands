package thebetweenlands.common.entity.mobs;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.Attributes.ModifiableAttributeInstance;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.movement.ClimberMoveHelper;
import thebetweenlands.common.entity.movement.IPathObstructionAwareEntity;
import thebetweenlands.common.entity.movement.ObstructionAwarePathNavigateClimber;
import thebetweenlands.common.entity.movement.ObstructionAwarePathNavigateGround;
import thebetweenlands.util.BoxSmoothingUtil;
import thebetweenlands.util.Matrix;

public abstract class EntityClimberBase extends EntityCreature implements IEntityBL, IPathObstructionAwareEntity {

	public static final DataParameter<Optional<BlockPos>> PATHING_TARGET = EntityDataManager.defineId(EntityClimberBase.class, DataSerializers.OPTIONAL_BLOCK_POS);

	public double prevStickingOffsetX, prevStickingOffsetY, prevStickingOffsetZ;
	public double stickingOffsetX, stickingOffsetY, stickingOffsetZ;

	public Vector3d orientationNormal = new Vector3d(0, 1, 0);
	public Vector3d prevOrientationNormal = new Vector3d(0, 1, 0);

	protected float collisionsInclusionRange = 2.0f;
	protected float collisionsSmoothingRange = 1.25f;

	protected Vector3d attachedSides = new Vector3d(0, 0, 0);
	protected Vector3d prevAttachedSides = new Vector3d(0, 0, 0);

	public EntityClimberBase(World world) {
		super(world);
		this.setSize(0.85F, 0.85F);
		this.moveControl = new ClimberMoveHelper(this);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(PATHING_TARGET, Optional.absent());
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		ObstructionAwarePathNavigateGround<EntityClimberBase> navigate = new ObstructionAwarePathNavigateClimber<EntityClimberBase>(this, worldIn, false, true, true);
		navigate.setCanSwim(true);
		return navigate;
	}

	@Override
	public float getBridgePathingMalus(MobEntity entity, BlockPos pos, PathPoint fallPathPoint) {
		return -1.0f;
	}

	@Override
	public void onPathingObstructed(Direction facing) {

	}

	@Override
	public int getMaxFallHeight() {
		return 0;
	}

	public float getMovementSpeed() {
		ModifiableAttributeInstance attribute = this.getAttributeMap().getAttributeInstance(Attributes.MOVEMENT_SPEED);
		return attribute != null ? (float) attribute.getValue() : 1.0f;
	}

	public Pair<Direction, Vector3d> getWalkingSide() {
		Direction avoidPathingFacing = null;

		Path path = this.getNavigation().getPath();
		if(path != null) {
			int index = path.getCurrentPathIndex();

			if(index < path.getCurrentPathLength()) {
				PathPoint point = path.getPathPointFromIndex(index);

				double maxDist = 0;

				for(Direction facing : Direction.VALUES) {
					double posEntity = Math.abs(facing.getStepX()) * this.getX() + Math.abs(facing.getStepY()) * this.getY() + Math.abs(facing.getStepZ()) * this.getZ();
					double posPath = Math.abs(facing.getStepX()) * point.x + Math.abs(facing.getStepY()) * point.y + Math.abs(facing.getStepZ()) * point.z;

					double distSigned = posPath + 0.5f - posEntity;
					if(distSigned * (facing.getStepX() + facing.getStepY() + facing.getStepZ()) > 0) {
						double dist = Math.abs(distSigned) - (facing.getAxis().isHorizontal() ? this.width / 2 : (facing == Direction.DOWN ? 0 : this.height));

						if(dist > maxDist) {
							maxDist = dist;

							if(dist < 1.732f) {
								avoidPathingFacing = facing.getOpposite();
							} else {
								//Don't avoid facing if further away than 1 block diagonal, otherwise it could start floating around
								//if next path point is still too far away
								avoidPathingFacing = null;
							}
						}
					}
				}
			}
		}

		AxisAlignedBB entityBox = this.getBoundingBox();

		double closestFacingDst = Double.MAX_VALUE;
		Direction closestFacing = null;

		Vector3d weighting = new Vector3d(0, 0, 0);

		float stickingDistance = this.zza != 0 ? 1.5f : 0.1f;

		for(Direction facing : Direction.VALUES) {
			if(avoidPathingFacing == facing) {
				continue;
			}

			List<AxisAlignedBB> collisionBoxes = this.world.getBlockCollisions(this, entityBox.inflate(0.2f).expand(facing.getStepX() * stickingDistance, facing.getStepY() * stickingDistance, facing.getStepZ() * stickingDistance));

			double closestDst = Double.MAX_VALUE;

			for(AxisAlignedBB collisionBox : collisionBoxes) {
				switch(facing) {
				case EAST:
				case WEST:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateXOffset(collisionBox, -facing.getStepX() * stickingDistance)));
					break;
				case UP:
				case DOWN:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateYOffset(collisionBox, -facing.getStepY() * stickingDistance)));
					break;
				case NORTH:
				case SOUTH:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateZOffset(collisionBox, -facing.getStepZ() * stickingDistance)));
					break;
				}
			}

			if(closestDst < closestFacingDst) {
				closestFacingDst = closestDst;
				closestFacing = facing;
			}

			if(closestDst < Double.MAX_VALUE) {
				weighting = weighting.add(new Vector3d(facing.getStepX(), facing.getStepY(), facing.getStepZ()).scale(1 - Math.min(closestDst, stickingDistance) / stickingDistance));
			}
		}

		if(closestFacing == null) {
			return Pair.of(Direction.DOWN, new Vector3d(0, -1, 0));
		}

		return Pair.of(closestFacing, weighting.normalize().add(0, -0.001f, 0).normalize());
	}

	public static class Orientation {
		public final Vector3d normal, forward, up, right;
		public final float forwardComponent, upComponent, rightComponent, yaw, pitch;

		private Orientation(Vector3d normal, Vector3d forward, Vector3d up, Vector3d right, float forwardComponent, float upComponent, float rightComponent, float yaw, float pitch) {
			this.normal = normal;
			this.forward = forward;
			this.up = up;
			this.right = right;
			this.forwardComponent = forwardComponent;
			this.upComponent = upComponent;
			this.rightComponent = rightComponent;
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public Vector3d getForward(float yaw, float pitch) {
			float cosYaw = MathHelper.cos(yaw * 0.017453292F);
			float sinYaw = MathHelper.sin(yaw * 0.017453292F);
			float cosPitch = -MathHelper.cos(-pitch * 0.017453292F);
			float sinPitch = MathHelper.sin(-pitch * 0.017453292F);
			return this.right.scale(sinYaw * cosPitch).add(this.up.scale(sinPitch)).add(this.forward.scale(cosYaw * cosPitch));
		}
	}

	public Orientation getOrientation(float partialTicks) {
		//Big oof, please don't look at this

		Vector3d orientationNormal = this.prevOrientationNormal.add(this.orientationNormal.subtract(this.prevOrientationNormal).scale(partialTicks));

		Vector3d fwdAxis = new Vector3d(0, 0, 1);
		Vector3d upAxis = new Vector3d(0, 1, 0);
		Vector3d rightAxis = new Vector3d(1, 0, 0);

		float fwd = (float)fwdAxis.dotProduct(orientationNormal);
		float up = (float)upAxis.dotProduct(orientationNormal);
		float right = (float)rightAxis.dotProduct(orientationNormal);

		float yaw = (float)Math.toDegrees(Math.atan2(right, fwd));

		fwdAxis = new Vector3d(Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));
		upAxis = new Vector3d(0, 1, 0);
		rightAxis = new Vector3d(Math.sin(Math.toRadians(yaw - 90)), 0, Math.cos(Math.toRadians(yaw - 90)));

		fwd = (float)fwdAxis.dotProduct(orientationNormal);
		up = (float)upAxis.dotProduct(orientationNormal);
		right = (float)rightAxis.dotProduct(orientationNormal);

		float pitch = (float)Math.toDegrees(Math.atan2(fwd, up)) * (float)Math.signum(fwd);

		Matrix m = new Matrix();
		m.rotate(Math.toRadians(yaw), 0, 1, 0);
		m.rotate(Math.toRadians(pitch), 1, 0, 0);
		m.rotate(Math.toRadians((float)Math.signum(0.1f - up) * yaw), 0, 1, 0);

		Vector3d localFwd = m.transform(new Vector3d(0, 0, -1));
		Vector3d localUp = m.transform(new Vector3d(0, 1, 0));
		Vector3d localRight = m.transform(new Vector3d(1, 0, 0));

		return new Orientation(orientationNormal, localFwd, localUp, localRight, fwd, up, right, yaw, pitch);
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.level.isClientSide()) {
			Path path = this.getNavigation().getPath();
			if(path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
				PathPoint point = path.getPathPointFromIndex(path.getCurrentPathIndex());
				this.entityData.set(PATHING_TARGET, Optional.of(new BlockPos(point.x, point.y, point.z)));
			} else {
				this.entityData.set(PATHING_TARGET, Optional.absent());
			}
		}
	}

	@Nullable
	public BlockPos getPathingTarget() {
		return this.entityData.get(PATHING_TARGET).orNull();
	}
	
	protected boolean canAttachToWalls() {
		return !this.isInWater();
	}

	protected void updateOffsetsAndOrientation() {
		Vector3d p = this.getDeltaMovement();

		Vector3d s = p.add(0, this.height / 2, 0);
		AxisAlignedBB inclusionBox = new AxisAlignedBB(s.x, s.y, s.z, s.x, s.y, s.z).inflate(this.collisionsInclusionRange);

		List<AxisAlignedBB> boxes = this.world.getBlockCollisions(this, inclusionBox);

		this.prevOrientationNormal = this.orientationNormal;

		this.prevStickingOffsetX = this.stickingOffsetX;
		this.prevStickingOffsetY = this.stickingOffsetY;
		this.prevStickingOffsetZ = this.stickingOffsetZ;

		Pair<Vector3d, Vector3d> closestSmoothPoint = null;
		if(this.canAttachToWalls()) closestSmoothPoint = BoxSmoothingUtil.findClosestSmoothPoint(boxes, this.collisionsSmoothingRange, 1.0f, 0.005f, 20, 0.05f, s);

		if(closestSmoothPoint != null) {
			this.stickingOffsetX = MathHelper.clamp(closestSmoothPoint.getLeft().x - p.x, -this.width / 2, this.width / 2);
			this.stickingOffsetY = MathHelper.clamp(closestSmoothPoint.getLeft().y - p.y, 0, this.height);
			this.stickingOffsetZ = MathHelper.clamp(closestSmoothPoint.getLeft().z - p.z, -this.width / 2, this.width / 2);

			this.orientationNormal = closestSmoothPoint.getRight();
		} else {
			this.stickingOffsetX *= 0.6f;
			this.stickingOffsetY = this.stickingOffsetY + (0.4f - this.stickingOffsetY) * 0.6f;
			this.stickingOffsetZ *= 0.6f;

			this.orientationNormal = new Vector3d(this.orientationNormal.x * 0.6f, this.orientationNormal.y + (1.0f - this.orientationNormal.y) * 0.4f, this.orientationNormal.z * 0.6f).normalize();
		}
	}

	public Vector3d getStickingForce(Pair<Direction, Vector3d> walkingSide) {
		if(!this.hasNoGravity()) {
			return walkingSide.getRight().scale(0.08f);
		}

		return new Vector3d(0, 0, 0);
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		Orientation orientation = this.getOrientation(1);

		Vector3d forwardVector = orientation.getForward(this.yRot, 0);
		Vector3d upVector = orientation.getForward(this.yRot, -90);

		if(this.isInWater()) {
			super.travel(strafe, vertical, forward);
		} else {
			if(this.isServerWorld() || this.canPassengerSteer()) {
				Pair<Direction, Vector3d> walkingSide = this.getWalkingSide();

				Vector3d stickingForce = this.getStickingForce(walkingSide);

				if(forward != 0) {
					float slipperiness = 0.91f;

					if(this.onGround) {
						BlockPos offsetPos = new BlockPos(this).offset(walkingSide.getLeft());
						BlockState offsetState = this.world.getBlockState(offsetPos);
						slipperiness = offsetState.getBlock().getSlipperiness(offsetState, this.world, offsetPos, this) * 0.91f;
					}

					float friction = (float)forward * 0.16277136F / (slipperiness * slipperiness * slipperiness);

					float f = (float)(forward * forward);
					if(f >= 1.0E-4F) {
						f = Math.max(MathHelper.sqrt(f), 1.0f);
						f = friction / f;

						Vector3d forwardOffset = new Vector3d(forwardVector.x * forward * f, forwardVector.y * forward * f, forwardVector.z * forward * f);

						double px = this.getX();
						double py = this.getY();
						double pz = this.getZ();
						double mx = this.motionX;
						double my = this.motionY;
						double mz = this.motionZ;
						AxisAlignedBB aabb = this.getBoundingBox();

						//Probe actual movement vector
						this.move(MoverType.SELF, forwardOffset.x, forwardOffset.y, forwardOffset.z);

						Vector3d movementDir = new Vector3d(this.getX() - px, this.getY() - py, this.getZ() - pz).normalize();

						this.setEntityBoundingBox(aabb);
						this.resetPositionToBB();
						this.motionX = mx;
						this.motionY = my;
						this.motionZ = mz;

						//Probe collision normal
						Vector3d probeVector = new Vector3d(Math.abs(movementDir.x) < 0.001D ? -Math.signum(upVector.x) : 0, Math.abs(movementDir.y) < 0.001D ? -Math.signum(upVector.y) : 0, Math.abs(movementDir.z) < 0.001D ? -Math.signum(upVector.z) : 0).normalize().scale(0.0001D);
						this.move(MoverType.SELF, probeVector.x, probeVector.y, probeVector.z);

						Vector3d collisionNormal = new Vector3d(Math.abs(this.getX() - px - probeVector.x) > 0.000001D ? Math.signum(-probeVector.x) : 0, Math.abs(this.getY() - py - probeVector.y) > 0.000001D ? Math.signum(-probeVector.y) : 0, Math.abs(this.getZ() - pz - probeVector.z) > 0.000001D ? Math.signum(-probeVector.z) : 0).normalize();

						this.setEntityBoundingBox(aabb);
						this.resetPositionToBB();
						this.motionX = mx;
						this.motionY = my;
						this.motionZ = mz;

						//Movement vector projected to surface
						Vector3d surfaceMovementDir = movementDir.subtract(collisionNormal.scale(collisionNormal.dotProduct(movementDir))).normalize();

						boolean isInnerCorner = Math.abs(collisionNormal.x) + Math.abs(collisionNormal.y) + Math.abs(collisionNormal.z) > 1.0001f;

						//Only project movement vector to surface if not moving across inner corner, otherwise it'd get stuck in the corner
						if(!isInnerCorner) {
							movementDir = surfaceMovementDir;
						}

						//Nullify sticking force along movement vector projected to surface
						stickingForce = stickingForce.subtract(surfaceMovementDir.scale(surfaceMovementDir.normalize().dotProduct(stickingForce)));

						float moveSpeed = forward * f;
						this.motionX += movementDir.x * moveSpeed;
						this.motionY += movementDir.y * moveSpeed;
						this.motionZ += movementDir.z * moveSpeed;
					}
				}

				this.motionX += stickingForce.x;
				this.motionY += stickingForce.y;
				this.motionZ += stickingForce.z;

				double px = this.getX();
				double py = this.getY();
				double pz = this.getZ();
				double mx = this.motionX;
				double my = this.motionY;
				double mz = this.motionZ;

				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

				this.prevAttachedSides = this.attachedSides;
				this.attachedSides = new Vector3d(this.getX() == px ? -Math.signum(mx) : 0, this.getY() == py ? -Math.signum(my) : 0, this.getZ() == pz ? -Math.signum(mz) : 0);

				boolean detachedX = this.attachedSides.x != this.prevAttachedSides.x;
				boolean detachedY = this.attachedSides.y != this.prevAttachedSides.y;
				boolean detachedZ = this.attachedSides.z != this.prevAttachedSides.z;

				if(detachedX || detachedY || detachedZ) {
					//Offset so that AABB is moved above the new surface
					this.move(MoverType.SELF, detachedX ? -this.prevAttachedSides.x * 0.25f : 0, detachedY ? -this.prevAttachedSides.y * 0.25f : 0, detachedZ ? -this.prevAttachedSides.z * 0.25f : 0);

					Vector3d axis = this.prevAttachedSides.normalize();
					Vector3d attachVector = upVector.scale(-1);
					attachVector = attachVector.subtract(axis.scale(axis.dotProduct(attachVector))).normalize();

					double attachDst = MathHelper.sqrt(mx * mx + my * my + mz * mz) + 0.1f;

					AxisAlignedBB aabb = this.getBoundingBox();
					mx = this.motionX;
					my = this.motionY;
					mz = this.motionZ;

					//Offset AABB towards new surface until it touches
					for(int i = 0; i < 10 && !this.onGround; i++) {
						this.move(MoverType.SELF, attachVector.x * attachDst, attachVector.y * attachDst, attachVector.z * attachDst);
					}

					if(!this.onGround) {
						this.setEntityBoundingBox(aabb);
						this.resetPositionToBB();
						this.motionX = mx;
						this.motionY = my;
						this.motionZ = mz;
					}

					this.motionX = this.motionY = this.motionZ = 0;
				}

				float slipperiness = 0.91f;

				if(this.onGround) {
					this.fallDistance = 0;

					BlockPos offsetPos = new BlockPos(this).offset(walkingSide.getLeft());
					BlockState offsetState = this.world.getBlockState(offsetPos);
					slipperiness = offsetState.getBlock().getSlipperiness(offsetState, this.world, offsetPos, this) * 0.91F;
				}

				Vector3d motion = new Vector3d(this.motionX, this.motionY, this.motionZ);
				Vector3d orthogonalMotion = upVector.scale(upVector.dotProduct(motion));
				Vector3d tangentialMotion = motion.subtract(orthogonalMotion);

				this.motionX = tangentialMotion.x * slipperiness + orthogonalMotion.x * 0.98f;
				this.motionY = tangentialMotion.y * slipperiness + orthogonalMotion.y * 0.98f;
				this.motionZ = tangentialMotion.z * slipperiness + orthogonalMotion.z * 0.98f;
			}
		}

		this.updateOffsetsAndOrientation();

		orientation = this.getOrientation(1);

		float rx = (float)orientation.forward.dotProduct(forwardVector);
		float ry = (float)orientation.right.dotProduct(forwardVector);

		this.yRot = MathHelper.wrapDegrees(270.0f - (float)Math.toDegrees(Math.atan2(rx, ry)));

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double traveledX = this.getX() - this.xOld;
		double traveledY = this.getY() - this.yOld;
		double traveledZ = this.getZ() - this.zOld;
		float traveled = Math.min(MathHelper.sqrt(traveledX * traveledX + traveledY * traveledY + traveledZ * traveledZ) * 4.0f, 1.0f);

		this.limbSwingAmount += (traveled - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		double py = this.getY();

		super.move(type, x, y, z);

		if(Math.abs(this.getY() - py - y) > 0.000001D) {
			this.motionY = 0.0D;
		}

		this.onGround = this.collided;
	}
}