package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelBlank;
import thebetweenlands.common.entity.mobs.EntityMovingSpawnerHole;
import thebetweenlands.common.lib.ModInfo;

@OnlyIn(Dist.CLIENT)
public class RenderMovingSpawnerHole extends RenderWallHole<EntityMovingSpawnerHole> {
	private static final ResourceLocation MODEL_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/wall_lamprey.png");

	public RenderMovingSpawnerHole(RenderManager renderManager) {
		super(renderManager, new ModelBlank(), MODEL_TEXTURE);
	}

	@Override
	protected TextureAtlasSprite getWallSprite(EntityMovingSpawnerHole entity) {
		return entity.getWallSprite();
	}

	@Override
	protected float getHoleDepthPercent(EntityMovingSpawnerHole entity, float partialTicks) {
		return entity.getHoleDepthPercent(partialTicks);
	}

	@Override
	protected float getMainModelVisibilityPercent(EntityMovingSpawnerHole entity, float partialTicks) {
		return 0;
	}
}
