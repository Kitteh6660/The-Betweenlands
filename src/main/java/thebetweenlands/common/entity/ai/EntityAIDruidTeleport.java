package thebetweenlands.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.PlayerEntity;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class EntityAIDruidTeleport extends EntityAIBase {
	private EntityAINearestAttackableTarget.Sorter nearestEntitySorter;
	private Predicate<Entity> farSelector;

	private EntityDarkDruid druid;

	private Entity entityToTeleportTo;

	public EntityAIDruidTeleport(EntityDarkDruid druid) {
		this.druid = druid;
		setMutexBits(1);
        nearestEntitySorter = new EntityAINearestAttackableTarget.Sorter(druid);
        farSelector = new FarEntitySelector(druid, 6);
	}

	@Override
	public boolean canUse() {
		if (druid.canTeleport() && druid.getRandom().nextFloat() < 0.4F) {
			List<PlayerEntity> nearPlayers = druid.world.getEntitiesOfClass(PlayerEntity.class, druid.getBoundingBox().inflate(24, 10, 24));
			Collections.sort(nearPlayers, nearestEntitySorter);
			for (PlayerEntity player : nearPlayers) {
				if (player.onGround && !player.capabilities.disableDamage) {
					entityToTeleportTo = player;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void start() {
		druid.teleportNearEntity(entityToTeleportTo);
	}

	@Override
	public void stop() {
		entityToTeleportTo = null;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}


	public class FarEntitySelector implements Predicate<Entity> {
		private Entity entity;
		private double minDistanceSquared;

		public FarEntitySelector(Entity entity, double minDistance) {
			this.entity = entity;
			this.minDistanceSquared = minDistance * minDistance;
		}

		@Override
		public boolean test(Entity entity) {
			return this.entity.getDistanceSq(entity) > minDistanceSquared;
		}
	}
}
