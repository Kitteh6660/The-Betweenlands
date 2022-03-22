package thebetweenlands.common.entity.ai;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowTarget extends EntityAIBase {
	public static class FollowClosest implements Supplier<LivingEntity> {
		private double range;
		private Class<? extends LivingEntity> type;
		private final MobEntity taskOwner;

		public FollowClosest(MobEntity taskOwner, Class<? extends LivingEntity> type, double range) {
			this.taskOwner = taskOwner;
			this.type = type;
			this.range = range;
		}

		@Override
		public LivingEntity get() {
			List<LivingEntity> entities = this.taskOwner.world.getEntitiesOfClass(this.type, this.taskOwner.getBoundingBox().grow(this.range));
			LivingEntity closest = null;
			for(LivingEntity entity : entities) {
				if(closest == null || entity.getDistanceSq(this.taskOwner) < closest.getDistanceSq(this.taskOwner)) {
					closest = entity;
				}
			}
			return closest;
		}
	}

	protected final MobEntity taskOwner;
	protected final Supplier<LivingEntity> target;
	protected World theWorld;
	protected final double speed;
	protected final PathNavigate navigator;
	protected int timeToRecalcPath;
	protected float maxDist;
	protected float minDist;
	protected float oldWaterCost;
	protected boolean teleport;

	public EntityAIFollowTarget(MobEntity taskOwner, Supplier<LivingEntity> target, double speed, float minDist, float maxDist, boolean teleport) {
		this.taskOwner = taskOwner;
		this.theWorld = taskOwner.world;
		this.target = target;
		this.speed = speed;
		this.navigator = taskOwner.getNavigator();
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.teleport = teleport;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		LivingEntity target = this.target.get();

		if (target == null) {
			return false;
		} else if (target instanceof PlayerEntity && ((PlayerEntity)target).isSpectator()) {
			return false;
		} else if (this.taskOwner.getDistanceSq(target) < (double)(this.minDist * this.minDist)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		LivingEntity target = this.target.get();
		return target != null && !this.navigator.noPath() && this.taskOwner.getDistanceSq(target) > (double)(this.maxDist * this.maxDist);
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.taskOwner.getPathPriority(PathNodeType.WATER);
		this.taskOwner.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	@Override
	public void resetTask() {
		this.navigator.clearPath();
		this.taskOwner.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	private boolean isEmptyBlock(BlockPos pos) {
		BlockState blockState = this.theWorld.getBlockState(pos);
		return blockState.getMaterial() == Material.AIR ? true : !blockState.isFullCube();
	}

	@Override
	public void updateTask() {
		LivingEntity target = this.target.get();

		if(target != null) {
			this.taskOwner.getLookHelper().setLookPositionWithEntity(target, 10.0F, (float)this.taskOwner.getVerticalFaceSpeed());

			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10;

				if (!this.navigator.tryMoveToEntityLiving(target, this.speed) && this.teleport) {
					if (!this.taskOwner.getLeashed()) {
						if (this.taskOwner.getDistanceSq(target) >= 144.0D) {
							int i = MathHelper.floor(target.getX()) - 2;
							int j = MathHelper.floor(target.getZ()) - 2;
							int k = MathHelper.floor(target.getBoundingBox().minY);

							for (int l = 0; l <= 4; ++l) {
								for (int i1 = 0; i1 <= 4; ++i1) {
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i1)).isBlockNormalCube() && this.isEmptyBlock(new BlockPos(i + l, k, j + i1)) && this.isEmptyBlock(new BlockPos(i + l, k + 1, j + i1))) {
										this.taskOwner.moveTo((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.taskOwner.yRot, this.taskOwner.xRot);
										this.navigator.clearPath();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}