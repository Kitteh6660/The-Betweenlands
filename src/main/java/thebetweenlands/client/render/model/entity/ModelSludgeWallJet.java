package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSludgeWallJet extends Model {
    ModelRenderer main;
    ModelRenderer shield_centre;
    ModelRenderer shield_right;
    ModelRenderer shield_left;

    public ModelSludgeWallJet() {
        textureWidth = 32;
        textureHeight = 32;
        shield_right = new ModelRenderer(this, 0, 16);
        shield_right.setPos(0.0F, 0.0F, 0.0F);
        shield_right.addBox(-3.52F, -4.01F, -4.43F, 3, 8, 1, 0.0F);
        setRotateAngle(shield_right, 0.0F, 0.3490658503988659F, 0.0F);
        shield_centre = new ModelRenderer(this, 10, 16);
        shield_centre.setPos(0.0F, 0.0F, 0.0F);
        shield_centre.addBox(-2.0F, -5.0F, -4.0F, 4, 10, 1, 0.0F);
        shield_left = new ModelRenderer(this, 22, 16);
        shield_left.setPos(0.0F, 0.0F, 0.0F);
        shield_left.addBox(0.52F, -4.01F, -4.43F, 3, 8, 1, 0.0F);
        setRotateAngle(shield_left, 0.0F, -0.3490658503988659F, 0.0F);
        main = new ModelRenderer(this, 0, 0);
        main.setPos(0.0F, 19.5F, 0.0F);
        main.addBox(-4.0F, -4.0F, -3.0F, 8, 8, 7, 0.0F);
        main.addChild(shield_right);
        main.addChild(shield_centre);
        main.addChild(shield_left);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float scale) {
        main.render(scale);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
