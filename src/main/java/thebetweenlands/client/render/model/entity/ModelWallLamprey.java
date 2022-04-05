package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityWallLamprey;

/**
 * BLWallLamprey - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelWallLamprey extends MowzieModelBase {
	public MowzieModelRenderer body_base;
	public MowzieModelRenderer head_base;
	public MowzieModelRenderer head_mid;
	public MowzieModelRenderer eye_left;
	public MowzieModelRenderer eye_right;
	public MowzieModelRenderer head_front;
	public MowzieModelRenderer fancyflap_left1;
	public MowzieModelRenderer fancyflap_right1;
	public MowzieModelRenderer mouthpart_top;
	public MowzieModelRenderer teeth4;
	public MowzieModelRenderer nostril_left1;
	public MowzieModelRenderer nostril_right1;
	public MowzieModelRenderer mouthpart_bottom;
	public MowzieModelRenderer mouthpart_left;
	public MowzieModelRenderer mouthpart_right;
	public MowzieModelRenderer teeth1;
	public MowzieModelRenderer teeth2;
	public MowzieModelRenderer teeth3;
	public MowzieModelRenderer mouthpart_corner1;
	public MowzieModelRenderer mouthpart_corner2;
	public MowzieModelRenderer mouthpart_corner3;
	public MowzieModelRenderer mouthpart_corner4;
	public MowzieModelRenderer nostril_left2;
	public MowzieModelRenderer nostril_left3;
	public MowzieModelRenderer nostril_right2;
	public MowzieModelRenderer nostril_right3;

	public ModelWallLamprey() {
		this.texWidth = 64;
		this.texHeight = 64;
		this.nostril_left3 = new MowzieModelRenderer(this, 40, 46);
		this.nostril_left3.setPos(2.0F, 0.0F, 0.0F);
		this.nostril_left3.addBox(0.0F, 0.0F, 0.0F, 2, 0, 3, 0.0F);
		this.setRotateAngle(nostril_left3, 0.0F, -0.136659280431156F, 0.0F);
		this.eye_right = new MowzieModelRenderer(this, 7, 53);
		this.eye_right.setPos(-4.0F, 3.0F, -3.0F);
		this.eye_right.addBox(-1.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
		this.setRotateAngle(eye_right, 0.0F, 0.091106186954104F, -0.136659280431156F);
		this.mouthpart_corner3 = new MowzieModelRenderer(this, 33, 27);
		this.mouthpart_corner3.setPos(-4.0F, 1.0F, 0.0F);
		this.mouthpart_corner3.addBox(-1.0F, -1.0F, -2.0F, 1, 1, 3, 0.0F);
		this.mouthpart_top = new MowzieModelRenderer(this, 33, 0);
		this.mouthpart_top.setPos(0.0F, 0.0F, -3.0F);
		this.mouthpart_top.addBox(-4.0F, -1.0F, -2.0F, 8, 2, 3, 0.0F);
		this.setRotateAngle(mouthpart_top, 0.045553093477052F, 0.0F, 0.0F);
		this.nostril_right1 = new MowzieModelRenderer(this, 33, 50);
		this.nostril_right1.setPos(-4.0F, 0.0F, -2.0F);
		this.nostril_right1.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 2, 0.0F);
		this.setRotateAngle(nostril_right1, 0.0F, 0.0F, -0.9105382707654417F);
		this.mouthpart_corner2 = new MowzieModelRenderer(this, 42, 22);
		this.mouthpart_corner2.setPos(4.0F, 7.0F, 0.0F);
		this.mouthpart_corner2.addBox(0.0F, 0.0F, -2.0F, 1, 1, 3, 0.0F);
		this.teeth4 = new MowzieModelRenderer(this, 46, 39);
		this.teeth4.setPos(0.0F, 4.0F, -3.0F);
		this.teeth4.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 1, 0.0F);
		this.mouthpart_corner4 = new MowzieModelRenderer(this, 42, 27);
		this.mouthpart_corner4.setPos(-4.0F, 7.0F, 0.0F);
		this.mouthpart_corner4.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 3, 0.0F);
		this.nostril_left2 = new MowzieModelRenderer(this, 36, 46);
		this.nostril_left2.setPos(1.0F, 0.0F, 0.0F);
		this.nostril_left2.addBox(0.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
		this.setRotateAngle(nostril_left2, 0.0F, -0.136659280431156F, 0.0F);
		this.head_front = new MowzieModelRenderer(this, 0, 41);
		this.head_front.setPos(0.01F, 0.0F, -3.0F);
		this.head_front.addBox(-4.0F, 0.0F, -3.0F, 8, 8, 3, 0.0F);
		this.setRotateAngle(head_front, 0.136659280431156F, 0.0F, 0.0F);
		this.fancyflap_right1 = new MowzieModelRenderer(this, 38, 54);
		this.fancyflap_right1.setPos(-4.0F, 0.0F, -2.0F);
		this.fancyflap_right1.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
		this.setRotateAngle(fancyflap_right1, 0.0F, 0.0F, -1.1838568316277536F);
		this.nostril_left1 = new MowzieModelRenderer(this, 33, 46);
		this.nostril_left1.setPos(4.0F, 0.0F, -2.0F);
		this.nostril_left1.addBox(0.0F, 0.0F, 0.0F, 1, 0, 2, 0.0F);
		this.setRotateAngle(nostril_left1, 0.0F, 0.0F, 0.9105382707654417F);
		this.teeth1 = new MowzieModelRenderer(this, 33, 32);
		this.teeth1.setPos(0.0F, 1.0F, -0.5F);
		this.teeth1.addBox(-3.0F, 0.0F, 0.0F, 6, 6, 0, 0.0F);
		this.mouthpart_left = new MowzieModelRenderer(this, 33, 12);
		this.mouthpart_left.setPos(3.0F, 4.0F, 0.0F);
		this.mouthpart_left.addBox(0.0F, -3.0F, -2.0F, 2, 6, 3, 0.0F);
		this.nostril_right3 = new MowzieModelRenderer(this, 40, 50);
		this.nostril_right3.setPos(-2.0F, 0.0F, 0.0F);
		this.nostril_right3.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 3, 0.0F);
		this.setRotateAngle(nostril_right3, 0.0F, 0.136659280431156F, 0.0F);
		this.head_mid = new MowzieModelRenderer(this, 0, 29);
		this.head_mid.setPos(0.01F, 0.0F, -3.0F);
		this.head_mid.addBox(-4.0F, 0.0F, -3.0F, 8, 8, 3, 0.0F);
		this.setRotateAngle(head_mid, 0.136659280431156F, 0.0F, 0.0F);
		this.body_base = new MowzieModelRenderer(this, 0, 0);
		this.body_base.setPos(0.0F, 22.0F, 1.0F);
		this.body_base.addBox(-4.0F, -6.0F, -4.0F, 8, 8, 8, 0.0F);
		this.setRotateAngle(body_base, -0.31869712141416456F, 0.0F, 0.0F);
		this.mouthpart_corner1 = new MowzieModelRenderer(this, 33, 22);
		this.mouthpart_corner1.setPos(4.0F, 1.0F, 0.0F);
		this.mouthpart_corner1.addBox(0.0F, -1.0F, -2.0F, 1, 1, 3, 0.0F);
		this.teeth2 = new MowzieModelRenderer(this, 46, 32);
		this.teeth2.setPos(0.0F, 1.0F, -1.0F);
		this.teeth2.addBox(-3.0F, 0.0F, 0.0F, 6, 6, 0, 0.0F);
		this.head_base = new MowzieModelRenderer(this, 0, 17);
		this.head_base.setPos(0.01F, -6.0F, -4.0F);
		this.head_base.addBox(-4.0F, 0.0F, -3.0F, 8, 8, 3, 0.0F);
		this.setRotateAngle(head_base, 0.136659280431156F, 0.0F, 0.0F);
		this.nostril_right2 = new MowzieModelRenderer(this, 36, 50);
		this.nostril_right2.setPos(-1.0F, 0.0F, 0.0F);
		this.nostril_right2.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
		this.setRotateAngle(nostril_right2, 0.0F, 0.136659280431156F, 0.0F);
		this.eye_left = new MowzieModelRenderer(this, 0, 53);
		this.eye_left.setPos(4.0F, 3.0F, -3.0F);
		this.eye_left.addBox(0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
		this.setRotateAngle(eye_left, 0.0F, -0.091106186954104F, 0.136659280431156F);
		this.teeth3 = new MowzieModelRenderer(this, 33, 39);
		this.teeth3.setPos(0.0F, 1.0F, -1.5F);
		this.teeth3.addBox(-3.0F, 0.0F, 0.0F, 6, 6, 0, 0.0F);
		this.mouthpart_bottom = new MowzieModelRenderer(this, 33, 6);
		this.mouthpart_bottom.setPos(0.0F, 7.0F, 0.0F);
		this.mouthpart_bottom.addBox(-4.0F, 0.0F, -2.0F, 8, 2, 3, 0.0F);
		this.fancyflap_left1 = new MowzieModelRenderer(this, 33, 54);
		this.fancyflap_left1.setPos(4.0F, 0.0F, -2.0F);
		this.fancyflap_left1.addBox(0.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F);
		this.setRotateAngle(fancyflap_left1, 0.0F, 0.0F, 1.1838568316277536F);
		this.mouthpart_right = new MowzieModelRenderer(this, 44, 12);
		this.mouthpart_right.setPos(-3.0F, 4.0F, 0.0F);
		this.mouthpart_right.addBox(-2.0F, -3.0F, -2.0F, 2, 6, 3, 0.0F);
		this.nostril_left2.addChild(this.nostril_left3);
		this.head_base.addChild(this.eye_right);
		this.mouthpart_top.addChild(this.mouthpart_corner3);
		this.head_front.addChild(this.mouthpart_top);
		this.head_front.addChild(this.nostril_right1);
		this.mouthpart_top.addChild(this.mouthpart_corner2);
		this.head_front.addChild(this.teeth4);
		this.mouthpart_top.addChild(this.mouthpart_corner4);
		this.nostril_left1.addChild(this.nostril_left2);
		this.head_mid.addChild(this.head_front);
		this.head_mid.addChild(this.fancyflap_right1);
		this.head_front.addChild(this.nostril_left1);
		this.mouthpart_top.addChild(this.teeth1);
		this.mouthpart_top.addChild(this.mouthpart_left);
		this.nostril_right2.addChild(this.nostril_right3);
		this.head_base.addChild(this.head_mid);
		this.mouthpart_top.addChild(this.mouthpart_corner1);
		this.mouthpart_top.addChild(this.teeth2);
		this.body_base.addChild(this.head_base);
		this.nostril_right1.addChild(this.nostril_right2);
		this.head_base.addChild(this.eye_left);
		this.mouthpart_top.addChild(this.teeth3);
		this.mouthpart_top.addChild(this.mouthpart_bottom);
		this.head_mid.addChild(this.fancyflap_left1);
		this.mouthpart_top.addChild(this.mouthpart_right);

		setInitPose();
	}

	@Override
	public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) { 
		this.body_base.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
	}

	@Override
	public void setLivingAnimations(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks) {
		this.setToInitPose();

		EntityWallLamprey lamprey = (EntityWallLamprey) entity;

		float hidePercent = lamprey.getLampreyHiddenPercent(partialTicks);

		float[] relHeadLook = lamprey.getRelativeHeadLookAngles(partialTicks);

		double relYaw = Math.toRadians(relHeadLook[0]) * (1 - hidePercent);
		double relPitch = Math.toRadians(relHeadLook[1]) * (1 - hidePercent);

		relYaw = this.easeOutCubic(Math.min(Math.PI * 0.7F, Math.abs(relYaw)) / (Math.PI * 0.7F)) * 0.61F * Math.signum(relYaw) / 4.0F;
		relPitch = this.easeOutCubic(Math.min(Math.PI * 0.7F, Math.abs(relPitch)) / (Math.PI * 0.7F)) * 0.61F * Math.signum(relPitch) / 4.0F;

		this.body_base.yRot += relYaw;
		this.head_base.yRot += relYaw;
		this.head_mid.yRot += relYaw;
		this.head_front.yRot += relYaw;

		this.body_base.xRot += relPitch;
		this.head_base.xRot += relPitch;
		this.head_mid.xRot += relPitch;
		this.head_front.xRot += relPitch;

		float frame = entity.tickCount + partialTicks;

		bob(body_base, 0.07f, 0.1f, false, frame, 1);

		float walkDrive = frame * 0.15F + limbSwing * limbSwingAmount * 0.25F;
		float walkDegree = 0.05F * (1 - hidePercent);

		float swingDrive = walkDrive * 0.9F + (float)Math.PI / 2.0F;
		float swingDegree = walkDegree * (1 - hidePercent);

		swing(this.body_base, 1, swingDegree, false, swingDrive, 0, 0, 1);
		swing(this.head_base, 1, swingDegree, false, swingDrive, 0, 0, 1);
		swing(this.head_mid, 1, swingDegree, false, swingDrive, 0, 0, 1);
		swing(this.head_front, 1, swingDegree, false, swingDrive, 0, 0, 1);

		walk(this.body_base, 1, walkDegree, false, walkDrive, 0, 0, 1);
		walk(this.head_base, 1, walkDegree, false, walkDrive, 0, 0, 1);
		walk(this.head_mid, 1, walkDegree, false, walkDrive, 0, 0, 1);
		walk(this.head_front, 1, walkDegree, false, walkDrive, 0, 0, 1);

		float flapDrive = frame * 0.15F * 3;
		float flapDegree = 0.1F * (1 - hidePercent);

		flap(this.fancyflap_left1, 1, flapDegree, false, flapDrive, 0, 0, 1);
		flap(this.fancyflap_right1, 1, flapDegree, true, flapDrive, 0, 0, 1);

		flap(this.nostril_left1, 1, flapDegree, false, flapDrive + 1.1F, 0, 0, 1);
		flap(this.nostril_right1, 1, flapDegree, true, flapDrive + 1.1F, 0, 0, 1);

		walk(this.body_base, 1, (float)Math.pow(hidePercent, 0.9F) * -(float)Math.PI / 2.0F, false, 0, 0, 0, 1);
		this.body_base.rotationPointY += Math.pow(hidePercent, 1.5F) * 16;

		this.teeth1.rotationPointZ += Math.sin(frame) * 0.1F;
		this.teeth2.rotationPointZ += Math.sin(frame + 0.8F) * 0.1F;
		this.teeth3.rotationPointZ += Math.sin(frame + 1.6F) * 0.1F;
		this.teeth4.rotationPointZ += Math.sin(frame + 2.4F) * 0.1F;
	}

	private double easeOutCubic(double t) {
		t -= 1;
		return (t * t * t + 1);
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
