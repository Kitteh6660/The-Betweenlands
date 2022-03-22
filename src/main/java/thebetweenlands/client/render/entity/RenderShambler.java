package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelShambler;
import thebetweenlands.common.entity.mobs.EntityShambler;

@OnlyIn(Dist.CLIENT)
public class RenderShambler extends RenderLiving<EntityShambler> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/shambler.png");
	public final ModelShambler model = new ModelShambler();
	public RenderShambler(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelShambler(), 0.5F);
	}

	@Override
	public void doRender(EntityShambler entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);
		//	renderDebugBoundingBox(entity, x, y, z, yaw, partialTicks, 0, 0, 0);

		if(entity.getTongueLength() > 0) {
/*
			renderDebugBoundingBox(entity.tongue_end, x, y, z, yaw, partialTicks, entity.tongue_end.getX() - entity.getX(), entity.tongue_end.getY() - entity.getY(), entity.tongue_end.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_1, x, y, z, yaw, partialTicks, entity.tongue_1.getX() - entity.getX(), entity.tongue_1.getY() - entity.getY(), entity.tongue_1.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_2, x, y, z, yaw, partialTicks, entity.tongue_2.getX() - entity.getX(), entity.tongue_2.getY() - entity.getY(), entity.tongue_2.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_3, x, y, z, yaw, partialTicks, entity.tongue_3.getX() - entity.getX(), entity.tongue_3.getY() - entity.getY(), entity.tongue_3.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_4, x, y, z, yaw, partialTicks, entity.tongue_4.getX() - entity.getX(), entity.tongue_4.getY() - entity.getY(), entity.tongue_4.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_5, x, y, z, yaw, partialTicks, entity.tongue_5.getX() - entity.getX(), entity.tongue_5.getY() - entity.getY(), entity.tongue_5.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_6, x, y, z, yaw, partialTicks, entity.tongue_6.getX() - entity.getX(), entity.tongue_6.getY() - entity.getY(), entity.tongue_6.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_7, x, y, z, yaw, partialTicks, entity.tongue_7.getX() - entity.getX(), entity.tongue_7.getY() - entity.getY(), entity.tongue_7.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_8, x, y, z, yaw, partialTicks, entity.tongue_8.getX() - entity.getX(), entity.tongue_8.getY() - entity.getY(), entity.tongue_8.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_9, x, y, z, yaw, partialTicks, entity.tongue_9.getX() - entity.getX(), entity.tongue_9.getY() - entity.getY(), entity.tongue_9.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_10, x, y, z, yaw, partialTicks, entity.tongue_10.getX() - entity.getX(), entity.tongue_10.getY() - entity.getY(), entity.tongue_10.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_11, x, y, z, yaw, partialTicks, entity.tongue_11.getX() - entity.getX(), entity.tongue_11.getY() - entity.getY(), entity.tongue_11.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_12, x, y, z, yaw, partialTicks, entity.tongue_12.getX() - entity.getX(), entity.tongue_12.getY() - entity.getY(), entity.tongue_12.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_13, x, y, z, yaw, partialTicks, entity.tongue_13.getX() - entity.getX(), entity.tongue_13.getY() - entity.getY(), entity.tongue_13.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_14, x, y, z, yaw, partialTicks, entity.tongue_14.getX() - entity.getX(), entity.tongue_14.getY() - entity.getY(), entity.tongue_14.getZ() - entity.getZ());
			renderDebugBoundingBox(entity.tongue_15, x, y, z, yaw, partialTicks, entity.tongue_15.getX() - entity.getX(), entity.tongue_15.getY() - entity.getY(), entity.tongue_15.getZ() - entity.getZ());
*/			
			double ex = entity.lastTickPosX + (entity.getX() - entity.lastTickPosX) * (double)partialTicks;
	        double ey = entity.lastTickPosY + (entity.getY() - entity.lastTickPosY) * (double)partialTicks;
	        double ez = entity.lastTickPosZ + (entity.getZ() - entity.lastTickPosZ) * (double)partialTicks;

	        double rx = ex - x;
	        double ry = ey - y;
	        double rz = ez - z;

	        for(int i = 0; i < entity.tongue_array.length; i++) {
	        	renderTonguePart(entity, entity.tongue_array[i], rx, ry, rz, partialTicks);
	        }
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShambler entity) {
		return TEXTURE;
	}

	private void renderTonguePart(EntityShambler entity, MultiPartEntityPart part, double rx, double ry, double rz, float partialTicks) {
		double x = part.lastTickPosX + (part.getX() - part.lastTickPosX) * (double)partialTicks - rx;
        double y = part.lastTickPosY + (part.getY() - part.lastTickPosY) * (double)partialTicks - ry;
        double z = part.lastTickPosZ + (part.getZ() - part.lastTickPosZ) * (double)partialTicks - rz;
        float yaw = entity.prevRotationYaw + (entity.yRot - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch + (entity.xRot - entity.prevRotationPitch) * partialTicks;
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y - 0.85D, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(180F + yaw, 0F, 1F, 0F);
		GlStateManager.rotate(180F + pitch, 1F, 0F, 0F);
		if(part == entity.tongue_end)
			model.renderTongueEnd(0.0625F);
		else
			model.renderTonguePart(0.0625F);
		GlStateManager.popMatrix();
	}

	private void renderDebugBoundingBox(Entity entity, double x, double y, double z, float yaw, float partialTicks, double xOff, double yOff, double zOff) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		AxisAlignedBB axisalignedbb = entity.getBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entity.getX() + x + xOff, axisalignedbb.minY - entity.getY() + y + yOff, axisalignedbb.minZ - entity.getZ() + z + zOff, axisalignedbb.maxX - entity.getX() + x + xOff, axisalignedbb.maxY - entity.getY() + y + yOff, axisalignedbb.maxZ - entity.getZ() + z + zOff);
		RenderGlobal.drawSelectionBoundingBox(axisalignedbb1, 1F, 1F, 1F, 1F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}
}
