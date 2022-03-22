package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelBloodSnail;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;

public class RenderBloodSnail extends LivingRenderer<EntityBloodSnail> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/blood_snail.png");

    public RenderBloodSnail(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBloodSnail(), 0.2F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBloodSnail entity) {
        return TEXTURE;
    }
}
