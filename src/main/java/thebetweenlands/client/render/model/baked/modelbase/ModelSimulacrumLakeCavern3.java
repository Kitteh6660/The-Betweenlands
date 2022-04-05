package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLLakeCavernStatuette3 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumLakeCavern3 extends Model {
	
    public ModelRenderer base;
    public ModelRenderer stone_main;
    public ModelRenderer edge_front;
    public ModelRenderer edge_back;

    public ModelSimulacrumLakeCavern3() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 64;
        this.stone_main = new ModelRenderer(this, 0, 14);
        this.stone_main.setPos(0.0F, -3.0F, 0.0F);
        this.stone_main.addBox(-4.0F, -6.0F, -4.0F, 8, 6, 8, 0.0F);
        this.edge_front = new ModelRenderer(this, 0, 29);
        this.edge_front.setPos(0.0F, -5.9F, -2.0F);
        this.edge_front.addBox(-4.0F, -2.0F, -2.0F, 8, 2, 2, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setPos(0.0F, 24.0F, 0.0F);
        this.base.addBox(-5.0F, -3.0F, -5.0F, 10, 3, 10, 0.0F);
        this.edge_back = new ModelRenderer(this, 21, 29);
        this.edge_back.setPos(0.0F, -6.0F, 2.0F);
        this.edge_back.addBox(-4.0F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
        this.base.addChild(this.stone_main);
        this.stone_main.addChild(this.edge_front);
        this.stone_main.addChild(this.edge_back);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.base.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
