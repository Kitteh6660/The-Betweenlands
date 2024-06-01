package thebetweenlands.api.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRiftMaskRenderer {
	/**
	 * Renders the sky mask
	 * @param partialTicks
	 * @param world
	 * @param mc
	 * @param skyBrightness
	 */
	@OnlyIn(Dist.CLIENT)
	public void renderMask(float partialTicks, ClientLevel world, Minecraft mc, float skyBrightness);

	/**
	 * Renders the rift overlay
	 * @param partialTicks
	 * @param world
	 * @param mc
	 * @param skyBrightness
	 */
	@OnlyIn(Dist.CLIENT)
	public void renderOverlay(float partialTicks, ClientLevel world, Minecraft mc, float skyBrightness);

	/**
	 * Renders the rift projection mesh
	 * @param partialTicks
	 * @param world
	 * @param mc
	 * @param skyBrightness
	 */
	@OnlyIn(Dist.CLIENT)
	public void renderRiftProjection(float partialTicks, ClientLevel world, Minecraft mc, float skyBrightness);
}
