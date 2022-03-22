package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * BLWindchime - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelWindChime extends Model {
	public ModelRenderer toprope;
	public ModelRenderer top;
	public ModelRenderer base;
	public ModelRenderer main_rotationpoint;
	public ModelRenderer edge_left;
	public ModelRenderer edge_right;
	public ModelRenderer string1;
	public ModelRenderer string2;
	public ModelRenderer string3;
	public ModelRenderer string4;
	public ModelRenderer string5;
	public ModelRenderer string6;
	public ModelRenderer midstring;
	public ModelRenderer rod1;
	public ModelRenderer rod2;
	public ModelRenderer rod3;
	public ModelRenderer rod4;
	public ModelRenderer rod5;
	public ModelRenderer rod6;
	public ModelRenderer thingy;

	public ModelWindChime() {
		this.texWidth = 32;
		this.texHeight = 32;
		this.string4 = new ModelRenderer(this, 22, 0);
		this.string4.setPos(2.0F, 1.0F, 0.0F);
		this.string4.addBox(0.0F, 0.0F, -0.5F, 0, 4, 1, 0.0F);
		this.string1 = new ModelRenderer(this, 13, 0);
		this.string1.setPos(-2.0F, 1.0F, 0.0F);
		this.string1.addBox(0.0F, 0.0F, -0.5F, 0, 4, 1, 0.0F);
		this.string3 = new ModelRenderer(this, 19, 0);
		this.string3.setPos(1.5F, 1.0F, -1.5F);
		this.string3.addBox(0.0F, 0.0F, -0.5F, 0, 5, 1, 0.0F);
		this.setRotateAngle(string3, 0.0F, 0.7853981633974483F, 0.0F);
		this.midstring = new ModelRenderer(this, 0, 23);
		this.midstring.setPos(0.0F, 1.0F, 0.0F);
		this.midstring.addBox(-0.5F, 0.0F, 0.0F, 1, 5, 0, 0.0F);
		this.setRotateAngle(midstring, 0.0F, 0.27314402793711257F, 0.0F);
		this.rod5 = new ModelRenderer(this, 20, 16);
		this.rod5.setPos(0.0F, 3.0F, 0.0F);
		this.rod5.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.rod3 = new ModelRenderer(this, 10, 16);
		this.rod3.setPos(0.0F, 5.0F, 0.0F);
		this.rod3.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.edge_right = new ModelRenderer(this, 20, 6);
		this.edge_right.setPos(-2.0F, 1.0F, 0.0F);
		this.edge_right.addBox(-1.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F);
		this.setRotateAngle(edge_right, 0.0F, 0.0F, 0.136659280431156F);
		this.rod4 = new ModelRenderer(this, 15, 16);
		this.rod4.setPos(0.0F, 4.0F, 0.0F);
		this.rod4.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.string5 = new ModelRenderer(this, 25, 0);
		this.string5.setPos(1.5F, 1.0F, 1.5F);
		this.string5.addBox(0.0F, 0.0F, -0.5F, 0, 3, 1, 0.0F);
		this.setRotateAngle(string5, 0.0F, -0.7853981633974483F, 0.0F);
		this.edge_left = new ModelRenderer(this, 13, 8);
		this.edge_left.setPos(2.0F, 1.0F, 0.0F);
		this.edge_left.addBox(0.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F);
		this.setRotateAngle(edge_left, 0.0F, 0.0F, -0.136659280431156F);
		this.string6 = new ModelRenderer(this, 28, 0);
		this.string6.setPos(-1.5F, 1.0F, 1.5F);
		this.string6.addBox(0.0F, 0.0F, -0.5F, 0, 3, 1, 0.0F);
		this.setRotateAngle(string6, 0.0F, 0.7853981633974483F, 0.0F);
		this.toprope = new ModelRenderer(this, 0, 12);
		this.toprope.setPos(0.0F, 8.0F, 0.0F);
		this.toprope.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.rod6 = new ModelRenderer(this, 25, 16);
		this.rod6.setPos(0.0F, 3.0F, 0.0F);
		this.rod6.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.top = new ModelRenderer(this, 0, 0);
		this.top.setPos(0.0F, 2.0F, 0.0F);
		this.top.addBox(-1.5F, 0.0F, -1.5F, 3, 1, 3, 0.0F);
		this.main_rotationpoint = new ModelRenderer(this, 0, 0);
		this.main_rotationpoint.setPos(0.0F, 1.0F, 0.0F);
		this.main_rotationpoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		this.string2 = new ModelRenderer(this, 16, 0);
		this.string2.setPos(-1.5F, 1.0F, -1.5F);
		this.string2.addBox(0.0F, 0.0F, -0.5F, 0, 5, 1, 0.0F);
		this.setRotateAngle(string2, 0.0F, -0.7853981633974483F, 0.0F);
		this.rod1 = new ModelRenderer(this, 0, 16);
		this.rod1.setPos(0.0F, 4.0F, 0.0F);
		this.rod1.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.rod2 = new ModelRenderer(this, 5, 16);
		this.rod2.setPos(0.0F, 5.0F, 0.0F);
		this.rod2.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
		this.base = new ModelRenderer(this, 0, 5);
		this.base.setPos(0.0F, 1.0F, 0.0F);
		this.base.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F);
		this.thingy = new ModelRenderer(this, 3, 23);
		this.thingy.setPos(0.0F, 5.0F, 0.0F);
		this.thingy.addBox(-1.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F);
		this.main_rotationpoint.addChild(this.string4);
		this.main_rotationpoint.addChild(this.string1);
		this.main_rotationpoint.addChild(this.string3);
		this.main_rotationpoint.addChild(this.midstring);
		this.string5.addChild(this.rod5);
		this.string3.addChild(this.rod3);
		this.base.addChild(this.edge_right);
		this.string4.addChild(this.rod4);
		this.main_rotationpoint.addChild(this.string5);
		this.base.addChild(this.edge_left);
		this.main_rotationpoint.addChild(this.string6);
		this.string6.addChild(this.rod6);
		this.toprope.addChild(this.top);
		this.base.addChild(this.main_rotationpoint);
		this.main_rotationpoint.addChild(this.string2);
		this.string1.addChild(this.rod1);
		this.string2.addChild(this.rod2);
		this.top.addChild(this.base);
		this.midstring.addChild(this.thingy);
	}

	public void render(float ticks, float strength) {
		this.string1.xRot = 0;
		this.string1.zRot = 0;
		this.rod1.zRot = 0;

		this.string2.xRot = 0;
		this.string2.zRot = 0;
		this.rod2.zRot = 0;

		this.string3.xRot = 0;
		this.string3.zRot = 0;
		this.rod3.zRot = 0;

		this.string4.xRot = -0;
		this.string4.zRot = 0;
		this.rod4.zRot = 0;

		this.string5.xRot = 0;
		this.string5.zRot = 0;
		this.rod5.zRot = 0;

		this.string6.xRot = 0;
		this.string6.zRot = 0;
		this.rod6.zRot = 0;

		this.midstring.xRot = 0;
		this.midstring.zRot = 0;
		this.thingy.zRot = 0;

		float frame2 = ticks * 0.1f;

		float b1 = MathHelper.cos(frame2) * 2.0f * 0.15f;
		float b2 = MathHelper.cos(frame2 - 0.1f) * 2.0f * b1 * 0.15f;
		float b3 = MathHelper.sin(frame2 * 0.5f + 0.1f) * 1.5f * 0.15f;

		float b4 = MathHelper.cos(frame2 * 0.94f + 0.5f) * 2.0f * 0.15f;
		float b5 = MathHelper.cos(frame2 * 0.94f + 0.5f - 0.1f) * 2.0f * b4 * 0.15f;
		float b6 = MathHelper.sin(frame2 * 0.94f * 0.5f + 0.5f + 0.1f) * 2.0f * 0.15f;

		this.string1.xRot += -b3 * 0.1f;
		this.string1.zRot += b1 * 0.1f;
		this.rod1.zRot += b2 * 0.1f;

		this.string2.xRot += -b6 * 0.1f;
		this.string2.zRot += -b4 * 0.1f;
		this.rod2.zRot += b5 * 0.1f;

		this.string3.xRot += b3 * 0.1f;
		this.string3.zRot += b1 * 0.1f;
		this.rod3.zRot += b2 * 0.1f;

		this.string4.xRot += -b6 * 0.1f;
		this.string4.zRot += b4 * 0.1f;
		this.rod4.zRot += b5 * 0.1f;

		this.string5.xRot += -b6 * 0.1f;
		this.string5.zRot += -b4 * 0.1f;
		this.rod5.zRot += -b5 * 0.1f;

		this.string6.xRot += b3 * 0.1f;
		this.string6.zRot += b1 * 0.1f;
		this.rod6.zRot += -b2 * 0.1f;

		this.midstring.xRot += -b3 * 0.1f;
		this.midstring.zRot += -b4 * 0.1f;
		this.thingy.zRot += -b2 * 0.1f;

		if(strength > 0.01f) {
			float frame = ticks * 0.8f;

			float a1 = MathHelper.cos(frame) * 2.0f * strength;
			float a2 = MathHelper.cos(frame - 0.1f) * 2.0f * a1 * strength;
			float a3 = MathHelper.sin(frame * 0.5f + 0.1f) * 1.5f * strength;

			float a4 = MathHelper.cos(frame * 0.94f + 0.5f) * 2.0f * strength;
			float a5 = MathHelper.cos(frame * 0.94f + 0.5f - 0.1f) * 2.0f * a4 * strength;
			float a6 = MathHelper.sin(frame * 0.94f * 0.5f + 0.5f + 0.1f) * 2.0f * strength;

			this.string1.xRot += -a3 * 0.1f;
			this.string1.zRot += a1 * 0.1f;
			this.rod1.zRot += a2 * 0.1f;

			this.string2.xRot += -a6 * 0.1f;
			this.string2.zRot += -a4 * 0.1f;
			this.rod2.zRot += a5 * 0.1f;

			this.string3.xRot += a3 * 0.1f;
			this.string3.zRot += a1 * 0.1f;
			this.rod3.zRot += a2 * 0.1f;

			this.string4.xRot += -a6 * 0.1f;
			this.string4.zRot += a4 * 0.1f;
			this.rod4.zRot += a5 * 0.1f;

			this.string5.xRot += -a6 * 0.1f;
			this.string5.zRot += -a4 * 0.1f;
			this.rod5.zRot += -a5 * 0.1f;

			this.string6.xRot += a3 * 0.1f;
			this.string6.zRot += a1 * 0.1f;
			this.rod6.zRot += -a2 * 0.1f;

			this.midstring.xRot += -a3 * 0.1f;
			this.midstring.zRot += -a4 * 0.1f;
			this.thingy.zRot += -a2 * 0.1f;
		}

		this.toprope.render(0.0625f);
	}

	@Override
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
		this.toprope.render(matrix, vertex, in1, in2, f, f1, f2, f3);
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
