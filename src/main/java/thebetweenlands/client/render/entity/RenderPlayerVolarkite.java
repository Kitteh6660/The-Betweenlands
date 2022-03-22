package thebetweenlands.client.render.entity;

import java.util.Iterator;

import net.minecraft.client.entity.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import thebetweenlands.client.render.entity.layer.LayerBipedArmorVolarkite;
import thebetweenlands.client.render.model.entity.PlayerModelVolarkite;

public class PlayerRendererVolarkite extends PlayerRenderer {
	public PlayerRendererVolarkite(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
		this.mainModel = new PlayerModelVolarkite(0.0F, useSmallArms);
		
		Iterator<LayerRenderer<ClientPlayerEntity>> it = this.layerRenderers.iterator();
		while(it.hasNext()) {
			LayerRenderer<ClientPlayerEntity> layer = it.next();
			if(layer.getClass() == LayerBipedArmor.class) {
				it.remove();
			}
		}
		
		this.addLayer(new LayerBipedArmorVolarkite(this));
	}
}
