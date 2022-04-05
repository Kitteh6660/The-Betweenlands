package thebetweenlands.common.entity.ai;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import thebetweenlands.common.entity.mobs.EntityWight;

public class EntityAIWightAttack extends MeleeAttackGoal {
	
	private final EntityWight wight;

	public EntityAIWightAttack(EntityWight wight, double speedIn, boolean useLongMemory) {
		super(wight, speedIn, useLongMemory);
		this.wight = wight;
		this.setMutexBits(1);
	}

	@Override
	public boolean canUse() {
		return super.canUse() && !this.wight.isHiding() && !this.wight.isVolatile();
	}

	@Override
	public boolean canContinueToUse() {
		return super.canContinueToUse() && !this.wight.isHiding() && !this.wight.isVolatile();
	}
}
