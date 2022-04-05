package thebetweenlands.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.tile.ModelPuffshroom;
import thebetweenlands.common.tile.TileEntityPuffshroom;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class TileEntityPuffshroomRenderer extends TileEntityRenderer<TileEntityPuffshroom> {
	
	public TileEntityPuffshroomRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/puffshroom.png");

	private final ModelPuffshroom model = new ModelPuffshroom();

	@Override
	public void render(TileEntityPuffshroom tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int pCombinedLight, int pCombinedOverlay) {
		if (tile.animation_1 != 0) {
			bindTexture(TEXTURE);
			matrix.pushPose();
			matrix.translate((float) x + 0.5F, (float) y + 1.89F, (float) z + 0.5F);
			matrix.scale(-1, -1, 1);
			model.render(tile, partialTicks);
			matrix.popPose();
		}
	}
}