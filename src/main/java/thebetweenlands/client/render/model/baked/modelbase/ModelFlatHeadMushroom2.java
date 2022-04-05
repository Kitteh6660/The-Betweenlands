package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLFlatheadMushroom - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5
 */
public class ModelFlatHeadMushroom2 extends Model {
	
    public ModelRenderer stalk1;
    public ModelRenderer stalk2;
    public ModelRenderer hat1;
    public ModelRenderer hat1a;
    public ModelRenderer hat1b;
    public ModelRenderer hat2;
    public ModelRenderer hat2a;
    public ModelRenderer hat2b;

    public ModelFlatHeadMushroom2() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.stalk2 = new ModelRenderer(this, 0, 12);
        this.stalk2.setPos(-2.0F, 24.0F, -2.5F);
        this.stalk2.addBox(-1.5F, -2.0F, -1.5F, 3, 5, 3, 0.0F);
        this.setRotateAngle(stalk2, 0.045553093477052F, 0.136659280431156F, -0.091106186954104F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setPos(2.0F, 24.0F, 2.0F);
        this.stalk1.addBox(-1.5F, -5.0F, -1.5F, 3, 8, 3, 0.0F);
        this.setRotateAngle(stalk1, -0.18203784098300857F, 0.0F, 0.091106186954104F);
        this.hat1 = new ModelRenderer(this, 0, 12);
        this.hat1.setPos(0.0F, -4.8F, 0.0F);
        this.hat1.addBox(-5.0F, -2.0F, -6.0F, 10, 2, 12, 0.0F);
        this.setRotateAngle(hat1, 0.136659280431156F, 0.0F, 0.0F);
        this.hat2a = new ModelRenderer(this, 33, 27);
        this.hat2a.setPos(0.0F, 0.0F, 2.5F);
        this.hat2a.addBox(-2.5F, -2.0F, 0.0F, 5, 2, 1, 0.0F);
        this.hat2 = new ModelRenderer(this, 33, 13);
        this.hat2.setPos(0.0F, -1.8F, 0.0F);
        this.hat2.addBox(-3.5F, -2.0F, -2.5F, 7, 2, 5, 0.0F);
        this.setRotateAngle(hat2, -0.091106186954104F, 0.0F, 0.091106186954104F);
        this.hat1a = new ModelRenderer(this, 13, 0);
        this.hat1a.setPos(-5.0F, 0.0F, 0.0F);
        this.hat1a.addBox(-1.0F, -2.0F, -5.0F, 1, 2, 10, 0.0F);
        this.hat2b = new ModelRenderer(this, 46, 27);
        this.hat2b.setPos(0.0F, 0.0F, -2.5F);
        this.hat2b.addBox(-2.5F, -2.0F, -1.0F, 5, 2, 1, 0.0F);
        this.hat1b = new ModelRenderer(this, 36, 0);
        this.hat1b.setPos(5.0F, 0.0F, 0.0F);
        this.hat1b.addBox(0.0F, -2.0F, -5.0F, 1, 2, 10, 0.0F);
        this.stalk1.addChild(this.hat1);
        this.hat2.addChild(this.hat2a);
        this.stalk2.addChild(this.hat2);
        this.hat1.addChild(this.hat1a);
        this.hat2.addChild(this.hat2b);
        this.hat1.addChild(this.hat1b);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.stalk2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.stalk1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
