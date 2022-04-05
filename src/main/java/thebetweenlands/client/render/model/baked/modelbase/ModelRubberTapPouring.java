package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLBucketFlow - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelRubberTapPouring extends Model {
	
    public ModelRenderer flow1;
    public ModelRenderer flow2;

    public ModelRubberTapPouring() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.flow1 = new ModelRenderer(this, 0, 0);
        this.flow1.setPos(0.0F, 8.0F, 12.0F);
        this.flow1.addBox(-0.5F, -1.0F, -6.0F, 1, 1, 6, 0.0F);
        this.setRotateAngle(flow1, 0.091106186954104F, 0.0F, 0.0F);
        this.flow2 = new ModelRenderer(this, 0, 8);
        this.flow2.setPos(0.0F, -1.0F, -6.0F);
        this.flow2.addBox(-0.51F, 0.0F, 0.0F, 1, 16, 1, 0.0F);
        this.setRotateAngle(flow2, -0.091106186954104F, 0.0F, 0.0F);
        this.flow1.addChild(this.flow2);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.flow1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
