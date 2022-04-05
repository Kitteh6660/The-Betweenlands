package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSpawnerCrystal extends Model {
    private ModelRenderer crystal;

    public ModelSpawnerCrystal() {
        this.texWidth = 64;
        this.texHeight = 32;

        this.crystal = new ModelRenderer(this, 0, 0);
        this.crystal.addBox(-16.0F, -16.0F, 0.0F, 16, 16, 16);
        this.crystal.setPos(0.0F, 32.0F, 0.0F);
        this.crystal.setTextureSize(64, 32);
        this.crystal.mirror = true;

        this.setRotation(this.crystal, 0.7071F, 0.0F, 0.7071F);
    }

    public void render() {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.1D, 0.3D, 0.1D);
        this.crystal.render(0.0625F);
        GlStateManager.popMatrix();
    }

    @Override
	public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) { 
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.crystal.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }
}
