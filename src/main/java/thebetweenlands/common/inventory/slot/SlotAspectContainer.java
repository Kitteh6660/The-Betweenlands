package thebetweenlands.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.herblore.aspect.AspectManager;

public class SlotAspectContainer extends Slot {
	
	private final AspectManager manager;

	public SlotAspectContainer(IInventory inventoryIn, int index, int xPosition, int yPosition, AspectManager manager) {
		super(inventoryIn, index, xPosition, yPosition);
		this.manager = manager;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return !ItemAspectContainer.fromItem(stack, this.manager).getAspects().isEmpty();
	}
}
