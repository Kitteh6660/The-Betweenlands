package thebetweenlands.common.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thebetweenlands.api.capability.IRuneChainUserCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class PlayerRuneChainHandler {
	@SubscribeEvent
	public static void onPlayerUpdate(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			PlayerEntity player = event.player;

			if(player.hasCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null)) {
				IRuneChainUserCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);

				cap.update();
			}
		}
	}
}
