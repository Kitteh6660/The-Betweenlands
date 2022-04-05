package thebetweenlands.client.render.model.tile;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLootPot1 extends Model {
	
    ModelRenderer foot;
    ModelRenderer cupside1;
    ModelRenderer cupside2;
    ModelRenderer cupside3;
    ModelRenderer cupside4;
    ModelRenderer lid;
    ModelRenderer lidtop;

    public ModelLootPot1() {
        texWidth = 128;
        texHeight = 64;
        cupside2 = new ModelRenderer(this, 29, 13);
        cupside2.setPos(0.0F, -2.0F, -4.0F);
        cupside2.addBox(-4.0F, -10.0F, -2.0F, 8, 10, 2, 0.0F);
        lidtop = new ModelRenderer(this, 64, 0);
        lidtop.setPos(0.0F, -2.0F, 0.0F);
        lidtop.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        foot = new ModelRenderer(this, 0, 0);
        foot.setPos(0.0F, 24.0F, 0.0F);
        foot.addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8, 0.0F);
        cupside4 = new ModelRenderer(this, 29, 26);
        cupside4.setPos(0.0F, -2.0F, 4.0F);
        cupside4.addBox(-4.0F, -10.0F, 0.0F, 8, 10, 2, 0.0F);
        cupside3 = new ModelRenderer(this, 0, 36);
        cupside3.setPos(4.0F, -2.0F, 0.0F);
        cupside3.addBox(0.0F, -10.0F, -6.0F, 2, 10, 12, 0.0F);
        lid = new ModelRenderer(this, 33, 0);
        lid.setPos(0.0F, -12.0F, 0.0F);
        lid.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
        setRotateAngle(lid, 0.0F, 0.045553093477052F, 0.0F);
        cupside1 = new ModelRenderer(this, 0, 13);
        cupside1.setPos(-4.0F, -2.0F, 0.0F);
        cupside1.addBox(-2.0F, -10.0F, -6.0F, 2, 10, 12, 0.0F);
        foot.addChild(cupside2);
        lid.addChild(lidtop);
        foot.addChild(cupside4);
        foot.addChild(cupside3);
        foot.addChild(lid);
        foot.addChild(cupside1);
    }

    public void render() {
        foot.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}