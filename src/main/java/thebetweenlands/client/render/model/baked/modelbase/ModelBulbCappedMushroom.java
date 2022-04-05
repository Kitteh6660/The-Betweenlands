package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLBlueCappedMushroom - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5+
 */
public class ModelBulbCappedMushroom extends Model {
	
    public ModelRenderer stalk1;
    public ModelRenderer stalk2;
    public ModelRenderer stalk3;
    public ModelRenderer stalk4;
    public ModelRenderer stalk5;
    public ModelRenderer cap6;
    public ModelRenderer cap7;
    public ModelRenderer cap8;
    public ModelRenderer cap1;
    public ModelRenderer cap2;
    public ModelRenderer cap3;
    public ModelRenderer cap4;
    public ModelRenderer cap5;

    public ModelBulbCappedMushroom() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.cap1 = new ModelRenderer(this, 9, 0);
        this.cap1.setPos(0.0F, -3.5F, 0.0F);
        this.cap1.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap1, 0.091106186954104F, 0.0F, -0.136659280431156F);
        this.cap5 = new ModelRenderer(this, 22, 14);
        this.cap5.setPos(0.0F, -2.5F, 0.0F);
        this.cap5.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap5, 0.045553093477052F, 0.0F, 0.091106186954104F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setPos(4.0F, 24.0F, 4.0F);
        this.stalk1.addBox(-1.0F, -4.0F, -1.0F, 2, 6, 2, 0.0F);
        this.setRotateAngle(stalk1, -0.136659280431156F, -0.27314402793711257F, 0.18203784098300857F);
        this.cap4 = new ModelRenderer(this, 22, 7);
        this.cap4.setPos(0.0F, -1.5F, 0.0F);
        this.cap4.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap4, -0.091106186954104F, 0.0F, -0.136659280431156F);
        this.stalk2 = new ModelRenderer(this, 0, 9);
        this.stalk2.setPos(-2.5F, 24.0F, -5.0F);
        this.stalk2.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stalk2, 0.136659280431156F, -0.045553093477052F, -0.091106186954104F);
        this.cap8 = new ModelRenderer(this, 35, 0);
        this.cap8.setPos(0.0F, 24.0F, 5.5F);
        this.cap8.addBox(-1.5F, -2.5F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap8, -0.136659280431156F, 0.31869712141416456F, -0.045553093477052F);
        this.cap6 = new ModelRenderer(this, 22, 21);
        this.cap6.setPos(-5.0F, 24.0F, 0.0F);
        this.cap6.addBox(-1.0F, -1.5F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(cap6, -0.18203784098300857F, -0.31869712141416456F, -0.136659280431156F);
        this.cap3 = new ModelRenderer(this, 22, 0);
        this.cap3.setPos(0.0F, -2.5F, 0.0F);
        this.cap3.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap3, 0.136659280431156F, 0.0F, 0.091106186954104F);
        this.stalk4 = new ModelRenderer(this, 0, 24);
        this.stalk4.setPos(5.5F, 24.0F, -4.5F);
        this.stalk4.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(stalk4, 0.27314402793711257F, 0.31869712141416456F, 0.18203784098300857F);
        this.cap7 = new ModelRenderer(this, 22, 26);
        this.cap7.setPos(4.5F, 24.0F, 0.5F);
        this.cap7.addBox(-1.0F, -1.5F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(cap7, -0.091106186954104F, 0.18203784098300857F, 0.18203784098300857F);
        this.stalk3 = new ModelRenderer(this, 0, 16);
        this.stalk3.setPos(-4.0F, 24.0F, 3.5F);
        this.stalk3.addBox(-1.0F, -3.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stalk3, -0.136659280431156F, -0.136659280431156F, -0.091106186954104F);
        this.cap2 = new ModelRenderer(this, 9, 7);
        this.cap2.setPos(0.0F, -1.5F, 0.0F);
        this.cap2.addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, 0.0F);
        this.setRotateAngle(cap2, -0.091106186954104F, 0.0F, 0.091106186954104F);
        this.stalk5 = new ModelRenderer(this, 9, 16);
        this.stalk5.setPos(1.5F, 24.0F, -1.0F);
        this.stalk5.addBox(-1.0F, -3.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stalk5, -0.045553093477052F, 0.0F, -0.136659280431156F);
        this.stalk1.addChild(this.cap1);
        this.stalk5.addChild(this.cap5);
        this.stalk4.addChild(this.cap4);
        this.stalk3.addChild(this.cap3);
        this.stalk2.addChild(this.cap2);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) { 
        this.stalk1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.stalk2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.cap8.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.cap6.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.stalk4.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.cap7.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.stalk3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.stalk5.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
