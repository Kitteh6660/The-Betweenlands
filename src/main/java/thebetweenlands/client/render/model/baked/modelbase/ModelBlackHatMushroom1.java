package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLBlackhatMushroom1 - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5+
 */
public class ModelBlackHatMushroom1 extends Model {
	
    public ModelRenderer stalk;
    public ModelRenderer hat1;
    public ModelRenderer hat2;

    public ModelBlackHatMushroom1() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.hat1 = new ModelRenderer(this, 0, 8);
        this.hat1.setPos(0.0F, -3.8F, 0.0F);
        this.hat1.addBox(-2.0F, -5.0F, -2.0F, 4, 5, 4, 0.0F);
        this.setRotateAngle(hat1, -0.091106186954104F, 0.0F, 0.0F);
        this.stalk = new ModelRenderer(this, 0, 0);
        this.stalk.setPos(0.0F, 24.0F, 0.0F);
        this.stalk.addBox(-1.0F, -4.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(stalk, 0.136659280431156F, 0.27314402793711257F, 0.0F);
        this.hat2 = new ModelRenderer(this, 0, 18);
        this.hat2.setPos(0.0F, -5.0F, 0.0F);
        this.hat2.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.stalk.addChild(this.hat1);
        this.hat1.addChild(this.hat2);
    }

	@Override
	public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) { 
		this.stalk.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
