package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.tile.TileEntityBarrel;

//TODO: Change this barrel to something else due to barrels being in vanilla 1.14+
public class ContainerBarrel extends Container {
	
	public static final int SLOT_FUEL = 0;
	public static final int SLOT_INPUT = 1;
	public static final int SLOT_INTERNAL = 2;

	protected TileEntityBarrel barrel;

	public ContainerBarrel(PlayerInventory inventory, TileEntityBarrel tileentity) {
		barrel = tileentity;

		int yOffset = 0;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yOffset));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142 + yOffset));
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if(index >= 27 && index < 36) {
				if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 0 && index < 27) {
				if (!this.moveItemStackTo(itemstack1, 27, 36, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if(itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

			if(index == 0) {
				playerIn.drop(itemstack2, false);
			}
		}

		return itemstack;
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		BlockPos pos = this.barrel.getBlockPos();
		if(playerIn.level.getBlockEntity(pos) != this.barrel) {
			return false;
		} else {
			return playerIn.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
		}
	}
}
