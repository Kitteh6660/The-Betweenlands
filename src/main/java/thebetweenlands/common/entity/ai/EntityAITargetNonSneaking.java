package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.PlayerEntity;

public class EntityAITargetNonSneaking extends EntityAINearestAttackableTarget<PlayerEntity> {
	public EntityAITargetNonSneaking(EntityCreature entity) {
		super(entity, PlayerEntity.class, true);
		this.setMutexBits(0);
	}

	@Override
	protected boolean isSuitableTarget(LivingEntity target, boolean ignoreDisabledDamage) {
		return super.isSuitableTarget(target, ignoreDisabledDamage) && !target.isCrouching();
	}
}
