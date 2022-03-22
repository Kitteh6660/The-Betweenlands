package thebetweenlands.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.PlayerEntity;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;

public class EntityAINearestAttackableTargetDruid extends EntityAINearestAttackableTarget<PlayerEntity> {
	private EntityDarkDruid druid;

	public EntityAINearestAttackableTargetDruid(EntityDarkDruid druid) {
		super(druid, PlayerEntity.class, true);
		this.druid = druid;
	}

	@Override
	protected boolean isSuitableTarget(LivingEntity target, boolean ignoreDisabledDamage) {
		return super.isSuitableTarget(target, ignoreDisabledDamage) && (target.onGround || target.isRiding()) && druid.getAttackCounter() == 0;
	}

	@Override
	public boolean shouldContinueExecuting() {
		Entity target = druid.getAttackTarget();
		return target != null && target.isEntityAlive() && (druid.getAttackCounter() != 0 || target.onGround || target.isRiding());
	}

	@Override
	protected double getTargetDistance() {
		return 7;
	}
}
