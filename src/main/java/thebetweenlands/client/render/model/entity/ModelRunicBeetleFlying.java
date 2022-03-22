package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * BLRunicBeetleFlying - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelRunicBeetleFlying extends Model {
	public ModelRenderer thorax_main;
	public ModelRenderer abdomen;
	public ModelRenderer head;
	public ModelRenderer elytra_left;
	public ModelRenderer elytra_right;
	public ModelRenderer leg_left_front1a;
	public ModelRenderer leg_left_mid1a;
	public ModelRenderer leg_left_back1a;
	public ModelRenderer leg_right_front1a;
	public ModelRenderer leg_right_mid1a;
	public ModelRenderer leg_right_back1a;
	public ModelRenderer wing_left;
	public ModelRenderer wing_right;
	public ModelRenderer jaw_left1a;
	public ModelRenderer jaw_right1a;
	public ModelRenderer antennae_left1a;
	public ModelRenderer antennae_right1a;
	public ModelRenderer jaw_left1b;
	public ModelRenderer jaw_right1b;
	public ModelRenderer antennae_left1b;
	public ModelRenderer antennae_right1b;
	public ModelRenderer leg_left_front1b;
	public ModelRenderer leg_left_mid1b;
	public ModelRenderer leg_left_back1b;
	public ModelRenderer leg_right_front1b;
	public ModelRenderer leg_right_mid1b;
	public ModelRenderer leg_right_back1b;

	public ModelRunicBeetleFlying() {
		this.texWidth = 32;
		this.texHeight = 32;
		this.leg_right_back1a = new ModelRenderer(this, 26, 21);
		this.leg_right_back1a.setPos(-0.5F, 2.0F, 1.5F);
		this.leg_right_back1a.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
		this.setRotateAngle(leg_right_back1a, -1.2747884856566583F, 2.86844862565268F, 0.0F);
		this.leg_right_mid1b = new ModelRenderer(this, 23, 24);
		this.leg_right_mid1b.setPos(0.0F, 2.0F, 0.0F);
		this.leg_right_mid1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_right_mid1b, -0.27314402793711257F, 0.0F, 0.0F);
		this.leg_left_front1b = new ModelRenderer(this, 20, 18);
		this.leg_left_front1b.setPos(0.0F, 2.0F, 0.0F);
		this.leg_left_front1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_left_front1b, -0.27314402793711257F, 0.0F, 0.0F);
		this.jaw_right1b = new ModelRenderer(this, 9, 20);
		this.jaw_right1b.setPos(0.0F, 1.0F, -2.0F);
		this.jaw_right1b.addBox(-1.0F, -1.0F, -2.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(jaw_right1b, -0.5009094953223726F, 0.0F, 0.0F);
		this.elytra_right = new ModelRenderer(this, 20, 8);
		this.elytra_right.setPos(0.0F, -0.2F, 0.0F);
		this.elytra_right.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
		this.setRotateAngle(elytra_right, 0.0F, -0.40980330836826856F, 0.9105382707654417F);
		this.antennae_right1a = new ModelRenderer(this, 6, 24);
		this.antennae_right1a.setPos(0.0F, 0.01F, -2.0F);
		this.antennae_right1a.addBox(-4.0F, 0.0F, -2.0F, 4, 0, 3, 0.0F);
		this.setRotateAngle(antennae_right1a, 0.0F, 0.40980330836826856F, 0.0F);
		this.wing_left = new ModelRenderer(this, 15, 26);
		this.wing_left.setPos(0.5F, 0.0F, 0.0F);
		this.wing_left.addBox(0.0F, 0.0F, 0.0F, 7, 0, 3, 0.0F);
		this.setRotateAngle(wing_left, 0.045553093477052F, 0.136659280431156F, -0.18203784098300857F);
		this.antennae_right1b = new ModelRenderer(this, 6, 28);
		this.antennae_right1b.setPos(0.0F, 0.0F, -2.0F);
		this.antennae_right1b.addBox(-4.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
		this.setRotateAngle(antennae_right1b, 0.4553564018453205F, 0.0F, 0.0F);
		this.leg_left_mid1a = new ModelRenderer(this, 23, 15);
		this.leg_left_mid1a.setPos(0.5F, 2.0F, 1.0F);
		this.leg_left_mid1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_left_mid1a, -1.1383037381507017F, -2.5953045977155678F, 0.0F);
		this.wing_right = new ModelRenderer(this, 15, 29);
		this.wing_right.setPos(-0.5F, 0.0F, 0.0F);
		this.wing_right.addBox(-7.0F, 0.0F, 0.0F, 7, 0, 3, 0.0F);
		this.setRotateAngle(wing_right, 0.045553093477052F, -0.136659280431156F, 0.136659280431156F);
		this.jaw_right1a = new ModelRenderer(this, 9, 15);
		this.jaw_right1a.setPos(-0.25F, 1.0F, -1.0F);
		this.jaw_right1a.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 3, 0.0F);
		this.setRotateAngle(jaw_right1a, 0.31869712141416456F, 0.18203784098300857F, 0.0F);
		this.leg_right_mid1a = new ModelRenderer(this, 23, 21);
		this.leg_right_mid1a.setPos(-0.5F, 2.0F, 1.0F);
		this.leg_right_mid1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_right_mid1a, -1.1383037381507017F, 2.5953045977155678F, 0.0F);
		this.leg_left_back1b = new ModelRenderer(this, 26, 19);
		this.leg_left_back1b.setPos(0.0F, 3.0F, 0.0F);
		this.leg_left_back1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_left_back1b, -0.18203784098300857F, 0.0F, 0.0F);
		this.antennae_left1b = new ModelRenderer(this, -3, 28);
		this.antennae_left1b.setPos(0.0F, 0.0F, -2.0F);
		this.antennae_left1b.addBox(0.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
		this.setRotateAngle(antennae_left1b, 0.4553564018453205F, 0.0F, 0.0F);
		this.jaw_left1b = new ModelRenderer(this, 0, 20);
		this.jaw_left1b.setPos(0.0F, 1.0F, -2.0F);
		this.jaw_left1b.addBox(0.0F, -1.0F, -2.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(jaw_left1b, -0.5009094953223726F, 0.0F, 0.0F);
		this.leg_left_mid1b = new ModelRenderer(this, 23, 18);
		this.leg_left_mid1b.setPos(0.0F, 2.0F, 0.0F);
		this.leg_left_mid1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_left_mid1b, -0.27314402793711257F, 0.0F, 0.0F);
		this.leg_left_front1a = new ModelRenderer(this, 20, 15);
		this.leg_left_front1a.setPos(0.5F, 2.0F, 0.5F);
		this.leg_left_front1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_left_front1a, -1.1383037381507017F, -1.0927506446736497F, 0.0F);
		this.thorax_main = new ModelRenderer(this, 0, 0);
		this.thorax_main.setPos(0.0F, 18.0F, 0.0F);
		this.thorax_main.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
		this.setRotateAngle(thorax_main, -0.7285004297824331F, 0.0F, 0.0F);
		this.leg_right_front1b = new ModelRenderer(this, 20, 24);
		this.leg_right_front1b.setPos(0.0F, 2.0F, 0.0F);
		this.leg_right_front1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_right_front1b, -0.27314402793711257F, 0.0F, 0.0F);
		this.leg_right_back1b = new ModelRenderer(this, 26, 25);
		this.leg_right_back1b.setPos(0.0F, 3.0F, 0.0F);
		this.leg_right_back1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
		this.setRotateAngle(leg_right_back1b, -0.18203784098300857F, 0.0F, 0.0F);
		this.antennae_left1a = new ModelRenderer(this, -3, 24);
		this.antennae_left1a.setPos(0.0F, 0.01F, -2.0F);
		this.antennae_left1a.addBox(0.0F, 0.0F, -2.0F, 4, 0, 3, 0.0F);
		this.setRotateAngle(antennae_left1a, 0.0F, -0.40980330836826856F, 0.0F);
		this.elytra_left = new ModelRenderer(this, 20, 1);
		this.elytra_left.setPos(0.0F, -0.2F, 0.0F);
		this.elytra_left.addBox(0.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
		this.setRotateAngle(elytra_left, 0.0F, 0.40980330836826856F, -0.9105382707654417F);
		this.leg_left_back1a = new ModelRenderer(this, 26, 15);
		this.leg_left_back1a.setPos(0.5F, 2.0F, 1.5F);
		this.leg_left_back1a.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
		this.setRotateAngle(leg_left_back1a, -1.2747884856566583F, -2.86844862565268F, 0.0F);
		this.head = new ModelRenderer(this, 0, 10);
		this.head.setPos(0.0F, 0.0F, 0.0F);
		this.head.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(head, 0.40980330836826856F, 0.0F, 0.0F);
		this.leg_right_front1a = new ModelRenderer(this, 20, 21);
		this.leg_right_front1a.setPos(-0.5F, 2.0F, 0.5F);
		this.leg_right_front1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
		this.setRotateAngle(leg_right_front1a, -1.1383037381507017F, 1.0927506446736497F, 0.0F);
		this.jaw_left1a = new ModelRenderer(this, 0, 15);
		this.jaw_left1a.setPos(0.25F, 1.0F, -1.0F);
		this.jaw_left1a.addBox(0.0F, 0.0F, -2.0F, 1, 1, 3, 0.0F);
		this.setRotateAngle(jaw_left1a, 0.31869712141416456F, -0.18203784098300857F, 0.0F);
		this.abdomen = new ModelRenderer(this, 0, 5);
		this.abdomen.setPos(0.01F, 0.0F, 2.0F);
		this.abdomen.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
		this.setRotateAngle(abdomen, -0.136659280431156F, 0.0F, 0.0F);
		this.thorax_main.addChild(this.leg_right_back1a);
		this.leg_right_mid1a.addChild(this.leg_right_mid1b);
		this.leg_left_front1a.addChild(this.leg_left_front1b);
		this.jaw_right1a.addChild(this.jaw_right1b);
		this.thorax_main.addChild(this.elytra_right);
		this.head.addChild(this.antennae_right1a);
		this.antennae_right1a.addChild(this.antennae_right1b);
		this.thorax_main.addChild(this.leg_left_mid1a);
		this.head.addChild(this.jaw_right1a);
		this.thorax_main.addChild(this.leg_right_mid1a);
		this.leg_left_back1a.addChild(this.leg_left_back1b);
		this.antennae_left1a.addChild(this.antennae_left1b);
		this.jaw_left1a.addChild(this.jaw_left1b);
		this.leg_left_mid1a.addChild(this.leg_left_mid1b);
		this.thorax_main.addChild(this.leg_left_front1a);
		this.leg_right_front1a.addChild(this.leg_right_front1b);
		this.leg_right_back1a.addChild(this.leg_right_back1b);
		this.head.addChild(this.antennae_left1a);
		this.thorax_main.addChild(this.elytra_left);
		this.thorax_main.addChild(this.leg_left_back1a);
		this.thorax_main.addChild(this.head);
		this.thorax_main.addChild(this.leg_right_front1a);
		this.head.addChild(this.jaw_left1a);
		this.thorax_main.addChild(this.abdomen);

		//Add wings last for transparency ordering
		this.thorax_main.addChild(this.wing_right);
		this.thorax_main.addChild(this.wing_left);
	}

	@Override
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder vertex, int in1, int in2, float f, float f1, float f2, float f3) {  
		this.thorax_main.render(matrix, vertex, in1, in2, f, f1, f2, f3);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		float speed = 2.5f;

		float flap = MathHelper.sin(ageInTicks * speed);
		float flap2 = MathHelper.sin(ageInTicks * speed - 1.72f);

		this.wing_right.xRot = 0.136659280431156F + 0.05f + flap2 * 0.4f;
		this.wing_right.zRot = -0.091106186954104F + flap * 0.2f;

		this.wing_left.xRot = 0.136659280431156F + 0.05f + flap2 * 0.4f;
		this.wing_left.zRot = 0.091106186954104F - flap * 0.2f;

		float crunch = (float) Math.pow(MathHelper.sin(ageInTicks * speed * 0.19f), 12);

		this.jaw_left1a.yRot = -0.36425021489121656F + 0.1f + crunch * 0.15f;
		this.jaw_left1a.zRot = 0.091106186954104F - 0.2f + crunch * 0.15f;

		this.jaw_right1a.yRot = 0.36425021489121656F - 0.1f - crunch * 0.15f;
		this.jaw_right1a.zRot = 0.091106186954104F + 0.2f - crunch * 0.15f;

		float flop = MathHelper.sin(ageInTicks * speed * 0.12f);

		this.elytra_left.zRot = -0.9105382707654417F + flop * 0.05f;
		this.elytra_right.zRot = 0.9105382707654417F - flop * 0.05f;

		this.antennae_left1a.zRot = flop * 0.05f;
		this.antennae_right1a.zRot = -flop * 0.05f;

		this.leg_left_front1a.zRot = 0 - flop * 0.05f;
		this.leg_left_mid1a.zRot = 0 - flop * 0.05f;
		this.leg_left_back1a.xRot = -1.2747884856566583F - flop * 0.05f;

		this.leg_right_front1a.zRot = 0 + flop * 0.05f;
		this.leg_right_mid1a.zRot = 0 + flop * 0.05f;
		this.leg_right_back1a.xRot = -1.2747884856566583F - flop * 0.05f;
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
