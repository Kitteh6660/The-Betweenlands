package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.ai.attributes.Attributes.RangedAttribute;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.common.capability.collision.RingOfDispersionEntityCapability;

public abstract class EntityMovingWallFace extends EntityWallFace {
	public static final IAttribute BLOCK_TRACKING_SEARCH_RANGE = (new RangedAttribute((IAttribute)null, "bl.blockTrackingSearchRange", 8.0D, 0.0D, 32.0D)).setDescription("Block Tracking Search Range");
	public static final IAttribute BLOCK_FIX_SEARCH_RANGE = (new RangedAttribute((IAttribute)null, "bl.blockFixSearchRange", 8.0D, 0.0D, 32.0D)).setDescription("Block Fix Search Range");

	public EntityMovingWallFace(World world) {
		super(world);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(BLOCK_TRACKING_SEARCH_RANGE);
		this.getAttributeMap().registerAttribute(BLOCK_FIX_SEARCH_RANGE);
		this.getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(48.0D);
	}

	@Override
	public void tick() {
		super.tick();

		if(this.isMoving() && this.level.isClientSide()) {
			if(this.tickCount % 3 == 0) {
				Direction facing = this.getFacing();
				double px = this.getX() + facing.getStepX() * this.width / 2;
				double py = this.getY() + this.height / 2 + facing.getStepY() * this.height / 2;
				double pz = this.getZ() + facing.getStepZ() * this.width / 2;
				for(int i = 0; i < 24; i++) {
					double rx = (this.world.rand.nextDouble() - 0.5D) * this.width;
					double ry = (this.world.rand.nextDouble() - 0.5D) * this.height;
					double rz = (this.world.rand.nextDouble() - 0.5D) * this.width;
					BlockPos pos = new BlockPos(px + rx, py + ry, pz + rz);
					BlockState state = this.world.getBlockState(pos);
					if(!state.getBlock().isAir(state, this.world, pos)) {
						double mx = facing.getStepX() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						double my = facing.getStepY() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						double mz = facing.getStepZ() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + rx, py + ry, pz + rz, mx, my, mz, Block.getStateId(state));
					}
				}
			}
		}
	}

	@Override
	public boolean canMoveFaceInto(BlockPos pos, Direction facing, Direction facingUp) {
		BlockState state = this.world.getBlockState(pos);
		if(state.getMaterial().isLiquid() || state.getBlock().isAir(state, this.world, pos)) {
			return true;
		}
		if(state.getBlock().isLeaves(state, this.world, pos)) {
			return false;
		}
		List<AxisAlignedBB> collisionBoxes = new ArrayList<>();
		state.addCollisionBoxToList(this.world, pos, new AxisAlignedBB(pos), collisionBoxes, this, false);
		return collisionBoxes.isEmpty();
	}

	@Override
	protected void fixUnsuitablePosition(int violatedChecks) {
		if(this.tickCount % 3 == 0) {
			int searchRange = MathHelper.ceil(this.getEntityAttribute(BLOCK_FIX_SEARCH_RANGE).getAttributeValue());

			for(int i = 0; i < 50; i++) {
				float rx = this.world.rand.nextFloat() * 2 - 1;
				float ry = this.world.rand.nextFloat() * 2 - 1;
				float rz = this.world.rand.nextFloat() * 2 - 1;
				BlockPos rndPos = new BlockPos(this.getX() + this.world.rand.nextInt(searchRange * 2) - searchRange, this.getY() + this.height / 2 + this.world.rand.nextInt(searchRange * 2) - searchRange, this.getZ() + this.world.rand.nextInt(searchRange * 2) - searchRange);
				if(this.world.isBlockLoaded(rndPos)) {
					Vector3d pos = new Vector3d(rndPos.getX() + 0.5D, rndPos.getY() + 0.5D, rndPos.getZ() + 0.5D);
					if(this.checkAnchorAt(pos, new Vector3d(rx, ry, rz), AnchorChecks.ALL) == 0) {
						this.lookHelper.setLookDirection(rx, ry, rz);
						this.moveHelper.setMoveTo(pos.x, pos.y, pos.z, 1);
						break;
					}
				}
			}
		}
	}

	protected abstract boolean isValidBlockForMovement(BlockPos pos, BlockState state);

	public List<BlockPos> findNearbyBlocksForMovement() {
		final int radius = MathHelper.ceil(this.getEntityAttribute(BLOCK_TRACKING_SEARCH_RANGE).getAttributeValue());
		BlockPos center = new BlockPos(this);
		List<BlockPos> blocks = new ArrayList<>();
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					pos.setPos(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					BlockState state = this.world.getBlockState(pos);
					if (this.isValidBlockForMovement(pos, state)) {
						blocks.add(pos.toImmutable());
					}
				}
			}
		}
		return blocks;
	}

	protected static abstract class AITrackTarget<T extends EntityMovingWallFace> extends EntityAIBase {
		protected final T entity;

		protected int findBlocksCooldown = 0;
		protected List<BlockPos> trackingBlocks;

		protected int checkCooldown = 0;

		protected boolean stayInRange;
		protected double maxRangeSq;

		public AITrackTarget(T entity) {
			this(entity, false, 0);
		}

		public AITrackTarget(T entity, boolean stayInRange, double maxRange) {
			this.entity = entity;
			this.stayInRange = stayInRange;
			this.maxRangeSq = maxRange * maxRange;
			this.setMutexBits(3);
		}

		protected abstract boolean canMove();

		protected boolean isTargetVisibleAndInRange() {
			return this.entity.getEntitySenses().canSee(this.entity.getAttackTarget()) && (!this.stayInRange || this.entity.getAttackTarget().getDistanceSq(this.entity) <= this.maxRangeSq);
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.isEntityAlive() && this.canMove() && !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() && !this.isTargetVisibleAndInRange();
		}

		@Override
		public void startExecuting() {
			this.checkCooldown = 0;
			this.findBlocksCooldown = 20 + this.entity.rand.nextInt(30);
			this.trackingBlocks = null;
		}

		@Override
		public void updateTask() {
			LivingEntity target = this.entity.getAttackTarget();

			if(target != null && this.canMove()) {
				if(this.findBlocksCooldown <= 0 && (this.trackingBlocks == null || this.trackingBlocks.isEmpty())) {
					this.findBlocksCooldown = 20 + this.entity.rand.nextInt(40);
					this.trackingBlocks = this.entity.findNearbyBlocksForMovement();
				}

				if(this.trackingBlocks != null && !this.trackingBlocks.isEmpty() && this.checkCooldown <= 0) {
					this.checkCooldown = 5 + this.entity.rand.nextInt(15);

					for(int i = 0; i < 16; i++) {
						if(this.trackingBlocks.isEmpty()) {
							break;
						}

						BlockPos pos = this.trackingBlocks.remove(this.entity.rand.nextInt(this.trackingBlocks.size()));

						if(!this.stayInRange || target.getDistanceSqToCenter(pos) <= this.maxRangeSq) {
							Vector3d center = new Vector3d(pos.getX() + this.entity.getBlockWidth() / 2.0D, pos.getY() + this.entity.getBlockHeight() / 2.0D, pos.getZ() + this.entity.getBlockWidth() / 2.0D);
							Vector3d lookDir = target.getPositionVector().add(0, target.getEyeHeight(), 0).subtract(center);

							Direction facing = Direction.getNearest((float)lookDir.x, (float)lookDir.y, (float)lookDir.z);

							if(this.canSeeFrom(pos, facing, target) && this.entity.checkAnchorAt(center, lookDir, AnchorChecks.ALL) == 0) {
								this.entity.moveHelper.setMoveTo(center.x, center.y, center.z, 1);
								this.entity.lookHelper.setLookDirection(facing.getStepX(), facing.getStepY(), facing.getStepZ());
								break;
							} else {
								for(Direction otherFacing : Direction.HORIZONTALS) {
									if(otherFacing != facing) {
										lookDir = new Vector3d(otherFacing.getStepX(), 0, otherFacing.getStepZ());

										if(this.canSeeFrom(pos, otherFacing, target) && this.entity.checkAnchorAt(center, lookDir, AnchorChecks.ALL) == 0) {
											this.entity.moveHelper.setMoveTo(center.x, center.y, center.z, 1);
											this.entity.lookHelper.setLookDirection(otherFacing.getStepX(), otherFacing.getStepY(), otherFacing.getStepZ());
											break;
										} 
									}
								}
							}
						}
					}
				}

				this.checkCooldown--;
				this.findBlocksCooldown--;
			}
		}

		protected boolean canSeeFrom(BlockPos pos, Direction facing, Entity entity) {
			return this.entity.world.rayTraceBlocks(new Vector3d(pos.getX() + this.entity.getBlockWidth() / 2.0D + facing.getStepX() * (this.entity.width / 2 + this.entity.getPeek()), pos.getY() + this.entity.getBlockHeight() / 2.0D + facing.getStepY() * (this.entity.height / 2 + this.entity.getPeek()), pos.getZ() + this.entity.getBlockWidth() / 2.0D + facing.getStepZ() * (this.entity.width / 2 + this.entity.getPeek())), new Vector3d(entity.getX(), entity.getY() + (double)entity.getEyeHeight(), entity.getZ()), false, true, false) == null;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() && !this.isTargetVisibleAndInRange();
		}
	}

	protected static abstract class AIWander<T extends EntityMovingWallFace> extends EntityAIBase {
		protected final T entity;

		protected int findBlocksCooldown = 0;
		protected List<BlockPos> trackingBlocks;

		protected int checkCooldown = 0;

		protected double speed;
		protected int chance;

		protected double range;
		protected double rangeSq;

		protected boolean wandered = false;

		public AIWander(T entity, double range, double speed) {
			this(entity, range, speed, 120);
		}

		public AIWander(T entity, double range, double speed, int chance) {
			this.entity = entity;
			this.range = range;
			this.rangeSq = range * range;
			this.speed = speed;
			this.chance = chance;
			this.setMutexBits(1);
		}

		protected abstract boolean canMove();

		@Override
		public boolean shouldExecute() {
			return this.canMove() && !this.entity.isMoving() && this.entity.getIdleTime() < 100 && this.entity.getRNG().nextInt(this.chance) == 0;
		}

		@Override
		public void startExecuting() {
			this.checkCooldown = 0;
			this.findBlocksCooldown = 20 + this.entity.getRNG().nextInt(30);
			this.trackingBlocks = null;
			this.wandered = false;
		}

		@Override
		public void updateTask() {
			if(this.findBlocksCooldown <= 0 && (this.trackingBlocks == null || this.trackingBlocks.isEmpty())) {
				this.findBlocksCooldown = 40 + this.entity.getRNG().nextInt(60);
				this.trackingBlocks = this.entity.findNearbyBlocksForMovement();
			}

			if(this.trackingBlocks != null && !this.trackingBlocks.isEmpty() && this.checkCooldown <= 0) {
				this.checkCooldown = 5 + this.entity.getRNG().nextInt(15);

				for(int i = 0; i < 16; i++) {
					if(this.trackingBlocks.isEmpty()) {
						break;
					}

					BlockPos pos = this.trackingBlocks.remove(this.entity.getRNG().nextInt(this.trackingBlocks.size()));

					if(this.entity.getDistanceSqToCenter(pos) <= this.rangeSq && this.entity.isWithinHomeDistanceFromPosition(pos)) {
						Vector3d center = new Vector3d(pos.getX() + this.entity.getBlockWidth() / 2.0D, pos.getY() + this.entity.getBlockHeight() / 2.0D, pos.getZ() + this.entity.getBlockWidth() / 2.0D);
						double dx = this.entity.getRNG().nextDouble() - 0.5D;
						double dy = this.entity.getRNG().nextDouble() - 0.5D;
						double dz = this.entity.getRNG().nextDouble() - 0.5D;
						if(this.entity.checkAnchorAt(center, new Vector3d(dx, dy, dz), AnchorChecks.ALL) == 0) {
							this.entity.getMoveHelper().setMoveTo(center.x, center.y, center.z, this.speed);
							this.entity.getLookHelper().setLookDirection(dx, dy, dz);
							this.wandered = true;
							return;
						}
					}
				}
			}

			this.checkCooldown--;
			this.findBlocksCooldown--;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !this.wandered && this.canMove() && !this.entity.isMoving();
		}
	}

	protected static class AIAttackMelee extends EntityAIAttackMelee {
		public AIAttackMelee(EntityCreature creature, double speedIn, boolean useLongMemory) {
			super(creature, speedIn, useLongMemory);
		}

		protected boolean isInReach(LivingEntity enemy, double reach) {
			double dist = Math.max(0, RingOfDispersionEntityCapability.calculateAABBDistance(this.attacker.getBoundingBox(), enemy.getBoundingBox()));
			return dist <= reach;
		}

		@Override
		protected double getAttackReachSqr(LivingEntity attackTarget) {
			float r1 = Math.max(attackTarget.width, attackTarget.height);
			float r2 = Math.max(this.attacker.width, this.attacker.height);
			double reach = this.getTrueAttackReach(attackTarget);
			return this.isInReach(attackTarget, reach) ? (r1 * r1  + r2 * r2 + reach * reach) * 2 : 0;
		}

		protected double getTrueAttackReach(LivingEntity attackTarget) {
			return 0.25D;
		}
	}
}
