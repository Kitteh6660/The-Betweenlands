package thebetweenlands.common.handler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.network.clientbound.MessageSyncStaticAspects;

public class AspectSyncHandler {
	@SubscribeEvent
	public static void joinWorld(EntityJoinWorldEvent event) {
		if (!event.getWorld().isClientSide() && event.getEntity() instanceof ServerPlayerEntity) {
			TheBetweenlands.networkWrapper.sendTo(new MessageSyncStaticAspects(AspectManager.get(event.getWorld())), (ServerPlayerEntity) event.getEntity());
		}
	}
}
