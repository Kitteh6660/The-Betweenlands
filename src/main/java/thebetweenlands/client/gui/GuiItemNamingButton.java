package thebetweenlands.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class GuiItemNamingButton extends Button {
	
    public GuiItemNamingButton(int x, int y, int width, int height, ITextComponent text, Button.IPressable pressable) {
        super(x, y, width, height, text, pressable);
    }

    @Override
    public void renderButton(MatrixStack mstack, int mouseX, int mouseY, float partialTicks) {
    	super.renderButton(mstack, mouseX, mouseY, partialTicks);
        /*if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiItemNaming.GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            if (this.hovered) {
                this.drawTexturedModalRect(this.x, this.y, 0, 77, 46, 18);
            } else {
                this.drawTexturedModalRect(this.x, this.y, 0, 57, 46, 18);
            }
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (this.packedFGColour != 0) {
                j = this.packedFGColour;
            } else if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 2) / 2, j);
        }*/
    }
}
