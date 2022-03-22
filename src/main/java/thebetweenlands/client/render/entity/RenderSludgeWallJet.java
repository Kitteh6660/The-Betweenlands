package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;

@OnlyIn(Dist.CLIENT)
public class RenderSludgeWallJet extends Render<EntitySludgeWallJet> {

	public RenderSludgeWallJet(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySludgeWallJet entity) {
		return null;
	}
}
