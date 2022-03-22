package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntitySludgeWorm;

@OnlyIn(Dist.CLIENT)
public class ModelSmolSludgeWorm extends Model {

	public ModelRenderer head1;
	public ModelRenderer mouth_left;
	public ModelRenderer mouth_bottom;
	public ModelRenderer jaw_bottom_left;
	public ModelRenderer jaw_bottom_right;
	public ModelRenderer butt;
    public ModelRenderer pincer_thingy_i_guess_a;
    public ModelRenderer pincer_thingy_i_guess_b;

	public ModelRenderer body1;

	public ModelSmolSludgeWorm() {
		textureWidth = 32;
		textureHeight = 32;
		jaw_bottom_right = new ModelRenderer(this, 11, 18);
		jaw_bottom_right.setPos(-1.5F, 1.0F, -2.5F);
		jaw_bottom_right.addBox(-0.5F, 0.0F, -3.5F, 1, 2, 4, 0.0F);
		setRotation(jaw_bottom_right, 0.136659280431156F, 0.0F, 0.7740535232594852F);
		mouth_left = new ModelRenderer(this, 0, 11);
		mouth_left.setPos(2.0F, -0.5F, -2.5F);
		mouth_left.addBox(-2.0F, -1.5F, -2.0F, 2, 3, 3, 0.0F);
		setRotation(mouth_left, 0.0F, -0.36425021489121656F, -0.22759093446006054F);
		jaw_bottom_left = new ModelRenderer(this, 0, 18);
		jaw_bottom_left.setPos(1.5F, 1.0F, -2.5F);
		jaw_bottom_left.addBox(-0.5F, 0.0F, -3.5F, 1, 2, 4, 0.0F);
		setRotation(jaw_bottom_left, 0.136659280431156F, 0.0F, -0.7740535232594852F);
		head1 = new ModelRenderer(this, 0, 0);
		head1.setPos(0.0F, 21.5F, 0.0F);
		head1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
		mouth_bottom = new ModelRenderer(this, 13, 11);
		mouth_bottom.setPos(-2.0F, -0.5F, -2.5F);
		mouth_bottom.addBox(0.0F, -1.5F, -2.0F, 2, 3, 3, 0.0F);
		setRotation(mouth_bottom, 0.0F, 0.36425021489121656F, 0.22759093446006054F);
		head1.addChild(jaw_bottom_right);
		head1.addChild(mouth_left);
		head1.addChild(jaw_bottom_left);
		head1.addChild(mouth_bottom);

		body1 = new ModelRenderer(this, 0, 15);
		body1.setPos(0.0F, 21.5F, 0.0F);
		body1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);

		pincer_thingy_i_guess_b = new ModelRenderer(this, 7, 9);
        pincer_thingy_i_guess_b.setPos(0.0F, 2.0F, 2.0F);
        pincer_thingy_i_guess_b.addBox(-0.5F, -2.0F, 0.0F, 1, 2, 3, 0.0F);
        setRotation(pincer_thingy_i_guess_b, 0.18203784098300857F, 0.0F, 0.0F);
        butt = new ModelRenderer(this, 0, 0);
        butt.setPos(0.0F, 21.5F, 0.0F);
        butt.addBox(-2.0F, -1.5F, -1.5F, 4, 4, 4, 0.0F);
        pincer_thingy_i_guess_a = new ModelRenderer(this, 0, 9);
        pincer_thingy_i_guess_a.setPos(0.0F, -0.2F, 2.5F);
        pincer_thingy_i_guess_a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        setRotation(pincer_thingy_i_guess_a, -0.22759093446006054F, 0.0F, 0.0F);
        pincer_thingy_i_guess_a.addChild(pincer_thingy_i_guess_b);
        butt.addChild(pincer_thingy_i_guess_a);
	}

	public void renderHead(EntitySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		float jaw_wibble = MathHelper.sin(1F + (smoothedTicks) * 0.5F) * 0.5F;
		GlStateManager.translate(0F, - 0.0625F - wibble * 0.5F, 0F + wibble * 2F);
		head1.render(0.0625F);
		head1.xRot = worm.xRot / (180F / (float) Math.PI);
	    jaw_bottom_left.yRot =  0F - jaw_wibble;
	    jaw_bottom_right.yRot = 0F + jaw_wibble;
	    mouth_bottom.yRot =  0F - jaw_wibble;
	    mouth_left.yRot = 0F + jaw_wibble;
	}

	public void renderBody(EntitySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		GlStateManager.translate(0F, 0F - wibble, 0F - wibble * 2F);
		GlStateManager.scale(1F + wibble * 2F, 1F + wibble, 1.25F - wibble * 1.5F);
		body1.render(0.0625F);
	}
	
	public void renderTail(EntitySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		GlStateManager.translate(0F, - 0.0625F - wibble * 0.5F, - 0.0625F + wibble * 2F);
		butt.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

}
