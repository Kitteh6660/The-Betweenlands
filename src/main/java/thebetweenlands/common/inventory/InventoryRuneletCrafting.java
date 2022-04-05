package thebetweenlands.common.inventory;

import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryRuneletCrafting extends InventoryCustomCrafting {
	
	public InventoryRuneletCrafting(Container eventHandler, ICustomCraftingGridChangeHandler tile, NonNullList<ItemStack> inventory, int width, int height) {
		super(eventHandler, tile, inventory, width, height, "container.bl.rune_carving_table");
	}
}
