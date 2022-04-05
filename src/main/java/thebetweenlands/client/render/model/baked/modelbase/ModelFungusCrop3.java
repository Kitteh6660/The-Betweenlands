package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLCropFungus3 - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5
 */
public class ModelFungusCrop3 extends Model {
	
    public ModelRenderer stalk1;
    public ModelRenderer hat3;
    public ModelRenderer stalk2;
    public ModelRenderer stalkfluff;
    public ModelRenderer stalk3;
    public ModelRenderer hat1;
    public ModelRenderer hat2;

    public ModelFungusCrop3() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.hat2 = new ModelRenderer(this, 13, 7);
        this.hat2.setPos(0.0F, -2.0F, 0.0F);
        this.hat2.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
        this.stalk3 = new ModelRenderer(this, 0, 18);
        this.stalk3.setPos(0.0F, -4.0F, 0.0F);
        this.stalk3.addBox(-1.52F, -4.0F, 0.0F, 3, 4, 3, 0.0F);
        this.setRotateAngle(stalk3, -0.18203784098300857F, 0.0F, 0.0F);
        this.hat3 = new ModelRenderer(this, 30, 0);
        this.hat3.setPos(5.0F, 24.0F, 1.0F);
        this.hat3.addBox(-1.0F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(hat3, 0.136659280431156F, -0.27314402793711257F, 0.136659280431156F);
        this.stalk2 = new ModelRenderer(this, 0, 10);
        this.stalk2.setPos(0.0F, -4.0F, -1.5F);
        this.stalk2.addBox(-1.51F, -4.0F, 0.0F, 3, 4, 3, 0.0F);
        this.setRotateAngle(stalk2, -0.18203784098300857F, 0.0F, 0.0F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setPos(0.0F, 24.0F, 0.0F);
        this.stalk1.addBox(-1.5F, -4.0F, -1.5F, 3, 6, 3, 0.0F);
        this.setRotateAngle(stalk1, 0.40980330836826856F, 0.31869712141416456F, 0.0F);
        this.hat1 = new ModelRenderer(this, 13, 0);
        this.hat1.setPos(0.0F, -3.8F, 1.5F);
        this.hat1.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.setRotateAngle(hat1, -0.091106186954104F, 0.0F, 0.0F);
        this.stalkfluff = new ModelRenderer(this, 13, 12);
        this.stalkfluff.setPos(0.0F, 0.0F, 0.0F);
        this.stalkfluff.addBox(-2.0F, -3.0F, -2.0F, 4, 5, 4, 0.0F);
        this.hat1.addChild(this.hat2);
        this.stalk2.addChild(this.stalk3);
        this.stalk1.addChild(this.stalk2);
        this.stalk3.addChild(this.hat1);
        this.stalk1.addChild(this.stalkfluff);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.hat3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
