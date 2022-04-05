package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityGasCloud;

@OnlyIn(Dist.CLIENT)
public class RenderGasCloud extends Render<EntityGasCloud> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/gas_cloud.png");

	public RenderGasCloud(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityGasCloud entity, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			
			//Use animated shader texture
			GlStateManager.bindTexture(ShaderHelper.INSTANCE.getWorldShader().getGasTexture());
		} else {
			//Use static texture
			this.bindTexture(TEXTURE);
		}

		if(Minecraft.getInstance().getCameraEntity() != null) {
			RenderHelper.disableStandardItemLighting();
			Minecraft.getInstance().entityRenderer.enableLightmap();
			
			BatchedParticleRenderer.INSTANCE.renderBatch(entity.getParticleBatch(), Minecraft.getInstance().getCameraEntity(), partialTicks);
		
			RenderHelper.enableStandardItemLighting();
			Minecraft.getInstance().entityRenderer.enableLightmap();
		}
		
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGasCloud entity) {
		return null;
	}
}
