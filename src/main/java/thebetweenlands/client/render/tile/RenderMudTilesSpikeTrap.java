package thebetweenlands.client.render.tile;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMudTilesSpikeTrap extends RenderSpikeTrap {
	public RenderMudTilesSpikeTrap() {
		super(new ResourceLocation("thebetweenlands:mud_tiles_spike_trap"), new ResourceLocation("thebetweenlands:textures/tiles/spike_block_spikes_2.png"));
	}
}