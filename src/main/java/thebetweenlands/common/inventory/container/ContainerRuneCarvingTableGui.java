package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import thebetweenlands.client.gui.inventory.GuiRuneCarvingTable;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;

public class ContainerRuneCarvingTableGui extends ContainerRuneCarvingTable {
	private GuiRuneCarvingTable gui;

	public ContainerRuneCarvingTableGui(PlayerInventory playerInventory, TileEntityRuneCarvingTable tile, boolean fullGrid) {
		super(playerInventory, tile, fullGrid);
	}

	public void setGui(GuiRuneCarvingTable gui) {
		this.gui = gui;
	}

	@Override
	protected void onCrafting() {
		this.gui.onCrafting();
	}
}