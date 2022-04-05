package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.container.runeweavingtable.ContainerRuneWeavingTable;
import thebetweenlands.common.tile.TileEntityRuneWeavingTable;

public class SlotRuneWeavingTableInput extends Slot {
	
	private final TileEntityRuneWeavingTable altar;
	private final ContainerRuneWeavingTable.Page page;

	public int prevHoverTicks;
	public int hoverTicks;
	
	public SlotRuneWeavingTableInput(TileEntityRuneWeavingTable altar, int index, int xPosition, int yPosition, ContainerRuneWeavingTable.Page page) {
		super(altar, index, xPosition, yPosition);
		this.altar = altar;
		this.page = page;
	}

	public ContainerRuneWeavingTable.Page getPage() {
		return this.page;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return this.altar.canPlaceItem(this.index, stack) && this.page.isInteractable() && this.index - TileEntityRuneWeavingTable.NON_INPUT_SLOTS < this.altar.getChainLength() + 1;
	}

	@Override
	public boolean mayPickup(PlayerEntity playerIn) {
		return this.page.isInteractable() && this.altar.isOutputItemAvailable() && this.index - TileEntityRuneWeavingTable.NON_INPUT_SLOTS < this.altar.getChainLength() + 1;
	}

	@Override
	public boolean isHere(IInventory inv, int slotIn) {
		return super.isHere(inv, slotIn) && this.page.isInteractable() && this.altar.isOutputItemAvailable() && this.index - TileEntityRuneWeavingTable.NON_INPUT_SLOTS < this.altar.getChainLength() + 1;
	}

	@Override
	public boolean isEnabled() {
		return this.page.isCurrent() && this.altar.isOutputItemAvailable() && this.index - TileEntityRuneWeavingTable.NON_INPUT_SLOTS < this.altar.getChainLength() + 1;
	}
}
