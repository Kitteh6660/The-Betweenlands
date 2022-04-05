package thebetweenlands.client.render.model.baked.modelbase.shields;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLShield_Valonite2 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelValoniteShield extends Model {
	
    public ModelRenderer handle;
    public ModelRenderer shield_main;
    public ModelRenderer shieldpiece1;
    public ModelRenderer shieldpiece5;
    public ModelRenderer shieldpiece10;
    public ModelRenderer midpiece1;
    public ModelRenderer shieldpiece2;
    public ModelRenderer shieldpiece7;
    public ModelRenderer shieldpiece3;
    public ModelRenderer shieldpiece8;
    public ModelRenderer shieldpiece6;
    public ModelRenderer shieldpiece11;
    public ModelRenderer midpiece2;
    public ModelRenderer midpiece3;

    public ModelValoniteShield() {
    	super(RenderType::entitySolid);
        this.texWidth = 128;
        this.texHeight = 64;
        this.shieldpiece5 = new ModelRenderer(this, 17, 16);
        this.shieldpiece5.setPos(3.0F, -3.0F, -2.0F);
        this.shieldpiece5.addBox(0.0F, 0.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(shieldpiece5, 0.0F, -0.136659280431156F, 0.0F);
        this.shieldpiece2 = new ModelRenderer(this, 36, 0);
        this.shieldpiece2.setPos(3.0F, -2.0F, -2.0F);
        this.shieldpiece2.addBox(0.0F, -2.0F, 0.0F, 3, 4, 2, 0.0F);
        this.setRotateAngle(shieldpiece2, 0.0F, -0.136659280431156F, 0.0F);
        this.shieldpiece1 = new ModelRenderer(this, 19, 0);
        this.shieldpiece1.setPos(0.0F, -3.0F, 0.0F);
        this.shieldpiece1.addBox(-3.0F, -4.0F, -2.0F, 6, 4, 2, 0.0F);
        this.setRotateAngle(shieldpiece1, -0.22759093446006054F, 0.0F, 0.0F);
        this.shieldpiece3 = new ModelRenderer(this, 47, 0);
        this.shieldpiece3.setPos(3.0F, 0.0F, 0.0F);
        this.shieldpiece3.addBox(0.0F, -3.0F, 0.0F, 3, 5, 2, 0.0F);
        this.setRotateAngle(shieldpiece3, 0.0F, -0.136659280431156F, 0.0F);
        this.midpiece3 = new ModelRenderer(this, 21, 42);
        this.midpiece3.setPos(-3.0F, 0.0F, -1.0F);
        this.midpiece3.addBox(-7.0F, 0.0F, 0.0F, 7, 2, 4, 0.0F);
        this.setRotateAngle(midpiece3, 0.0F, 0.31869712141416456F, 0.0F);
        this.shieldpiece10 = new ModelRenderer(this, 39, 16);
        this.shieldpiece10.setPos(-3.0F, -3.0F, -2.0F);
        this.shieldpiece10.addBox(-3.0F, 0.0F, 0.0F, 3, 16, 2, 0.0F);
        this.setRotateAngle(shieldpiece10, 0.0F, 0.136659280431156F, 0.0F);
        this.shieldpiece8 = new ModelRenderer(this, 47, 8);
        this.shieldpiece8.setPos(-3.0F, 0.0F, 0.0F);
        this.shieldpiece8.addBox(-3.0F, -3.0F, 0.0F, 3, 5, 2, 0.0F);
        this.setRotateAngle(shieldpiece8, 0.0F, 0.136659280431156F, 0.0F);
        this.midpiece1 = new ModelRenderer(this, 0, 35);
        this.midpiece1.setPos(0.0F, -4.0F, -2.0F);
        this.midpiece1.addBox(-3.0F, 0.0F, -1.0F, 6, 5, 4, 0.0F);
        this.setRotateAngle(midpiece1, -0.091106186954104F, 0.0F, 0.0F);
        this.shield_main = new ModelRenderer(this, 0, 16);
        this.shield_main.setPos(0.0F, 0.0F, -2.0F);
        this.shield_main.addBox(-3.0F, -3.0F, -2.0F, 6, 14, 2, 0.0F);
        this.setRotateAngle(shield_main, 0.091106186954104F, 0.0F, 0.0F);
        this.shieldpiece6 = new ModelRenderer(this, 28, 16);
        this.shieldpiece6.setPos(3.0F, 0.0F, 0.0F);
        this.shieldpiece6.addBox(0.0F, 0.0F, 0.0F, 3, 7, 2, 0.0F);
        this.setRotateAngle(shieldpiece6, 0.0F, -0.136659280431156F, 0.0F);
        this.shieldpiece7 = new ModelRenderer(this, 36, 8);
        this.shieldpiece7.setPos(-3.0F, -2.0F, -2.0F);
        this.shieldpiece7.addBox(-3.0F, -2.0F, 0.0F, 3, 4, 2, 0.0F);
        this.setRotateAngle(shieldpiece7, 0.0F, 0.136659280431156F, 0.0F);
        this.handle = new ModelRenderer(this, 0, 0);
        this.handle.setPos(0.0F, 0.0F, 0.0F);
        this.handle.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 6, 0.0F);
        this.shieldpiece11 = new ModelRenderer(this, 50, 16);
        this.shieldpiece11.setPos(-3.0F, 0.0F, 0.0F);
        this.shieldpiece11.addBox(-3.0F, 0.0F, 0.0F, 3, 9, 2, 0.0F);
        this.setRotateAngle(shieldpiece11, 0.0F, 0.136659280431156F, 0.0F);
        this.midpiece2 = new ModelRenderer(this, 21, 35);
        this.midpiece2.setPos(3.0F, 0.0F, -1.0F);
        this.midpiece2.addBox(0.0F, 0.0F, 0.0F, 7, 2, 4, 0.0F);
        this.setRotateAngle(midpiece2, 0.0F, -0.31869712141416456F, 0.0F);
        this.shield_main.addChild(this.shieldpiece5);
        this.shieldpiece1.addChild(this.shieldpiece2);
        this.shield_main.addChild(this.shieldpiece1);
        this.shieldpiece2.addChild(this.shieldpiece3);
        this.midpiece1.addChild(this.midpiece3);
        this.shield_main.addChild(this.shieldpiece10);
        this.shieldpiece7.addChild(this.shieldpiece8);
        this.shield_main.addChild(this.midpiece1);
        this.shieldpiece5.addChild(this.shieldpiece6);
        this.shieldpiece1.addChild(this.shieldpiece7);
        this.shieldpiece10.addChild(this.shieldpiece11);
        this.midpiece1.addChild(this.midpiece2);
    }

	@Override
	public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) { 
		this.handle.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		this.shield_main.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
