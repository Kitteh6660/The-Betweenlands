package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * BLBauble - TripleHeadedSheep
 * Created using Tabula 7.0.1, updated to 1.16.5
 */
public class ModelBauble extends Model {
	
    public ModelRenderer base_main;
    public ModelRenderer midpiece;
    public ModelRenderer front1;
    public ModelRenderer left1;
    public ModelRenderer right1;
    public ModelRenderer back1;
    public ModelRenderer toppiece;

    public ModelBauble() {
    	super(RenderType::entityCutout);
        this.texWidth = 32;
        this.texHeight = 32;
        this.left1 = new ModelRenderer(this, 11, 15);
        this.left1.setPos(2.5F, 3.0F, 0.0F);
        this.left1.addBox(-2.0F, -1.0F, -1.5F, 2, 3, 3, 0.0F);
        this.setRotateAngle(left1, 0.0F, 0.0F, -0.18203784098300857F);
        this.front1 = new ModelRenderer(this, 0, 16);
        this.front1.setPos(0.0F, 3.0F, -2.5F);
        this.front1.addBox(-1.5F, -1.0F, 0.0F, 3, 3, 2, 0.0F);
        this.setRotateAngle(front1, -0.18203784098300857F, 0.0F, 0.0F);
        this.midpiece = new ModelRenderer(this, 0, 9);
        this.midpiece.setPos(0.0F, 3.0F, 0.0F);
        this.midpiece.addBox(-1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F);
        this.right1 = new ModelRenderer(this, 0, 22);
        this.right1.setPos(-2.5F, 3.0F, 0.0F);
        this.right1.addBox(0.0F, -1.0F, -1.5F, 2, 3, 3, 0.0F);
        this.setRotateAngle(right1, 0.0F, 0.0F, 0.18203784098300857F);
        this.base_main = new ModelRenderer(this, 0, 0);
        this.base_main.setPos(0.0F, 16.0F, 0.0F);
        this.base_main.addBox(-2.5F, 0.0F, -2.5F, 5, 3, 5, 0.0F);
        this.back1 = new ModelRenderer(this, 11, 23);
        this.back1.setPos(0.0F, 3.0F, 2.5F);
        this.back1.addBox(-1.5F, -1.0F, -2.0F, 3, 3, 2, 0.0F);
        this.setRotateAngle(back1, 0.18203784098300857F, 0.0F, 0.0F);
        this.toppiece = new ModelRenderer(this, 13, 9);
        this.toppiece.setPos(0.0F, 0.0F, 0.0F);
        this.toppiece.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
        this.base_main.addChild(this.left1);
        this.base_main.addChild(this.front1);
        this.base_main.addChild(this.midpiece);
        this.base_main.addChild(this.right1);
        this.base_main.addChild(this.back1);
        this.base_main.addChild(this.toppiece);
    }

	@Override
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) { 
		this.base_main.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
