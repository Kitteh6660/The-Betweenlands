package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class BrightnessHandler {
	private BrightnessHandler() { }

	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END) {
			PlayerEntity player = Minecraft.getInstance().player;
			World world = Minecraft.getInstance().level;
			if(player != null && world != null && world.isClientSide() && world.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
				provider.updateClientLightTable(Minecraft.getInstance().player);
			}
		}
	}
}
