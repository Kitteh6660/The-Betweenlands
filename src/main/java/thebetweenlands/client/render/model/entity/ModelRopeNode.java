package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelRopeNode extends Model {
    public ModelRenderer cap6;

    public ModelRopeNode() {
        this.texWidth = 64;
        this.texHeight = 64; //height is 64 here because block's texture must be 64x64
        this.cap6 = new ModelRenderer(this, 22, 21);
        this.cap6.setPos(0, 0, 0);
        this.cap6.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(cap6, 0, 0, 0);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.cap6.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
