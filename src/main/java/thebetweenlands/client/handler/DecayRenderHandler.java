package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.PlayerRendererEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.RenderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.mojang.blaze3d.platform.GlStateManager;

public class DecayRenderHandler {
	public static final ResourceLocation PLAYER_DECAY_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/player_decay.png");

	public static class LayerDecay implements LayerRenderer<ClientPlayerEntity> {
		private final RenderLivingBase<ClientPlayerEntity> renderer;
		private final Predicate<ModelRenderer> modelExclusions;

		public LayerDecay(RenderLivingBase<ClientPlayerEntity> renderer, Predicate<ModelRenderer> modelExclusions) {
			this.renderer = renderer;
			this.modelExclusions = modelExclusions;
		}

		public LayerDecay(RenderLivingBase<ClientPlayerEntity> renderer) {
			this(renderer, box -> {
				if(renderer instanceof PlayerRenderer) {
					PlayerRenderer PlayerRenderer = (PlayerRenderer) renderer;
					PlayerModel playerModel = PlayerRenderer.getMainModel();
					return box == playerModel.hat || box == playerModel.bipedRightLegwear ||
							box == playerModel.bipedLeftLegwear || box == playerModel.bipedBodyWear ||
							box == playerModel.rightSleeve || box == playerModel.leftSleeve;
				}
				return false;
			});
		}

		@Override
		public void doRenderLayer(ClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(cap != null) {
				if(cap.isDecayEnabled()) {
					int decay = cap.getDecayStats().getDecayLevel();
					if(decay > 0) {
						ModelBase model = this.renderer.getMainModel();
						Map<ModelRenderer, Boolean> visibilities = new HashMap<>();
						for(ModelRenderer box : model.boxList) {
							if(this.modelExclusions.test(box)) {
								visibilities.put(box, box.showModel);
								box.showModel = false;
							}
						}

						//Render decay overlay
						float glow = (float) ((Math.cos(player.tickCount / 10.0D) + 1.0D) / 2.0D) * 0.15F;
						float transparency = 0.85F * decay / 20.0F - glow;
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
						this.renderer.bindTexture(PLAYER_DECAY_TEXTURE);
						GlStateManager.color(1, 1, 1, transparency);
						model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
						GlStateManager.color(1, 1, 1, 1);

						for(Entry<ModelRenderer, Boolean> entry : visibilities.entrySet()) {
							entry.getKey().showModel = entry.getValue();
						}
					}
				}
			}
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPrePlayerRenderer(PlayerRendererEvent.Pre event) {
		PlayerEntity player = event.getEntityPlayer();

		IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
		if(cap != null) {
			if(cap.isDecayEnabled() && cap.getDecayStats().getDecayLevel() > 0) {
				if(!RenderUtils.doesRendererHaveLayer(event.getRenderer(), LayerDecay.class, false)) {
					event.getRenderer().addLayer(new LayerDecay(event.getRenderer()));
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderHand(RenderSpecificHandEvent event) {
		GlStateManager.pushMatrix();
		PlayerEntity player = Minecraft.getInstance().player;

		if(player != null) {
			IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(cap != null && cap.isDecayEnabled() && cap.getDecayStats().getDecayLevel() > 0) {
				int decay = cap.getDecayStats().getDecayLevel();
				boolean isMainHand = event.getHand() == Hand.MAIN_HAND;
				if(isMainHand && !player.isInvisible() && event.getItemStack().isEmpty()) {
					EnumHandSide enumhandside = isMainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
					renderArmFirstPersonWithDecay(event.getEquipProgress(), event.getSwingProgress(), enumhandside, decay);
					event.setCanceled(true);
				}
			}
		}

		GlStateManager.popMatrix();
	}

	/**
	 * From ItemRenderer#renderArmFirstPerson
	 * @param swingProgress
	 * @param equipProgress
	 * @param handSide
	 * @param decay
	 */
	private static void renderArmFirstPersonWithDecay(float swingProgress, float equipProgress, EnumHandSide handSide, int decay) {
		Minecraft mc = Minecraft.getInstance();
		RenderManager renderManager = mc.getRenderManager();
		boolean flag = handSide != EnumHandSide.LEFT;
		float f = flag ? 1.0F : -1.0F;
		float f1 = MathHelper.sqrt(equipProgress);
		float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
		float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
		float f4 = -0.4F * MathHelper.sin(equipProgress * (float)Math.PI);
		GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + swingProgress * -0.6F, f4 + -0.71999997F);
		GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
		float f5 = MathHelper.sin(equipProgress * equipProgress * (float)Math.PI);
		float f6 = MathHelper.sin(f1 * (float)Math.PI);
		GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
		ClientPlayerEntity ClientPlayerEntity = mc.player;
		mc.getTextureManager().bindTexture(ClientPlayerEntity.getLocationSkin());
		GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
		GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
		PlayerRenderer PlayerRenderer = (PlayerRenderer)renderManager.<ClientPlayerEntity>getEntityRenderObject(ClientPlayerEntity);
		GlStateManager.disableCull();

		if (flag && PlayerRenderer != null) {
			PlayerRenderer.renderRightArm(ClientPlayerEntity);

			mc.renderEngine.bindTexture(PLAYER_DECAY_TEXTURE);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			float glow = (float) ((Math.cos(ClientPlayerEntity.tickCount / 10.0D) + 1.0D) / 2.0D) * 0.15F;
			float transparency = 0.85F * decay / 20.0F - glow;
			GlStateManager.color(1, 1, 1, transparency);

			//From PlayerRenderer#renderRightArm
			PlayerModel PlayerModel = PlayerRenderer.getModel();
			GlStateManager.enableBlend();
			PlayerModel.swingProgress = 0.0F;
			PlayerModel.isSneak = false;
			PlayerModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, ClientPlayerEntity);
			PlayerModel.rightArm.xRot = 0.0F;
			PlayerModel.rightArm.render(0.0625F);
			PlayerModel.rightSleeve.xRot = 0.0F;
			PlayerModel.rightSleeve.render(0.0625F);
			GlStateManager.disableBlend();
		} else {
			PlayerRenderer.renderLeftArm(ClientPlayerEntity);

			mc.renderEngine.bindTexture(PLAYER_DECAY_TEXTURE);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			float glow = (float) ((Math.cos(ClientPlayerEntity.tickCount / 10.0D) + 1.0D) / 2.0D) * 0.15F;
			float transparency = 0.85F * decay / 20.0F - glow;
			GlStateManager.color(1, 1, 1, transparency);

			//From PlayerRenderer#renderLeftArm
			PlayerModel PlayerModel = PlayerRenderer.getModel();
			GlStateManager.enableBlend();
			PlayerModel.isSneak = false;
			PlayerModel.swingProgress = 0.0F;
			PlayerModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, ClientPlayerEntity);
			PlayerModel.leftArm.xRot = 0.0F;
			PlayerModel.leftArm.render(0.0625F);
			PlayerModel.leftSleeve.xRot = 0.0F;
			PlayerModel.leftSleeve.render(0.0625F);
			GlStateManager.disableBlend();
		}

		GlStateManager.color(1, 1, 1, 1);

		GlStateManager.enableCull();
	}
}
