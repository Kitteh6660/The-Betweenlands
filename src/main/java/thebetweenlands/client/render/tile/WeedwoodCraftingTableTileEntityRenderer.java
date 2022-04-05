package thebetweenlands.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class WeedwoodCraftingTableTileEntityRenderer extends TileEntityRenderer<TileEntityWeedwoodWorkbench> {

	public WeedwoodCraftingTableTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(TileEntityWeedwoodWorkbench table, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		matrix.pushPose();
		matrix.translate(x + 0.5D, y + 0.875D, z + 0.5D);
		matrix.scale(0.25F, 0.25F, 0.25F);
		matrix.rotate(90.0F * (-table.rotation + 3), 0.0F, 1.0F, 0.0F);
		matrix.translate(-1.5F, -0.0F, -1.0F);
		matrix.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		matrix.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		float prevLGTX = OpenGlHelper.lastBrightnessX;
		float prevLGTY = OpenGlHelper.lastBrightnessY;
		BlockPos pos = table.getPos();
		int bright = table.getWorld().getCombinedLight(pos.above(), 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, bright % 65536, bright / 65536);

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				ItemStack stack = table.craftingSlots.get(column * 3 + row);
				if (!stack.isEmpty()) {
					matrix.pushPose();
					matrix.translate(row * 0.75F, 0.0D, column * 0.75F);
					matrix.translate(0.75F, 0.52F, 0.25F);
					matrix.scale(0.5F, 0.5F, 0.5F);
					matrix.mulPose(); .rotate(-90.0F, 1.0F, 0.0F, 0.0F);
					RenderHelper.disableStandardItemLighting();
					ItemRenderer ItemRenderer = Minecraft.getInstance().getItemRenderer();
					ItemRenderer.ItemRenderer(stack, ItemRenderer.getItemModelWithOverrides(stack, null, null));
					matrix.popPose();
				}
			}
		}
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLGTX, prevLGTY);

		matrix.popPose();
	}
}
