package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLCropFungus5 - TripleHeadedSheep
 * Created using Tabula 4.1.1, updated for 1.16.5
 */
public class ModelFungusCrop4Decayed extends Model {
	
    public ModelRenderer stalk1;
    public ModelRenderer hat3;
    public ModelRenderer hat4;
    public ModelRenderer stalk2;
    public ModelRenderer stalkfluff1;
    public ModelRenderer stalkfluff2;

    public ModelFungusCrop4Decayed() {
    	super(RenderType::entityCutout);
        this.texWidth = 64;
        this.texHeight = 32;
        this.stalk2 = new ModelRenderer(this, 0, 10);
        this.stalk2.setPos(0.0F, -4.0F, -1.5F);
        this.stalk2.addBox(-1.51F, -5.0F, 0.0F, 3, 5, 3, 0.0F);
        this.setRotateAngle(stalk2, -0.18203784098300857F, 0.0F, 0.0F);
        this.hat4 = new ModelRenderer(this, 13, 7);
        this.hat4.setPos(4.0F, 24.0F, -3.0F);
        this.hat4.addBox(-1.0F, -1.0F, -1.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(hat4, 0.40980330836826856F, 0.045553093477052F, 0.40980330836826856F);
        this.stalkfluff2 = new ModelRenderer(this, 27, 11);
        this.stalkfluff2.setPos(0.0F, -4.0F, -2.0F);
        this.stalkfluff2.addBox(-2.01F, -6.0F, 0.0F, 4, 6, 4, 0.0F);
        this.setRotateAngle(stalkfluff2, -0.18203784098300857F, 0.0F, 0.0F);
        this.hat3 = new ModelRenderer(this, 13, 0);
        this.hat3.setPos(5.0F, 24.0F, 1.0F);
        this.hat3.addBox(-1.0F, -1.3F, -1.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(hat3, 0.18203784098300857F, -0.27314402793711257F, 0.31869712141416456F);
        this.stalk1 = new ModelRenderer(this, 0, 0);
        this.stalk1.setPos(0.0F, 24.0F, 0.0F);
        this.stalk1.addBox(-1.5F, -4.0F, -1.5F, 3, 6, 3, 0.0F);
        this.setRotateAngle(stalk1, 0.4553564018453205F, 0.31869712141416456F, 0.0F);
        this.stalkfluff1 = new ModelRenderer(this, 26, 0);
        this.stalkfluff1.setPos(0.0F, 0.0F, 0.0F);
        this.stalkfluff1.addBox(-2.0F, -4.0F, -2.0F, 4, 6, 4, 0.0F);
        this.stalk1.addChild(this.stalk2);
        this.stalkfluff1.addChild(this.stalkfluff2);
        this.stalk1.addChild(this.stalkfluff1);
    }

    @Override
    public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
        this.hat4.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.hat3.render(matrix, vertex, in1, in2, f, f1, f2, f3);
        this.stalk1.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
