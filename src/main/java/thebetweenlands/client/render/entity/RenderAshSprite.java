package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelAshSprite;
import thebetweenlands.common.entity.mobs.EntityAshSprite;
import thebetweenlands.common.lib.ModInfo;

@OnlyIn(Dist.CLIENT)
public class RenderAshSprite extends RenderLiving<EntityAshSprite> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/ash_sprite.png");

	public RenderAshSprite(RenderManager renderManager) {
		super(renderManager, new ModelAshSprite(), 0.2F);
	}

	@Override
	protected void preRenderCallback(EntityAshSprite entity, float partialTickTime) {
		GlStateManager.translate(0F, 0.85F, 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAshSprite entity) {
		return TEXTURE;
	}
}
