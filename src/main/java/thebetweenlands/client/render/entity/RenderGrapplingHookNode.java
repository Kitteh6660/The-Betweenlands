package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.PlayerRendererEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.client.handler.DebugHandlerClient;
import thebetweenlands.client.render.model.entity.ModelRopeNode;
import thebetweenlands.client.render.model.entity.ModelShambler;
import thebetweenlands.client.render.particle.entity.ParticleBeam;
import thebetweenlands.common.entity.EntityGrapplingHookNode;
import thebetweenlands.common.item.misc.ItemGrapplingHook;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.util.RotationMatrix;

public class RenderGrapplingHookNode extends Render<EntityGrapplingHookNode> {
	private Frustum frustum;

	protected static final ResourceLocation TEXTURE_ROPE = new ResourceLocation("thebetweenlands:textures/entity/grappling_hook_rope.png");
	protected static final ResourceLocation TEXTURE_SHAMBLER = new ResourceLocation("thebetweenlands:textures/entity/shambler.png");

	protected static final ModelRopeNode nodeModel = new ModelRopeNode();
	protected static final ModelShambler shamblerModel = new ModelShambler();

	protected final ItemRenderer ItemRenderer;
	protected final ItemStack anglerTooth;

	public RenderGrapplingHookNode(RenderManager renderManager) {
		super(renderManager);
		this.frustum = new Frustum();
		this.ItemRenderer = Minecraft.getInstance().getRenderItem();
		this.anglerTooth = EnumItemMisc.ANGLER_TOOTH.create(1);
	}

	@Override
	public void doRender(EntityGrapplingHookNode entity, double x, double y, double z, float yaw, float partialTicks) {
		this.bindEntityTexture(entity);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		double camPosX = interpolate(entity.lastTickPosX, entity.getX(), partialTicks) - x;
		double camPosY = interpolate(entity.lastTickPosY, entity.getY(), partialTicks) - y;
		double camPosZ = interpolate(entity.lastTickPosZ, entity.getZ(), partialTicks) - z;

		if(this.getRenderManager().isDebugBoundingBox() && entity.isMountNode()) {
			Vector3d weightPos = entity.getWeightPos(partialTicks);

			GlStateManager.disableLighting();
			GlStateManager.disableBlend();
			GlStateManager.disableTexture2D();
			GlStateManager.color(0.5F, 0, 0, 1);
			DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(weightPos.x - 0.1D, weightPos.y - 0.1D, weightPos.z - 0.1D, weightPos.x + 0.1D, weightPos.y + 0.1D, weightPos.z + 0.1D).offset(-camPosX, -camPosY, -camPosZ));
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.enableLighting();
		}

		this.frustum.setPosition(camPosX, camPosY, camPosZ);

		Entity prevNode = entity.getPreviousNodeClient();

		if(prevNode instanceof EntityGrapplingHookNode) {
			if(!this.renderManager.getEntityRenderObject(prevNode).shouldRender(prevNode, this.frustum, camPosX, camPosY, camPosZ)) {
				//Previous node not rendered, render rope
				GlStateManager.pushMatrix();
				double renderOffsetX = interpolate(prevNode.lastTickPosX - entity.lastTickPosX, prevNode.getX() - entity.getX(), partialTicks);
				double renderOffsetY = interpolate(prevNode.lastTickPosY - entity.lastTickPosY, prevNode.getY() - entity.getY(), partialTicks);
				double renderOffsetZ = interpolate(prevNode.lastTickPosZ - entity.lastTickPosZ, prevNode.getZ() - entity.getZ(), partialTicks);
				GlStateManager.translate(renderOffsetX, renderOffsetY, renderOffsetZ);
				this.renderConnection(prevNode, entity, tessellator, buffer, x, y, z, partialTicks);
				GlStateManager.popMatrix();
			}
		}

		Entity nextNode = entity.getNextNodeClient();

