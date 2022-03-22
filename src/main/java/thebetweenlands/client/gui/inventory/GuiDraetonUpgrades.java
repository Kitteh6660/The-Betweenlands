package thebetweenlands.client.gui.inventory;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.inventory.container.ContainerDraetonUpgrades;

@OnlyIn(Dist.CLIENT)
public class GuiDraetonUpgrades extends GuiContainer {
	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation("thebetweenlands:textures/gui/draeton_upgrades_0.png"),
			new ResourceLocation("thebetweenlands:textures/gui/draeton_upgrades_1.png"),
			new ResourceLocation("thebetweenlands:textures/gui/draeton_upgrades_2.png"),
			new ResourceLocation("thebetweenlands:textures/gui/draeton_upgrades_3.png")
			};

	private final EntityDraeton draeton;
	
	public GuiDraetonUpgrades(PlayerInventory playerInventory, EntityDraeton draeton) {
		super(new ContainerDraetonUpgrades(playerInventory, draeton));
		this.draeton = draeton;
		xSize = 182;
		ySize = 256;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        
        Slot hoveredSlot = this.getSlotUnderMouse();
        if(hoveredSlot != null && hoveredSlot.getStack().isEmpty()) {
        	String key = null;
        	
        	switch(this.inventorySlots.inventorySlots.indexOf(hoveredSlot)) {
        	default:
        		break;
        	case 0:
        	case 1:
        	case 2:
        	case 3:
        	case 4:
        	case 5:
        		key = "gui.bl.draeton.main.puller";
        		break;
        	case 6:
        	case 7:
        	case 8:
        	case 9:
        		key = "gui.bl.draeton.main.upgrade_utility";
        		break;
        	case 10:
        		key = "gui.bl.draeton.main.upgrade_anchor";
        		break;
        	}
        	
        	if(key != null) {
    			List<String> lines = ItemTooltipHandler.splitTooltip(I18n.get(key), 0);
    			this.drawHoveringText(lines, mouseX, mouseY);
        	}
        }
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.get(new TranslationTextComponent("container.inventory").getFormattedText()), xSize - 170, ySize - 93, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		int damageStage = (this.draeton.getLeakages().size() + 1) / 2;
		mc.getTextureManager().bindTexture(TEXTURES[Math.min(damageStage, TEXTURES.length - 1)]);
		
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}
}