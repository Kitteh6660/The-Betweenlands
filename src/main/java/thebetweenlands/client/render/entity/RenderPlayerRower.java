package thebetweenlands.client.render.entity;

import net.minecraft.client.entity.ClientPlayerEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.handler.DecayRenderHandler;
import thebetweenlands.client.render.entity.RenderWeedwoodRowboat.ArmArticulation;
import thebetweenlands.client.render.entity.layer.LayerRowerArmor;
import thebetweenlands.client.render.entity.layer.LayerRowerWear;
import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower;
import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower.BipedTextureUVs;
import thebetweenlands.client.render.model.entity.rowboat.PlayerModelRower;

public class PlayerRendererRower extends RenderLivingBase<ClientPlayerEntity> {
    private ModelBipedRower[] models;

    private ModelBiped wearModel;

    public PlayerRendererRower(RenderManager mgr, boolean slimArms) {
        super(mgr, new PlayerModelRower(0, slimArms, new BipedTextureUVs(32, 48, 40, 16, 16, 48, 0, 16)), 0.5F);
        layerRenderers.clear();
        LayerRowerWear wear = new LayerRowerWear(slimArms);
        addLayer(wear);
        LayerRowerArmor armor = new LayerRowerArmor(this);
        addLayer(armor);
        addLayer(new LayerCustomHead(new HeadTransform(getMainModel())));
        addLayer(new DecayRenderHandler.LayerDecay(this, box -> false));
        models = new ModelBipedRower[] { getMainModel(), wear.getModel(), armor.getChest(), armor.getLeggings() };
        wearModel = wear.getModel();
    }

    class HeadTransform extends ModelRenderer {
        HeadTransform(ModelBase model) {
            super(model);
        }

        @Override
        public void postRender(float scale) {
            getMainModel().bipedBody.postRender(scale);
            getMainModel().bipedHead.postRender(scale);
        }
    }

    @Override
    public PlayerModelRower getMainModel() {
        return (PlayerModelRower) super.getMainModel();
    }

    public void renderPilot(ClientPlayerEntity player, ArmArticulation leftArm, ArmArticulation rightArm, float bodyxRot, float bodyyRot, double x, double y, double z, float delta) {
        for (ModelBipedRower model : models) {
            model.bipedLeftArm.xRot = leftArm.shoulderAngleX;
            model.bipedLeftArm.yRot = leftArm.shoulderAngleY;
            model.setLeftArmFlexionAngle(leftArm.flexionAngle);
            model.bipedRightArm.xRot = rightArm.shoulderAngleX;
            model.bipedRightArm.yRot = rightArm.shoulderAngleY;
            model.setRightArmFlexionAngle(rightArm.flexionAngle);
            model.bipedBody.xRot = bodyxRot;
            model.bipedBody.yRot = bodyyRot;
            model.bipedHead.xRot = -bodyxRot * 0.75F;
            model.bipedHead.yRot = -bodyyRot * 0.75F;
            model.bipedLeftArm.rotationPointZ = leftArm.shoulderZ * 16;
            model.bipedRightArm.rotationPointZ = rightArm.shoulderZ * 16;
        }
        doRender(player, x, y, z, player.prevRotationYaw + (player.yRot - player.prevRotationYaw) * delta, delta);
    }

    @Override
    public void doRender(ClientPlayerEntity player, double x, double y, double z, float yaw, float delta) {
        getMainModel().bipedHeadwear.showModel = player.isWearing(EnumPlayerModelParts.HAT);
        wearModel.bipedBody.showModel = player.isWearing(EnumPlayerModelParts.JACKET);
        wearModel.bipedLeftLeg.showModel = player.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
        wearModel.bipedRightLeg.showModel = player.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
        wearModel.bipedLeftArm.showModel = player.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
        wearModel.bipedRightArm.showModel = player.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        super.doRender(player, x, y, z, yaw, delta);
        GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
    }

    @Override
    protected void preRenderCallback(ClientPlayerEntity player, float delta) {
        float scale = 0.9375F;
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected boolean canRenderName(ClientPlayerEntity entity) {
        return !entity.isUser() && super.canRenderName(entity);
    }

    @Override
    protected void renderEntityName(ClientPlayerEntity player, double x, double y, double z, String name, double distance) {
        if (distance < 100) {
            Scoreboard scoreboard = player.getWorldScoreboard();
            ScoreObjective sidebarObj = scoreboard.getObjectiveInDisplaySlot(2);
            if (sidebarObj != null) {
                Score score = scoreboard.getOrCreateScore(player.getName(), sidebarObj);
                renderLivingLabel(player, score.getScorePoints() + " " + sidebarObj.getDisplayName(), x, y, z, 64);
                y += getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.025F;
            }
        }
        super.renderEntityName(player, x, y, z, name, distance);
    }

    @Override
    protected ResourceLocation getEntityTexture(ClientPlayerEntity entity) {
        return entity.getLocationSkin();
    }
}