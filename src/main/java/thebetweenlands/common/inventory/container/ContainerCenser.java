package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.tile.TileEntityCenser;

public class ContainerCenser extends Container {
	public static final int SLOT_FUEL = 0;
	public static final int SLOT_INPUT = 1;
	public static final int SLOT_INTERNAL = 2;

	protected TileEntityCenser censer;

	public ContainerCenser(PlayerInventory inventory, TileEntityCenser tileentity) {
		censer = tileentity;

		int yOffset = 91;

		this.addSlot(new SlotRestriction(tileentity, 0, 80, 48 + yOffset, EnumItemMisc.SULFUR.create(1), 64, this));
		this.addSlot(new Slot(tileentity, 1, 44, 12 + yOffset));
		this.addSlot(new Slot(tileentity, 2, 80, 12 + yOffset) {
			@Override
			@OnlyIn(Dist.CLIENT)
			public boolean isEnabled() {
				return false;
			}

			@Override
			public boolean canTakeStack(PlayerEntity playerIn) {
				return false;
			}

			@Override
			public ItemStack removeItem(int amount) {
				return ItemStack.EMPTY;
			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}

			@Override
			public void putStack(ItemStack stack) {
			}
		});

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
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		if(slotIndex == SLOT_INTERNAL) {
			return ItemStack.EMPTY;
		}
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getStack();
			newStack = slotStack.copy();
			if (slotIndex > 2) {
				if (EnumItemMisc.SULFUR.isItemOf(slotStack)) {
					if (!moveItemStackTo(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!moveItemStackTo(slotStack, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(slotStack, 3, inventorySlots.size(), false))
				return ItemStack.EMPTY;
			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (slotStack.getCount() != newStack.getCount())
				slot.onTake(player, slotStack);
			else
				return ItemStack.EMPTY;
		}
		return newStack;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		censer.sendGUIData(this, listener);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			censer.sendGUIData(this, listener);
		}
	}

	@Override
	public void updateProgressBar(int id, int value) {
		censer.receiveGUIData(id, value);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return this.censer.stillValid(player);
	}
}
