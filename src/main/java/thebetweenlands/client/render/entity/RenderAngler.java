package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelAngler;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityAngler;

@OnlyIn(Dist.CLIENT)
public class RenderAngler extends MobRenderer<EntityAngler, ModelAngler<EntityAngler>> {
	public final static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/angler.png");

	public RenderAngler(EntityRendererManager manager) {
		super(manager, new ModelAngler(), 0.5F);
		this.addLayer(new LayerOverlay<EntityAngler>(this, new ResourceLocation("thebetweenlands:textures/entity/angler_glow.png")).setGlow(true));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAngler entity) {
		return TEXTURE;
	}

	@Override
	protected void preRenderCallback(EntityAngler entity, float f) {
		EnderDragonRenderer
		GL11.glTranslatef(0F, 0.5F, 0F);
		if (entity.isGrounded() && !entity.isLeaping()) {
			GL11.glRotatef(90F, 0F, 0F, 1F);
			GL11.glTranslatef(-0.7F, 0.7F, 0F);
		}
	}

	@Override
	public void render(EntityAngler entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.render(entity, x, y, z, entityYaw, partialTicks);

        if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
        	ShaderHelper.INSTANCE.require();
        	double rx = entity.lastTickPosX + (entity.getX() - entity.lastTickPosX) * partialTicks;
			double ry = entity.lastTickPosY + (entity.getY() - entity.lastTickPosY) * partialTicks;
			double rz = entity.lastTickPosZ + (entity.getZ() - entity.lastTickPosZ) * partialTicks;
            double xOff = Math.sin(Math.toRadians(-entity.renderYawOffset)) * 0.3f;
            double zOff = Math.cos(Math.toRadians(-entity.renderYawOffset)) * 0.3f;
            ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx + xOff, ry + 0.95f, rz + zOff,
                    2.6f,
                    30.0f / 255.0f * 13.0F,
                    90.0f / 255.0f * 13.0F,
                    60.0f / 255.0f * 13.0F));
        }
	}
}
