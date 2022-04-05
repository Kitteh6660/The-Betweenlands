package thebetweenlands.client.render.model.baked.modelbase.shields;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;

/**
 * BLShield_Lurkerskin - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelLurkerSkinShield extends Model {
	
	public ModelRenderer handle;
	public ModelRenderer shield_main;
	public ModelRenderer plate1;
	public ModelRenderer plate3;
	public ModelRenderer beam_mid1;
	public ModelRenderer beam_left1;
	public ModelRenderer beam_right1;
	public ModelRenderer skin1;
	public ModelRenderer plate2;
	public ModelRenderer beam_mid2;
	public ModelRenderer beam_mid4;
	public ModelRenderer beam_mid3;
	public ModelRenderer beam_left2;
	public ModelRenderer beam_left5;
	public ModelRenderer beam_left3;
	public ModelRenderer beam_left4;
	public ModelRenderer beam_left6;
	public ModelRenderer beam_right2;
	public ModelRenderer beam_right5;
	public ModelRenderer beam_right3;
	public ModelRenderer beam_right4;
	public ModelRenderer beam_right6;
	public ModelRenderer skin2;

	public ModelLurkerSkinShield() {
		super(RenderType::entitySolid);
		this.texWidth = 128;
		this.texHeight = 64;
		this.beam_left5 = new ModelRenderer(this, 38, 39);
		this.beam_left5.setPos(0.0F, 10.0F, 0.0F);
		this.beam_left5.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(beam_left5, 0.5009094953223726F, 0.0F, 0.0F);
		this.beam_mid1 = new ModelRenderer(this, 29, 0);
		this.beam_mid1.setPos(0.0F, 0.09F, -4.0F);
		this.beam_mid1.addBox(-1.0F, 0.0F, 0.0F, 2, 11, 2, 0.0F);
		this.setRotateAngle(beam_mid1, 0.091106186954104F, 0.0F, 0.0F);
		this.beam_right3 = new ModelRenderer(this, 47, 26);
		this.beam_right3.setPos(0.0F, -10.0F, 0.0F);
		this.beam_right3.addBox(-0.99F, -4.0F, 0.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(beam_right3, -0.5009094953223726F, 0.0F, 0.0F);
		this.skin2 = new ModelRenderer(this, 56, 11);
		this.skin2.setPos(0.0F, 0.0F, 0.0F);
		this.skin2.addBox(-5.0F, -10.0F, 0.0F, 10, 10, 0, 0.0F);
		this.setRotateAngle(skin2, -0.27314402793711257F, 0.0F, 0.0F);
		this.beam_right5 = new ModelRenderer(this, 47, 39);
		this.beam_right5.setPos(0.0F, 10.0F, 0.0F);
		this.beam_right5.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(beam_right5, 0.5009094953223726F, 0.0F, 0.0F);
		this.plate3 = new ModelRenderer(this, 0, 46);
		this.plate3.setPos(0.0F, 10.0F, -2.0F);
		this.plate3.addBox(-6.0F, 0.0F, 0.0F, 12, 3, 2, 0.0F);
		this.setRotateAngle(plate3, 0.5009094953223726F, 0.0F, 0.0F);
		this.beam_right1 = new ModelRenderer(this, 47, 0);
		this.beam_right1.setPos(-6.0F, 0.0F, -3.5F);
		this.beam_right1.addBox(-0.99F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
		this.setRotateAngle(beam_right1, 0.091106186954104F, 0.0F, 0.0F);
		this.handle = new ModelRenderer(this, 0, 0);
		this.handle.setPos(0.0F, 0.0F, 0.0F);
		this.handle.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 7, 0.0F);
		this.shield_main = new ModelRenderer(this, 0, 14);
		this.shield_main.setPos(0.0F, 0.0F, -2.0F);
		this.shield_main.addBox(-6.0F, 0.0F, -2.0F, 12, 10, 2, 0.0F);
		this.setRotateAngle(shield_main, 0.045553093477052F, 0.0F, 0.0F);
		this.beam_right4 = new ModelRenderer(this, 47, 33);
		this.beam_right4.setPos(-1.0F, -4.0F, 1.0F);
		this.beam_right4.addBox(0.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F);
		this.setRotateAngle(beam_right4, 0.0F, 0.0F, 0.5462880558742251F);
		this.plate1 = new ModelRenderer(this, 0, 27);
		this.plate1.setPos(0.0F, 0.0F, -2.0F);
		this.plate1.addBox(-6.0F, -10.0F, 0.0F, 12, 10, 2, 0.0F);
		this.setRotateAngle(plate1, -0.091106186954104F, 0.0F, 0.0F);
		this.beam_left1 = new ModelRenderer(this, 38, 0);
		this.beam_left1.setPos(6.0F, 0.0F, -3.5F);
		this.beam_left1.addBox(-1.01F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
		this.setRotateAngle(beam_left1, 0.091106186954104F, 0.0F, 0.0F);
		this.beam_mid3 = new ModelRenderer(this, 29, 28);
		this.beam_mid3.setPos(0.0F, -11.0F, 0.0F);
		this.beam_mid3.addBox(-1.0F, -5.0F, 0.0F, 2, 5, 2, 0.0F);
		this.setRotateAngle(beam_mid3, -0.5009094953223726F, 0.0F, 0.0F);
		this.beam_mid2 = new ModelRenderer(this, 29, 14);
		this.beam_mid2.setPos(0.0F, 0.0F, 0.0F);
		this.beam_mid2.addBox(-1.01F, -11.0F, 0.0F, 2, 11, 2, 0.0F);
		this.setRotateAngle(beam_mid2, -0.27314402793711257F, 0.0F, 0.0F);
		this.plate2 = new ModelRenderer(this, 0, 40);
		this.plate2.setPos(0.0F, -10.0F, 0.0F);
		this.plate2.addBox(-6.0F, -3.0F, 0.0F, 12, 3, 2, 0.0F);
		this.setRotateAngle(plate2, -0.5009094953223726F, 0.0F, 0.0F);
		this.beam_mid4 = new ModelRenderer(this, 29, 36);
		this.beam_mid4.setPos(0.0F, 11.0F, 0.0F);
		this.beam_mid4.addBox(-1.01F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(beam_mid4, 0.5009094953223726F, 0.0F, 0.0F);
		this.beam_left3 = new ModelRenderer(this, 38, 26);
		this.beam_left3.setPos(0.0F, -10.0F, 0.0F);
		this.beam_left3.addBox(-1.01F, -4.0F, 0.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(beam_left3, -0.5009094953223726F, 0.0F, 0.0F);
		this.beam_left2 = new ModelRenderer(this, 38, 13);
		this.beam_left2.setPos(0.0F, 0.0F, 0.0F);
		this.beam_left2.addBox(-1.0F, -10.0F, 0.0F, 2, 10, 2, 0.0F);
		this.setRotateAngle(beam_left2, -0.27314402793711257F, 0.0F, 0.0F);
		this.beam_right6 = new ModelRenderer(this, 47, 46);
		this.beam_right6.setPos(-1.0F, 4.0F, 1.0F);
		this.beam_right6.addBox(0.0F, 0.0F, -1.0F, 2, 3, 2, 0.0F);
		this.setRotateAngle(beam_right6, 0.0F, 0.0F, -0.5462880558742251F);
		this.beam_left4 = new ModelRenderer(this, 38, 33);
		this.beam_left4.setPos(1.0F, -4.0F, 1.0F);
		this.beam_left4.addBox(-2.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F);
		this.setRotateAngle(beam_left4, 0.0F, 0.0F, -0.5462880558742251F);
		this.beam_right2 = new ModelRenderer(this, 47, 13);
		this.beam_right2.setPos(0.0F, 0.0F, 0.0F);
		this.beam_right2.addBox(-1.0F, -10.0F, 0.0F, 2, 10, 2, 0.0F);
		this.setRotateAngle(beam_right2, -0.27314402793711257F, 0.0F, 0.0F);
		this.beam_left6 = new ModelRenderer(this, 38, 46);
		this.beam_left6.setPos(1.0F, 4.0F, 1.0F);
		this.beam_left6.addBox(-2.0F, 0.0F, -1.0F, 2, 3, 2, 0.0F);
		this.setRotateAngle(beam_left6, 0.0F, 0.0F, 0.5462880558742251F);
		this.skin1 = new ModelRenderer(this, 56, 0);
		this.skin1.setPos(0.0F, 0.0F, -3.5F);
		this.skin1.addBox(-5.0F, 0.0F, 0.0F, 10, 10, 0, 0.0F);
		this.setRotateAngle(skin1, 0.091106186954104F, 0.0F, 0.0F);
		this.beam_left1.addChild(this.beam_left5);
		this.shield_main.addChild(this.beam_mid1);
		this.beam_right2.addChild(this.beam_right3);
		this.skin1.addChild(this.skin2);
		this.beam_right1.addChild(this.beam_right5);
		this.shield_main.addChild(this.plate3);
		this.shield_main.addChild(this.beam_right1);
		this.handle.addChild(this.shield_main);
		this.beam_right3.addChild(this.beam_right4);
		this.shield_main.addChild(this.plate1);
		this.shield_main.addChild(this.beam_left1);
		this.beam_mid2.addChild(this.beam_mid3);
		this.beam_mid1.addChild(this.beam_mid2);
		this.plate1.addChild(this.plate2);
		this.beam_mid1.addChild(this.beam_mid4);
		this.beam_left2.addChild(this.beam_left3);
		this.beam_left1.addChild(this.beam_left2);
		this.beam_right5.addChild(this.beam_right6);
		this.beam_left3.addChild(this.beam_left4);
		this.beam_right1.addChild(this.beam_right2);
		this.beam_left5.addChild(this.beam_left6);
		this.shield_main.addChild(this.skin1);
	}

	@Override
	public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) { 
		this.handle.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
	}

	public void renderAsRaft(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) { 
		matrix.pushPose();
		matrix.mulPose(Vector3f.XP.rotationDegrees(90));
		this.shield_main.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		matrix.popPose();
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
