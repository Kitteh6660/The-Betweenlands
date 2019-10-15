package thebetweenlands.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.gallery.GalleryEntry;
import thebetweenlands.client.handler.gallery.GalleryManager;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.EntityGalleryFrame;
import thebetweenlands.common.network.serverbound.MessageSetGalleryUrl;

public class GuiGalleryFrame extends GuiScreen {
	protected static final double WIDTH = 200;
	protected static final double HEIGHT = 200;

	protected int xStart;
	protected int yStart;

	protected EntityGalleryFrame frame;

	public GuiGalleryFrame(EntityGalleryFrame frame) {
		this.frame = frame;

		//Set to first available picture
		if(this.frame.getUrl().length() == 0) {
			this.switchPicture(false);
		}
	}

	@Override
	public void initGui() {
		this.xStart = (this.width - (int) WIDTH) / 2;
		this.yStart = (this.height - (int) HEIGHT) / 2;

		this.buttonList.add(new GuiButton(0, this.xStart - 60, this.yStart + (int)HEIGHT / 2, 30, 20, "<-"));
		this.buttonList.add(new GuiButton(1, this.xStart + (int)WIDTH + 30, this.yStart + (int)HEIGHT / 2, 30, 20, "->"));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button.id == 0 || button.id == 1) {
			this.switchPicture(button.id == 1);
		}
	}

	private void switchPicture(boolean next) {
		Map<String, GalleryEntry> available = GalleryManager.INSTANCE.getEntries();

		if(!available.isEmpty()) {
			GalleryEntry entry = available.get(this.frame.getUrl());

			GalleryEntry selectedEntry = null;

			List<GalleryEntry> availableList = new ArrayList<>(available.values());

			Collections.sort(availableList, (e1, e2) -> e1.getTitle().compareTo(e2.getTitle()));

			if(entry == null) {
				selectedEntry = availableList.get(0);
			} else {
				int currentIndex = availableList.indexOf(entry);

				if(currentIndex >= 0) {
					int newIndex = next ? currentIndex + 1 : currentIndex -1;
					if(newIndex < 0) {
						newIndex = availableList.size() + newIndex;
					}
					newIndex = newIndex % availableList.size();

					selectedEntry = availableList.get(newIndex);
				} else {
					selectedEntry = availableList.get(0);
				}
			}

			if(selectedEntry != null) {
				TheBetweenlands.networkWrapper.sendToServer(new MessageSetGalleryUrl(this.frame, selectedEntry.getUrl()));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawScreen(int mouseX, int mouseY, float renderPartials) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, renderPartials);

		GlStateManager.color(1F, 1F, 1F, 1F);

		GalleryEntry entry = GalleryManager.INSTANCE.getEntries().get(this.frame.getUrl());

		ResourceLocation pictureLocation = entry != null && entry.isUploaded() ? entry.getLocation() : TextureManager.RESOURCE_LOCATION_EMPTY;

		Minecraft.getMinecraft().getTextureManager().bindTexture(pictureLocation);

		float relWidthMargin = 0; 
		float relHeightMargin = 0;

		if(entry != null) {
			int maxDim = Math.max(entry.getWidth(), entry.getHeight());
			relWidthMargin = (maxDim - entry.getWidth()) / (float)maxDim / 2.0f;
			relHeightMargin = (maxDim - entry.getHeight()) / (float)maxDim / 2.0f;
		}

		drawTexture(xStart + (int)(WIDTH * relWidthMargin), yStart + (int)(HEIGHT * relHeightMargin), (int) WIDTH - (int)(WIDTH * relWidthMargin * 2), (int) HEIGHT - (int)(HEIGHT * relHeightMargin * 2), WIDTH, HEIGHT, 0.0D, WIDTH, 0.0D, HEIGHT);

		if(entry != null) {
			this.fontRenderer.drawString(TextFormatting.UNDERLINE.toString() + TextFormatting.BOLD.toString() + entry.getTitle(), this.xStart + (int)WIDTH / 2 - this.fontRenderer.getStringWidth(TextFormatting.UNDERLINE.toString() + TextFormatting.BOLD.toString() + entry.getTitle()) / 2 , this.yStart - 20, 0xFFFFFFFF);

			int maxLineWidth = 0;

			String authorLine = I18n.format("tooltip.gallery.author");
			String authorText = authorLine + TextFormatting.RESET.toString() + entry.getAuthor();
			maxLineWidth = Math.max(maxLineWidth, this.fontRenderer.getStringWidth(authorText));


			String descName = I18n.format("tooltip.gallery.description");
			int descNameWidth = this.fontRenderer.getStringWidth(descName);
			String[] descLines = entry.getDescription().split("\\n");
			for(int i = 0; i < descLines.length; i++) {
				if(i == 0) {
					maxLineWidth = Math.max(maxLineWidth, this.fontRenderer.getStringWidth(descName + descLines[0]));
				} else {
					maxLineWidth = Math.max(maxLineWidth, this.fontRenderer.getStringWidth(descLines[i]) + descNameWidth);
				}
			}

			String sourceLine = I18n.format("tooltip.gallery.source_url");
			String sourceText = null;
			if(entry.getSourceUrl() != null) {
				sourceText = sourceLine + TextFormatting.RESET.toString() + entry.getSourceUrl();
				maxLineWidth = Math.max(maxLineWidth, this.fontRenderer.getStringWidth(sourceText));
			}

			this.fontRenderer.drawString(authorText, this.xStart + (int)WIDTH / 2 - maxLineWidth / 2, this.yStart + (int)HEIGHT + 8, 0xFFFFFFFF);

			int yOff = 0;

			for(int i = 0; i < descLines.length; i++) {
				if(i == 0) {
					this.fontRenderer.drawString(descName + TextFormatting.RESET.toString() + descLines[0], this.xStart + (int)WIDTH / 2 - maxLineWidth / 2, this.yStart + (int)HEIGHT + 23 + yOff, 0xFFFFFFFF);
				} else {
					this.fontRenderer.drawString(descLines[i], this.xStart + descNameWidth + (int)WIDTH / 2 - maxLineWidth / 2, this.yStart + (int)HEIGHT + 23 + yOff, 0xFFFFFFFF);
				}
				yOff += 12;
			}

			if(sourceText != null) {
				this.fontRenderer.drawString(sourceText, this.xStart + (int)WIDTH / 2 - maxLineWidth / 2, this.yStart + (int)HEIGHT + 26 + yOff, 0xFFFFFFFF);
			}
		} else {
			String notFoundText = I18n.format("tooltip.gallery.info_not_found");
			this.fontRenderer.drawString(notFoundText, this.xStart + (int)WIDTH / 2 - this.fontRenderer.getStringWidth(notFoundText) / 2, this.yStart + (int)HEIGHT + 12, 0xFFFFFFFF);
		}
	}

	/**
	 * Drawing a scalable texture
	 *
	 * @param xStart        the x coordinate to start drawing
	 * @param yStart        the y coordinate to start drawing
	 * @param width         the width for drawing
	 * @param height        the height for drawing
	 * @param textureWidth  the width of the texture
	 * @param textureHeight the height of the texture
	 * @param textureXStart the x start in the texture
	 * @param textureXEnd   the x end in the texture
	 * @param textureYStart the y start in the texture
	 * @param textureYEnd   the y end in the texture
	 */
	private void drawTexture(int xStart, int yStart, int width, int height, double textureWidth, double textureHeight, double textureXStart, double textureXEnd, double textureYStart, double textureYEnd) {
		double umin = 1.0D / textureWidth * textureXStart;
		double umax = 1.0D / textureWidth * textureXEnd;
		double vmin = 1.0D / textureHeight * textureYStart;
		double vmax = 1.0D / textureHeight * textureYEnd;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(xStart, yStart, 0).tex(umin, vmin).endVertex();
		buffer.pos(xStart, yStart + height, 0).tex(umin, vmax).endVertex();
		buffer.pos(xStart + width, yStart + height, 0).tex(umax, vmax).endVertex();
		buffer.pos(xStart + width, yStart, 0).tex(umax, vmin).endVertex();
		tessellator.draw();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
