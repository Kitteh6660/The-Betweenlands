package thebetweenlands.common.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

public abstract class EntityAIMoveToDirect<T extends MobEntity> extends Goal {
	protected final T entity;
	protected double speed;

	public EntityAIMoveToDirect(T entity, double speed) {
		this.entity = entity;
		this.speed = speed;
		this.setMutexBits(1 << 8);
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public boolean canUse() {
		return this.getTarget() != null;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void updateTask() {
		Vector3d target = this.getTarget();
		if(target != null) {
			this.entity.getMoveHelper().setMoveTo(target.x, target.y, target.z, this.speed);
		}
	}

	/**
	 * Returns the target. Returns null if there is no target
	 * @return
	 */
	@Nullable
	protected abstract Vector3d getTarget();
}
