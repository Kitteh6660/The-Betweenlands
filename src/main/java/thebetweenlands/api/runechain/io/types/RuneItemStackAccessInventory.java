package thebetweenlands.api.runechain.io.types;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class RuneItemStackAccessInventory implements IInventory {
	private final IRuneItemStackAccess access;

	public RuneItemStackAccessInventory(IRuneItemStackAccess access) {
		this.access = access;
	}

	@Override
	public void clearContent() {
		this.access.set(ItemStack.EMPTY);
	}

	@Override
	public void startOpen(PlayerEntity player) { }

	@Override
	public void stopOpen(PlayerEntity player) { }

	@Override
	public ItemStack removeItem(int index, int count) {
		if(index == 0) {
			return this.access.remove(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ITextComponent getName() {
		return "Rune Item Stack Access";
	}

	@Override
	public ITextComponent getHoverName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void setField(int id, int value) { }

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if(index == 0) {
			this.access.set(stack);
		}
	}

	@Override
	public ItemStack getItem(int index) {
		if(index == 0) {
			return this.access.get();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if(index == 0) {
			ItemStack result = this.access.get();

			if(this.access.set(ItemStack.EMPTY)) {
				return result;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isEmpty() {
		return this.access.get().isEmpty();
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		if(index == 0) {
			return this.access.mayPlace(stack);
		}
		return false;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}

	@Override
	public void setChanged() { }


}
