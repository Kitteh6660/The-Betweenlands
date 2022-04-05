package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class SlotPassthroughCraftingInput extends SlotCrafting {
	
	private final PlayerEntity player;
	private final CraftingInventory craftMatrix;

	private final IInventory resultInventory;
	private final IInventory persistentInventory;

	private final Container eventHandler;
	
	private boolean craftingTake;

	public SlotPassthroughCraftingInput(PlayerEntity player, CraftingInventory craftingMatrix, CraftingResultSlot resultInventory, int slotIndex, int xPosition, int yPosition, IInventory persistentInventory, Container eventHandler) {
		super(player, craftingMatrix, new InventoryCraftResult() {
			@Override
			public ITextComponent getName() {
				return resultInventory.getName();
			}

			@Override
			public boolean hasCustomName() {
				return resultInventory.hasCustomName();
			}

			@Override
			public ITextComponent getDisplayName() {
				return resultInventory.getDisplayName();
			}

			@Override
			public int getContainerSize() {
				return resultInventory.getContainerSize();
			}

			@Override
			public boolean isEmpty() {
				return resultInventory.isEmpty() && this.getItem(slotIndex).isEmpty();
			}

			@Override
			public ItemStack getItem(int index) {
				ItemStack persistent = persistentInventory.getItem(slotIndex);
				if(!persistent.isEmpty()) {
					return persistent;
				}
				return resultInventory.getItem(index);
			}

			@Override
			public ItemStack removeItem(int index, int count) {
				ItemStack persistent = persistentInventory.getItem(slotIndex);
				if(!persistent.isEmpty()) {
					if(count > persistent.getCount()) {
						persistentInventory.setItem(slotIndex, ItemStack.EMPTY);
						eventHandler.onCraftMatrixChanged(this);
						return persistent;
					} else {
						ItemStack result = persistent.split(count);
						persistentInventory.setItem(slotIndex, persistent);
						eventHandler.onCraftMatrixChanged(this);
						return result;
					}
				}

				ItemStack result = resultInventory.removeItem(index, count);

				if(result.getCount() > count) {
					//Excess number of items extracted, store rest in persistent inventory
					ItemStack corrected = result.split(count);
					persistentInventory.setItem(slotIndex, result);
					eventHandler.onCraftMatrixChanged(this);
					return corrected;
				}

				return result;
			}

			@Override
			public ItemStack removeStackFromSlot(int index) {
				ItemStack persistent = persistentInventory.getItem(slotIndex);
				if(!persistent.isEmpty()) {
					persistentInventory.setItem(slotIndex, ItemStack.EMPTY);
					eventHandler.onCraftMatrixChanged(this);
					return persistent;
				}
				return resultInventory.removeStackFromSlot(index);
			}

			@Override
			public void setItem(int index, ItemStack stack) {
				resultInventory.setItem(index, stack);
			}

			@Override
			public int getMaxStackSize() {
				return resultInventory.getMaxStackSize();
			}

			@Override
			public void setChanged() {
				resultInventory.setChanged();
			}

			@Override
			public boolean stillValid(PlayerEntity player) {
				return resultInventory.stillValid(player);
			}

			@Override
			public void startOpen(PlayerEntity player) {
				resultInventory.startOpen(player);
			}

			@Override
			public void stopOpen(PlayerEntity player) {
				resultInventory.stopOpen(player);
			}

			@Override
			public boolean canPlaceItem(int index, ItemStack stack) {
				return resultInventory.canPlaceItem(index, stack);
			}

			@Override
			public int getField(int id) {
				return 0;
			}

			@Override
			public void setField(int id, int value) {
			}

			@Override
			public int getFieldCount() {
				return 0;
			}

			@Override
			public void clear() {
				persistentInventory.setItem(slotIndex, ItemStack.EMPTY);
				resultInventory.clear();
			}
		}, slotIndex, xPosition, yPosition);

		this.player = player;
		this.craftMatrix = craftingMatrix;
		this.resultInventory = resultInventory;
		this.persistentInventory = persistentInventory;
		this.eventHandler = eventHandler;
		
		this.craftingTake = !this.hasPersistentItem();
	}

	public boolean hasPersistentItem() {
		return !this.persistentInventory.getItem(this.getSlotIndex()).isEmpty();
	}

	@Override
	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		this.onCrafting(stack);

		if(this.craftingTake) {
			this.craftingTake = false;

			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
			NonNullList<ItemStack> remainingStacks = CraftingManager.getRemainingItems(this.craftMatrix, thePlayer.world);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

			for(int i = 0; i < remainingStacks.size(); ++i) {
				ItemStack currentStack = this.craftMatrix.getItem(i);
				ItemStack remainingStack = remainingStacks.get(i);

				if(!currentStack.isEmpty()) {
					this.craftMatrix.removeItem(i, 1);
					currentStack = this.craftMatrix.getItem(i);
				}

				if(!remainingStack.isEmpty()) {
					if(currentStack.isEmpty()) {
						this.craftMatrix.setItem(i, remainingStack);
					} else if(ItemStack.areItemsEqual(currentStack, remainingStack) && ItemStack.areItemStackTagsEqual(currentStack, remainingStack)) {
						remainingStack.inflate(currentStack.getCount());
						this.craftMatrix.setItem(i, remainingStack);
					} else if(!this.player.inventory.add(remainingStack)) {
						this.player.drop(remainingStack, false);
					}
				}
			}
		}

		if(!this.hasPersistentItem()) {
			this.craftingTake = true;
			this.craftMatrix.eventHandler.onCraftMatrixChanged(this.craftMatrix);
		}

		return stack;
	}

	@Override
	public void putStack(ItemStack stack) {
		//On server side this is only called when the player puts something in the slot
		if(!this.player.level.isClientSide()) {
			//Store stack persistently
			this.persistentInventory.setItem(this.getSlotIndex(), stack);
			this.eventHandler.onCraftMatrixChanged(this.inventory);
			this.setChanged();
		} else {
			super.putStack(stack);
		}
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		//Allow putting in stacks if slot is empty
		return this.inventory.getItem(0).isEmpty();
	}
}
