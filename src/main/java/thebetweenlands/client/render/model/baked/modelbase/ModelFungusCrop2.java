package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLCropFungus2 - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5
 */
public class ModelFungusCrop2 extends Model {
	
    public ModelRenderer stalk1;
    public ModelRenderer stalk2;
    public ModelRenderer hat1;

    public ModelFungusCrop2() {
    	super(RenderType::entityCutout);
        this.texWidth = 32;
        this.texHeight = 16;
        this.hat1 = new ModelRenderer(this, 9, 0);
        this.hat1.setPos(0.0F, -2.8F, 1.0F);
        this.hat1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.setRotateAngle(hat1, -0.136659280431156F, 0.0F, 0.0F);
        this.stalk2 = new ModelRenderer(this, 0, 8);
        this.stalk2.setPos(0.0F, -3.0F, -1.0F);
        this.stalk2.addBox(-1.01F, -3.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(stalk2, -0.18203784098300857F, 0.0F, 0.0F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setPos(0.0F, 24.0F, 0.0F);
        this.stalk1.addBox(-1.0F, -3.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stalk1, 0.36425021489121656F, 0.31869712141416456F, 0.0F);
        this.stalk2.addChild(this.hat1);
        this.stalk1.addChild(this.stalk2);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
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
