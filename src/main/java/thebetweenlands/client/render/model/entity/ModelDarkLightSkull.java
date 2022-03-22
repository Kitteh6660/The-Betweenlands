package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityDarkLight;
import thebetweenlands.common.entity.mobs.EntitySludge;

@OnlyIn(Dist.CLIENT)
public class ModelDarkLightSkull extends MowzieModelBase {
	private MowzieModelRenderer head1;
	private MowzieModelRenderer head2;
	private MowzieModelRenderer jaw;
	private MowzieModelRenderer teeth;
	private float scale;

	public ModelDarkLightSkull() {
		textureWidth = 128;
		textureHeight = 64;

		this.head2 = new MowzieModelRenderer(this, 0, 16);
		this.head2.setPos(0.0F, 15.0F, 3.0F);
		this.head2.addBox(-3.0F, 0.0F, -3.0F, 6, 2, 3, -0.01F);
		this.setRotation(head2, -0.07435102760791776F, 0.0F, -0.11154399067163465F);
		this.jaw = new MowzieModelRenderer(this, 0, 22);
		this.jaw.setPos(0.0F, 0.0F, 0.0F);
		this.jaw.addBox(-4.0F, -1.0F, -8.0F, 8, 2, 7, 0.0F);
		this.setRotation(jaw, 0.5940400797409059F, -0.01100643542294784F, 0.1483095853034341F);
		this.teeth = new MowzieModelRenderer(this, 0, 32);
		this.teeth.setPos(0.0F, -0.07428254352663011F, 0.9972372354295702F);
		this.teeth.addBox(-4.0F, 0.0F, -8.0F, 8, 1, 5, 0.0F);
		this.head1 = new MowzieModelRenderer(this, 0, 0);
		this.head1.setPos(0.0F, 0.0F, 0.0F);
		this.head1.addBox(-4.0F, -6.0F, -8.0F, 8, 6, 8, 0.0F);
		this.head2.addChild(this.jaw);
		this.head1.addChild(this.teeth);
		this.head2.addChild(this.head1);

		setInitPose();
	}

	public void render() {
		head2.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float yRot, float xRot, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, yRot, xRot, unitPixel, entity);
	}

	@Override
	public void setLivingAnimations(LivingEntity entity, float f, float f1, float partialTicks) {
		setToInitPose();
		scale = new ControlledAnimation(5).getAnimationProgressSinSqrt(partialTicks);
		float frame = entity.tickCount + partialTicks;
		float controller = (float) (0.5 * Math.sin(frame * 0.1f) * Math.sin(frame * 0.1f)) + 0.5f;
		head2.rotationPointY += 1.5f;
		walk(jaw, 0.5f, 0.3f * controller, false, 0, -0.2f * controller, frame, 1f);
		bob(head2, 0.5f, 1f * controller, false, frame, 1f);
		head2.rotationPointX += 2 * Math.sin(frame * 0.25) * controller;
		flap(head2, 0.25f, 0.2f * controller, false, 0, 0, frame, 1f);
	}
}
