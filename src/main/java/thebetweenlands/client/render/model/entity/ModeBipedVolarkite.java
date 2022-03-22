package thebetweenlands.client.render.model.entity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;

public class ModeBipedVolarkite<T extends LivingEntity> extends BipedModel<T> {
	
	public ModeBipedVolarkite(float modelSize) {
		super(modelSize);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		this.leftArm.yRot = (float)Math.PI;
		this.leftArm.xRot = -0.01F;
		this.leftArm.zRot = -2.7F;
		this.leftArm.y = 1;
		this.leftArm.x = 4;

		this.rightArm.yRot = (float)Math.PI;
		this.rightArm.xRot = -0.01F;
		this.rightArm.zRot = 2.7F;
		this.rightArm.y = 1;
		this.rightArm.x = -4;
	}
}
