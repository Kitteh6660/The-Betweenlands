package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;

@OnlyIn(Dist.CLIENT)
public class ModelTinySludgeWorm extends Model {

	ModelRenderer head;
	ModelRenderer beak_right;
	ModelRenderer beak_left;
	ModelRenderer dat_detailed_hot_bod;
	ModelRenderer cute_lil_butt;
	ModelRenderer spoopy_stinger;

	public ModelTinySludgeWorm() {
		textureWidth = 32;
		textureHeight = 32;
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 22.5F, 0.0F);
		head.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F);
		beak_left = new ModelRenderer(this, 0, 14);
		beak_left.setPos(1.5F, 0.5F, -1.5F);
		beak_left.addBox(-2.0F, -2.0F, -2.0F, 2, 3, 3, 0.0F);
		setRotation(beak_left, 0.0F, -0.31869712141416456F, 0.0F);
		beak_right = new ModelRenderer(this, 0, 7);
		beak_right.setPos(-1.5F, 0.0F, -1.5F);
		beak_right.addBox(0.0F, -1.5F, -2.0F, 2, 3, 3, 0.0F);
		setRotation(beak_right, 0.0F, 0.31869712141416456F, 0.0F);

		dat_detailed_hot_bod = new ModelRenderer(this, 13, 0);
		dat_detailed_hot_bod.setPos(0.0F, 22.5F, 0.0F);
		dat_detailed_hot_bod.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F);

		cute_lil_butt = new ModelRenderer(this, 13, 7);
		cute_lil_butt.setPos(0.0F, 23.0F, 0.0F);
		cute_lil_butt.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		spoopy_stinger = new ModelRenderer(this, 13, 11);
		spoopy_stinger.setPos(0.0F, -1.3F, 1.0F);
		spoopy_stinger.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
		setRotation(spoopy_stinger, -0.18203784098300857F, 0.0F, 0.0F);

		head.addChild(beak_left);
		head.addChild(beak_right);
		cute_lil_butt.addChild(spoopy_stinger);
	}

	public void renderHead(EntityTinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		float jaw_wibble = MathHelper.sin(1F + (smoothedTicks) * 0.5F) * 0.5F;
		GlStateManager.translate(0F, -0.0625F - wibble * 0.5F, 0F + wibble * 2F);
		head.render(0.0625F);
		head.xRot = worm.xRot / (180F / (float) Math.PI);
		beak_left.yRot = 0F - jaw_wibble;
		beak_right.yRot = 0F + jaw_wibble;
	}

	public void renderBody(EntityTinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		GlStateManager.translate(0F, -0.125F - wibble, 0F - wibble * 2F);
		GlStateManager.scale(1F + wibble * 2F, 1F + wibble, 1.25F - wibble * 1.5F);
		dat_detailed_hot_bod.render(0.0625F);
	}

	public void renderTail(EntityTinySludgeWorm worm, int frame, float wibbleStrength, float partialTicks) {
		float smoothedTicks = worm.tickCount + frame + (worm.tickCount + frame - (worm.tickCount + frame - 1)) * partialTicks;
		float wibble = MathHelper.sin(1F + (smoothedTicks) * 0.25F) * 0.125F * wibbleStrength;
		GlStateManager.translate(0F, -0.0625F - wibble * 0.5F, -0.0625F + wibble * 2F);
		cute_lil_butt.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}