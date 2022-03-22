package thebetweenlands.client.render.entity;

import net.minecraft.client.entity.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import thebetweenlands.client.render.model.entity.PlayerModelColored;

public class PlayerRendererColored extends PlayerRenderer {
	private final PlayerModelColored modelColored;

	public PlayerRendererColored(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
		this.mainModel = this.modelColored = new PlayerModelColored(0.0F, useSmallArms);
		this.layerRenderers.clear();
	}

	@Override
	protected boolean canRenderName(ClientPlayerEntity entity) {
		return false;
	}

	@Override
	public void renderName(ClientPlayerEntity entity, double x, double y, double z) { }

	@Override
	protected boolean setBrightness(ClientPlayerEntity entitylivingbaseIn, float partialTicks, boolean combineTextures) {
		return false;
	}

	@Override
	protected void unsetBrightness() { }

	public void setColor(float r, float g, float b, float a) {
		this.modelColored.setColor(r, g, b, a);
	}
}
