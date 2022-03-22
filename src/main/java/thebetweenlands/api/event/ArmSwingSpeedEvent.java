package thebetweenlands.api.event;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ArmSwingSpeedEvent extends LivingEvent {
	private float speed;

	public ArmSwingSpeedEvent(LivingEntity living) {
		super(living);
		this.speed = 1.0F;
	}

	public float getSpeed() {
		return this.speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
