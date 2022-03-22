package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLLakeCavernStatuette1 - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelSimulacrumLakeCavern1 extends Model {
	
    public ModelRenderer base;
    public ModelRenderer stone_main;
    public ModelRenderer stone_top;

    public ModelSimulacrumLakeCavern1() {
    	super(RenderType::entityCutout);
        this.texWidth = 32;
        this.texHeight = 32;
        this.stone_main = new ModelRenderer(this, 0, 8);
        this.stone_main.setPos(0.0F, -3.0F, 0.0F);
        this.stone_main.addBox(-2.0F, -12.0F, -1.0F, 4, 12, 2, 0.0F);
        this.stone_top = new ModelRenderer(this, 0, 23);
        this.stone_top.setPos(0.0F, -10.0F, 0.0F);
        this.stone_top.addBox(-2.0F, -3.0F, -1.0F, 3, 1, 2, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setPos(0.0F, 24.0F, 0.0F);
        this.base.addBox(-3.0F, -3.0F, -2.0F, 6, 3, 4, 0.0F);
        this.base.addChild(this.stone_main);
        this.stone_main.addChild(this.stone_top);
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
