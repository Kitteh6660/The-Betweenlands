package thebetweenlands.client.render.model.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;

@OnlyIn(Dist.CLIENT)
public class ModelChiromawHatchling extends MowzieModelBase {
    MowzieModelRenderer egg_base;
    MowzieModelRenderer chiromaw_base;
    MowzieModelRenderer egg1;
    MowzieModelRenderer egg2;
    MowzieModelRenderer chiromaw_body;
    MowzieModelRenderer wee_lil_tail1;
    MowzieModelRenderer leg_left1;
    MowzieModelRenderer leg_right1;
    MowzieModelRenderer neck;
    MowzieModelRenderer arm_left1;
    MowzieModelRenderer arm_right1;
    MowzieModelRenderer head1;
    MowzieModelRenderer head2;
    MowzieModelRenderer babytooth_left;
    MowzieModelRenderer babytooth_right;
    MowzieModelRenderer egg3;
    MowzieModelRenderer jaw;
    MowzieModelRenderer arm_left2;
    MowzieModelRenderer wing_left1;
    MowzieModelRenderer wing_left2;
    MowzieModelRenderer arm_right2;
    MowzieModelRenderer wing_right1;
    MowzieModelRenderer wing_right2;
    MowzieModelRenderer wee_lil_tail2;
    MowzieModelRenderer leg_left2;
    MowzieModelRenderer leg_right2;

    MowzieModelRenderer[] partsJustBaby;

