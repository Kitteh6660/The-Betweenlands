package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLWalkway - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelWalkwayBlock extends Model {
	
	public ModelRenderer plank1;
	public ModelRenderer plank2;
	public ModelRenderer plank3;
	public ModelRenderer beamleft;
	public ModelRenderer beamright;
	public ModelRenderer standleft;
	public ModelRenderer standright;

	public ModelWalkwayBlock() {
    	super(RenderType::entityCutout);
		this.texWidth = 128;
		this.texHeight = 64;
		this.plank3 = new ModelRenderer(this, 0, 16);
		this.plank3.setPos(0.0F, 14.0F, 0.0F);
		this.plank3.addBox(-8.0F, 0.0F, 2.85F, 16, 2, 5, 0.0F);
		this.beamright = new ModelRenderer(this, 43, 20);
		this.beamright.setPos(-5.0F, 16.0F, 0.0F);
		this.beamright.addBox(-1.0F, 0.0F, -8.0F, 2, 3, 16, 0.0F);
		this.plank1 = new ModelRenderer(this, 0, 0);
		this.plank1.setPos(0.0F, 14.0F, 0.0F);
		this.plank1.addBox(-8.0F, 0.0F, -7.8F, 16, 2, 5, 0.0F);
		this.standright = new ModelRenderer(this, 13, 24);
		this.standright.setPos(-4.0F, 16.0F, 0.0F);
		this.standright.addBox(-3.0F, 0.0F, -1.5F, 3, 9, 3, 0.0F);
		this.setRotateAngle(standright, 0.0F, 0.0F, 0.091106186954104F);
		this.standleft = new ModelRenderer(this, 0, 24);
		this.standleft.setPos(4.0F, 16.0F, 0.0F);
		this.standleft.addBox(0.0F, 0.0F, -1.5F, 3, 9, 3, 0.0F);
		this.setRotateAngle(standleft, 0.0F, 0.0F, -0.091106186954104F);
		this.beamleft = new ModelRenderer(this, 43, 0);
		this.beamleft.setPos(5.0F, 16.0F, 0.0F);
		this.beamleft.addBox(-1.0F, 0.0F, -8.0F, 2, 3, 16, 0.0F);
		this.plank2 = new ModelRenderer(this, 0, 8);
		this.plank2.setPos(0.0F, 14.0F, 0.0F);
		this.plank2.addBox(-8.0F, 0.0F, -2.5F, 16, 2, 5, 0.0F);
	}

	@Override
	public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
		this.plank3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		this.beamright.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		this.plank1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		this.standright.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		this.standleft.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		this.beamleft.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		this.plank2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
