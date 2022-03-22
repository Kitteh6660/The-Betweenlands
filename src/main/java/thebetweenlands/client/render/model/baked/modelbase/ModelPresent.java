package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * Present - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelPresent extends Model {
	
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape5;

	public ModelPresent() {
    	super(RenderType::entityCutout);
		this.texWidth = 64;
		this.texHeight = 64;
		this.shape5 = new ModelRenderer(this, 0, 27);
		this.shape5.setPos(0.0F, 1.0F, 0.0F);
		this.shape5.addBox(0.0F, 0.0F, -6.0F, 0, 10, 12, 0.0F);
		this.setRotateAngle(shape5, 0.0F, 0.7853981633974483F, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setPos(-6.0F, 15.0F, -6.0F);
		this.shape1.addBox(0.0F, 0.0F, 0.0F, 12, 9, 12, 0.0F);
		this.shape3 = new ModelRenderer(this, 0, 27);
		this.shape3.setPos(0.0F, 1.0F, 0.0F);
		this.shape3.addBox(0.0F, 0.0F, -6.0F, 0, 10, 12, 0.0F);
		this.setRotateAngle(shape3, 0.0F, -0.7853981633974483F, 0.0F);
		this.shape2 = new ModelRenderer(this, 0, 21);
		this.shape2.setPos(-7.0F, 11.0F, -7.0F);
		this.shape2.addBox(0.0F, 0.0F, 0.0F, 14, 4, 14, 0.0F);
	}

	@Override
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
		this.shape5.render(matrix, vertex, in1, in2, f, f1, f2, f3);
		this.shape1.render(matrix, vertex, in1, in2, f, f1, f2, f3);
		this.shape3.render(matrix, vertex, in1, in2, f, f1, f2, f3);
		this.shape2.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
