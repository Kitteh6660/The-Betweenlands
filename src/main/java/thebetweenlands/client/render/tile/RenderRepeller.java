package thebetweenlands.client.render.tile;

import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.render.model.tile.ModelRepeller;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.block.container.BlockRepeller;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityRepeller;
import thebetweenlands.util.StatePropertyHelper;

public class RenderRepeller<T extends TileEntity> extends TileEntityRenderer<TileEntityRepeller> {
	protected static final ModelRepeller MODEL = new ModelRepeller();

	protected static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/repeller.png");

	public RenderRepeller(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public boolean shouldRenderOffScreen(TileEntityRepeller te) {
		return true;
	}
	
	@Override
	public void render(TileEntityRepeller te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Direction facing = StatePropertyHelper.getStatePropertySafely(tile, BlockRepeller.class, BlockRepeller.FACING, Direction.NORTH);

		double xOff = -facing.getStepX() * 0.12F;
		double zOff = facing.getStepZ() * 0.12F;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.rotate(facing.getHorizontalAngle() + 180.0F, 0, 1, 0);

		this.bindTexture(TEXTURE);
		MODEL.render(tile != null ? tile.getFuel() / (float)tile.getMaxFuel() : 0.0F);

		GlStateManager.popMatrix();

		if(tile != null && tile.hasShimmerstone()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)(x + 0.5F + xOff), (float)(y + 1.15F), (float)(z + 0.5F - zOff));
			GlStateManager.scale(0.008F, 0.008F, 0.008F);
			this.renderShine(tile.renderTicks + partialTicks, 20, 
					1.0F, 0.8F, 0.0F, 0.0F,
					1.0F, 0.8F, 0.0F, 1.0F);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.popMatrix();
		}

		if(tile != null && tile.isRunning()) {
			if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();
			}
			WorldRenderHandler.REPELLER_SHIELDS.add(Pair.of(new Vector3d(x + 0.5F + xOff, y + 1.15F, z + 0.5F - zOff), tile.getRadius(partialTicks)));
		}
	}

	protected void renderShine(float ticks, int iterations, float or, float og, float ob, float oa, float ir, float ig, float ib, float ia) {
		Random random = new Random(432L);
		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.enableCull();
		GlStateManager.depthMask(false);
		GlStateManager.pushMatrix();
		float rotation = ticks / 360.0F;
		float size = ((float)Math.sin(ticks / 40.0F) + 1.0F) * 1.8F + 1.1F;
		float brightness = MathHelper.clamp(size / 2.0F + 0.45F, 0.0f, 1.0f);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		for (int i = 0; (float) i < iterations; ++i) {
			GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F + rotation * 90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F + rotation * 180.0F, 1.0F, 0.0F, 0.0F);
			float pos1 = random.nextFloat() * 20.0F + 5.0F + size * 10.0F;
			float pos2 = random.nextFloat() * 2.0F + 1.0F + size * 2.0F;
			buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(0.0D, 0.0D, 0.0D).color(ir, ig, ib, ia * brightness).endVertex();
			buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(or, og, ob, oa * brightness).endVertex();
			buffer.pos(0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(or, og, ob, oa * brightness).endVertex();
			buffer.pos(0.0D, (double) pos1, (double) (1.0F * pos2)).color(or, og, ob, oa * brightness).endVertex();
			buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(or, og, ob, oa * brightness).endVertex();
			tessellator.draw();
		}
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		RenderHelper.enableStandardItemLighting();
	}
}
