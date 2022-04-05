package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ElixirRipening extends ElixirEffect {
	
	public ElixirRipening(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
		this.setType(EffectType.BENEFICIAL);
	}

	@Override
	protected void performEffect(LivingEntity entity, int strength) {
		if(!entity.level.isClientSide() && entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			IDecayCapability cap = (IDecayCapability) player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(cap != null) {
				if(cap.isDecayEnabled()) {
					cap.getDecayStats().addStats(-1, 0.6F);
				}
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerHeal = 100 >> strength;
		return ticksPerHeal > 0 ? ticks % ticksPerHeal == 0 : true;
	}
}
