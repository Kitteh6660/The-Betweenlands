package thebetweenlands.common.entity.ai.puppet;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.vector.Vector3d;

public class EntityAIStay extends Goal {
	
	protected final MobEntity taskOwner;
	protected boolean stay;
	protected Vector3d pos;
	protected int delayCounter = 0;
	protected int failedPathFindingPenalty = 0;

	public EntityAIStay(MobEntity taskOwner) {
		this.taskOwner = taskOwner;
		this.setMutexBits(1);
	}

	public void setStay(boolean stay) {
		this.stay = stay;
		this.pos = this.taskOwner.getDeltaMovement();
	}

	public boolean getStay() {
		return this.stay;
	}
	
	@Override
	public boolean canUse() {
		return this.stay;
	}

	@Override
	public boolean canContinueToUse() {
		return this.stay;
	}

	@Override
	public void updateTask() {
		PathNavigator navigator = this.taskOwner.getNavigation();
		if(navigator != null && this.pos != null) {
			double dist = this.taskOwner.distanceToSqr(this.pos.x, this.pos.y, this.pos.z);
			
			if(dist > 1.0D) {
				--this.delayCounter;
	
				if (this.delayCounter <= 0) {
					this.delayCounter = 2 + this.taskOwner.getRandom().nextInt(5);
	
					this.delayCounter += this.failedPathFindingPenalty;
	
					if (this.taskOwner.getNavigation().getPath() != null) {
						PathPoint finalPathPoint = this.taskOwner.getNavigation().getPath().getFinalPathPoint();
						if (finalPathPoint != null && this.pos.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 2) {
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
	
					if (!this.taskOwner.getNavigation().tryMoveToXYZ(this.pos.x, this.pos.y, this.pos.z, 0.75D)) {
						this.delayCounter += 15;
					}
				}
			} else {
				navigator.clearPath();
			}
		}
	}
}
