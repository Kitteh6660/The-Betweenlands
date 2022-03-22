package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.EntityTriggeredFallingBlock;

@OnlyIn(Dist.CLIENT)
public class RenderTriggeredFallingBlock extends Render<EntityTriggeredFallingBlock> {

	public RenderTriggeredFallingBlock(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTriggeredFallingBlock entity) {
		return null;
	}
}
