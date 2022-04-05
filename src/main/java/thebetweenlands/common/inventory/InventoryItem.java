package thebetweenlands.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;

public class InventoryItem implements IInventory {
	
	private String name = "";
	private final ItemStack invItem;
	private NonNullList<ItemStack> inventory;

	public InventoryItem(ItemStack stack, int inventorySize, String inventoryName) {
		this.invItem = stack;
		this.inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
		this.name = inventoryName;
		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
		}
		this.load(stack.getTag());
	}
	
	public ItemStack getInventoryItemStack() {
		return this.invItem;
	}

	@Override
	public int getContainerSize() {
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack stack = getItem(slot);
		if(!stack.isEmpty()) {
			if(stack.getCount() > amount) {
				stack = stack.split(amount);
				this.setChanged();
			} else {
				this.setItem(slot, ItemStack.EMPTY);
			}
		}
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		for (int i = 0; i < this.getContainerSize(); ++i) {
			if (!this.getItem(i).isEmpty() && this.getItem(i).getCount() == 0) {
				this.inventory.set(i, ItemStack.EMPTY);
			}
		}
		this.save(this.invItem.getTag());
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack itemstack) {
		return !(itemstack.getItem() instanceof ItemLurkerSkinPouch);
	}

	public void load(CompoundNBT compound) {
		/*ListNBT items = compound.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < items.size(); ++i) {
			CompoundNBT item = items.getCompound(i);
			int slot = item.getInt("Slot");
			if (slot >= 0 && slot < this.getContainerSize()) {
				this.inventory[slot] = new ItemStack(item);
			}
		}*/
		inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, inventory);
	}

	public void save(CompoundNBT tagcompound) {
		ItemStackHelper.saveAllItems(tagcompound, inventory, true);
		/*ListNBT items = new ListNBT();

		for (int i = 0; i < this.getContainerSize(); ++i) {
			if (this.getItem(i) != null) {
				CompoundNBT item = new CompoundNBT();
				item.putInt("Slot", i);
				getItem(i).save(item);
				items.appendTag(item);
			}
		}

		tagcompound.setTag("ItemInventory", items);*/
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean hasCustomName() {
		return this.name.length() > 0;
	}

	@Override
	public ITextComponent getDisplayName() {
		return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TranslationTextComponent(this.getName(), new Object[0]));
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = this.getItem(slot);
		this.setItem(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void startOpen(PlayerEntity player) { }

	@Override
	public void stopOpen(PlayerEntity player) { }

	@Override
	public void clearContent() {
		for(int i = 0; i < this.getContainerSize(); i++) {
			this.setItem(i, ItemStack.EMPTY);
		}
	}

}
