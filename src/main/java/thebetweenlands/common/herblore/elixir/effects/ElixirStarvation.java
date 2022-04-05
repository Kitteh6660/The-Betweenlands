package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class ElixirStarvation extends ElixirEffect {
	
	public ElixirStarvation(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
		this.setType(EffectType.HARMFUL);
	}

	@Override
	protected void performEffect(LivingEntity entity, int strength) {
		if(!entity.level.isClientSide() && entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if(player.getFoodData().getFoodLevel() > 0) {
				player.getFoodData().causeFoodExhaustion(4);
			} else {
				player.getFoodData().setFoodLevel(0);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerStarve = 80 >> strength;
		return ticksPerStarve > 0 ? ticks % ticksPerStarve == 0 : true;
	}
}
