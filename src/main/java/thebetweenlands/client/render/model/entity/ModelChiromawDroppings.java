package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.projectiles.EntityChiromawDroppings;

@OnlyIn(Dist.CLIENT)
public class ModelChiromawDroppings extends Model {
    public ModelRenderer poop_1;

    public ModelChiromawDroppings() {
        this.texWidth = 32;
        this.texHeight = 16;
        this.poop_1 = new ModelRenderer(this, 0, 0);
        this.poop_1.setPos(0.0F, 0.0F, 0.0F);
        this.poop_1.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
    }

    public void render(EntityChiromawDroppings entity, float partialTicks) {
    	GlStateManager.pushMatrix();
		GlStateManager.rotate(entity.prevRotationTicks + (entity.rotationTicks - entity.prevRotationTicks) * partialTicks, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(entity.prevRotationTicks + (entity.rotationTicks - entity.rotationTicks) * partialTicks, 1.0F, 0.0F, 0.0F);
        this.poop_1.render(0.0625F);
        GlStateManager.popMatrix();
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}