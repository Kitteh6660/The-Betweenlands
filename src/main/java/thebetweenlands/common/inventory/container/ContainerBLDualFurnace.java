package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.FurnaceResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import thebetweenlands.common.inventory.slot.SlotBLFurnaceFuel;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;

public class ContainerBLDualFurnace extends ContainerAbstractBLFurnace {

	public ContainerBLDualFurnace(PlayerInventory inventory, TileEntityBLDualFurnace tile) {
		super(tile);

		this.addSlot(new Slot(tile, 0, 56, 21));
		this.addSlot(new SlotBLFurnaceFuel(tile, 1, 56, 57));
		this.addSlot(new FurnaceResultSlot(inventory.player, tile, 2, 116, 39));
		this.addSlot(new SlotRestriction(tile, 3, 26, 39, new ItemStack(ItemRegistry.LIMESTONE_FLUX.get()), 64, this));

		this.addSlot(new Slot(tile, 4, 56, 92));
		this.addSlot(new SlotBLFurnaceFuel(tile, 5, 56, 128));
		this.addSlot(new FurnaceResultSlot(inventory.player, tile, 6, 116, 110));
		this.addSlot(new SlotRestriction(tile, 7, 26, 110, new ItemStack(ItemRegistry.LIMESTONE_FLUX.get()), 64, this));

		int i;
		for (i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 163 + i * 18));

		for (i = 0; i < 9; ++i)
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 221));
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex == 2 || slotIndex == 6) {
				if (!moveItemStackTo(itemstack1, 8, 44, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (slotIndex != 1 && slotIndex != 0 && slotIndex != 3 && slotIndex != 4 && slotIndex != 5 && slotIndex != 7) {
				if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
					if (!moveItemStackTo(itemstack1, 0, 1, false) && !moveItemStackTo(itemstack1, 4, 5, false))
						return ItemStack.EMPTY;
				}
				else if (AbstractFurnaceTileEntity.isFuel(itemstack1) || itemstack1.getItem() instanceof ItemMisc && itemstack.getItem() == ItemRegistry.SULFUR.get()) {
					if (!moveItemStackTo(itemstack1, 1, 2, false) && !moveItemStackTo(itemstack1, 5, 6, false))
						return ItemStack.EMPTY;
				}
				 else if (itemstack1.getItem() instanceof ItemMisc && itemstack.getItem() == ItemRegistry.LIMESTONE_FLUX.get()) {
	                    if (!moveItemStackTo(itemstack1, 3, 4, false) && !moveItemStackTo(itemstack1, 7, 8, false))
	                        return ItemStack.EMPTY;
	                }
				else if (slotIndex >= 8 && slotIndex < 35) {
					if (!moveItemStackTo(itemstack1, 35, 44, false))
						return ItemStack.EMPTY;
				}
				else if (slotIndex >= 35 && slotIndex < 44 && !moveItemStackTo(itemstack1, 8, 35, false))
					return ItemStack.EMPTY;
			}
			else if (!moveItemStackTo(itemstack1, 8, 44, false))
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
