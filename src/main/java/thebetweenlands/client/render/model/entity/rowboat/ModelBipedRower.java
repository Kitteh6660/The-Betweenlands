package thebetweenlands.client.render.model.entity.rowboat;

import java.util.EnumMap;
import java.util.Iterator;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.client.render.model.entity.ModelBoxCustomizable;
import thebetweenlands.common.entity.rowboat.ShipSide;
import thebetweenlands.util.MathUtils;

public class ModelBipedRower extends BipedModel<LivingEntity> {
	
    private EnumMap<ShipSide, ModelBipedLimb> arms;

    public ModelRenderer leftForearm, rightForearm;

    public ModelBipedRower(float expand) {
        this(expand, false);
    }

    public ModelBipedRower(float expand, boolean slimArms) {
        this(expand, false, slimArms, 64, 32, new BipedTextureUVs(40, 16, 40, 16, 0, 16, 0, 16));
    }

    public ModelBipedRower(float expand, boolean expandJointed, boolean slimArms, int textureWidth, int textureHeight, BipedTextureUVs uvs) {
        super(expand, 0, textureWidth, textureHeight);
        removeCuboids(body);
        removeCuboids(head);
        removeCuboids(hat);
        removeCuboids(leftLeg);
        removeCuboids(rightLeg);
        body = new AdvancedModelRenderer(this, uvs.bodyU, uvs.bodyV);
        body.setPos(0, 12, 0);
        body.addBox(-4, -12, -2, 8, 12, 4, expand);
        head.setPos(0, -12, 0);
        // additional expand to prevent head z-fighting with body
        head.addBox(-4, -8, -4, 8, 8, 8, expand + 0.025F);
        body.addChild(head);
        hat.setPos(0, 0, 0);
        hat.addBox(-4, -8, -4, 8, 8, 8, expand + 0.5F);
        head.addChild(hat);
        leftArm.rotationPointY = -10;
        rightArm.rotationPointY = leftArm.rotationPointY;
        if (expand == 0 || expandJointed) {
            arms = ShipSide.newEnumMap(ModelBipedLimb.class);
            ModelBipedLimb left = createReplacementArm(leftArm, uvs.armLeftU, uvs.armLeftV, slimArms, expand);
            leftArm = left;
            arms.put(ShipSide.STARBOARD, left);
            ModelBipedLimb right = createReplacementArm(rightArm, uvs.armRightU, uvs.armRightV, slimArms, expand);
            rightArm = right;
            arms.put(ShipSide.PORT, right);
        } else {
            leftArm = createExpandReplacementArm(leftArm, uvs.armLeftU, uvs.armLeftV, slimArms, expand);
            rightArm = createExpandReplacementArm(rightArm, uvs.armRightU, uvs.armRightV, slimArms, expand);
        }
        if (slimArms) {
            rightArm.rotationPointX++;
        }
        leftLeg = new ModelRenderer(this, uvs.legLeftU, uvs.legLeftV);
        leftLeg.mirror = true;
        leftLeg.addBox(-2, 0, -2, 4, 12, 4, expand);
        leftLeg.setPos(1.9F, 12, 0);
        leftLeg.xRot = -1.25F;
        leftLeg.yRot = -0.314F;
        rightLeg = new ModelRenderer(this, uvs.legRightU, uvs.legRightV);
        rightLeg.addBox(-2, 0, -2, 4, 12, 4, expand);
        rightLeg.setPos(-1.9F, 12, 0);
        rightLeg.xRot = -1.25F;
        rightLeg.yRot = 0.314F;
    }

    private void removeCuboids(ModelRenderer renderer) {
        renderer.cubeList.clear();
        boxList.remove(renderer);
    }

    private ModelBipedLimb createReplacementArm(ModelRenderer oldLimb, int textureOffsetX, int textureOffsetY, boolean slimArms, float expand) {
        ModelBipedLimb limb = new ModelBipedLimb(this, textureOffsetX, textureOffsetY, slimArms ? 3 : 4, 4, expand);
        limb.setPos(Math.signum(oldLimb.rotationPointX) * 6, oldLimb.rotationPointY, oldLimb.rotationPointZ);
        removeCuboids(oldLimb);
        limb.offsetX = -2;
        limb.offsetY = -2;
        limb.offsetZ = -2;
        body.addChild(limb);
        return limb;
    }

