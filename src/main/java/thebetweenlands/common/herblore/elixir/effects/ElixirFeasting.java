package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class ElixirFeasting extends ElixirEffect {
	
	public ElixirFeasting(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
		this.setType(EffectType.BENEFICIAL);
	}

	@Override
	protected void performEffect(LivingEntity entity, int strength) {
		if(!entity.level.isClientSide() && entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if(player.getFoodData().needsFood()) {
				player.getFoodData().eat(1, 0.5F);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerFood = 100 >> strength;
		return ticksPerFood > 0 ? ticks % ticksPerFood == 0 : true;
	}
}
