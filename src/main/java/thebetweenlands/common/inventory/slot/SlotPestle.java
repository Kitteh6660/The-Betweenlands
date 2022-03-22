package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.tools.ItemPestle;

public class SlotPestle extends Slot {

    public SlotPestle(IInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
        index = slotIndex;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof ItemPestle;
    }

    @Override
    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        if (!stack.isEmpty() && index == 1) {
            if(stack.getTag().getBoolean("active")) {
                stack.getTag().putBoolean("active", false);
            }
        }
        return super.onTake(thePlayer, stack);
    }
}