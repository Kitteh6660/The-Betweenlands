package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLFlatheadMushroom1 - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5
 */
public class ModelFlatHeadMushroom1 extends Model {
	
	public ModelRenderer stalk1;
	public ModelRenderer hat1;
	public ModelRenderer hat1a;
	public ModelRenderer hat1b;

	public ModelFlatHeadMushroom1() {
    	super(RenderType::entityCutout);
		this.texWidth = 64;
		this.texHeight = 32;
		this.hat1b = new ModelRenderer(this, 19, 22);
		this.hat1b.setPos(0.0F, 0.0F, 4.0F);
		this.hat1b.addBox(-4.0F, -3.0F, 0.0F, 8, 2, 1, 0.0F);
		this.stalk1 = new ModelRenderer(this, 0, 0);
		this.stalk1.setPos(0.0F, 24.0F, 0.0F);
		this.stalk1.addBox(-1.5F, -4.0F, -1.5F, 3, 7, 3, 0.0F);
		this.setRotateAngle(stalk1, -0.091106186954104F, 0.0F, 0.136659280431156F);
		this.hat1 = new ModelRenderer(this, 0, 11);
		this.hat1.setPos(0.0F, -2.8F, 0.0F);
		this.hat1.addBox(-5.0F, -3.0F, -4.0F, 10, 2, 8, 0.0F);
		this.setRotateAngle(hat1, 0.045553093477052F, 0.0F, -0.091106186954104F);
		this.hat1a = new ModelRenderer(this, 0, 22);
		this.hat1a.setPos(0.0F, 0.0F, -3.0F);
		this.hat1a.addBox(-4.0F, -3.0F, -2.0F, 8, 2, 1, 0.0F);
		this.hat1.addChild(this.hat1b);
		this.stalk1.addChild(this.hat1);
		this.hat1.addChild(this.hat1a);
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
