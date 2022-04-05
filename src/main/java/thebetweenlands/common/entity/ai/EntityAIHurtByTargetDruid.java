package thebetweenlands.common.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;

public class EntityAIHurtByTargetDruid extends HurtByTargetGoal {
	
	private EntityDarkDruid druid;

	public EntityAIHurtByTargetDruid(EntityDarkDruid druid) {
		super(druid, true);
		this.druid = druid;
	}

	@Override
	protected boolean isSuitableTarget(LivingEntity target, boolean ignoreDisabledDamage) {
		return super.isSuitableTarget(target, ignoreDisabledDamage) && (target.onGround || target.isRiding()) && druid.getAttackCounter() == 0;
	}

	@Override
	public boolean canContinueToUse() {
		return super.canContinueToUse() && (druid.getAttackCounter() != 0 || (druid.getAttackTarget() != null && (druid.getAttackTarget().onGround || druid.getAttackTarget().isRiding())));
	}
}
