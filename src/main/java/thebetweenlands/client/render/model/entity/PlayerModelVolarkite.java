package thebetweenlands.client.render.model.entity;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class PlayerModelVolarkite extends PlayerModel {
	
	public PlayerModelVolarkite(float modelSize, boolean smallArmsIn) {
		super(modelSize, smallArmsIn);
	}

	@Override
	public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, ) {
		super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

		this.leftSleeve.yRot = this.leftArm.yRot = (float)Math.PI;
		this.leftSleeve.xRot = this.leftArm.xRot = 0;
		this.leftSleeve.zRot = this.leftArm.zRot = -2.7F;
		this.leftSleeve.y = this.leftArm.y = 1;
		this.leftSleeve.x = this.leftArm.x = 4;

		this.rightSleeve.yRot = this.rightArm.yRot = (float)Math.PI;
		this.rightSleeve.xRot = this.rightArm.xRot = 0;
		this.rightSleeve.zRot = this.rightArm.zRot = 2.7F;
		this.rightSleeve.y = this.rightArm.y = 1;
		this.rightSleeve.x = this.rightArm.x = -4;
	}
}
