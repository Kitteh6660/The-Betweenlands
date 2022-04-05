package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLWhitePear1 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelWhitePearCrop1 extends Model {
	
    public ModelRenderer stem;
    public ModelRenderer leaf1a;
    public ModelRenderer leaf2a;
    public ModelRenderer leaf1b;
    public ModelRenderer leaf2b;

    public ModelWhitePearCrop1() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.leaf1b = new ModelRenderer(this, 3, 3);
        this.leaf1b.setPos(0.0F, 0.0F, 2.0F);
        this.leaf1b.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
        this.setRotateAngle(leaf1b, -0.6829473363053812F, 0.0F, 0.0F);
        this.stem = new ModelRenderer(this, 0, 0);
        this.stem.setPos(0.0F, 24.0F, 0.0F);
        this.stem.addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(stem, 0.091106186954104F, 0.40980330836826856F, 0.22759093446006054F);
        this.leaf2a = new ModelRenderer(this, 7, 0);
        this.leaf2a.setPos(0.0F, -1.8F, -0.5F);
        this.leaf2a.addBox(-1.5F, 0.0F, -3.0F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf2a, -0.8651597102135892F, 0.0F, -0.22759093446006054F);
        this.leaf2b = new ModelRenderer(this, 7, 4);
        this.leaf2b.setPos(0.0F, 0.0F, -3.0F);
        this.leaf2b.addBox(-1.5F, 0.0F, -2.9F, 3, 0, 3, 0.0F);
        this.setRotateAngle(leaf2b, 0.4553564018453205F, 0.0F, 0.0F);
        this.leaf1a = new ModelRenderer(this, 3, 0);
        this.leaf1a.setPos(0.0F, -1.8F, 0.5F);
        this.leaf1a.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
        this.setRotateAngle(leaf1a, 1.0471975511965976F, 0.0F, -0.22759093446006054F);
        this.leaf1a.addChild(this.leaf1b);
        this.stem.addChild(this.leaf2a);
        this.leaf2a.addChild(this.leaf2b);
        this.stem.addChild(this.leaf1a);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.stem.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
