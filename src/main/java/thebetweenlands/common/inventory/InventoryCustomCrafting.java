package thebetweenlands.common.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class InventoryCustomCrafting extends CraftingInventory {
	
	public static interface ICustomCraftingGridChangeHandler {
		public void onCraftMatrixChanged();

		public void startOpen(InventoryCustomCrafting inv);

		public void stopOpen(InventoryCustomCrafting inv);
	}

	private NonNullList<ItemStack> stackList;
	private final TileEntity tile;
	private final ICustomCraftingGridChangeHandler grid;
	private final String name;

	private boolean isBatchCrafting = false;
	private boolean batchCraftingGridChange = false;

	public InventoryCustomCrafting(Container eventHandler, ICustomCraftingGridChangeHandler tile, NonNullList<ItemStack> inventory, int width, int height, String name) {
		super(eventHandler, width, height);
		this.name = name;
		this.stackList = inventory;
		this.tile = (TileEntity)tile;
		this.grid = tile;
	}

	public void startBatchCrafting() {
		this.isBatchCrafting = true;
		this.batchCraftingGridChange = false;
	}

	public void stopBatchCrafting() {
		this.isBatchCrafting = false;

		if(this.batchCraftingGridChange) {
			this.batchCraftingGridChange = false;

			this.grid.onCraftMatrixChanged();
		}
	}

	@Override
	public int getContainerSize() {
		return this.stackList.size();
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.stackList.get(slot);
	}

	@Override
	public ItemStack getStackInRowAndColumn(int row, int col) {
		if (row >= 0 && row < this.getHeight()) {
			int k = row + col * this.getWidth();
			return this.getItem(k);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ITextComponent getName() {
		return this.name;
	}

	@Override
	public ITextComponent getDisplayName() {
		return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TranslationTextComponent(this.getName(), new Object[0]));
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack itemstack = ItemStackHelper.removeItem(this.stackList, slot, amount);

		if(!itemstack.isEmpty()) {
			this.batchCraftingGridChange = true;
			if(!this.isBatchCrafting) {
				this.grid.onCraftMatrixChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.stackList.set(slot, stack);
		this.batchCraftingGridChange = true;
		if(!this.isBatchCrafting) {
			this.grid.onCraftMatrixChanged();
		}
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		this.tile.setChanged();
		BlockState state = this.tile.getLevel().getBlockState(this.tile.getBlockPos());
		this.tile.getLevel().sendBlockUpdated(this.tile.getBlockPos(), state, state, 3);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public void startOpen(PlayerEntity player) {
		super.startOpen(player);

		this.grid.startOpen(this);
	}

	@Override
	public void stopOpen(PlayerEntity player) {
		super.stopOpen(player);

		this.grid.stopOpen(this);
	}
}
