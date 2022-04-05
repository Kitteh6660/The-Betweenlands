package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityPurifier;

public class ContainerPurifier extends Container {
	
	protected TileEntityPurifier purifier;

	public ContainerPurifier(PlayerInventory inventory, TileEntityPurifier tileentity) {
		purifier = tileentity;

		addSlot(new SlotRestriction(tileentity, 0, 61, 54, EnumItemMisc.SULFUR.create(1), 64, this));
		addSlot(new Slot(tileentity, 1, 61, 14));
		addSlot(new SlotOutput(tileentity, 2, 121, 34, this));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = slots.get(slotIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			newStack = slotStack.copy();
			if (slotIndex == 2 && slotStack.getItem() instanceof ICorrodible && player instanceof ServerPlayerEntity) {
				AdvancementCriterionRegistry.PURIFY_TOOL.trigger((ServerPlayerEntity) player);
			}
			if (slotIndex > 2) {
				if (slotStack.getItem() == ItemRegistry.SULFUR.get()) {
					if (!moveItemStackTo(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!moveItemStackTo(slotStack, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(slotStack, 3, slots.size(), false))
				return ItemStack.EMPTY;
			if (slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
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
		purifier.sendGUIData(this, listener);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			purifier.sendGUIData(this, listener);
		}
	}

	@Override
	public void updateProgressBar(int id, int value) {
		purifier.getGUIData(id, value);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return this.purifier.stillValid(player);
	}
}
