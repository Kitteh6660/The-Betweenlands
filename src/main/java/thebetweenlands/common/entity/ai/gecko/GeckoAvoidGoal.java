package thebetweenlands.common.entity.ai.gecko;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import thebetweenlands.common.entity.mobs.EntityGecko;

public class GeckoAvoidGoal extends GeckoHideGoal {
	
	public final Predicate<Entity> viableSelector = new Predicate<Entity>() {
		@Override
		public boolean apply(Entity entity) {
			return entity.isAlive() && gecko.getSensing().canSee(entity);
		}
	};

	private Entity closestLivingEntity;

	private float distance;

	private Class<? extends Entity> avoidingEntityClass;

	public GeckoAvoidGoal(EntityGecko gecko, Class<? extends Entity> avoidingEntityClass, float distance, double farSpeed, double nearSpeed) {
		super(gecko, nearSpeed, nearSpeed);
		this.distance = distance;
		this.avoidingEntityClass = avoidingEntityClass;
	}

	@Override
	protected boolean shouldFlee() {
		if (avoidingEntityClass == PlayerEntity.class) {
			closestLivingEntity = gecko.level.getNearestPlayer(gecko, distance);
			if (closestLivingEntity == null) {
				return false;
			}
		} else {
			List<Entity> list = gecko.level.getEntitiesOfClass(avoidingEntityClass, gecko.getBoundingBox().inflate(distance, 3.0D, distance), viableSelector);
			if (list.isEmpty()) {
				return false;
			}
			closestLivingEntity = list.get(0);
		}

		return true;
	}

	@Override
	protected Vector3d getFleeingCausePosition() {
		return gecko != null ? gecko.getDeltaMovement() : null;
	}
}