    public ModelChiromawHatchling() {
        textureWidth = 128;
        textureHeight = 64;
        arm_right2 = new MowzieModelRenderer(this, 97, 4);
        arm_right2.setPos(0.0F, 1.5F, 0.5F);
        arm_right2.addBox(-0.51F, 0.0F, -1.0F, 1, 3, 1, 0.0F);
        setRotateAngle(arm_right2, -0.9105382707654417F, 0.0F, 0.0F);
        wing_right1 = new MowzieModelRenderer(this, 97, 11);
        wing_right1.setPos(-0.5F, -0.5F, 0.5F);
        wing_right1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(wing_right1, 0.0F, 0.9105382707654417F, 0.0F);
        wee_lil_tail1 = new MowzieModelRenderer(this, 65, 37);
        wee_lil_tail1.setPos(0.0F, 1.0F, 2.0F);
        wee_lil_tail1.addBox(-0.5F, -1.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(wee_lil_tail1, 0.27314402793711257F, 0.0F, 0.0F);
        neck = new MowzieModelRenderer(this, 65, 14);
        neck.setPos(0.0F, 0.0F, -4.0F);
        neck.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 1, 0.0F);
        setRotateAngle(neck, 0.22759093446006054F, 0.0F, 0.0F);
        leg_left1 = new MowzieModelRenderer(this, 90, 17);
        leg_left1.setPos(1.5F, 1.0F, 1.0F);
        leg_left1.addBox(0.0F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        setRotateAngle(leg_left1, 0.36425021489121656F, -0.27314402793711257F, -0.36425021489121656F);
        egg2 = new MowzieModelRenderer(this, 0, 34);
        egg2.setPos(0.0F, -8.0F, 0.0F);
        egg2.addBox(-5.0F, -4.0F, -5.0F, 10, 4, 10, 0.0F);
        wing_left1 = new MowzieModelRenderer(this, 90, 11);
        wing_left1.setPos(0.5F, -0.5F, 0.5F);
        wing_left1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(wing_left1, 0.0F, -0.9105382707654417F, 0.0F);
        arm_left2 = new MowzieModelRenderer(this, 90, 4);
        arm_left2.setPos(0.0F, 1.5F, 0.5F);
        arm_left2.addBox(-0.49F, 0.0F, -1.0F, 1, 3, 1, 0.0F);
        setRotateAngle(arm_left2, -0.9105382707654417F, 0.0F, 0.0F);
        head1 = new MowzieModelRenderer(this, 65, 18);
        head1.setPos(0.0F, 0.0F, -1.0F);
        head1.addBox(-2.5F, -2.0F, -5.0F, 5, 4, 5, 0.0F);
        setRotateAngle(head1, 0.40980330836826856F, 0.0F, 0.0F);
        babytooth_left = new MowzieModelRenderer(this, 80, 32);
        babytooth_left.setPos(1.5F, 2.0F, -5.0F);
        babytooth_left.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        head2 = new MowzieModelRenderer(this, 65, 28);
        head2.setPos(0.0F, 2.0F, 0.0F);
        head2.addBox(-2.5F, 0.0F, -2.0F, 5, 1, 2, 0.0F);
        wee_lil_tail2 = new MowzieModelRenderer(this, 72, 37);
        wee_lil_tail2.setPos(0.0F, 0.0F, 2.0F);
        wee_lil_tail2.addBox(-0.5F, -1.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(wee_lil_tail2, 0.40980330836826856F, 0.0F, 0.0F);
        jaw = new MowzieModelRenderer(this, 65, 32);
        jaw.setPos(0.0F, 0.0F, -2.0F);
        jaw.addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3, 0.0F);
        setRotateAngle(jaw, 0.5918411493512771F, 0.0F, 0.0F);
        arm_left1 = new MowzieModelRenderer(this, 90, 0);
        arm_left1.setPos(2.5F, 1.0F, -3.0F);
        arm_left1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(arm_left1, 0.40980330836826856F, 1.0471975511965976F, -0.8651597102135892F);
        arm_right1 = new MowzieModelRenderer(this, 97, 0);
        arm_right1.setPos(-2.5F, 1.0F, -3.0F);
        arm_right1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(arm_right1, 0.40980330836826856F, -1.0471975511965976F, 0.8651597102135892F);
        chiromaw_body = new MowzieModelRenderer(this, 65, 6);
        chiromaw_body.setPos(0.0F, 0.0F, 0.0F);
        chiromaw_body.addBox(-2.5F, 0.0F, -4.0F, 5, 3, 4, 0.0F);
        setRotateAngle(chiromaw_body, 0.31869712141416456F, 0.0F, 0.0F);
        wing_left2 = new MowzieModelRenderer(this, 90, 6);
        wing_left2.setPos(0.5F, 0.0F, 0.0F);
        wing_left2.addBox(0.0F, 0.0F, 0.0F, 0, 3, 3, 0.0F);
        setRotateAngle(wing_left2, 0.0F, -0.4553564018453205F, 0.0F);
        egg3 = new MowzieModelRenderer(this, 0, 49);
        egg3.setPos(0.0F, -0.5F, -2.5F);
        egg3.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
        setRotateAngle(egg3, 0.0F, 0.091106186954104F, -0.136659280431156F);
        leg_right1 = new MowzieModelRenderer(this, 97, 17);
        leg_right1.setPos(-1.5F, 1.0F, 1.0F);
        leg_right1.addBox(-1.0F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        setRotateAngle(leg_right1, 0.36425021489121656F, 0.27314402793711257F, 0.36425021489121656F);
        chiromaw_base = new MowzieModelRenderer(this, 65, 0);
        chiromaw_base.setPos(0.0F, 13.0F, 0.0F);
        chiromaw_base.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 2, 0.0F);
        setRotateAngle(chiromaw_base, -1.1383037381507017F, 0.0F, 0.0F);
        leg_left2 = new MowzieModelRenderer(this, 90, 22);
        leg_left2.setPos(0.0F, 1.0F, -1.0F);
        leg_left2.addBox(-0.01F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotateAngle(leg_left2, 0.40980330836826856F, 0.0F, 0.0F);
        leg_right2 = new MowzieModelRenderer(this, 97, 22);
        leg_right2.setPos(0.0F, 1.0F, -1.0F);
        leg_right2.addBox(-1.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotateAngle(leg_right2, 0.40980330836826856F, 0.0F, 0.0F);
        babytooth_right = new MowzieModelRenderer(this, 80, 35);
        babytooth_right.setPos(-1.5F, 2.0F, -5.0F);
        babytooth_right.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        egg1 = new MowzieModelRenderer(this, 0, 13);
        egg1.setPos(0.0F, -2.0F, 0.0F);
        egg1.addBox(-6.0F, -8.0F, -6.0F, 12, 8, 12, 0.0F);
        egg_base = new MowzieModelRenderer(this, 0, 0);
        egg_base.setPos(0.0F, 24.0F, 0.0F);
        egg_base.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
        wing_right2 = new MowzieModelRenderer(this, 97, 6);
        wing_right2.setPos(-0.5F, 0.0F, 0.0F);
        wing_right2.addBox(0.0F, 0.0F, 0.0F, 0, 3, 3, 0.0F);
        setRotateAngle(wing_right2, 0.0F, 0.4553564018453205F, 0.0F);
        arm_right1.addChild(arm_right2);
        arm_right1.addChild(wing_right1);
        chiromaw_base.addChild(wee_lil_tail1);
        chiromaw_body.addChild(neck);
        chiromaw_base.addChild(leg_left1);
        egg1.addChild(egg2);
        arm_left1.addChild(wing_left1);
        arm_left1.addChild(arm_left2);
        neck.addChild(head1);
        head1.addChild(babytooth_left);
        head1.addChild(head2);
        wee_lil_tail1.addChild(wee_lil_tail2);
        head2.addChild(jaw);
        chiromaw_body.addChild(arm_left1);
        chiromaw_body.addChild(arm_right1);
        chiromaw_base.addChild(chiromaw_body);
        arm_left2.addChild(wing_left2);
        head1.addChild(egg3);
        chiromaw_base.addChild(leg_right1);
        leg_left1.addChild(leg_left2);
        leg_right1.addChild(leg_right2);
        head1.addChild(babytooth_right);
        egg_base.addChild(egg1);
        arm_right2.addChild(wing_right2);

        partsJustBaby = new MowzieModelRenderer[] {
    			chiromaw_base,
    			chiromaw_body,
    			wee_lil_tail1,
    			leg_left1,
    			leg_right1,
    			neck,
    			arm_left1,
    			arm_right1,
    			head1,
    			head2,
    			babytooth_left,
    			babytooth_right,
    			jaw,
    			arm_left2,
    			wing_left1,
    			wing_left2,
    			arm_right2,
    			wing_right1,
    			wing_right2,
    			wee_lil_tail2,
    			leg_left2,
    			leg_right2
        };
        setInitPose();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float scale) {
    	this.renderEgg((EntityChiromawHatchling) entity, entityTickTime - entity.tickCount, 0.0625f);
    }
 
	public void renderBaby(EntityChiromawHatchling entity, float partialTicks, float scale) {
    	EntityChiromawHatchling chiromaw = (EntityChiromawHatchling) entity;
    	float smootherRise = chiromaw.prevRise + (chiromaw.getRiseCount() - chiromaw.prevRise) * partialTicks;
    	float flyUp = chiromaw.getTransformCount() + (chiromaw.prevTransformTick - chiromaw.getTransformCount()) * partialTicks;
    	float flap = MathHelper.sin((chiromaw.tickCount + partialTicks) * 0.5F) * 0.15F;
		if (!chiromaw.getIsTransforming())
			flap = 0F;

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 0.5F - smootherRise * 0.0125F - flyUp * 0.01F - flap * 0.5F, 0.0F);
		GlStateManager.translate(0.0F, 0.0F, 0.25F - smootherRise * 0.00625F);
		
        GlStateManager.enableCull();
        GlStateManager.cullFace(CullFace.FRONT);
        wing_left1.showModel = false;
        wing_left2.showModel = false;
        wing_right1.showModel = false;
        wing_right2.showModel = false;
        chiromaw_base.render(scale);
        wing_left1.showModel = true;
        wing_left2.showModel = true;
        wing_right1.showModel = true;
        wing_right2.showModel = true;
        GlStateManager.cullFace(CullFace.BACK);
        chiromaw_base.render(scale);
        GlStateManager.disableCull();
		GlStateManager.popMatrix();
		
	}

	public void renderEgg(EntityChiromawHatchling entity, float partialTicks, float scale) {
		GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
		
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        egg_base.render(scale);
        
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
    public void setLivingAnimations(LivingEntity entity, float swing, float speed, float partialRenderTicks) {
    	EntityChiromawHatchling chiromaw = (EntityChiromawHatchling) entity;
    	setToInitPose();
    	float globalSpeed = 1F;
        float globalDegree = 0.5F;
        float frame = chiromaw.tickCount + partialRenderTicks;
        float flap = MathHelper.sin((frame) * 0.5F) * 0.6F;
    	float smoother = chiromaw.prevRise + (chiromaw.getRiseCount() - chiromaw.prevRise) * partialRenderTicks;
    	float smootherFeed = chiromaw.prevFeederRotation + (chiromaw.feederRotation - chiromaw.prevFeederRotation) * partialRenderTicks;
    	float smootherHead = chiromaw.prevHeadPitch + (chiromaw.headPitch - chiromaw.prevHeadPitch) * partialRenderTicks;
    	chiromaw_base.yRot = convertDegtoRad(smootherFeed);
    	if (!chiromaw.getIsTransforming()) {
			head1.xRot = convertDegtoRad(-43.5F + smootherHead * 1.5F);
			jaw.xRot = convertDegtoRad(4F + smoother * 1.5F);
			arm_right1.xRot = convertDegtoRad(-23.5F + smoother);
			arm_left1.xRot = convertDegtoRad(-23.5F + smoother);
			arm_right2.xRot = convertDegtoRad(-92F + smoother);
			arm_left2.xRot = convertDegtoRad(-92F + smoother);
			arm_right1.yRot = convertDegtoRad(-20F - smootherHead * 1.5F);
			arm_left1.yRot = convertDegtoRad(20F + smootherHead * 1.5F);
			arm_right2.yRot = convertDegtoRad(60F - smootherHead * 1.5F);
			arm_left2.yRot = convertDegtoRad(-60F + smootherHead * 1.5F);

			if(chiromaw.flapArms) {
				flap(arm_right2, globalSpeed, globalDegree * 0.25f, false, 2.0f, 0f, chiromaw.flapArmsCount, 1F);
				flap(arm_left2, globalSpeed, globalDegree * 0.25f, true, 2.0f, 0f, chiromaw.flapArmsCount, 1F);
				flap(arm_right1, globalSpeed, globalDegree * 0.25f, false, 2.0f, 0f, chiromaw.flapArmsCount, 1F);
				flap(arm_left1, globalSpeed, globalDegree * 0.25f, true, 2.0f, 0f, chiromaw.flapArmsCount, 1F);
			}

			if (chiromaw.getRiseCount() >= EntityChiromawHatchling.MAX_RISE - 20 && chiromaw.getIsHungry()) {
				neck.yRot = 0F + flap;
				walk(arm_right2, globalSpeed * 0.5f, globalDegree * 0.5f, false, 2.0f, 0f, frame, 1F);
				walk(arm_left2, globalSpeed * 0.5f, globalDegree * 0.5f, false, 2.0f, 0f, frame, 1F);
			} else {
				neck.yRot = 0F;
			}

			if (chiromaw.getIsChewing()) {
				swing(jaw, globalSpeed * 0.75f, globalDegree * 0.5f, false, 2.0f, 0f, frame / ((float) Math.PI), 1F);
				walk(jaw, globalSpeed * 0.5f, globalDegree * 0.5f, false, 2.0f, -0.75f, frame, 1F);
			} else if (!chiromaw.getIsHungry() && chiromaw.getRiseCount() >= EntityChiromawHatchling.MAX_RISE)
				walk(jaw, globalSpeed * 0.125f, globalDegree * 0.5f, false, 2.0f, -0.75f, frame, 1F);
		}
    	if (chiromaw.getIsTransforming()) {
    		flap(arm_right1, globalSpeed * 0.5f, globalDegree * 1f, false, 2.0f, 0f, frame, 1F);
			flap(arm_left1, globalSpeed * 0.5f, globalDegree * 1f, true, 2.0f, 0f, frame, 1F);
			flap(arm_right2, globalSpeed * 0.5f, globalDegree * 1f, false, 2.0f, 0f, frame, 1F);
			flap(arm_left2, globalSpeed * 0.5f, globalDegree * 1f, true, 2.0f, 0f, frame, 1F);
			
            swing(arm_right1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0.5f, frame, 1F);
            swing(arm_left1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, -0.5f, frame, 1F);
            walk(jaw, globalSpeed * 0.125f, globalDegree * 0.5f, false, 2.0f, 0f, frame, 1F);
    	}
	}

    public void setRotateAngle(MowzieModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

	//just some helpers for future
	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

	public float convertRadtoDeg(float radIn) {
		return radIn * 180F / ((float) Math.PI);
	}


}
