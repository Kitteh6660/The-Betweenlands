package thebetweenlands.common.entity.ai.puppet;

import javax.annotation.Nullable;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

public class EntityAIGoTo extends Goal {
	
	protected final MobEntity taskOwner;
	protected BlockPos pos;
	protected int delayCounter;
	protected Path entityPathEntity;
	protected double speed;
	protected int failedPathFindingPenalty = 0;

	public EntityAIGoTo(MobEntity taskOwner, double speed) {
		this.taskOwner = taskOwner;
		this.speed = speed;
	}

	public void setTarget(@Nullable BlockPos pos) {
		this.pos = pos;
		this.failedPathFindingPenalty = 0;
		this.taskOwner.getNavigation().stop();;
	}
	
	public BlockPos getTarget() {
		return this.pos;
	}

	@Override
	public boolean canUse() {
		if(this.pos != null) {
			if (--this.delayCounter <= 0) {
				this.entityPathEntity = this.taskOwner.getNavigation().getPathToPos(this.pos);
				this.delayCounter = 4 + this.taskOwner.getRandom().nextInt(7);
				return this.entityPathEntity != null;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return this.pos != null;
	}

	@Override
	public void start() {
		this.taskOwner.getNavigation().createPath(this.entityPathEntity, this.speed);
		this.delayCounter = 0;
	}

	@Override
	public void stop() {
		this.taskOwner.getNavigation().stop();
	}

	@Override
	public void updateTask() {
		if(this.pos != null) {
			this.taskOwner.getLookHelper().setLookPosition(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 30.0F, 30.0F);

			double dist = this.taskOwner.getDistanceSq(this.pos);

			if(dist < 1.0D) {
				this.pos = null;
				return;
			}

			--this.delayCounter;

			if (this.delayCounter <= 0) {
				this.delayCounter = 2 + this.taskOwner.getRandom().nextInt(5);

				this.delayCounter += this.failedPathFindingPenalty;

				if (this.taskOwner.getNavigation().getPath() != null) {
					PathPoint finalPathPoint = this.taskOwner.getNavigation().getPath().getFinalPathPoint();
					if (finalPathPoint != null && this.pos.getDistance(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1) {
						this.failedPathFindingPenalty = 0;
					} else {
						this.failedPathFindingPenalty += 6;
					}
				} else {
					this.failedPathFindingPenalty += 6;
				}

				if (dist > 1024.0D) {
					this.delayCounter += 6;
				} else if (dist > 256.0D) {
					this.delayCounter += 3;
				}

				if (!this.taskOwner.getNavigation().tryMoveToXYZ(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, this.speed)) {
					this.delayCounter += 15;
				}
			}
		}
	}
}
