package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import thebetweenlands.common.inventory.slot.SlotBLFurnaceFuel;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.tile.TileEntityBLFurnace;

public class ContainerBLFurnace extends ContainerAbstractBLFurnace {

    public ContainerBLFurnace(PlayerInventory inventory, TileEntityBLFurnace tile) {
        super(tile);

        this.addSlot(new Slot(tile, 0, 56, 17));
        this.addSlot(new SlotBLFurnaceFuel(tile, 1, 56, 53));
        this.addSlot(new SlotFurnaceOutput(inventory.player, tile, 2, 116, 35));
        this.addSlot(new SlotRestriction(tile, 3, 26, 35, EnumItemMisc.LIMESTONE_FLUX.create(1), 64, this));

        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
    }

	@Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex == 2) {
                if (!moveItemStackTo(itemstack1, 3, 39, true))
                    return ItemStack.EMPTY;
                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotIndex != 1 && slotIndex != 0 && slotIndex != 3) {
                if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
                    if (!moveItemStackTo(itemstack1, 0, 1, false))
                        return ItemStack.EMPTY;
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1) || itemstack1.getItem() instanceof ItemMisc && itemstack.getDamageValue() == EnumItemMisc.SULFUR.getID()) {
                    if (!moveItemStackTo(itemstack1, 1, 2, false))
                        return ItemStack.EMPTY;
                }
                else if (itemstack1.getItem() instanceof ItemMisc && itemstack.getDamageValue() == EnumItemMisc.LIMESTONE_FLUX.getID()) {
                    if (!moveItemStackTo(itemstack1, 3, 4, false))
                        return ItemStack.EMPTY;
                }
                else if (slotIndex >= 3 && slotIndex <= 30) {
                    if (!moveItemStackTo(itemstack1, 31, 40, false))
                        return ItemStack.EMPTY;
                }
                else if (slotIndex >= 31 && slotIndex < 40 && !moveItemStackTo(itemstack1, 4, 30, false))
                    return ItemStack.EMPTY;
            }
            else if (!moveItemStackTo(itemstack1, 4, 39, false))
                return ItemStack.EMPTY;
            if (itemstack1.getCount() == 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }
}
