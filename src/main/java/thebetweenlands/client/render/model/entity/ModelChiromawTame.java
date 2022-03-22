package thebetweenlands.client.render.model.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

@OnlyIn(Dist.CLIENT)
public class ModelChiromawTame extends MowzieModelBase {
	MowzieModelRenderer body_base;
	MowzieModelRenderer body_buttpart;
	MowzieModelRenderer neck;
	MowzieModelRenderer arm_left1;
	MowzieModelRenderer arm_right1;
	MowzieModelRenderer leg_left1;
	MowzieModelRenderer leg_right1;
	MowzieModelRenderer lil_tail1;
	MowzieModelRenderer leg_left2;
	MowzieModelRenderer leg_right2;
	MowzieModelRenderer lil_tail2;
	MowzieModelRenderer lil_tail3;
	MowzieModelRenderer head_base;
	MowzieModelRenderer head_connection;
	MowzieModelRenderer head_jaw1;
	MowzieModelRenderer fang_left;
	MowzieModelRenderer fang_right;
	MowzieModelRenderer teeth_upperjaw_left;
	MowzieModelRenderer teeth_upperjaw_right;
	MowzieModelRenderer teeth_upperjaw_front;
	MowzieModelRenderer teeth_lowerjaw_left;
	MowzieModelRenderer teeth_lowerjaw_right;
	MowzieModelRenderer teeth_lowerjaw_front;
	MowzieModelRenderer arm_left2;
	MowzieModelRenderer wing_left1;
	MowzieModelRenderer wing_left2;
	MowzieModelRenderer arm_right2;
	MowzieModelRenderer wing_right1;
	MowzieModelRenderer wing_right2;

