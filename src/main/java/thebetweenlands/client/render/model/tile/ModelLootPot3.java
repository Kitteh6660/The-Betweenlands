package thebetweenlands.client.render.model.tile;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLootPot3 extends Model {
	
    ModelRenderer foot;
    ModelRenderer cup1;
    ModelRenderer cup2;
    ModelRenderer rim1;
    ModelRenderer rim2;
    ModelRenderer rim3;
    ModelRenderer rim4;

    public ModelLootPot3() {
        texWidth = 128;
        texHeight = 64;
        cup1 = new ModelRenderer(this, 0, 13);
        cup1.setPos(0.0F, -2.0F, 0.0F);
        cup1.addBox(-7.0F, -10.0F, -7.0F, 14, 10, 14, 0.0F);
        rim3 = new ModelRenderer(this, 71, 0);
        rim3.setPos(0.0F, -2.0F, 3.0F);
        rim3.addBox(-5.0F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
        cup2 = new ModelRenderer(this, 0, 38);
        cup2.setPos(0.0F, -12.0F, 0.0F);
        cup2.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
        rim1 = new ModelRenderer(this, 50, 0);
        rim1.setPos(0.0F, -2.0F, -3.0F);
        rim1.addBox(-3.0F, -2.0F, -2.0F, 8, 2, 2, 0.0F);
        rim2 = new ModelRenderer(this, 50, 5);
        rim2.setPos(3.0F, -2.0F, 0.0F);
        rim2.addBox(0.0F, -2.0F, -3.0F, 2, 2, 8, 0.0F);
        rim4 = new ModelRenderer(this, 71, 5);
        rim4.setPos(-3.0F, -2.0F, 0.0F);
        rim4.addBox(-2.0F, -2.0F, -5.0F, 2, 2, 8, 0.0F);
        foot = new ModelRenderer(this, 0, 0);
        foot.setPos(0.0F, 24.0F, 0.0F);
        foot.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
        foot.addChild(cup1);
        cup2.addChild(rim3);
        foot.addChild(cup2);
        cup2.addChild(rim1);
        cup2.addChild(rim2);
        cup2.addChild(rim4);
    }

    public void render() {
        foot.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y,
                               float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
