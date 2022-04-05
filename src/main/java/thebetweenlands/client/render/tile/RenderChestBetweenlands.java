package thebetweenlands.client.render.tile;

import java.util.Calendar;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.container.BlockChestBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;

@OnlyIn(Dist.CLIENT)
public class RenderChestBetweenlands<T extends TileEntity & IChestLid> extends TileEntityRenderer<T> {

	private static final ResourceLocation TEXTURE_WEEDWOOD = new ResourceLocation(ModInfo.ID, "textures/tiles/weedwood_chest.png");
	private static final ResourceLocation TEXTURE_WEEDWOOD_LARGE = new ResourceLocation(ModInfo.ID, "textures/tiles/weedwood_chest_large.png");

	private final ModelRenderer lid;
	private final ModelRenderer bottom;
	private final ModelRenderer lock;
	private final ModelRenderer doubleLeftLid;
	private final ModelRenderer doubleLeftBottom;
	private final ModelRenderer doubleLeftLock;
	private final ModelRenderer doubleRightLid;
	private final ModelRenderer doubleRightBottom;
	private final ModelRenderer doubleRightLock;

	private boolean xmasTextures;

	public RenderChestBetweenlands(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.xmasTextures = true;
		}

		this.bottom = new ModelRenderer(64, 64, 0, 19);
		this.bottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.lid = new ModelRenderer(64, 64, 0, 0);
		this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.lid.y = 9.0F;
		this.lid.z = 1.0F;
		this.lock = new ModelRenderer(64, 64, 0, 0);
		this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.lock.y = 8.0F;
		this.doubleLeftBottom = new ModelRenderer(64, 64, 0, 19);
		this.doubleLeftBottom.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.doubleLeftLid = new ModelRenderer(64, 64, 0, 0);
		this.doubleLeftLid.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.doubleLeftLid.y = 9.0F;
		this.doubleLeftLid.z = 1.0F;
		this.doubleLeftLock = new ModelRenderer(64, 64, 0, 0);
		this.doubleLeftLock.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.doubleLeftLock.y = 8.0F;
		this.doubleRightBottom = new ModelRenderer(64, 64, 0, 19);
		this.doubleRightBottom.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.doubleRightLid = new ModelRenderer(64, 64, 0, 0);
		this.doubleRightLid.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.doubleRightLid.y = 9.0F;
		this.doubleRightLid.z = 1.0F;
		this.doubleRightLock = new ModelRenderer(64, 64, 0, 0);
		this.doubleRightLock.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.doubleRightLock.y = 8.0F;
	}

	@Override
	public void render(T te, float partialTicks, MatrixStack pMatrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		World world = te.getLevel();
		boolean flag = world != null;
		BlockState blockstate = flag ? te.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
		ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
		Block block = blockstate.getBlock();
		if (block instanceof BlockChestBetweenlands) {
			AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock) block;
			boolean flag1 = chesttype != ChestType.SINGLE;
			pMatrixStack.pushPose();
			float f = blockstate.getValue(ChestBlock.FACING).toYRot();
			pMatrixStack.translate(0.5D, 0.5D, 0.5D);
			pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(-f));
			pMatrixStack.translate(-0.5D, -0.5D, -0.5D);
			TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> icallbackwrapper;
			if (flag) {
				icallbackwrapper = abstractchestblock.combine(blockstate, world, te.getBlockPos(), true);
			} else {
				icallbackwrapper = TileEntityMerger.ICallback::acceptNone;
			}

			float f1 = icallbackwrapper.<Float2FloatFunction>apply(ChestBlock.opennessCombiner(te)).get(partialTicks);
			f1 = 1.0F - f1;
			f1 = 1.0F - f1 * f1 * f1;
			int i = icallbackwrapper.<Int2IntFunction>apply(new DualBrightnessCallback<>()).applyAsInt(combinedLight);
			RenderMaterial rendermaterial = this.getMaterial(te, chesttype);
			IVertexBuilder ivertexbuilder = rendermaterial.buffer(buffer, RenderType::entityCutout);
			if (flag1) {
				if (chesttype == ChestType.LEFT) {
					this.render(pMatrixStack, ivertexbuilder, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, f1, i, combinedOverlay);
				} else {
					this.render(pMatrixStack, ivertexbuilder, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, f1, i, combinedOverlay);
				}
			} else {
				this.render(pMatrixStack, ivertexbuilder, this.lid, this.lock, this.bottom, f1, i, combinedOverlay);
			}

			pMatrixStack.popPose();
		}
	}

	private void render(MatrixStack pMatrixStack, IVertexBuilder buffer, ModelRenderer pChestLid, ModelRenderer pChestLatch, ModelRenderer pChestBottom, float pLidAngle, int combinedLight, int combinedOverlay) {
		pChestLid.xRot = -(pLidAngle * ((float) Math.PI / 2F));
		pChestLatch.xRot = pChestLid.xRot;
		pChestLid.render(pMatrixStack, buffer, combinedLight, combinedOverlay);
		pChestLatch.render(pMatrixStack, buffer, combinedLight, combinedOverlay);
		pChestBottom.render(pMatrixStack, buffer, combinedLight, combinedOverlay);
	}

	protected RenderMaterial getMaterial(T tileEntity, ChestType chestType) {
		return Atlases.chooseMaterial(tileEntity, chestType, this.xmasTextures);
	}

}