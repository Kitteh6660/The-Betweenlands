package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLLanternPaper - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelLanternPaper extends Model {
	
    public ModelRenderer lamp_base;
    public ModelRenderer top_mid;
    public ModelRenderer top_left;
    public ModelRenderer top_back;
    public ModelRenderer top_connection;

    public ModelLanternPaper() {
    	super(RenderType::entityCutout);
        this.texWidth = 32;
        this.texHeight = 32;
        this.lamp_base = new ModelRenderer(this, 0, 0);
        this.lamp_base.setPos(0.0F, 18.0F, 0.0F);
        this.lamp_base.addBox(-2.5F, 0.0F, -2.5F, 5, 6, 5, 0.0F);
        this.top_mid = new ModelRenderer(this, 0, 12);
        this.top_mid.setPos(0.0F, 0.0F, 0.0F);
        this.top_mid.addBox(-2.0F, -2.0F, -3.0F, 4, 2, 6, 0.0F);
        this.top_connection = new ModelRenderer(this, 19, 0);
        this.top_connection.setPos(0.0F, -2.0F, 0.0F);
        this.top_connection.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
        this.top_left = new ModelRenderer(this, 0, 21);
        this.top_left.setPos(2.0F, -2.0F, 0.0F);
        this.top_left.addBox(0.0F, 0.0F, -2.99F, 2, 2, 6, 0.0F);
        this.setRotateAngle(top_left, 0.0F, 0.0F, 0.4553564018453205F);
        this.top_back = new ModelRenderer(this, 16, 18);
        this.top_back.setPos(-2.0F, -2.0F, 0.0F);
        this.top_back.addBox(-2.0F, 0.0F, -2.99F, 2, 2, 6, 0.0F);
        this.setRotateAngle(top_back, 0.0F, 0.0F, -0.4553564018453205F);
        this.lamp_base.addChild(this.top_mid);
        this.top_mid.addChild(this.top_connection);
        this.top_mid.addChild(this.top_left);
        this.top_mid.addChild(this.top_back);
    }

    @Override
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {  
        this.lamp_base.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
