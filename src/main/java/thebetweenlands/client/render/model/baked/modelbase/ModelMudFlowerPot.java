package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * Created by Bart on 21-6-2015.
 */
public class ModelMudFlowerPot extends Model {
	
    public ModelRenderer pot_base;
    public ModelRenderer pot;
    public ModelRenderer edge1;
    public ModelRenderer pot_base2;
    public ModelRenderer edge2;
    public ModelRenderer edge3;
    public ModelRenderer edge4;

    public ModelMudFlowerPot() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.edge4 = new ModelRenderer(this, 11, 26);
        this.edge4.setPos(0.0F, 18.0F, 0.0F);
        this.edge4.addBox(1.5F, -1.0F, -2.5F, 1, 1, 4, 0.0F);
        this.pot = new ModelRenderer(this, 0, 14);
        this.pot.setPos(0.0F, 21.0F, 0.0F);
        this.pot.addBox(-2.5F, -3.0F, -2.5F, 5, 3, 5, 0.0F);
        this.edge2 = new ModelRenderer(this, 0, 26);
        this.edge2.setPos(0.0F, 18.0F, 0.0F);
        this.edge2.addBox(-2.5F, -1.0F, -1.5F, 1, 1, 4, 0.0F);
        this.edge3 = new ModelRenderer(this, 11, 23);
        this.edge3.setPos(0.0F, 18.0F, 0.0F);
        this.edge3.addBox(-1.5F, -1.0F, 1.5F, 4, 1, 1, 0.0F);
        this.pot_base = new ModelRenderer(this, 0, 0);
        this.pot_base.setPos(0.0F, 24.0F, 0.0F);
        this.pot_base.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.pot_base2 = new ModelRenderer(this, 0, 9);
        this.pot_base2.setPos(0.0F, 22.0F, 0.0F);
        this.pot_base2.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
        this.edge1 = new ModelRenderer(this, 0, 23);
        this.edge1.setPos(0.0F, 18.0F, 0.0F);
        this.edge1.addBox(-2.5F, -1.0F, -2.5F, 4, 1, 1, 0.0F);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) { 
        this.edge4.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.pot.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.edge2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.edge3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.pot_base.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.pot_base2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.edge1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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