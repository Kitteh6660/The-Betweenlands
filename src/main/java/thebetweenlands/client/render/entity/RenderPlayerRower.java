package thebetweenlands.client.render.entity;

import net.minecraft.client.entity.player.ClientPlayerEntity;
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
            getMainModel().body.postRender(scale);
            getMainModel().head.postRender(scale);
        }
    }

    @Override
    public PlayerModelRower getMainModel() {
        return (PlayerModelRower) super.getMainModel();
    }

    public void renderPilot(ClientPlayerEntity player, ArmArticulation leftArm, ArmArticulation rightArm, float bodyxRot, float bodyyRot, double x, double y, double z, float delta) {
        for (ModelBipedRower model : models) {
            model.leftArm.xRot = leftArm.shoulderAngleX;
            model.leftArm.yRot = leftArm.shoulderAngleY;
            model.setLeftArmFlexionAngle(leftArm.flexionAngle);
            model.rightArm.xRot = rightArm.shoulderAngleX;
            model.rightArm.yRot = rightArm.shoulderAngleY;
            model.setRightArmFlexionAngle(rightArm.flexionAngle);
            model.body.xRot = bodyxRot;
            model.body.yRot = bodyyRot;
            model.head.xRot = -bodyxRot * 0.75F;
            model.head.yRot = -bodyyRot * 0.75F;
            model.leftArm.rotationPointZ = leftArm.shoulderZ * 16;
            model.rightArm.rotationPointZ = rightArm.shoulderZ * 16;
        }
        doRender(player, x, y, z, player.prevRotationYaw + (player.yRot - player.prevRotationYaw) * delta, delta);
    }

    @Override
    public void doRender(ClientPlayerEntity player, double x, double y, double z, float yaw, float delta) {
        getMainModel().t.showModel = player.isWearing(EnumPlayerModelParts.HAT);
        wearModel.body.showModel = player.isWearing(EnumPlayerModelParts.JACKET);
        wearModel.leftLeg.showModel = player.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
        wearModel.rightLeg.showModel = player.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
        wearModel.leftArm.showModel = player.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
        wearModel.rightArm.showModel = player.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
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