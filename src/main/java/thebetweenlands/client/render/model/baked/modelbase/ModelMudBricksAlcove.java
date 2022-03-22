package thebetweenlands.client.render.model.baked.modelbase;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;


@OnlyIn(Dist.CLIENT)
public class ModelMudBricksAlcove extends Model {
	
	public final ModelRenderer backwall;
	public final ModelRenderer outcrop_a;
	public final ModelRenderer candle1;
	public final ModelRenderer candle2;
	public final ModelRenderer cobweb1;
	public final ModelRenderer cobweb2;
	public final ModelRenderer top;
	public final ModelRenderer left1;
	public final ModelRenderer right1;
	public final ModelRenderer back;
	public final ModelRenderer backleft1;
	public final ModelRenderer backright1;
	public final ModelRenderer left2;
	public final ModelRenderer left3;
	public final ModelRenderer right2;
	public final ModelRenderer right3;
	public final ModelRenderer outcrop_b;
	public final ModelRenderer wicker1;
	public final ModelRenderer drip_texture1;
	public final ModelRenderer wicker2;
	public final ModelRenderer drip_texture2;
	public final ModelRenderer cobweb2b;

	public ModelMudBricksAlcove() {
    	super(RenderType::entityCutout);
		this.texWidth = 128;
		this.texHeight = 128;
		backwall = new ModelRenderer(this, 0, 0);
		backwall.setPos(0.0F, 24.0F, 2.0F);
		backwall.addBox(-8.0F, -16.0F, 0.0F, 16, 16, 6, 0.0F);
		backleft1 = new ModelRenderer(this, 29, 38);
		backleft1.setPos(5.0F, 2.0F, 0.0F);
		backleft1.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		left2 = new ModelRenderer(this, 9, 43);
		left2.setPos(0.0F, -12.0F, -2.0F);
		left2.addBox(-1.0F, 0.0F, -8.0F, 2, 2, 8, 0.0F);
		outcrop_a = new ModelRenderer(this, 60, 0);
		outcrop_a.setPos(0.0F, 8.0F, -8.0F);
		outcrop_a.addBox(-1.5F, 0.0F, -2.0F, 3, 5, 2, 0.0F);
		setRotateAngle(outcrop_a, 0.091106186954104F, 0.0F, 0.0F);
		cobweb2 = new ModelRenderer(this, 60, 29);
		cobweb2.setPos(-2.8F, 12.0F, -2.7F);
		cobweb2.addBox(-4.0F, 0.0F, 0.0F, 7, 5, 0, 0.0F);
		setRotateAngle(cobweb2, 0.6373942428283291F, -0.18203784098300857F, -0.22759093446006054F);
		left3 = new ModelRenderer(this, 30, 43);
		left3.setPos(0.0F, 2.0F, 0.0F);
		left3.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		wicker1 = new ModelRenderer(this, 69, 8);
		wicker1.setPos(0.0F, -5.0F, 0.0F);
		wicker1.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 0, 0.0F);
		setRotateAngle(wicker1, -0.18203784098300857F, 0.5009094953223726F, -0.136659280431156F);
		back = new ModelRenderer(this, 0, 38);
		back.setPos(0.0F, 4.0F, 0.0F);
		back.addBox(-6.0F, 0.0F, -2.0F, 12, 2, 2, 0.0F);
		cobweb1 = new ModelRenderer(this, 60, 22);
		cobweb1.setPos(-3.5F, 24.0F, -1.6F);
		cobweb1.addBox(-3.0F, -6.0F, 0.0F, 8, 6, 0, 0.0F);
		setRotateAngle(cobweb1, -0.8196066167365371F, -0.4553564018453205F, 0.0F);
		right2 = new ModelRenderer(this, 9, 60);
		right2.setPos(0.0F, -12.0F, -2.0F);
		right2.addBox(-1.0F, 0.0F, -8.0F, 2, 2, 8, 0.0F);
		backright1 = new ModelRenderer(this, 38, 38);
		backright1.setPos(-5.0F, 2.0F, 0.0F);
		backright1.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		right1 = new ModelRenderer(this, 0, 60);
		right1.setPos(-7.0F, 0.0F, 0.0F);
		right1.addBox(-1.0F, -12.0F, -2.0F, 2, 12, 2, 0.0F);
		wicker2 = new ModelRenderer(this, 69, 16);
		wicker2.setPos(0.0F, -3.0F, 0.0F);
		wicker2.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 0, 0.0F);
		setRotateAngle(wicker2, 0.0F, -0.31869712141416456F, 0.22759093446006054F);
		cobweb2b = new ModelRenderer(this, 75, 29);
		cobweb2b.setPos(0.0F, 5.0F, 0.0F);
		cobweb2b.addBox(-4.0F, 0.0F, 0.0F, 7, 3, 0, 0.0F);
		setRotateAngle(cobweb2b, 0.091106186954104F, 0.0F, 0.0F);
		drip_texture2 = new ModelRenderer(this, 72, 16);
		drip_texture2.setPos(0.0F, -0.02F, 0.0F);
		drip_texture2.addBox(-2.0F, 0.0F, -2.0F, 4, 0, 4, 0.0F);
		right3 = new ModelRenderer(this, 30, 60);
		right3.setPos(0.0F, 2.0F, 0.0F);
		right3.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		outcrop_b = new ModelRenderer(this, 71, 0);
		outcrop_b.setPos(0.0F, 5.0F, -2.0F);
		outcrop_b.addBox(-1.5F, -2.0F, -1.0F, 3, 2, 1, 0.0F);
		setRotateAngle(outcrop_b, -0.091106186954104F, 0.0F, 0.0F);
		candle1 = new ModelRenderer(this, 60, 8);
		candle1.setPos(5.0F, 24.0F, -1.5F);
		candle1.addBox(-1.0F, -5.0F, -1.0F, 2, 5, 2, 0.0F);
		setRotateAngle(candle1, 0.0F, -0.18203784098300857F, 0.0F);
		top = new ModelRenderer(this, 0, 23);
		top.setPos(0.0F, -16.0F, 0.0F);
		top.addBox(-8.0F, 0.0F, -10.0F, 16, 4, 10, 0.0F);
		drip_texture1 = new ModelRenderer(this, 72, 8);
		drip_texture1.setPos(0.0F, -0.03F, 0.0F);
		drip_texture1.addBox(-2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F);
		candle2 = new ModelRenderer(this, 60, 16);
		candle2.setPos(6.5F, 24.0F, -4.0F);
		candle2.addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F);
		setRotateAngle(candle2, 0.0F, -0.5918411493512771F, 0.0F);
		left1 = new ModelRenderer(this, 0, 43);
		left1.setPos(7.0F, 0.0F, 0.0F);
		left1.addBox(-1.0F, -12.0F, -2.0F, 2, 12, 2, 0.0F);
		back.addChild(backleft1);
		left1.addChild(left2);
		left2.addChild(left3);
		candle1.addChild(wicker1);
		top.addChild(back);
		right1.addChild(right2);
		back.addChild(backright1);
		backwall.addChild(right1);
		candle2.addChild(wicker2);
		cobweb2.addChild(cobweb2b);
		candle2.addChild(drip_texture2);
		right2.addChild(right3);
		outcrop_a.addChild(outcrop_b);
		backwall.addChild(top);
		candle1.addChild(drip_texture1);
		backwall.addChild(left1);
	}

	//TODO: Move this to a tile entity renderer.
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) { 
		backwall.render(scale);
		if (tile.outcrop)
			outcrop_a.render(scale);
		if (tile.topWeb)
			cobweb2.render(scale);
		if (tile.bottomWeb)
			cobweb1.render(scale);
		if (tile.smallCandle)
			candle2.render(scale);
		if (tile.bigCandle)
			candle1.render(scale);
	}

	public void ItemRenderer(float scale) {
		backwall.render(scale);
		outcrop_a.render(scale);
		cobweb2.render(scale);
		cobweb1.render(scale);
		candle2.render(scale);
		candle1.render(scale);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}
