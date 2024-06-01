package thebetweenlands.api.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBetweenlandsSky {
	@OnlyIn(Dist.CLIENT)
	public void render(float partialTicks, ClientLevel world, Minecraft mc);

	/**
	 * Sets the rift renderer that renders the rift
	 * @param maskRenderer
	 */
	@OnlyIn(Dist.CLIENT)
	public void setRiftRenderer(IRiftRenderer renderer);

	/**
	 * Returns the rift renderer
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public IRiftRenderer getRiftRenderer();
}
