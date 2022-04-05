package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.entity.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower;
import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower.BipedTextureUVs;
import thebetweenlands.client.render.model.entity.rowboat.PlayerModelRower;

public class LayerRowerWear implements LayerRenderer<ClientPlayerEntity> {
    private ModelBipedRower model;

    public LayerRowerWear(boolean slimArms) {
        model = new PlayerModelRower(0.25F, true, slimArms, new BipedTextureUVs(48, 48, 40, 32, 0, 48, 0, 32, 16, 32));
        model.head.showModel = model.hat.showModel = false;
    }

    public ModelBipedRower getModel() {
        return model;
    }

    @Override
    public void doRenderLayer(ClientPlayerEntity player, float swing, float speed, float delta, float age, float yaw, float pitch, float scale) {
        model.render(player, swing, speed, age, yaw, pitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
