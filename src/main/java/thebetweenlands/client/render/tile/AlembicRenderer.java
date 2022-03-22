package thebetweenlands.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.tile.ModelAlembic;
import thebetweenlands.common.block.container.BlockAlembic;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.tile.TileEntityAlembic;

@OnlyIn(Dist.CLIENT)
public class AlembicRenderer<T extends TileEntity> extends TileEntityRenderer<TileEntityAlembic> {

	private final ModelAlembic model = new ModelAlembic();
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/alembic.png");
	public static final ResourceLocation ATLAS = new ResourceLocation("thebetweenlands:atlas/alembic.png");
	public static final RenderMaterial MATERIAL = new RenderMaterial(Atlases.CHEST_SHEET, TEXTURE);
	
	public AlembicRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	@Override
	public void render(TileEntityAlembic te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		BlockState state = te.getBlockState();
		float f = state.getValue(BlockAlembic.FACING).toYRot();
		//bindTexture(TEXTURE);
		matrixStackIn.pushPose();
		matrixStackIn.translate((float) 0.5F, (float) 1.5F, (float) 0.5F);
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-f));
		matrixStackIn.scale(1F, -1F, -1F);
		RenderMaterial rendermaterial = MATERIAL;
		IVertexBuilder ivertexbuilder = rendermaterial.buffer(bufferIn, RenderType::entityCutout);
		if(te != null && te.isFull()) {
			float[] colors = ElixirRecipe.getInfusionColor(te.getElixirRecipe(), te.getInfusionTime());
			model.renderWithLiquid(colors[0], colors[1], colors[2], te.getProgress());
		} else {
			model.render();
		}
		matrixStackIn.popPose();
	}
}