	public ModelChiromawTame() {
		textureWidth = 128;
		textureHeight = 64;
		leg_left1 = new MowzieModelRenderer(this, 46, 0);
		leg_left1.setPos(1.5F, 2.0F, -1.0F);
		leg_left1.addBox(0.0F, -1.0F, -1.5F, 2, 5, 3, 0.0F);
		setRotation(leg_left1, -2.276432943376204F, -0.5009094953223726F, 0.27314402793711257F);
		head_connection = new MowzieModelRenderer(this, 21, 18);
		head_connection.setPos(0.0F, 2.0F, 0.0F);
		head_connection.addBox(-3.0F, 0.0F, -2.0F, 6, 2, 2, 0.0F);
		neck = new MowzieModelRenderer(this, 21, 0);   //
		neck.setPos(0.0F, 0.0F, 2.0F);
		neck.addBox(-1.5F, 0.0F, -3.0F, 3, 3, 3, 0.0F);
		setRotation(neck, -0.7285004297824331F, 0.0F, 0.0F);
		teeth_lowerjaw_right = new MowzieModelRenderer(this, 32, 26);
		teeth_lowerjaw_right.setPos(-2.5F, -1.0F, -3.0F);
		teeth_lowerjaw_right.addBox(0.0F, -2.0F, -2.0F, 0, 2, 5, 0.0F);
		setRotation(teeth_lowerjaw_right, 0.0F, 0.0F, -0.22759093446006054F);
		head_base = new MowzieModelRenderer(this, 21, 7);
		head_base.setPos(0.0F, 0.0F, -3.0F);
		head_base.addBox(-3.0F, -2.0F, -6.0F, 6, 4, 6, 0.0F);
		setRotation(head_base, 0.091106186954104F, 0.0F, 0.0F);
		lil_tail2 = new MowzieModelRenderer(this, 0, 24);
		lil_tail2.setPos(0.0F, 3.0F, 0.0F);
		lil_tail2.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		setRotation(lil_tail2, 0.36425021489121656F, 0.0F, 0.0F);
		teeth_upperjaw_left = new MowzieModelRenderer(this, 21, 39);
		teeth_upperjaw_left.setPos(3.0F, 2.0F, -3.0F);
		teeth_upperjaw_left.addBox(0.0F, 0.0F, -2.0F, 0, 1, 3, 0.0F);
		setRotation(teeth_upperjaw_left, 0.0F, 0.0F, -0.091106186954104F);
		body_base = new MowzieModelRenderer(this, 0, 0);
		body_base.setPos(0.0F, 12.0F, 0.0F);
		body_base.addBox(-3.0F, 0.0F, -2.0F, 6, 6, 4, 0.0F);
		setRotation(body_base, 0.5462880558742251F, 0.0F, 0.0F);
		lil_tail3 = new MowzieModelRenderer(this, 0, 30);
		lil_tail3.setPos(0.0F, 3.0F, 0.0F);
		lil_tail3.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		setRotation(lil_tail3, 0.40980330836826856F, 0.0F, 0.0F);
		wing_right2 = new MowzieModelRenderer(this, 57, 44);
		wing_right2.setPos(-1.0F, 0.0F, 1.0F);
		wing_right2.addBox(0.0F, -1.0F, 0.0F, 0, 8, 5, 0.0F);
		setRotation(wing_right2, 0.0F, 0.27314402793711257F, 0.0F);
		head_jaw1 = new MowzieModelRenderer(this, 21, 23);
		head_jaw1.setPos(0.0F, 3.0F, -1.0F);
		head_jaw1.addBox(-2.5F, -1.0F, -5.0F, 5, 2, 5, 0.0F);
		setRotation(head_jaw1, 0.9560913642424937F, 0.0F, 0.0F);

		lil_tail1 = new MowzieModelRenderer(this, 0, 18);
		lil_tail1.setPos(0.0F, 3.0F, -2.0F);
		lil_tail1.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
		setRotation(lil_tail1, 0.27314402793711257F, 0.0F, 0.0F);
		leg_right2 = new MowzieModelRenderer(this, 57, 9);
		leg_right2.setPos(-1.0F, 4.0F, 0.0F);
		leg_right2.addBox(-1.01F, 0.0F, -1.5F, 2, 7, 2, 0.0F);
		setRotation(leg_right2, 2.1855012893472994F, 0.0F, 0.0F);
		fang_left = new MowzieModelRenderer(this, 21, 37);
		fang_left.setPos(3.0F, 2.0F, -6.0F);
		fang_left.addBox(-1.0F, -1.0F, 0.0F, 1, 3, 1, 0.0F);
		setRotation(fang_left, -0.091106186954104F, 0.0F, -0.091106186954104F);

		leg_left2 = new MowzieModelRenderer(this, 46, 9);
		leg_left2.setPos(1.0F, 4.0F, 0.0F);
		leg_left2.addBox(-0.99F, 0.0F, -1.5F, 2, 7, 2, 0.0F);
		setRotation(leg_left2, 2.1855012893472994F, 0.0F, 0.0F);
		wing_left1 = new MowzieModelRenderer(this, 46, 34);
		wing_left1.setPos(1.0F, 0.0F, 1.0F);
		wing_left1.addBox(0.0F, 0.0F, 0.0F, 0, 7, 5, 0.0F);
		setRotation(wing_left1, 0.0F, -0.27314402793711257F, 0.0F);

		teeth_lowerjaw_left = new MowzieModelRenderer(this, 21, 26);
		teeth_lowerjaw_left.setPos(2.5F, -1.0F, -3.0F);
		teeth_lowerjaw_left.addBox(0.0F, -2.0F, -2.0F, 0, 2, 5, 0.0F);
		setRotation(teeth_lowerjaw_left, 0.0F, 0.0F, 0.22759093446006054F);
		leg_right1 = new MowzieModelRenderer(this, 57, 0);
		leg_right1.setPos(-1.5F, 2.0F, -1.0F);
		leg_right1.addBox(-2.0F, -1.0F, -1.5F, 2, 5, 3, 0.0F);
		setRotation(leg_right1, -2.276432943376204F, 0.5009094953223726F, -0.27314402793711257F);
		teeth_lowerjaw_front = new MowzieModelRenderer(this, 21, 34);
		teeth_lowerjaw_front.setPos(0.0F, -1.0F, -5.0F);
		teeth_lowerjaw_front.addBox(-2.5F, -2.0F, 0.0F, 5, 2, 0, 0.0F);
		setRotation(teeth_lowerjaw_front, 0.136659280431156F, 0.0F, 0.0F);
		wing_right1 = new MowzieModelRenderer(this, 46, 44);
		wing_right1.setPos(-1.0F, 0.0F, 1.0F);
		wing_right1.addBox(0.0F, 0.0F, 0.0F, 0, 7, 5, 0.0F);
		setRotation(wing_right1, 0.0F, 0.27314402793711257F, 0.0F);
		fang_right = new MowzieModelRenderer(this, 26, 37);
		fang_right.setPos(-3.0F, 2.0F, -6.0F);
		fang_right.addBox(0.0F, -1.0F, 0.0F, 1, 3, 1, 0.0F);
		setRotation(fang_right, -0.091106186954104F, 0.0F, 0.091106186954104F);
		wing_left2 = new MowzieModelRenderer(this, 57, 34);
		wing_left2.setPos(1.0F, 0.0F, 1.0F);
		wing_left2.addBox(0.0F, -1.0F, 0.0F, 0, 8, 5, 0.0F);
		setRotation(wing_left2, 0.0F, -0.27314402793711257F, 0.0F);
		teeth_upperjaw_right = new MowzieModelRenderer(this, 27, 39);
		teeth_upperjaw_right.setPos(-3.0F, 2.0F, -3.0F);
		teeth_upperjaw_right.addBox(0.0F, 0.0F, -2.0F, 0, 1, 3, 0.0F);
		setRotation(teeth_upperjaw_right, 0.0F, 0.0F, 0.091106186954104F);
		teeth_upperjaw_front = new MowzieModelRenderer(this, 21, 44);
		teeth_upperjaw_front.setPos(0.0F, 2.0F, -6.0F);
		teeth_upperjaw_front.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 0, 0.0F);
		setRotation(teeth_upperjaw_front, -0.091106186954104F, 0.0F, 0.0F);
		body_buttpart = new MowzieModelRenderer(this, 0, 11);
		body_buttpart.setPos(0.0F, 6.0F, 2.0F);
		body_buttpart.addBox(-2.0F, 0.0F, -3.0F, 4, 3, 3, 0.0F);
		setRotation(body_buttpart, -0.22759093446006054F, 0.0F, 0.0F);
		arm_right1 = new MowzieModelRenderer(this, 46, 29);
		arm_right1.setPos(-3.0F, 0.0F, 1.0F);
		arm_right1.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
		setRotation(arm_right1, -0.8196066167365371F, -0.091106186954104F, 0.5462880558742251F);
		arm_right2 = new MowzieModelRenderer(this, 55, 29);
		arm_right2.setPos(0.0F, 7.0F, 0.0F);
		arm_right2.addBox(-1.01F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
		setRotation(arm_right2, -0.31869712141416456F, 0.0F, 0.0F);
		arm_left1 = new MowzieModelRenderer(this, 46, 19);
		arm_left1.setPos(3.0F, 0.0F, 1.0F);
		arm_left1.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
		setRotation(arm_left1, -0.8196066167365371F, 0.091106186954104F, -0.5462880558742251F);
		arm_left2 = new MowzieModelRenderer(this, 55, 19);
		arm_left2.setPos(0.0F, 7.0F, 0.0F);
		arm_left2.addBox(-0.99F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
		setRotation(arm_left2, -0.31869712141416456F, 0.0F, 0.0F);

		body_buttpart.addChild(leg_left1);
		head_base.addChild(head_connection);
		body_base.addChild(neck);
		head_jaw1.addChild(teeth_lowerjaw_right);
		neck.addChild(head_base);
		lil_tail1.addChild(lil_tail2);
		head_base.addChild(teeth_upperjaw_left);
		lil_tail2.addChild(lil_tail3);
		arm_right2.addChild(wing_right2);
		head_base.addChild(head_jaw1);
		arm_right1.addChild(arm_right2);
		body_buttpart.addChild(lil_tail1);
		leg_right1.addChild(leg_right2);
		head_base.addChild(fang_left);
		arm_left1.addChild(arm_left2);
		leg_left1.addChild(leg_left2);
		arm_left1.addChild(wing_left1);
		body_base.addChild(arm_left1);
		head_jaw1.addChild(teeth_lowerjaw_left);
		body_buttpart.addChild(leg_right1);
		head_jaw1.addChild(teeth_lowerjaw_front);
		arm_right1.addChild(wing_right1);
		head_base.addChild(fang_right);
		arm_left2.addChild(wing_left2);
		head_base.addChild(teeth_upperjaw_right);
		head_base.addChild(teeth_upperjaw_front);
		body_base.addChild(arm_right1);
		body_base.addChild(body_buttpart);

		setInitPose();
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float unitPixel) {
		EntityChiromawTame chiromaw = (EntityChiromawTame) entity;
		// hax to prevent rotation derpiness
		if (chiromaw.getOwner() == null || chiromaw.tickCount > 4) {
			GlStateManager.pushMatrix();

			if (chiromaw.isSitting()) {
				GlStateManager.translate(0.0F, 2.125F, 0.0F);
				GlStateManager.rotate(180, 1F, 0F, 0.0F);
			} else if (chiromaw.isRiding()) {
				//something else
			} else {
				GlStateManager.rotate(40, 1F, 0F, 0.0F);
				GlStateManager.translate(0.0F, 0F, -0.8F);
			}

			GlStateManager.enableCull();
			GlStateManager.cullFace(CullFace.FRONT);
			wing_left1.showModel = false;
			wing_left2.showModel = false;
			wing_right1.showModel = false;
			wing_right2.showModel = false;
			body_base.render(unitPixel);
			wing_left1.showModel = true;
			wing_left2.showModel = true;
			wing_right1.showModel = true;
			wing_right2.showModel = true;
			GlStateManager.cullFace(CullFace.BACK);
			body_base.render(unitPixel);
			GlStateManager.disableCull();

			GlStateManager.popMatrix();
		}
	}

	public void setRotation(MowzieModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

	@Override
	public void setLivingAnimations(LivingEntity entity, float swing, float speed, float partialRenderTicks) {
		EntityChiromawTame chiromaw = (EntityChiromawTame) entity;
		setToInitPose();
		float flap = MathHelper.sin((chiromaw.tickCount + partialRenderTicks) * 0.5F) * 0.6F;
		float flapSlower = MathHelper.sin((chiromaw.tickCount + partialRenderTicks) * 0.125F) * 0.1F;
		
		if (chiromaw.isSitting() || chiromaw.isRiding()) {
			float wingRaised = (chiromaw.prevRaiseWingTicks + (chiromaw.raiseWingsTicks - chiromaw.prevRaiseWingTicks) * partialRenderTicks) / 2.5f;

			float wingFlapRaised = wingRaised;
			float wingFlapTicks = chiromaw.prevWingFlapTicks + (chiromaw.wingFlapTicks - chiromaw.prevWingFlapTicks) * partialRenderTicks;
			if(wingFlapTicks > 1) {
				float wingSwing = (20 - wingFlapTicks) / 5.0f;
				wingSwing = 1.0f / (1.0f + (float)Math.exp(3 - wingSwing * 6));
				wingFlapRaised = Math.max(0.0f, wingRaised - wingSwing);
			} else if(wingFlapTicks == 1) {
				wingFlapRaised = 0.0f;
			}
			
			arm_right1.zRot = 0.5462880558742251F;
			arm_right2.zRot = 0F;
			arm_left1.zRot = -0.5462880558742251F;
			arm_left2.zRot = 0;

			arm_right1.yRot = 0.9091106186954104F;
			arm_right2.yRot = 0F;
			arm_left1.yRot = -0.9091106186954104F;
			arm_left2.yRot = 0;

			float globalSpeed = 1F;
			float globalDegree = 1F;
			float frame = wingFlapRaised * 6;

			swing(arm_right1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0.5f, frame, 1F);
			flap(arm_right2, globalSpeed * 0.5f, globalDegree * 0.8f, false, 2.0f, 0f, frame, 1F);
			swing(arm_left1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, -0.5f, frame, 1F);
			flap(arm_left2, globalSpeed * 0.5f, globalDegree * 0.8f, true, 2.0f, 0f, frame, 1F);

			walk(arm_right1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, frame, 1F);
			walk(arm_right2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, frame, 1F);
			walk(arm_left1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, frame, 1F);
			walk(arm_left2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, frame, 1F);
			
			globalSpeed = 0.1f;
			globalDegree = 0.1f;
			frame = chiromaw.tickCount + partialRenderTicks;

			swing(arm_right1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0, frame, 1F);
			flap(arm_right2, globalSpeed * 0.5f, globalDegree * 0.8f, false, 2.0f, 0, frame, 1F);
			swing(arm_left1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, 0, frame, 1F);
			flap(arm_left2, globalSpeed * 0.5f, globalDegree * 0.8f, true, 2.0f, 0, frame, 1F);

			
			leg_right1.xRot = -2.276432943376204F;
			leg_left1.xRot = -2.276432943376204F;
			
			lil_tail1.xRot = 0.27314402793711257F;
			lil_tail2.xRot = 0.36425021489121656F;
			lil_tail3.xRot = 0.40980330836826856F;

			lil_tail1.xRot = 0.27314402793711257F;
			lil_tail2.xRot = 0.36425021489121656F;
			lil_tail3.xRot = 0.40980330836826856F;

			head_jaw1.xRot = 0.9560913642424937F - flapSlower;
			head_base.xRot = 0.091106186954104F;
			
			float stance = wingRaised * 0.6f;
			
			leg_right2.zRot -= stance * 2;
			leg_left2.zRot += stance * 2;
			
			leg_right2.xRot -= stance * 1.5f;
			leg_left2.xRot -= stance * 1.5f;
			
			leg_right2.yRot -= stance * 0.15f;
			leg_left2.yRot += stance * 0.15f;
			
			head_base.xRot -= stance;
			body_base.xRot += stance;
			
			body_base.rotationPointY += stance * 3;
			body_base.rotationPointZ -= stance * 3 + 3;
		} else {
			arm_right1.zRot = 0.5462880558742251F;
			arm_right2.zRot = 0F;
			arm_left1.zRot = -0.5462880558742251F;
			arm_left2.zRot = 0;

			arm_right1.yRot = 0.9091106186954104F;
			arm_right2.yRot = 0F;
			arm_left1.yRot = -0.9091106186954104F;
			arm_left2.yRot = 0;

			float globalSpeed = 1F;
			float globalDegree = 1F;
			float frame = chiromaw.tickCount + partialRenderTicks;

			swing(arm_right1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0.5f, frame, 1F);
			flap(arm_right2, globalSpeed * 0.5f, globalDegree * 0.8f, false, 2.0f, 0f, frame, 1F);
			swing(arm_left1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, -0.5f, frame, 1F);
			flap(arm_left2, globalSpeed * 0.5f, globalDegree * 0.8f, true, 2.0f, 0f, frame, 1F);

			walk(arm_right1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, frame, 1F);
			walk(arm_right2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, frame, 1F);
			walk(arm_left1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, frame, 1F);
			walk(arm_left2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, frame, 1F);

			leg_right1.xRot = -2.276432943376204F + flap * 0.5F;
			leg_left1.xRot = -2.276432943376204F + flap * 0.5F;

			lil_tail1.xRot = 0.27314402793711257F + flap * 0.5F;
			lil_tail2.xRot = 0.36425021489121656F + flap * 0.25F;
			lil_tail3.xRot = 0.40980330836826856F + flap * 0.125F;

			head_jaw1.xRot = 0.9560913642424937F - flap * 0.5F;
			head_base.xRot = -0.698132F;
		}   
	}

	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

	public float convertRadtoDeg(float radIn) {
		return radIn * 180F / ((float) Math.PI);
	}

}
