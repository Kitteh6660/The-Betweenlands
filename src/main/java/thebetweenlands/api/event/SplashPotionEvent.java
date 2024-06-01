package thebetweenlands.api.event;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when a splash potion tries to apply its splash potion effects to an entitiy
 */
@Cancelable
public class SplashPotionEvent extends EntityEvent {
	private final Entity potion;
	private final LivingEntity target;
	private final MobEffect effect;
	private final boolean instant;

	public SplashPotionEvent(Entity potion, LivingEntity target, MobEffect effect, boolean instant) {
		super(potion);
		this.potion = potion;
		this.target = target;
		this.effect = effect;
		this.instant = instant;
	}

	public Entity getSplashPotionEntity() {
		return this.potion;
	}

	public LivingEntity getTarget() {
		return this.target;
	}

	public MobEffect getPotionEffect() {
		return this.effect;
	}

	public boolean isInstantEffect() {
		return this.instant;
	}
}
