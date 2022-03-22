package thebetweenlands.client.render.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityAshSprite;

@OnlyIn(Dist.CLIENT)
public class ModelAshSprite extends Model {
    ModelRenderer head_base;
    ModelRenderer head_connection;
    ModelRenderer maskplate_mid1;
    ModelRenderer cloth_left_front1;
    ModelRenderer cloth_right_front1;
    ModelRenderer neck;
    ModelRenderer jaw_lower;
    ModelRenderer tooth_right1;
    ModelRenderer tooth_left1;
    ModelRenderer tooth_right2;
    ModelRenderer tooth_left2;
    ModelRenderer maskplate_mid2;
    ModelRenderer maskplate_right1;
    ModelRenderer maskplate_left1;
    ModelRenderer maskplate_right2;
    ModelRenderer maskplate_right3;
    ModelRenderer cloth_right_back1;
    ModelRenderer cloth_right_back2;
    ModelRenderer cloth_right_back3;
    ModelRenderer maskplate_left2;
    ModelRenderer maskplate_left3;
    ModelRenderer cloth_left_back1;
    ModelRenderer cloth_left_back2;
    ModelRenderer cloth_left_back3;
    ModelRenderer cloth_left_front2;
    ModelRenderer cloth_right_front2;
    ModelRenderer torso1;
    ModelRenderer torso2;
    ModelRenderer torso3;

