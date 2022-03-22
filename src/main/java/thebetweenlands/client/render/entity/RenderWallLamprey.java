package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelWallLamprey;
import thebetweenlands.common.entity.mobs.EntityWallLamprey;
import thebetweenlands.common.lib.ModInfo;

@OnlyIn(Dist.CLIENT)
public class RenderWallLamprey extends RenderWallHole<EntityWallLamprey> {
	private static final ResourceLocation MODEL_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/wall_lamprey.png");

	public RenderWallLamprey(RenderManager renderManager) {
		super(renderManager, new ModelWallLamprey(), MODEL_TEXTURE);
	}

	@Override
	protected TextureAtlasSprite getWallSprite(EntityWallLamprey entity) {
		return entity.getWallSprite();
	}

	@Override
	protected float getHoleDepthPercent(EntityWallLamprey entity, float partialTicks) {
		return entity.getHoleDepthPercent(partialTicks);
	}

	@Override
	protected float getMainModelVisibilityPercent(EntityWallLamprey entity, float partialTicks) {
		return 1.0F - entity.getLampreyHiddenPercent(partialTicks);
	}
}
