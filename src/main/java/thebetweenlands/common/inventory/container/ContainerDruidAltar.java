package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.slot.SlotDruidAltar;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.tile.TileEntityDruidAltar;

public class ContainerDruidAltar extends Container {
	private final TileEntityDruidAltar altar;
	
    public ContainerDruidAltar(PlayerInventory playerInventory, TileEntityDruidAltar tile) {
    	this.altar = tile;
    	
    	int numRows = 2;
        int i = (numRows - 4) * 18;

        this.addSlot(new SlotOutput(tile, 0, 81, 35, this));
        this.addSlot(new SlotDruidAltar(tile, 1, 53, 7));
        this.addSlot(new SlotDruidAltar(tile, 2, 109, 7));
        this.addSlot(new SlotDruidAltar(tile, 3, 53, 63));
        this.addSlot(new SlotDruidAltar(tile, 4, 109, 63));

        for (int j = 0; j < 3; j++)
            for (int k = 0; k < 9; k++)
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 120 + j * 18 + i));
        for (int j = 0; j < 9; j++)
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 178 + i));
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getStack();
            assert !stack1.isEmpty();
            stack = stack1.copy();
            if (slotIndex >= 5) {
                //Prevents wrong items from being shift-clicked into the slots
                if (DruidAltarRecipe.isValidItem(stack1)) {
                    if (!moveItemStackTo(stack1, 1, 5, false))
                        return ItemStack.EMPTY;
                } else {
                    //Moves items from hotbar to inventory and vice versa
                    if (slotIndex < 32) {
                        if (!moveItemStackTo(stack1, 32, 41, false))
                            return ItemStack.EMPTY;
                    } else {
                        if (!moveItemStackTo(stack1, 5, 31, false))
                            return ItemStack.EMPTY;
                    }
                }
            } else if (!moveItemStackTo(stack1, 5, inventorySlots.size(), false))
                return ItemStack.EMPTY;
            if (stack1.getCount() == 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.setChanged();
            if (stack1.getCount() != stack.getCount())
                slot.onTake(player, stack1);
            else
                return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.altar.stillValid(player);
    }
}
