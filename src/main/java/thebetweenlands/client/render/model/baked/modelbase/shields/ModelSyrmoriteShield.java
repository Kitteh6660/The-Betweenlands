package thebetweenlands.client.render.model.baked.modelbase.shields;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLShield_Syrmorite - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5
 */
public class ModelSyrmoriteShield extends Model {
	
    public ModelRenderer handle1;
    public ModelRenderer handle2;
    public ModelRenderer shield_main;
    public ModelRenderer shieldplate1;
    public ModelRenderer rimplate1;
    public ModelRenderer rimplate2;
    public ModelRenderer midrim1;
    public ModelRenderer rimplate3;
    public ModelRenderer rimplate4;
    public ModelRenderer midrim2;
    public ModelRenderer midrim3;

    public ModelSyrmoriteShield() {
    	super(RenderType::entitySolid);
        this.texWidth = 128;
        this.texHeight = 64;
        this.shieldplate1 = new ModelRenderer(this, 21, 14);
        this.shieldplate1.setPos(0.0F, 0.0F, 0.0F);
        this.shieldplate1.addBox(-4.0F, -14.0F, -2.0F, 8, 14, 2, 0.0F);
        this.setRotateAngle(shieldplate1, -0.091106186954104F, 0.0F, 0.0F);
        this.rimplate2 = new ModelRenderer(this, 11, 31);
        this.rimplate2.setPos(-4.0F, 0.0F, -2.0F);
        this.rimplate2.addBox(-3.0F, 0.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(rimplate2, 0.0F, 0.27314402793711257F, 0.0F);
        this.handle1 = new ModelRenderer(this, 0, 0);
        this.handle1.setPos(-3.0F, 0.0F, 0.0F);
        this.handle1.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 7, 0.0F);
        this.handle2 = new ModelRenderer(this, 19, 0);
        this.handle2.setPos(6.0F, 0.0F, 0.0F);
        this.handle2.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 7, 0.0F);
        this.midrim1 = new ModelRenderer(this, 38, 0);
        this.midrim1.setPos(0.0F, 0.0F, -2.0F);
        this.midrim1.addBox(-4.0F, -1.0F, -1.0F, 8, 2, 2, 0.0F);
        this.setRotateAngle(midrim1, -0.045553093477052F, 0.0F, 0.0F);
        this.midrim3 = new ModelRenderer(this, 55, 5);
        this.midrim3.setPos(-4.0F, 0.0F, -1.0F);
        this.midrim3.addBox(-4.0F, -1.0F, 0.0F, 4, 2, 4, 0.0F);
        this.setRotateAngle(midrim3, 0.0F, 0.31869712141416456F, 0.0F);
        this.rimplate1 = new ModelRenderer(this, 0, 31);
        this.rimplate1.setPos(4.0F, 0.0F, -2.0F);
        this.rimplate1.addBox(0.0F, 0.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(rimplate1, 0.0F, -0.27314402793711257F, 0.0F);
        this.rimplate4 = new ModelRenderer(this, 33, 31);
        this.rimplate4.setPos(-4.0F, 0.0F, -2.0F);
        this.rimplate4.addBox(-3.0F, -15.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(rimplate4, 0.0F, 0.27314402793711257F, 0.0F);
        this.midrim2 = new ModelRenderer(this, 38, 5);
        this.midrim2.setPos(4.0F, 0.0F, -1.0F);
        this.midrim2.addBox(0.0F, -1.0F, 0.0F, 4, 2, 4, 0.0F);
        this.setRotateAngle(midrim2, 0.0F, -0.31869712141416456F, 0.0F);
        this.shield_main = new ModelRenderer(this, 0, 14);
        this.shield_main.setPos(3.0F, 0.0F, -2.0F);
        this.shield_main.addBox(-4.0F, 0.0F, -2.0F, 8, 14, 2, 0.0F);
        this.setRotateAngle(shield_main, 0.045553093477052F, 0.0F, 0.0F);
        this.rimplate3 = new ModelRenderer(this, 22, 31);
        this.rimplate3.setPos(4.0F, 0.0F, -2.0F);
        this.rimplate3.addBox(0.0F, -15.0F, 0.0F, 3, 15, 2, 0.0F);
        this.setRotateAngle(rimplate3, 0.0F, -0.27314402793711257F, 0.0F);
        this.shield_main.addChild(this.shieldplate1);
        this.shield_main.addChild(this.rimplate2);
        this.handle1.addChild(this.handle2);
        this.shield_main.addChild(this.midrim1);
        this.midrim1.addChild(this.midrim3);
        this.shield_main.addChild(this.rimplate1);
        this.shieldplate1.addChild(this.rimplate4);
        this.midrim1.addChild(this.midrim2);
        this.handle1.addChild(this.shield_main);
        this.shieldplate1.addChild(this.rimplate3);
    }

	@Override
	public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) { 
		this.handle1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
