package thebetweenlands.api.sky;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRiftRenderer {
	/**
	 * Renders the sky rift
	 * @param partialTicks
	 * @param world
	 * @param mc
	 */
	@OnlyIn(Dist.CLIENT)
	public void render(MatrixStack matrix, float partialTicks, ClientWorld world, Minecraft mc);

	/**
	 * Sets the rift mask renderer that renders the rift mask and the overlay
	 * @param maskRenderer
	 */
	@OnlyIn(Dist.CLIENT)
	public void setRiftMaskRenderer(IRiftMaskRenderer maskRenderer);

	/**
	 * Returns the rift mask renderer
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public IRiftMaskRenderer getRiftMaskRenderer();

	/**
	 * Sets the rift sky renderer
	 * @param skyRenderer
	 */
	@OnlyIn(Dist.CLIENT)
	public void setRiftSkyRenderer(IRiftSkyRenderer skyRenderer);

	/**
	 * Returns the rift sky renderer
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public IRiftSkyRenderer getRiftSkyRenderer();
}
