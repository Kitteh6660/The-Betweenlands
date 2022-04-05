package thebetweenlands.common.entity.ai;

import java.util.function.Supplier;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIJumpRandomly extends EntityAIBase {
	private final MobEntity taskOwner;
	private int chance;
	private Supplier<Boolean> condition;

	public EntityAIJumpRandomly(MobEntity taskOwner, int chance, Supplier<Boolean> condition) {
		this.taskOwner = taskOwner;
		this.chance = chance;
		this.condition = condition;
		this.setMutexBits(1);
	}

	@Override
	public boolean canUse() {
		return this.taskOwner.isEntityAlive() && this.taskOwner.getRandom().nextInt(this.chance) == 0 && this.condition.get();
	}

	@Override
	public void start() {
		this.taskOwner.getJumpHelper().setJumping();
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}
}
