package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public class ContainerDraetonBurner extends Container {

	private final EntityDraeton draeton;

	public ContainerDraetonBurner(PlayerInventory playerInventory, IInventory entityInventory, EntityDraeton draeton) {
		this.draeton = draeton;

		this.addSlot(new Slot(entityInventory, 0, 83, 85));

		int i = 37;

		for (int y = 0; y < 3; ++y)
			for (int x = 0; x < 9; ++x)
				this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 11 + x * 18, 104 + y * 18 + i));

		for (int x = 0; x < 9; ++x)
			this.addSlot(new Slot(playerInventory, x, 11 + x * 18, 162 + i));
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return this.draeton.getDistanceSq(player) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		ItemStack is = ItemStack.EMPTY;
		Slot slot = (Slot) slots.get(slotIndex);

		if (slot != null && slot.hasItem()) {
			ItemStack is1 = slot.getItem();
			is = is1.copy();

			if (slotIndex < 1) {
				if (!moveItemStackTo(is1, 1, this.slots.size(), false))
					return ItemStack.EMPTY;
			} else if (!moveItemStackTo(is1, 0, 1, false))
				return ItemStack.EMPTY;

			if (is1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return is;
	}
}