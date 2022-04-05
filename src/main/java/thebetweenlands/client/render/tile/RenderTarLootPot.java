package thebetweenlands.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelTarLootPot1;
import thebetweenlands.client.render.model.tile.ModelTarLootPot2;
import thebetweenlands.client.render.model.tile.ModelTarLootPot3;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.block.container.BlockTarLootPot;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.util.StatePropertyHelper;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;

public abstract class RenderTarLootPot<T extends TileEntity> extends TileEntityRenderer<TileEntityLootPot> {

	private static final ModelTarLootPot1 LOOT_POT = new ModelTarLootPot1();
	private static final ModelTarLootPot2 LOOT_POT_2 = new ModelTarLootPot2();
	private static final ModelTarLootPot3 LOOT_POT_3 = new ModelTarLootPot3();

	private static final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/tiles/tar_loot_pot_1.png");
	private static final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/tiles/tar_loot_pot_2.png");
	private static final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/tiles/tar_loot_pot_3.png");

	public abstract EnumLootPot getType();

	public RenderTarLootPot(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(TileEntityLootPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		EnumLootPot type = this.getType();
		Direction rotation = StatePropertyHelper.getStatePropertySafely(te, BlockTarLootPot.class, BlockLootPot.FACING, Direction.NORTH);
		int offset = 0;

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		switch (type){
		default:
		case POT_1: {
			bindTexture(TEXTURE_1);
			break;
		}
		case POT_2: {
			bindTexture(TEXTURE_2);
			break;
		}
		case POT_3: {
			bindTexture(TEXTURE_3);
			break;
		}
		}

		switch (rotation) {
		default:
		case NORTH:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case EAST:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 90.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case SOUTH:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 180.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case WEST:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 270.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		}
	}

	private void renderType(EnumLootPot type){
		switch (type){
		case POT_1: {
			LOOT_POT.render();
			break;
		}
		case POT_2: {
			LOOT_POT_2.render();
			break;
		}
		case POT_3: {
			LOOT_POT_3.render();
			break;
		}
		}
	}
}
