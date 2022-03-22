package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientPlayerEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.client.render.entity.RenderGasCloud;
import thebetweenlands.client.render.entity.PlayerRendererColored;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.shader.GeometryBuffer;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.util.FramebufferStack;


public class WorldRenderHandler {
	private static final Minecraft MC = Minecraft.getInstance();

	public static final List<Pair<Vector3d, Float>> REPELLER_SHIELDS = new ArrayList<>();

	private static float partialTicks;

	private static int sphereDispList = -2;

	private static PlayerRendererColored PlayerRendererSmallArmsColored;
	private static PlayerRendererColored PlayerRendererNormalArmsColored;

	@SubscribeEvent
	public static void onRenderTick(RenderTickEvent event) {
		if(event.phase == Phase.START) {
			partialTicks = event.renderTickTime;
		}
	}

	public static float getPartialTicks() {
		return partialTicks;
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END && !Minecraft.getInstance().isGamePaused() && Minecraft.getInstance().world != null) {
			BatchedParticleRenderer.INSTANCE.update();
		}
	}

	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		Framebuffer mainFramebuffer = MC.getFramebuffer();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();

		//// Repeller shields ////
		if(sphereDispList == -2) {
			sphereDispList = GL11.glGenLists(1);
			GL11.glNewList(sphereDispList, GL11.GL_COMPILE);
			new Sphere().draw(1.0F, 30, 30);
			GL11.glEndList();
		}
		if(ShaderHelper.INSTANCE.isWorldShaderActive() && sphereDispList >= 0) {
			WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
			if(shader != null) {
				GeometryBuffer gBuffer = shader.getRepellerShieldBuffer();
				if(gBuffer != null) {
					try(FramebufferStack.State state = FramebufferStack.push()) {
						gBuffer.updateGeometryBuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);

						if(gBuffer.isInitialized()) {
							gBuffer.bind();
							gBuffer.clear(0.0F, 0.0F, 0.0F, 1.0F);

							GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

							if(!REPELLER_SHIELDS.isEmpty()) {
								GlStateManager.depthMask(true);
								GlStateManager.disableTexture2D();
								GlStateManager.disableBlend();
								GlStateManager.color(0.0F, 0.4F + (float)(Math.sin(System.nanoTime() / 500000000.0F) + 1.0F) * 0.2F, 0.8F - (float)(Math.cos(System.nanoTime() / 400000000.0F) + 1.0F) * 0.2F, 1.0F);
								GlStateManager.disableCull();

								//Render to G-Buffer 1
								for(Entry<Vector3d, Float> e : REPELLER_SHIELDS) {
									Vector3d pos = e.getKey();
									GlStateManager.pushMatrix();
									GlStateManager.translate(pos.x, pos.y, pos.z);
									GlStateManager.scale(e.getValue(), e.getValue(), e.getValue());
									GL11.glCallList(sphereDispList);
									GlStateManager.popMatrix();
								}

								GlStateManager.enableTexture2D();
								GlStateManager.enableCull();
								GlStateManager.color(1, 1, 1, 1);
							}

							World world = MC.world;

							if(world != null) {
								for(PlayerEntity player : MC.world.playerEntities) {
									if(player instanceof ClientPlayerEntity && (player != TheBetweenlands.proxy.getClientPlayer() || Minecraft.getInstance().gameSettings.thirdPersonView != 0)) {
										ClientPlayerEntity clientPlayer = (ClientPlayerEntity) player;

										IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

										if(cap != null) {
											double obstructionDistance = cap.getObstructionDistance();

											if(obstructionDistance < 0) {
												ShaderHelper.INSTANCE.require();

												float strength = Math.min((float)-obstructionDistance * 2, 1);

												GlStateManager.disableLighting();
												PlayerRendererGlow(clientPlayer, strength, 0.98F, event.getPartialTicks());
												GlStateManager.disableLighting();
											}
										}
									}
								}
							}

							GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

							gBuffer.updateDepthBuffer();
						}
					}
				}
			}
		} else if(sphereDispList >= 0 && !REPELLER_SHIELDS.isEmpty()) {
			GlStateManager.depthMask(false);
			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.cullFace(CullFace.BACK);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
			GlStateManager.color(0.0F, (0.4F + (float)(Math.sin(System.nanoTime() / 500000000.0F) + 1.0F) * 0.2F) / 3.0F, (0.8F - (float)(Math.cos(System.nanoTime() / 400000000.0F) + 1.0F) * 0.2F) / 3.0F, 0.3F);
			for(Entry<Vector3d, Float> e : REPELLER_SHIELDS) {
				Vector3d pos = e.getKey();
				GlStateManager.pushMatrix();
				GlStateManager.translate(pos.x, pos.y, pos.z);
				GlStateManager.scale(e.getValue(), e.getValue(), e.getValue());
				GL11.glCallList(sphereDispList);
				GlStateManager.popMatrix();
			}
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.enableTexture2D();
			GlStateManager.depthMask(true);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableCull();
		}
		REPELLER_SHIELDS.clear();

		if(MC.getRenderViewEntity() != null) {
			RenderHelper.disableStandardItemLighting();
			Minecraft.getInstance().entityRenderer.enableLightmap();
			GlStateManager.enableFog();
			
			BatchedParticleRenderer.INSTANCE.renderAll(MC.getRenderViewEntity(), event.getPartialTicks());
			
			GlStateManager.disableFog();
			RenderHelper.disableStandardItemLighting();
			Minecraft.getInstance().entityRenderer.disableLightmap();
		}

		if(ShaderHelper.INSTANCE.isWorldShaderActive() && (!DefaultParticleBatches.HEAT_HAZE_PARTICLE_ATLAS.isEmpty() || !DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS.isEmpty())) {
			ShaderHelper.INSTANCE.require();
		}

		//Gas clouds/Heat haze
		if(ShaderHelper.INSTANCE.isWorldShaderActive() && MC.getRenderViewEntity() != null) {
			GeometryBuffer fbo = ShaderHelper.INSTANCE.getWorldShader().getGasParticleBuffer();
			if(fbo != null) {
				try(FramebufferStack.State state = FramebufferStack.push()) {
					fbo.updateGeometryBuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);
					fbo.clear(0, 0, 0, 0, 1);

					RenderHelper.disableStandardItemLighting();
					Minecraft.getInstance().entityRenderer.enableLightmap();
					GlStateManager.enableFog();
					
					if(!DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE.isEmpty()) {
						MC.getTextureManager().bindTexture(RenderGasCloud.TEXTURE);

						BatchedParticleRenderer.INSTANCE.renderBatch(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, MC.getRenderViewEntity(), event.getPartialTicks());
					}

					if(!DefaultParticleBatches.HEAT_HAZE_PARTICLE_ATLAS.isEmpty()) {
						BatchedParticleRenderer.INSTANCE.renderBatch(DefaultParticleBatches.HEAT_HAZE_PARTICLE_ATLAS, MC.getRenderViewEntity(), event.getPartialTicks());
					}

					if(!DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS.isEmpty()) {
						BatchedParticleRenderer.INSTANCE.renderBatch(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, MC.getRenderViewEntity(), event.getPartialTicks());
					}

					GlStateManager.disableFog();
					RenderHelper.disableStandardItemLighting();
					Minecraft.getInstance().entityRenderer.disableLightmap();
					
					//Update gas particles depth buffer
					fbo.updateDepthBuffer();
				}
			}
		}

		MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		//Sludge worm dungeon ground fog
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
			if(shader != null) {
				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(Minecraft.getInstance().world);

				for (ILocalStorage sharedStorage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {
					if (sharedStorage instanceof LocationSludgeWormDungeon) {
						if(((LocationSludgeWormDungeon) sharedStorage).addGroundFogVolumesToShader(shader)) {
							ShaderHelper.INSTANCE.require();
						}
					}
				}
			}
		}
	}

	public static void PlayerRendererGlow(ClientPlayerEntity player, float strength, float alpha, float partialTicks) {
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		//From MC.getRenderManager().renderEntityStatic(player, partialTicks, false);

		if (player.tickCount == 0)
		{
			player.lastTickPosX = player.getX();
			player.lastTickPosY = player.getY();
			player.lastTickPosZ = player.getZ();
		}

		double d0 = player.lastTickPosX + (player.getX() - player.lastTickPosX) * (double)partialTicks;
		double d1 = player.lastTickPosY + (player.getY() - player.lastTickPosY) * (double)partialTicks;
		double d2 = player.lastTickPosZ + (player.getZ() - player.lastTickPosZ) * (double)partialTicks;
		float f = player.prevRotationYaw + (player.yRot - player.prevRotationYaw) * partialTicks;

		GlStateManager.color(1, 1, 1, 1);

		boolean isSmallArms = "slim".equals(player.getSkinType());

		PlayerRendererColored playerRenderer;

		if(isSmallArms) {
			if(PlayerRendererSmallArmsColored == null) {
				PlayerRendererSmallArmsColored = new PlayerRendererColored(MC.getRenderManager(), true);
			}
			playerRenderer = PlayerRendererSmallArmsColored;
		} else {
			if(PlayerRendererNormalArmsColored == null) {
				PlayerRendererNormalArmsColored = new PlayerRendererColored(MC.getRenderManager(), false);
			}
			playerRenderer = PlayerRendererNormalArmsColored;
		}

		//Set alpha < 0.99 so that shader inverts inBack check
		playerRenderer.setColor(strength, strength, strength, alpha);

		//Polygon offset is necessary for inBack check in shader
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(0, -5);

		playerRenderer.doRender(player, d0 - MC.getRenderManager().renderPosX, d1 - MC.getRenderManager().renderPosY, d2 - MC.getRenderManager().renderPosZ, f, partialTicks);

		GlStateManager.disablePolygonOffset();
	}
}