    private ModelRenderer createExpandReplacementArm(ModelRenderer oldLimb, int textureOffsetX, int textureOffsetY, boolean slimArms, float expand) {
        ModelRenderer limb = new ModelRenderer(this, textureOffsetX, textureOffsetY);
        body.addChild(limb);
        limb.mirror = oldLimb.mirror;
        ModelBox box = oldLimb.cubeList.get(0);
        removeCuboids(oldLimb);
        ModelBoxCustomizable arm = new ModelBoxCustomizable(limb, textureOffsetX, textureOffsetY, -2, -2, -2, slimArms ? 3 : 4, 6, 4, expand);
        arm.setVisibleSides(~ModelBoxCustomizable.SIDE_BOTTOM);
        limb.cubeList.add(arm);
        limb.setPos(Math.signum(oldLimb.rotationPointX) * 6, oldLimb.rotationPointY, oldLimb.rotationPointZ);
        ModelRenderer lowerLimb = new ModelRenderer(this, textureOffsetX, textureOffsetY - 6);
        if (leftArm == oldLimb) {
            leftForearm = lowerLimb;
        } else {
            rightForearm = lowerLimb;
        }
        lowerLimb.mirror = oldLimb.mirror;
        lowerLimb.setPos(-2 + 2, box.getY()1 + 6, box.getZ()1 + 2);
        ModelBoxCustomizable forearm = new ModelBoxCustomizable(lowerLimb, textureOffsetX, textureOffsetY + 6, -2, 0, -2, slimArms ? 3 : 4, 6, 4, expand * 0.75F, -6);
        forearm.setVisibleSides(~ModelBoxCustomizable.SIDE_TOP);
        lowerLimb.cubeList.add(forearm);
        limb.addChild(lowerLimb);
        return limb;
    }

    public ModelRenderer getArm(ShipSide side) {
        return arms.get(side);
    }

    public void setLeftArmFlexionAngle(float flexionAngle) {
        if (arms == null) {
            leftForearm.xRot = flexionAngle * MathUtils.DEG_TO_RAD;
        } else {
            arms.get(ShipSide.STARBOARD).setFlexionAngle(flexionAngle);
        }
    }

    public void setRightArmFlexionAngle(float flexionAngle) {
        if (arms == null) {
            rightForearm.xRot = flexionAngle * MathUtils.DEG_TO_RAD;
        } else {
            arms.get(ShipSide.PORT).setFlexionAngle(flexionAngle);
        }
    }

    @Override
    public void render(Entity entity, float swing, float speed, float age, float yaw, float pitch, float scale) {
        body.render(scale);
        rightLeg.render(scale);
        leftLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float swing, float speed, float age, float yaw, float pitch, float scale, Entity entity) {}

    public static class BipedTextureUVs {
        int armLeftU, armLeftV;
        int armRightU, armRightV;
        int legLeftU, legLeftV;
        int legRightU, legRightV;
        int bodyU, bodyV;

        public BipedTextureUVs(int armLeftU, int armLeftV, int armRightU, int armRightV, int legLeftU, int legLeftV, int legRightU, int legRightV) {
            this(armLeftU, armLeftV, armRightU, armRightV, legLeftU, legLeftV, legRightU, legRightV, 16, 16);
        }

        public BipedTextureUVs(int armLeftU, int armLeftV, int armRightU, int armRightV, int legLeftU, int legLeftV, int legRightU, int legRightV, int bodyU, int bodyV) {
            this.armLeftU = armLeftU;
            this.armLeftV = armLeftV;
            this.armRightU = armRightU;
            this.armRightV = armRightV;
            this.legLeftU = legLeftU;
            this.legLeftV = legLeftV;
            this.legRightU = legRightU;
            this.legRightV = legRightV;
            this.bodyU = bodyU;
            this.bodyV = bodyV;
        }
    }
}