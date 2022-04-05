package thebetweenlands.common.inventory.container;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.slot.SlotPouch;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ContainerPouch extends Container {
	@Nullable
	private final InventoryItem inventory;

	private int numRows = 3;

	/**
	 * Creates a new lurker skin pouch container
	 * @param player The player that opened the inventory
	 * @param playerInventory The player's inventory
	 * @param itemInventory The item inventory, null if the renaming GUI was opened
	 */
	public ContainerPouch(PlayerEntity player, PlayerInventory playerInventory, @Nullable InventoryItem itemInventory) {
		this.inventory = itemInventory;

		if(this.inventory == null || this.inventory.isEmpty()) {
			return;
		}

		this.numRows = this.inventory.getContainerSize() / 9;
		int yOffset = (this.numRows - 4) * 18;

		for (int row = 0; row < this.numRows; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.this.addSlot(new SlotPouch(itemInventory, column + row * 9, 8 + column * 18, 18 + row * 18));
			}
		}

		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + yOffset));
			}
		}

		for (int column = 0; column < 9; ++column) {
			this.this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 161 + yOffset));
		}
	}

	public InventoryItem getItemInventory() {
		return this.inventory;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		if(this.inventory == null) {
			return true; //Renaming pouch
		}

		if(this.getItemInventory().getInventoryItemStack().isEmpty()) {
			return false;
		}

		//Check if pouch is in equipment
		IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if (cap != null) {
			IInventory inv = cap.getInventory(EnumEquipmentInventory.MISC);

			for (int i = 0; i < inv.getContainerSize(); i++) {
				if (inv.getItem(i) == this.inventory.getInventoryItemStack()) {
					return true;
				}
			}
		}

		//Check if pouch is in main inventory
		for(int i = 0; i < player.inventory.getContainerSize(); i++) {
			if(player.inventory.getItem(i) == this.inventory.getInventoryItemStack()) {
				return true;
			}
		}

		//Check if pouch is in draeton
		List<EntityDraeton> draetons = player.world.getEntitiesOfClass(EntityDraeton.class, player.getBoundingBox().inflate(6));
		for(EntityDraeton dreaton : draetons) {
			if(player.getDistanceSq(dreaton) <= 64.0D) {
				IInventory inv = dreaton.getUpgradesInventory();
				for(int i = 0; i < inv.getContainerSize(); i++) {
					if(inv.getItem(i) == this.inventory.getInventoryItemStack()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);

		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (slotStack.getItem() instanceof ItemLurkerSkinPouch) {
				return ItemStack.EMPTY;
			}

			if (slotIndex < this.numRows * 9) {
				if (!moveItemStackTo(slotStack, this.numRows * 9, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(slotStack, 0, this.numRows * 9, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return stack;
	}
}