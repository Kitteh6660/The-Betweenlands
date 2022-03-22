package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLLakeCavernStatuette2 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumLakeCavern2 extends Model {
	
    public ModelRenderer base;
    public ModelRenderer stone_mid;
    public ModelRenderer stone_left;
    public ModelRenderer stone_right;
    public ModelRenderer top_left;
    public ModelRenderer top_right;

    public ModelSimulacrumLakeCavern2() {
    	super(RenderType::entityCutout);
        this.texWidth = 32;
        this.texHeight = 32;
        this.stone_right = new ModelRenderer(this, 18, 7);
        this.stone_right.setPos(-1.0F, 0.0F, -1.0F);
        this.stone_right.addBox(-2.0F, -14.0F, 0.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(stone_right, 0.0F, 0.22759093446006054F, 0.0F);
        this.stone_mid = new ModelRenderer(this, 0, 7);
        this.stone_mid.setPos(0.0F, -2.0F, -0.25F);
        this.stone_mid.addBox(-1.0F, -14.0F, -1.0F, 2, 14, 2, 0.0F);
        this.top_right = new ModelRenderer(this, 18, 24);
        this.top_right.setPos(-2.0F, -14.0F, 0.0F);
        this.top_right.addBox(-1.0F, 0.0F, 0.0F, 1, 4, 2, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setPos(0.0F, 24.0F, 0.0F);
        this.base.addBox(-5.0F, -2.0F, -2.0F, 10, 2, 4, 0.0F);
        this.stone_left = new ModelRenderer(this, 9, 7);
        this.stone_left.setPos(1.0F, 0.0F, -1.0F);
        this.stone_left.addBox(0.0F, -14.0F, 0.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(stone_left, 0.0F, -0.22759093446006054F, 0.0F);
        this.top_left = new ModelRenderer(this, 9, 24);
        this.top_left.setPos(2.0F, -14.0F, 0.0F);
        this.top_left.addBox(0.0F, 0.0F, 0.0F, 1, 4, 2, 0.0F);
        this.stone_mid.addChild(this.stone_right);
        this.base.addChild(this.stone_mid);
        this.stone_right.addChild(this.top_right);
        this.stone_mid.addChild(this.stone_left);
        this.stone_left.addChild(this.top_left);
    }

    @Override
    public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
        this.base.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
