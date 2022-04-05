package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLCropFungus1 - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5
 */
public class ModelFungusCrop1 extends Model {
	
    public ModelRenderer stalk1;
    public ModelRenderer hat1;

    public ModelFungusCrop1() {
    	super(RenderType::entityCutout);
        this.texWidth = 32;
        this.texHeight = 16;
        this.hat1 = new ModelRenderer(this, 0, 7);
        this.hat1.setPos(0.0F, -2.0F, -1.0F);
        this.hat1.addBox(-1.01F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(hat1, -0.18203784098300857F, 0.0F, 0.0F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setPos(0.0F, 24.0F, 0.0F);
        this.stalk1.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stalk1, 0.27314402793711257F, 0.31869712141416456F, 0.0F);
        this.stalk1.addChild(this.hat1);
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
