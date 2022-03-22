package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import thebetweenlands.common.entity.AngryPebbleEntity;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderAngryPebble extends RenderSnowball<AngryPebbleEntity> {
	public RenderAngryPebble(RenderManager renderManager, ItemRenderer itemRenderer) {
		super(renderManager, ItemRegistry.ANGRY_PEBBLE, itemRenderer);
	}
}
