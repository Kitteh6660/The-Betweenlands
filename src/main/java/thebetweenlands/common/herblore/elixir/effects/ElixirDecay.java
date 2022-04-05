package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ElixirDecay extends ElixirEffect {
	
	public ElixirDecay(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(LivingEntity entity, int strength) {
		if(!entity.level.isClientSide() && entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			IDecayCapability cap = (IDecayCapability) player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(cap != null) {
				if(cap.isDecayEnabled()) {
					cap.getDecayStats().addDecayAcceleration(4.0F);
				}
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerDecay = 100 >> strength;
		return ticksPerDecay > 0 ? ticks % ticksPerDecay == 0 : true;
	}
}
