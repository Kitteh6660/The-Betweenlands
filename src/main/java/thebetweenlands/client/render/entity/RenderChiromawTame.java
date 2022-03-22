package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelChiromawTame;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

@OnlyIn(Dist.CLIENT)
public class RenderChiromawTame extends RenderLiving<EntityChiromawTame> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw_tame.png");
	public static final ResourceLocation TEXTURE_LIGHTNING = new ResourceLocation("thebetweenlands:textures/entity/chiromaw_tame_lightning.png");

	public RenderChiromawTame(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelChiromawTame(), 0.5F);
        addLayer(new LayerOverlay<EntityChiromawTame >(this, new ResourceLocation("thebetweenlands:textures/entity/chiromaw_glow.png")) {
        	@Override
        	public void doRenderLayer(EntityChiromawTame  entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        		if(!entity.getElectricBoogaloo()) {
        			super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        		}
        	}
        }.setGlow(true));

        addLayer(new LayerOverlay<EntityChiromawTame >(this, new ResourceLocation("thebetweenlands:textures/entity/chiromaw_tame_lightning_glow.png")) {
        	@Override
        	public void doRenderLayer(EntityChiromawTame  entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        		if(entity.getElectricBoogaloo()) {
        			super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        		}
        	}
        }.setGlow(true));
	}

	@Override
	protected void preRenderCallback(EntityChiromawTame chiromaw, float partialTickTime) {
		if (!chiromaw.isSitting() && !chiromaw.isRiding()) {
			float flap = MathHelper.sin((chiromaw.tickCount + partialTickTime) * 0.5F) * 0.6F;
			GlStateManager.translate(0.0F, 0F - flap * 0.5F, 0.0F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChiromawTame entity) {
		return entity.getElectricBoogaloo()? TEXTURE_LIGHTNING : TEXTURE;
	}
}