    public ModelAshSprite(float expand) {
    	super(expand);
        texWidth = 64;
        texHeight = 64;
        tooth_right2 = new ModelRenderer(this, 0, 21);
        tooth_right2.setPos(0.0F, -1.0F, 0.0F);
        tooth_right2.addBox(0.0F, -2.0F, 0.0F, 1, 2, 1, 0.0F);
        setRotateAngle(tooth_right2, 0.0F, 0.0F, 0.31869712141416456F);
        cloth_left_front2 = new ModelRenderer(this, 33, 49);
        cloth_left_front2.setPos(0.0F, 3.0F, 0.0F);
        cloth_left_front2.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(cloth_left_front2, 0.0F, 0.0F, 0.27314402793711257F);
        tooth_left2 = new ModelRenderer(this, 5, 21);
        tooth_left2.setPos(0.0F, -1.0F, 0.0F);
        tooth_left2.addBox(-1.0F, -2.0F, 0.0F, 1, 2, 1, 0.0F);
        setRotateAngle(tooth_left2, 0.0F, 0.0F, -0.31869712141416456F);
        maskplate_right3 = new ModelRenderer(this, 20, 20);
        maskplate_right3.setPos(1.5F, 0.0F, 3.0F);
        maskplate_right3.addBox(-3.0F, -2.0F, 0.0F, 3, 2, 2, 0.0F);
        setRotateAngle(maskplate_right3, 0.0F, -0.136659280431156F, 0.0F);
        cloth_left_front1 = new ModelRenderer(this, 33, 43);
        cloth_left_front1.setPos(2.5F, -2.0F, -2.0F);
        cloth_left_front1.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(cloth_left_front1, 0.0F, 0.0F, -0.27314402793711257F);
        maskplate_left3 = new ModelRenderer(this, 33, 20);
        maskplate_left3.setPos(-1.5F, 0.0F, 3.0F);
        maskplate_left3.addBox(0.0F, -2.0F, 0.0F, 3, 2, 2, 0.0F);
        setRotateAngle(maskplate_left3, 0.0F, 0.136659280431156F, 0.0F);
        cloth_right_front1 = new ModelRenderer(this, 20, 43);
        cloth_right_front1.setPos(-2.5F, -2.0F, -2.0F);
        cloth_right_front1.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(cloth_right_front1, 0.0F, 0.0F, 0.27314402793711257F);
        tooth_left1 = new ModelRenderer(this, 5, 18);
        tooth_left1.setPos(1.5F, 1.0F, -2.5F);
        tooth_left1.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F);
        setRotateAngle(tooth_left1, -0.18203784098300857F, -0.091106186954104F, 1.1838568316277536F);
        tooth_right1 = new ModelRenderer(this, 0, 18);
        tooth_right1.setPos(-1.5F, 1.0F, -2.5F);
        tooth_right1.addBox(0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F);
        setRotateAngle(tooth_right1, -0.18203784098300857F, 0.091106186954104F, -1.1838568316277536F);
        maskplate_right1 = new ModelRenderer(this, 20, 10);
        maskplate_right1.setPos(0.0F, 0.0F, 2.5F);
        maskplate_right1.addBox(-3.0F, -1.0F, 0.0F, 3, 2, 1, 0.0F);
        setRotateAngle(maskplate_right1, 0.0F, -0.091106186954104F, 0.0F);
        maskplate_mid1 = new ModelRenderer(this, 20, 0);
        maskplate_mid1.setPos(0.0F, -2.5F, -4.0F);
        maskplate_mid1.addBox(-3.0F, -1.0F, -0.5F, 6, 2, 3, 0.0F);
        setRotateAngle(maskplate_mid1, 0.136659280431156F, 0.0F, 0.0F);
        neck = new ModelRenderer(this, 0, 25);
        neck.setPos(0.0F, -2.0F, 1.0F);
        neck.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
        setRotateAngle(neck, 0.4553564018453205F, 0.0F, 0.0F);
        torso3 = new ModelRenderer(this, 0, 44);
        torso3.setPos(0.0F, 3.0F, -2.0F);
        torso3.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 2, 0.0F);
        setRotateAngle(torso3, 0.18203784098300857F, 0.0F, 0.0F);
        jaw_lower = new ModelRenderer(this, 0, 13);
        jaw_lower.setPos(0.0F, 0.0F, -1.0F);
        jaw_lower.addBox(-1.5F, 0.0F, -3.0F, 3, 1, 3, 0.0F);
        setRotateAngle(jaw_lower, 0.091106186954104F, 0.0F, 0.0F);
        cloth_right_back1 = new ModelRenderer(this, 20, 25);
        cloth_right_back1.setPos(-1.5F, 0.0F, 0.0F);
        cloth_right_back1.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        head_base = new ModelRenderer(this, 0, 0);
        head_base.setPos(0.0F, 4.0F, 1.0F);
        head_base.addBox(-2.0F, -3.0F, -4.0F, 4, 3, 5, 0.0F);
        setRotateAngle(head_base, 0.27314402793711257F, 0.0F, 0.0F);
        maskplate_mid2 = new ModelRenderer(this, 20, 6);
        maskplate_mid2.setPos(0.0F, 0.0F, -0.5F);
        maskplate_mid2.addBox(-2.0F, -1.0F, -1.0F, 4, 2, 1, 0.0F);
        head_connection = new ModelRenderer(this, 0, 9);
        head_connection.setPos(0.0F, 0.0F, 0.0F);
        head_connection.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2, 0.0F);
        maskplate_left1 = new ModelRenderer(this, 33, 10);
        maskplate_left1.setPos(0.0F, 0.0F, 2.5F);
        maskplate_left1.addBox(0.0F, -1.0F, 0.0F, 3, 2, 1, 0.0F);
        setRotateAngle(maskplate_left1, 0.0F, 0.091106186954104F, 0.0F);
        cloth_left_back2 = new ModelRenderer(this, 33, 31);
        cloth_left_back2.setPos(0.0F, 3.0F, 0.0F);
        cloth_left_back2.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(cloth_left_back2, 0.0F, 0.0F, -0.22759093446006054F);
        cloth_right_back3 = new ModelRenderer(this, 20, 37);
        cloth_right_back3.setPos(0.0F, 3.0F, 0.0F);
        cloth_right_back3.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(cloth_right_back3, 0.0F, 0.0F, 0.22759093446006054F);
        cloth_left_back1 = new ModelRenderer(this, 33, 25);
        cloth_left_back1.setPos(1.5F, 0.0F, 0.0F);
        cloth_left_back1.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        torso2 = new ModelRenderer(this, 0, 38);
        torso2.setPos(0.0F, 3.0F, 0.0F);
        torso2.addBox(-2.0F, 0.0F, -2.0F, 4, 3, 2, 0.0F);
        setRotateAngle(torso2, -0.6829473363053812F, 0.0F, 0.0F);
        maskplate_right2 = new ModelRenderer(this, 20, 14);
        maskplate_right2.setPos(-1.5F, 1.0F, 1.0F);
        maskplate_right2.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3, 0.0F);
        setRotateAngle(maskplate_right2, 0.31869712141416456F, 0.0F, 0.0F);
        torso1 = new ModelRenderer(this, 0, 31);
        torso1.setPos(0.0F, 3.0F, 0.0F);
        torso1.addBox(-2.5F, 0.0F, -3.0F, 5, 3, 3, 0.0F);
        setRotateAngle(torso1, -0.22759093446006054F, 0.0F, 0.0F);
        cloth_right_back2 = new ModelRenderer(this, 20, 31);
        cloth_right_back2.setPos(0.0F, 3.0F, 0.0F);
        cloth_right_back2.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(cloth_right_back2, 0.0F, 0.0F, 0.22759093446006054F);
        cloth_left_back3 = new ModelRenderer(this, 33, 37);
        cloth_left_back3.setPos(0.0F, 3.0F, 0.0F);
        cloth_left_back3.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(cloth_left_back3, 0.0F, 0.0F, -0.22759093446006054F);
        maskplate_left2 = new ModelRenderer(this, 33, 14);
        maskplate_left2.setPos(1.5F, 1.0F, 1.0F);
        maskplate_left2.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3, 0.0F);
        setRotateAngle(maskplate_left2, 0.31869712141416456F, 0.0F, 0.0F);
        cloth_right_front2 = new ModelRenderer(this, 20, 49);
        cloth_right_front2.setPos(0.0F, 3.0F, 0.0F);
        cloth_right_front2.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(cloth_right_front2, 0.0F, 0.0F, -0.27314402793711257F);
        tooth_right1.addChild(tooth_right2);
        cloth_left_front1.addChild(cloth_left_front2);
        tooth_left1.addChild(tooth_left2);
        maskplate_right2.addChild(maskplate_right3);
        head_base.addChild(cloth_left_front1);
        maskplate_left2.addChild(maskplate_left3);
        head_base.addChild(cloth_right_front1);
        jaw_lower.addChild(tooth_left1);
        jaw_lower.addChild(tooth_right1);
        maskplate_mid1.addChild(maskplate_right1);
        head_base.addChild(maskplate_mid1);
        head_base.addChild(neck);
        torso2.addChild(torso3);
        head_connection.addChild(jaw_lower);
        maskplate_right2.addChild(cloth_right_back1);
        maskplate_mid1.addChild(maskplate_mid2);
        head_base.addChild(head_connection);
        maskplate_mid1.addChild(maskplate_left1);
        cloth_left_back1.addChild(cloth_left_back2);
        cloth_right_back2.addChild(cloth_right_back3);
        maskplate_left2.addChild(cloth_left_back1);
        torso1.addChild(torso2);
        maskplate_right1.addChild(maskplate_right2);
        neck.addChild(torso1);
        cloth_right_back1.addChild(cloth_right_back2);
        cloth_left_back2.addChild(cloth_left_back3);
        maskplate_left1.addChild(maskplate_left2);
        cloth_right_front1.addChild(cloth_right_front2);
    }

    @Override
    public void renderToBuffer(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float scale) {
    	GlStateManager.pushMatrix();
    	GlStateManager.enableCull();
    	head_base.render(scale);
    	GlStateManager.disableCull();
    	GlStateManager.popMatrix();
    }
    
    @Override
	public void setLivingAnimations(LivingEntity entity, float limbSwing, float limbSwingAngle, float partialRenderTicks) {
    	EntityAshSprite sprite = (EntityAshSprite) entity;
    	float flap = MathHelper.sin((sprite.tickCount + partialRenderTicks) * 0.6F) * 0.8F;
    	float flapJaw = MathHelper.sin((sprite.tickCount + partialRenderTicks) * 0.4F) * 0.75F;
    	jaw_lower.xRot = convertDegtoRad(26F) - flapJaw * 0.5F;
    	cloth_left_back1.zRot = convertDegtoRad(0F) - flap * 0.0625F;
    	cloth_left_back2.zRot = convertDegtoRad(-13F) + flap * 0.25F;
    	cloth_left_back3.zRot = convertDegtoRad(-13F) - flap * 0.5F;

    	cloth_right_back1.zRot = convertDegtoRad(0F) + flap * 0.0625F;
    	cloth_right_back2.zRot = convertDegtoRad(13F) - flap * 0.25F;
    	cloth_right_back3.zRot = convertDegtoRad(13F) + flap * 0.5F;

    	cloth_left_front1.zRot = convertDegtoRad(-16F) - flap * 0.0625F;
    	cloth_left_front2.zRot = convertDegtoRad(16F) + flap * 0.25F;

    	cloth_right_front1.zRot = convertDegtoRad(16F) + flap * 0.0625F;
    	cloth_right_front2.zRot = convertDegtoRad(-16F) - flap * 0.25F;

    	if(sprite.motion < 0) {
    		cloth_left_back1.zRot = (float) (convertDegtoRad(0F) - flap * 0.0625F + sprite.motionY * 4F);
    		cloth_right_back1.zRot = (float) (convertDegtoRad(0F) + flap * 0.0625F - sprite.motionY * 4F);
    		cloth_left_front1.zRot = (float) (convertDegtoRad(-16F) - flap * 0.0625F + sprite.motionY * 4F);
    		cloth_right_front1.zRot = (float) (convertDegtoRad(16F) + flap * 0.0625F + sprite.motionY * 4F);
    	}
    }
    
	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

}
