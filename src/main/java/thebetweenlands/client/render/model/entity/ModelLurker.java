package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.util.MathUtils;

@OnlyIn(Dist.CLIENT)
public class ModelLurker extends Model {
	private ModelRenderer trunk;
	private ModelRenderer lumbarVertebrae;
	private ModelRenderer tailFirst;
	private ModelRenderer tailSecond;
	private ModelRenderer tailThird;
	private ModelRenderer bumpFirst;
	private ModelRenderer bumpSecond;
	private ModelRenderer bumpThird;
	private ModelRenderer head;
	private AdvancedModelRenderer mandable;
	private ModelRenderer forefinLeftProximal;
	private ModelRenderer forefinRightProximal;
	private ModelRenderer forefinLeftDistal;
	private ModelRenderer forefinRightDistal;
	private ModelRenderer hindfinLeft;
	private ModelRenderer hindfinRight;

	private ModelRenderer[] tail;
	private float[] restingTailRotationAngleXs;

	public ModelLurker() {
		textureWidth = 256;
		textureHeight = 128;

		trunk = new ModelRenderer(this, 0, 0);
		trunk.addBox(-5F, 0F, 0F, 10, 10, 22);
		// add dorsal buldge
		trunk.setTextureOffset(0, 33);
		trunk.addBox(-5.5F, -0.5F, 3, 11, 8, 16);
		// add cervical vertebrae
		trunk.setTextureOffset(0, 58);
		trunk.addBox(-3.5F, 1.5F, -1.5F, 7, 7, 2);
		trunk.setPos(0F, 11F, -11F);

		bumpFirst = new ModelRenderer(this, 66, 0);
		bumpFirst.addBox(-1.5F, -2F, 4F, 3, 2, 3);
		setRotation(bumpFirst, -0.0940825F, 0F, 0F);
		trunk.addChild(bumpFirst);

		bumpSecond = new ModelRenderer(this, 66, 7);
		bumpSecond.addBox(-1F, -2F, 10F, 2, 2, 2);
		setRotation(bumpSecond, -0.0766374F, 0, 0);
		trunk.addChild(bumpSecond);

		bumpThird = new ModelRenderer(this, 66, 13);
		bumpThird.addBox(-1F, -2F, 15F, 2, 2, 2);
		setRotation(bumpThird, -0.0591841F, 0F, 0F);
		trunk.addChild(bumpThird);

		head = new ModelRenderer(this, 95, 0);
		head.addBox(-4F, -2F, -6F, 8, 7, 6);
		head.setPos(0F, 3F, -1F);
		setRotation(head, 0.0743572F, 0F, 0F);
		trunk.addChild(head);

		ModelRenderer maxilla = new ModelRenderer(this, 95, 36);
		maxilla.addBox(-3.5F, 0, 0, 7, 3, 12);
		// add first horn
		maxilla.setTextureOffset(129, 53);
		maxilla.addBox(-1F, -0.5F, 3F, 2, 1, 2);
		// add second horn
		maxilla.setTextureOffset(129, 57);
		maxilla.addBox(-0.5F, -0.5F, 8F, 1, 1, 1);
		maxilla.setPos(0F, -0.2F, -16.5F);
		setRotation(maxilla, 0.1115297F, 0, 0);
		head.addChild(maxilla);

		mandable = new AdvancedModelRenderer(this, 95, 15);
		mandable.addBox(-4.5F, -2F, -15F, 9, 4, 16);
		mandable.setPos(0F, 3F, -2F);
		head.addChild(mandable);

		ModelRenderer tooth = new ModelRenderer(this, 95, 53);
		tooth.addBox(3.5F, -7F, -15F, 1, 3, 1);
		tooth.setPos(0F, -4F, 2F);
		setRotation(tooth, 0.4F, 0F, 0.1487144F);
		mandable.addChild(tooth);

		tooth = new ModelRenderer(this, 101, 53);
		tooth.addBox(-4.5F, -7F, -15F, 1, 3, 1);
		tooth.setPos(0F, -4F, 2F);
		setRotation(tooth, 0.4F, 0F, -0.1487195F);
		mandable.addChild(tooth);

		tooth = new ModelRenderer(this, 107, 53);
		tooth.addBox(2.5F, -2F, -14F, 1, 1, 1);
		tooth.setPos(0F, -4F, 2F);
		setRotation(tooth, 0.2230717F, -0.0743572F, 0.1487144F);
		mandable.addChild(tooth);

		tooth = new ModelRenderer(this, 112, 53);
		tooth.addBox(-3.5F, -2F, -14F, 1, 1, 1);
		tooth.setPos(0F, -4F, 2F);
		setRotation(tooth, 0.2230717F, 0.074351F, -0.1487195F);
		mandable.addChild(tooth);

		tooth = new ModelRenderer(this, 118, 53);
		tooth.addBox(3.5F, -2F, -11F, 1, 1, 1);
		tooth.setPos(0F, -4F, 2F);
		setRotation(tooth, 0.2602503F, 0F, 0.1487144F);
		mandable.addChild(tooth);

		tooth = new ModelRenderer(this, 123, 53);
		tooth.addBox(-4.5F, -2F, -11F, 1, 1, 1);
		tooth.setPos(0F, -4F, 2F);
		setRotation(tooth, 0.2602503F, 0F, -0.1487195F);
		mandable.addChild(tooth);

		lumbarVertebrae = new ModelRenderer(this, 20, 58);
		lumbarVertebrae.addBox(-4F, -4F, 0F, 8, 8, 6);
		lumbarVertebrae.setPos(0F, 4F, 21.75F);
		setRotation(lumbarVertebrae, -0.0371786F, 0F, 0F);
		trunk.addChild(lumbarVertebrae);

		tailFirst = new ModelRenderer(this, 0, 74);
		tailFirst.addBox(-3F, -3.5F, 0F, 6, 7, 7);
		tailFirst.setPos(0F, -0.5F, 5.75F);
		setRotation(tailFirst, -0.0371786F, 0F, 0F);
		lumbarVertebrae.addChild(tailFirst);

		tailSecond = new ModelRenderer(this, 0, 90);
		tailSecond.addBox(-2F, -2.5F, 0F, 4, 5, 8);
		tailSecond.setPos(0F, -0.25F, 6.75F);
		setRotation(tailSecond, -0.0743572F, 0F, 0F);
		tailFirst.addChild(tailSecond);

		tailThird = new ModelRenderer(this, 0, 105);
		tailThird.addBox(-1.5F, -2F, 0F, 3, 4, 9);
		tailThird.setPos(0F, -0.1F, 7.75F);
		setRotation(tailThird, 0F, 0F, 0F);
		tailSecond.addChild(tailThird);

		ModelRenderer tailFin = new ModelRenderer(this, 28, 74);
		tailFin.addBox(-0.5F, -9, -4, 1, 9, 4);
		tailFin.setPos(0F, 1F, 9.5F);
		setRotation(tailFin, -0.7807509F, 0F, 0F);
		tailThird.addChild(tailFin);

		tailFin = new ModelRenderer(this, 40, 74);
		tailFin.addBox(-0.5F, 0, 0, 1, 7, 3);
		tailFin.setPos(0F, 2F, 6F);
		setRotation(tailFin, 0.6320364F, 0F, 0F);
		tailThird.addChild(tailFin);

		tailFin = new ModelRenderer(this, 49, 74);
		tailFin.addBox(-0.5F, 0, 0, 1, 4, 2);
		tailFin.setPos(0F, 2, 1);
		setRotation(tailFin, 0.4461433F, 0F, 0F);
		tailThird.addChild(tailFin);

		forefinLeftProximal = new ModelRenderer(this, 155, 0);
		forefinLeftProximal.addBox(0F, 0F, -1F, 5, 2, 3);
		forefinLeftProximal.setPos(4F, 7F, 3F);
		setRotation(forefinLeftProximal, -0.2602503F, 0.2230717F, 0.4461433F);
		trunk.addChild(forefinLeftProximal);

		forefinRightProximal = new ModelRenderer(this, 172, 0);
		forefinRightProximal.addBox(0F, 0F, -2F, 5, 2, 3);
		forefinRightProximal.setPos(-4F, 7F, 3F);
		setRotation(forefinRightProximal, 0.260246F, 2.918522F, -0.4461411F);
		trunk.addChild(forefinRightProximal);

		forefinLeftDistal = new ModelRenderer(this, 155, 7);
		forefinLeftDistal.addBox(-1F, 0.5F, -1F, 5, 1, 8);
		// add first nail
		forefinLeftDistal.setTextureOffset(155, 30);
		forefinLeftDistal.addBox(2.5F, 0.3F, 6F, 1, 1, 2);
		// add second nail
		forefinLeftDistal.setTextureOffset(170, 30);
		forefinLeftDistal.addBox(0.5F, 0.3F, 6.5F, 1, 1, 1);
		forefinLeftDistal.setPos(3, 0, 0);
		setRotation(forefinLeftDistal, 0, 0.2F, 0);
		forefinLeftProximal.addChild(forefinLeftDistal);

		forefinRightDistal = new ModelRenderer(this, 182, 7);
		forefinRightDistal.addBox(-4F, 0.5F, -1F, 5, 1, 8);
		// add first nail
		forefinRightDistal.setTextureOffset(162, 30);
		forefinRightDistal.addBox(-3.5F, 0.3F, 6F, 1, 1, 2);
		// add second nail
		forefinRightDistal.setTextureOffset(176, 30);
		forefinRightDistal.addBox(-1.5F, 0.3F, 6.5F, 1, 1, 1);
		forefinRightDistal.setPos(3, 0, 0);
		setRotation(forefinRightDistal, 0, -0.2F + MathUtils.PI, 0);
		forefinRightProximal.addChild(forefinRightDistal);

		hindfinLeft = new ModelRenderer(this, 155, 18);
		hindfinLeft.addBox(0F, -1F, -0.5F, 4, 1, 8);
		hindfinLeft.setPos(4F, 9F, 16F);
		setRotation(hindfinLeft, -0.260246F, 0.4461411F, 0.4461411F);
		trunk.addChild(hindfinLeft);

		hindfinRight = new ModelRenderer(this, 181, 18);
		hindfinRight.addBox(-4F, -1F, -0.5F, 4, 1, 8);
		hindfinRight.setPos(-4F, 9F, 16F);
		setRotation(hindfinRight, -0.260246F, -0.4461411F, -0.4461411F);
		trunk.addChild(hindfinRight);

		tail = new ModelRenderer[]{lumbarVertebrae, tailFirst, tailSecond, tailThird};
		restingTailRotationAngleXs = new float[tail.length];
		for (int i = 0; i < restingTailRotationAngleXs.length; i++) {
			restingTailRotationAngleXs[i] = tail[i].xRot;
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float tickCount, float yRot, float xRot, float scale) {
		yRot = MathHelper.clamp(yRot, -60, 60);
		head.yRot = yRot * MathUtils.DEG_TO_RAD;
		head.xRot += xRot * MathUtils.DEG_TO_RAD;
		trunk.render(scale);
	}

	@Override
	public void setLivingAnimations(LivingEntity entity, float swing, float speed, float delta) {
		EntityLurker lurker = (EntityLurker) entity;
		float mouthOpen = lurker.getMouthOpen(delta);
		float yaw = lurker.getTailYaw(delta) * MathUtils.DEG_TO_RAD * 0.2F;
		float pitch = lurker.getTailPitch(delta) * MathUtils.DEG_TO_RAD * 0.2F;
		forefinLeftProximal.xRot = MathHelper.cos(swing * 0.8F - MathUtils.PI / 8) * speed * 1.5F - 0.2602503F;
		forefinLeftProximal.yRot = MathHelper.cos(swing * 0.8F) * speed * 1.7F + 0.2230717F;
		forefinLeftProximal.zRot = MathHelper.sin(swing * 0.8F) * speed * 0.7F + 0.4461433F - speed * 0.7F;
		forefinLeftDistal.yRot = MathHelper.sin(swing * 0.8F + MathUtils.PI / 4) * speed * 0.7F + 0.2F;
		hindfinLeft.xRot = MathHelper.cos(swing * 0.8F - MathUtils.PI / 2) * speed * 1.0F - 0.260246F + speed * 0.8F;
		hindfinLeft.yRot = MathHelper.sin(swing * 0.8F - MathUtils.PI / 2) * speed * 1.2F + 0.4461411F;
		forefinRightProximal.xRot = MathHelper.cos(swing * 0.8F - MathUtils.PI / 8) * speed * 1.5F + 0.2602503F;
		forefinRightProximal.yRot = MathHelper.cos(swing * 0.8F) * speed * 1.7F + 2.918522F;
		forefinRightProximal.zRot = MathHelper.sin(swing * 0.8F) * speed * 0.7F - 0.4461433F + speed * 0.7F;
		forefinRightDistal.yRot = MathHelper.sin(swing * 0.8F + MathUtils.PI / 4) * speed * 0.7F - 0.2F + MathUtils.PI;
		hindfinRight.xRot = MathHelper.sin(swing * 0.8F + MathUtils.PI / 2) * speed * 1.0F - 0.260246F + speed * 0.8F;
		hindfinRight.yRot = MathHelper.cos(swing * 0.8F + MathUtils.PI / 2) * speed * 1.2F - 0.4461411F;
		trunk.zRot = MathHelper.sin(swing * 0.8F) * speed * 0.1F;
		head.zRot = -trunk.zRot;
		head.xRot = -mouthOpen * 0.4F + 0.0743572F;
		mandable.xRot = mouthOpen * 0.4F;
		for (int i = 0; i < tail.length; i++) {
			ModelRenderer segment = tail[i];
			segment.xRot = restingTailRotationAngleXs[i] + pitch;
			segment.yRot = yaw + MathHelper.sin(swing * 0.4F - i * 1.6F) * speed * ((i / (float) tail.length * 2 + 0.1F)) * 0.6F;
			segment.zRot = -trunk.zRot / tail.length;
		}
	}
}
