package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.entity.Entity;

public class PlayerModelVolarkite extends PlayerModel {
	public PlayerModelVolarkite(float modelSize, boolean smallArmsIn) {
		super(modelSize, smallArmsIn);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

		this.bipedLeftArmwear.yRot = this.bipedLeftArm.yRot = (float)Math.PI;
		this.bipedLeftArmwear.xRot = this.bipedLeftArm.xRot = 0;
		this.bipedLeftArmwear.zRot = this.bipedLeftArm.zRot = -2.7F;
		this.bipedLeftArmwear.rotationPointY = this.bipedLeftArm.rotationPointY = 1;
		this.bipedLeftArmwear.rotationPointX = this.bipedLeftArm.rotationPointX = 4;

		this.bipedRightArmwear.yRot = this.bipedRightArm.yRot = (float)Math.PI;
		this.bipedRightArmwear.xRot = this.bipedRightArm.xRot = 0;
		this.bipedRightArmwear.zRot = this.bipedRightArm.zRot = 2.7F;
		this.bipedRightArmwear.rotationPointY = this.bipedRightArm.rotationPointY = 1;
		this.bipedRightArmwear.rotationPointX = this.bipedRightArm.rotationPointX = -4;
	}
}
