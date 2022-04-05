package thebetweenlands.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class InventoryCustomCraftResult extends CraftResultInventory {
	
	private final TileEntity tile;
	
	private final Container eventHandler;
	
	private boolean recursing = false;
	
	public InventoryCustomCraftResult(TileEntity tile, @Nullable Container eventHandler) {
		this.tile = tile;
		this.eventHandler = eventHandler;
	}

	@Override
	public void setChanged() {
		this.tile.setChanged();
		BlockState state = this.tile.getLevel().getBlockState(this.tile.getBlockPos());
		this.tile.getLevel().sendBlockUpdated(this.tile.getBlockPos(), state, state, 3);
	}
	
	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack result = super.removeItem(index, count);
		if(!this.recursing && this.eventHandler != null) {
			try {
				this.recursing = true;
				//this.eventHandler.onCraftMatrixChanged(this);
			} finally {
				this.recursing = false;
			}
		}
		return result;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack result = super.removeItemNoUpdate(index);
		if(!this.recursing && this.eventHandler != null) {
			try {
				this.recursing = true;
				//this.eventHandler.onCraftMatrixChanged(this);
			} finally {
				this.recursing = false;
			}
		}
		return result;
	}
	
	@Override
	public void setItem(int index, ItemStack stack) {
		super.setItem(index, stack);
		if(!this.recursing && this.eventHandler != null) {
			try {
				this.recursing = true;
				//this.eventHandler.onCraftMatrixChanged(this);
			} finally {
				this.recursing = false;
			}
		}
	}
}