		if(nextNode instanceof EntityGrapplingHookNode) {
			this.renderConnection(entity, nextNode, tessellator, buffer, x, y, z, partialTicks);

			double dx = interpolate(entity.lastTickPosX, entity.getX(), partialTicks) - interpolate(nextNode.lastTickPosX, nextNode.getX(), partialTicks);
			double dy = interpolate(entity.lastTickPosY, entity.getY(), partialTicks) - interpolate(nextNode.lastTickPosY, nextNode.getY(), partialTicks);
			double dz = interpolate(entity.lastTickPosZ, entity.getZ(), partialTicks) - interpolate(nextNode.lastTickPosZ, nextNode.getZ(), partialTicks);

			GlStateManager.pushMatrix();

			GlStateManager.translate(x, y, z);

			GlStateManager.rotate(-(float)Math.toDegrees(Math.atan2(dz, dx)), 0, 1, 0);
			GlStateManager.rotate((float)Math.toDegrees(Math.atan2(Math.sqrt(dx * dx + dz * dz), -dy)) + 180, 0, 0, 1);

			if(prevNode == null) {
				//Last node, render shambler tongue
				this.bindTexture(TEXTURE_SHAMBLER);

				GlStateManager.pushMatrix();

				GlStateManager.scale(1.1D, 1.1D, 1.1D);

				GlStateManager.rotate(-90, 1, 0, 0);

				GlStateManager.translate(0, -1, 0.1D);

				GlStateManager.disableCull();
				shamblerModel.renderTongueEnd(0.0625F);
				GlStateManager.enableCull();

				GlStateManager.popMatrix();
			} else {
				//Render bone hooks
				this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

				GlStateManager.disableLighting();

				GlStateManager.pushMatrix();
				GlStateManager.scale(0.25D, 0.25D, 0.25D);

				GlStateManager.pushMatrix();
				GlStateManager.translate(0.4, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				this.ItemRenderer.ItemRenderer(this.anglerTooth, TransformType.FIXED);
				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.4, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				GlStateManager.rotate(180, 0, 1, 0);
				this.ItemRenderer.ItemRenderer(this.anglerTooth, TransformType.FIXED);
				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.rotate(90, 0, 1, 0);

				GlStateManager.pushMatrix();
				GlStateManager.translate(0.4, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				this.ItemRenderer.ItemRenderer(this.anglerTooth, TransformType.FIXED);
				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.4, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				GlStateManager.rotate(180, 0, 1, 0);
				this.ItemRenderer.ItemRenderer(this.anglerTooth, TransformType.FIXED);
				GlStateManager.popMatrix();

				GlStateManager.popMatrix();

				GlStateManager.enableLighting();

				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();

			this.bindEntityTexture(entity);
		}

		GlStateManager.popMatrix();
	}

	protected static double interpolate(double prev, double now, double partialTicks) {
		return prev + (now - prev) * partialTicks;
	}

	protected void renderConnection(Entity node1, Entity node2, Tessellator tessellator, BufferBuilder buffer, double x, double y, double z, float partialTicks) {
		if(node2 != null) {
			double camPosX = interpolate(node1.xOld - x, node1.getX() - x, partialTicks);
			double camPosY = interpolate(node1.yOld - y, node1.getY() - y, partialTicks);
			double camPosZ = interpolate(node1.zOld - z, node1.getZ() - z, partialTicks);

			double startX = x;
			double startY = y;
			double startZ = z;
			double endX = interpolate(node2.xOld - camPosX, node2.getX() - camPosX, partialTicks);
			double endY = interpolate(node2.yOld - camPosY, node2.getY() - camPosY, partialTicks);
			double endZ = interpolate(node2.zOld - camPosZ, node2.getZ() - camPosZ, partialTicks);
			if(node2 instanceof EntityGrapplingHookNode && ((EntityGrapplingHookNode) node2).isMountNode() && node2.getControllingPassenger() != null) {
				Entity controller = node2.getControllingPassenger();

				double yaw;
				if(controller instanceof LivingEntity) {
					yaw = interpolate(((LivingEntity) controller).prevRenderYawOffset, ((LivingEntity) controller).renderYawOffset, partialTicks);
				} else {
					yaw = interpolate(controller.prevRotationYaw, controller.yRot, partialTicks);
				}

				Hand activeHand = Hand.MAIN_HAND;
				if(controller instanceof LivingEntity) {
					activeHand = !((LivingEntity) controller).getItemInHand(Hand.OFF_HAND).isEmpty() && ((LivingEntity) controller).getItemInHand(Hand.OFF_HAND).getItem() instanceof ItemGrapplingHook ? Hand.OFF_HAND : Hand.MAIN_HAND;
				}

				double rotX = 0;
				double rotZ = 0;

				if(activeHand == Hand.MAIN_HAND) {
					rotX += -Math.cos(Math.toRadians(-yaw)) * 0.6D;
					rotZ += Math.sin(Math.toRadians(-yaw)) * 0.6D; 
				} else {
					rotX += -Math.cos(Math.toRadians(-yaw)) * -0.6D;
					rotZ += Math.sin(Math.toRadians(-yaw)) * -0.6D; 
				}

				rotX += -Math.cos(Math.toRadians(-yaw + 90)) * 0.4D;
				rotZ += Math.sin(Math.toRadians(-yaw + 90)) * 0.4D;

				double yOffset = 0;
				if(controller instanceof LivingEntity && !((LivingEntity) controller).getItemInHand(activeHand).isEmpty()) {
					yOffset += 0.2D;
				}
				if(activeHand == Hand.OFF_HAND) {
					yOffset += 0.2D;
				}

				Vector3d offset = new Vector3d(rotX, 1.1D + yOffset, rotZ);

				//Below is the same as this, and as the rotation in onPlayerRenderPre
				/*GlStateManager.rotate(-bodyYaw, 0, 1, 0);
				GlStateManager.translate(0.6D, 0, -0.4D);
				GlStateManager.rotate(bodyYaw, 0, 1, 0);*/
				/*GlStateManager.rotate(yaw, 0, 1, 0);
				GlStateManager.rotate(pitch, 0, 0, 1);
				GlStateManager.rotate(yaw, 0, -1, 0);*/

				Vector3d weightPos = ((EntityGrapplingHookNode) node2).getWeightPos(partialTicks);

				double dx = interpolate(node2.lastTickPosX, node2.getX(), partialTicks) - weightPos.x;
				double dy = (interpolate(node2.lastTickPosY, node2.getY(), partialTicks) + node2.height) - weightPos.y;
				double dz = interpolate(node2.lastTickPosZ, node2.getZ(), partialTicks) - weightPos.z;

				float rotYaw = -(float)Math.toDegrees(Math.atan2(dz, dx));
				float rotPitch = (float)Math.toDegrees(Math.atan2(Math.sqrt(dx * dx + dz * dz), -dy)) - 180;

				float pitchMin = -30.0F;
				float pitchMax = 30.0F;

				float t = (rotPitch - pitchMin) / (pitchMax - pitchMin);
				rotPitch = (pitchMin + (pitchMax - pitchMin) * (1.0F / (1.0F + (float)Math.pow(200.0F, 0.5F - t))));

				RotationMatrix matrix = new RotationMatrix();

				matrix.setRotations(0, -(float)Math.toRadians(yaw), 0);
				offset = offset.add(matrix.transformVec(new Vector3d(activeHand == Hand.MAIN_HAND ? 0.6D : -0.6D, 0, -0.4D), Vector3d.ZERO));

				matrix.setRotations(0, -(float)Math.toRadians(rotYaw), 0);
				offset = matrix.transformVec(offset, Vector3d.ZERO);

				matrix.setRotations(0, 0, (float)Math.toRadians(rotPitch));
				offset = matrix.transformVec(offset.add(0, -1.4D, 0), Vector3d.ZERO);

				matrix.setRotations(0, (float)Math.toRadians(rotYaw), 0);
				offset = matrix.transformVec(offset, Vector3d.ZERO);

				offset = offset.add(0, 1.4D, 0);
				
				endX = interpolate(controller.lastTickPosX - camPosX, controller.getX() - camPosX, partialTicks) + offset.x;
				endY = interpolate(controller.lastTickPosY - camPosY, controller.getY() - camPosY, partialTicks) + offset.y;
				endZ = interpolate(controller.lastTickPosZ - camPosZ, controller.getZ() - camPosZ, partialTicks) + offset.z;
			}

			double diffX = (double)((float)(endX - startX));
			double diffY = (double)((float)(endY - startY));
			double diffZ = (double)((float)(endZ - startZ));

			GlStateManager.enableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

			ParticleBeam.buildBeam(x + diffX, y + diffY, z + diffZ, new Vector3d(-diffX, -diffY, -diffZ), 0.05F, 0, 2F,
					ActiveRenderInfo.getRotationX(), ActiveRenderInfo.getRotationZ(), ActiveRenderInfo.getRotationYZ(), ActiveRenderInfo.getRotationXY(), ActiveRenderInfo.getRotationXZ(),
					(vx, vy, vz, u, v) -> {
						buffer.pos(vx, vy, vz).tex(u, v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
					});

			tessellator.draw();

			GlStateManager.enableLighting();
			GlStateManager.enableCull();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGrapplingHookNode entity) {
		return TEXTURE_ROPE;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerRenderPre(PlayerRendererEvent.Pre event) {
		GlStateManager.pushMatrix();

		PlayerEntity player = event.getEntityPlayer();

		Entity ridingEntity = player.getRidingEntity();

		if(ridingEntity instanceof EntityGrapplingHookNode) {
			EntityGrapplingHookNode node = (EntityGrapplingHookNode) ridingEntity;
			Entity prevNode = node.getPreviousNode();

			if(prevNode != null) {
				float partialTicks = event.getPartialRenderTick();

				Vector3d weightPos = node.getWeightPos(partialTicks);

				double dx = interpolate(node.lastTickPosX, node.getX(), partialTicks) - weightPos.x;
				double dy = interpolate(node.lastTickPosY, node.getY(), partialTicks) + node.height - weightPos.y;
				double dz = interpolate(node.lastTickPosZ, node.getZ(), partialTicks) - weightPos.z;

				float yaw = -(float)Math.toDegrees(Math.atan2(dz, dx));
				float pitch = (float)Math.toDegrees(Math.atan2(Math.sqrt(dx * dx + dz * dz), -dy)) - 180;

				float pitchMin = -30.0F;
				float pitchMax = 30.0F;

				float t = (pitch - pitchMin) / (pitchMax - pitchMin);
				pitch = (pitchMin + (pitchMax - pitchMin) * (1.0F / (1.0F + (float)Math.pow(200.0F, 0.5F - t))));

				//Make sure origin is at feet when rotating
				GlStateManager.translate(event.getX(), event.getY(), event.getZ());
				
				GlStateManager.translate(0, 1.4D, 0);
				
				GlStateManager.rotate(yaw, 0, 1, 0);
				GlStateManager.rotate(pitch, 0, 0, 1);
				GlStateManager.rotate(-yaw, 0, 1, 0);
				
				float bodyYaw = (float) interpolate(player.prevRenderYawOffset, player.renderYawOffset, partialTicks);

				Hand activeHand = !player.getItemInHand(Hand.OFF_HAND).isEmpty() && player.getItemInHand(Hand.OFF_HAND).getItem() instanceof ItemGrapplingHook ? Hand.OFF_HAND : Hand.MAIN_HAND;

				GlStateManager.rotate(-bodyYaw, 0, 1, 0);
				GlStateManager.translate(activeHand == Hand.MAIN_HAND ? 0.6D : -0.6D, -1.4D, -0.4D);
				GlStateManager.rotate(bodyYaw, 0, 1, 0);

				//Undo previous offset
				GlStateManager.translate(-event.getX(), -event.getY(), -event.getZ());
				
				player.swingingHand = activeHand;
				player.swingProgress = 0.12f;
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerRenderPost(PlayerRendererEvent.Post event) {
		GlStateManager.popMatrix();
	}
}
