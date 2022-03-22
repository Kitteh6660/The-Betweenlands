package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelMossBed extends Model {
	
	public ModelRenderer bedbase;
	public ModelRenderer stand_frontr;
	public ModelRenderer stand_frontl;
	public ModelRenderer stand_backr;
	public ModelRenderer stand_backl;
	public ModelRenderer moss;
	public ModelRenderer beam_front1;
	public ModelRenderer stand_frontm;
	public ModelRenderer beam_back1;
	public ModelRenderer stand_backm;
	public ModelRenderer beam_back2;

	public ModelMossBed() {
    	super(RenderType::entityCutout);
		this.texWidth = 128;
		this.texHeight = 70;
		this.beam_front1 = new ModelRenderer(this, 72, 13);
		this.beam_front1.setPos(1.0F, -10.0F, 1.5F);
		this.beam_front1.addBox(-15.0F, -2.8F, -1.51F, 15, 3, 3, 0.0F);
		this.setRotateAngle(beam_front1, 0.0F, 0.0F, 0.045553093477052F);
		this.stand_backl = new ModelRenderer(this, 72, 35);
		this.stand_backl.setPos(-7.04F, 2.0F, 22.0F);
		this.stand_backl.addBox(-1.0F, -12.0F, 0.0F, 3, 12, 3, 0.0F);
		this.setRotateAngle(stand_backl, 0.0F, 0.0F, 0.045553093477052F);
		this.stand_backm = new ModelRenderer(this, 85, 35);
		this.stand_backm.setPos(-7.5F, 0.0F, 0.0F);
		this.stand_backm.addBox(-1.5F, 0.0F, -0.59F, 3, 12, 2, 0.0F);
		this.stand_backr = new ModelRenderer(this, 59, 35);
		this.stand_backr.setPos(7.04F, 2.0F, 22.0F);
		this.stand_backr.addBox(-2.0F, -12.0F, 0.0F, 3, 12, 3, 0.0F);
		this.setRotateAngle(stand_backr, 0.0F, 0.0F, -0.045553093477052F);
		this.stand_frontr = new ModelRenderer(this, 59, 0);
		this.stand_frontr.setPos(6.95F, 2.0F, -8.0F);
		this.stand_frontr.addBox(-2.0F, -10.0F, 0.0F, 3, 10, 3, 0.0F);
		this.setRotateAngle(stand_frontr, 0.0F, 0.0F, -0.045553093477052F);
		this.beam_back2 = new ModelRenderer(this, 59, 58);
		this.beam_back2.setPos(-7.5F, -2.8F, 0.0F);
		this.beam_back2.addBox(-6.5F, -1.0F, -1.5F, 13, 1, 3, 0.0F);
		this.bedbase = new ModelRenderer(this, 0, 0);
		this.bedbase.setPos(0.0F, 22.0F, 0.0F);
		this.bedbase.addBox(-7.0F, -4.0F, -7.0F, 14, 4, 30, 0.0F);
		this.moss = new ModelRenderer(this, 0, 35);
		this.moss.setPos(0.0F, -4.0F, 0.0F);
		this.moss.addBox(-7.0F, -2.01F, -7.0F, 14, 2, 30, 0.0F);
		this.beam_back1 = new ModelRenderer(this, 59, 51);
		this.beam_back1.setPos(1.0F, -12.0F, 1.5F);
		this.beam_back1.addBox(-15.0F, -2.8F, -1.49F, 15, 3, 3, 0.0F);
		this.setRotateAngle(beam_back1, 0.0F, 0.0F, 0.045553093477052F);
		this.stand_frontm = new ModelRenderer(this, 72, 0);
		this.stand_frontm.setPos(-7.5F, 0.0F, 0.0F);
		this.stand_frontm.addBox(-1.5F, 0.0F, -1.49F, 3, 10, 2, 0.0F);
		this.stand_frontl = new ModelRenderer(this, 59, 14);
		this.stand_frontl.setPos(-6.95F, 2.0F, -8.0F);
		this.stand_frontl.addBox(-1.0F, -10.0F, 0.0F, 3, 10, 3, 0.0F);
		this.setRotateAngle(stand_frontl, 0.0F, 0.0F, 0.045553093477052F);
		this.stand_frontr.addChild(this.beam_front1);
		this.bedbase.addChild(this.stand_backl);
		this.beam_back1.addChild(this.stand_backm);
		this.bedbase.addChild(this.stand_backr);
		this.bedbase.addChild(this.stand_frontr);
		this.beam_back1.addChild(this.beam_back2);
		this.bedbase.addChild(this.moss);
		this.stand_backr.addChild(this.beam_back1);
		this.beam_front1.addChild(this.stand_frontm);
		this.bedbase.addChild(this.stand_frontl);
	}

	public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) { 
		this.bedbase.render(matrix, vertex, in1, in2, f, f1, f2, f3);
		//0.0625F
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}
