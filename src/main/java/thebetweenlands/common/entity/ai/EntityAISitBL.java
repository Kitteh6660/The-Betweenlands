package thebetweenlands.common.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.passive.EntityTameable;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

/**
 * Same as {@link EntityAISit} but setting mutex to not look at players
 */
public class EntityAISitBL extends EntityAISit {
	private final EntityTameable tameable;
	private boolean isSitting;

	public EntityAISitBL(EntityTameable entity) {
		super(entity);
		tameable = entity;
		setMutexBits(7);
	}

	@Override
	public boolean canUse() {
		if (!tameable.isTamed())
			return false;
		else if (tameable.isInWater())
			return false;
		else if (!tameable.onGround && !(tameable instanceof EntityChiromawTame))
			return false;
		else {
			LivingEntity entitylivingbase = tameable.getOwner();
			if (entitylivingbase == null)
				return true;
			else
				return tameable.getDistanceSq(entitylivingbase) < 144.0D && entitylivingbase.getRevengeTarget() != null ? false : isSitting;
		}
	}

	@Override
	public void start() {
		tameable.getNavigation().clearPath();
		tameable.setSitting(true);
	}

	@Override
	public void stop() {
		tameable.setSitting(false);
	}

	@Override
	public void setSitting(boolean sitting) {
		isSitting = sitting;
	}
}