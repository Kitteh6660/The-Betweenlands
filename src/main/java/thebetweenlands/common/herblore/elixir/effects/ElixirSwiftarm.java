package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

public class ElixirSwiftarm extends ElixirEffect {
	public ElixirSwiftarm(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(LivingEntity entity, int strength) {
		if(entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if(player.swingProgress >= 20) player.swingArm(Hand.MAIN_HAND);
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}
