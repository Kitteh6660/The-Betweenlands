package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.inventory.slot.SlotSizeRestriction;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAnimator;


public class ContainerAnimator extends Container {

	public static class SlotLifeCrystal extends Slot {
		public SlotLifeCrystal(IInventory inventory, int slotIndex, int x, int y) {
	        super(inventory, slotIndex, x, y);
	    }

	    @Override
	    public boolean mayPlace(ItemStack stack) {
	        if (stack.getItem() instanceof ItemLifeCrystal)
	            return true;
	        return false;
	    }

	    @Override
		public int getMaxStackSize() {
	        return 1;
	    }
	}
	
    private final int numRows = 2;
    private TileEntityAnimator animator;

    public ContainerAnimator(PlayerInventory playerInventory, TileEntityAnimator tile) {
        super();
        int i = (numRows - 4) * 18;
        animator = tile;

        this.addSlot(new SlotSizeRestriction(tile, 0, 79, 23, 1));
        this.addSlot(new SlotLifeCrystal(tile, 1, 34, 57));
        this.addSlot(new SlotRestriction(tile, 2, 124, 57, new ItemStack(ItemRegistry.ITEMS_MISC, 1, ItemMisc.EnumItemMisc.SULFUR.getID()), 64, this));

        for (int j = 0; j < 3; j++)
            for (int k = 0; k < 9; k++)
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 7 + k * 18, 119 + j * 18 + i));
        for (int j = 0; j < 9; j++)
            this.addSlot(new Slot(playerInventory, j, 7 + j * 18, 177 + i));
    }


    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            if (slotIndex > 2) {
                if (stack1.getItem() == ItemRegistry.ITEMS_MISC && stack1.getDamageValue() == ItemMisc.EnumItemMisc.SULFUR.getID())
                    if (!moveItemStackTo(stack1, 2, 3, true))
                        return ItemStack.EMPTY;
                if (stack1.getItem() instanceof ItemLifeCrystal)
                    if (!moveItemStackTo(stack1, 1, 2, true))
                        return ItemStack.EMPTY;
                if (stack1.getCount() == 1 && stack1 != new ItemStack(ItemRegistry.ITEMS_MISC, 1, ItemMisc.EnumItemMisc.SULFUR.getID()) && stack1.getItem() instanceof ItemLifeCrystal == false)
                    if (!moveItemStackTo(stack1, 0, 1, true))
                        return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack1, 3, inventorySlots.size(), false))
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
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        animator.sendGUIData(this, listener);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : listeners)
            animator.sendGUIData(this, (IContainerListener) crafter);
    }

    @Override
	@OnlyIn(Dist.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        super.updateProgressBar(id, value);
        animator.getGUIData(id, value);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.animator.stillValid(player);
    }
}