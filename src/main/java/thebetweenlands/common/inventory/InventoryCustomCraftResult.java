package thebetweenlands.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class InventoryCustomCraftResult extends InventoryCraftResult {
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
		BlockState state = this.tile.getWorld().getBlockState(this.tile.getPos());
		this.tile.getWorld().sendBlockUpdated(this.tile.getPos(), state, state, 3);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack result = super.decrStackSize(index, count);
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
	public ItemStack removeStackFromSlot(int index) {
		ItemStack result = super.removeStackFromSlot(index);
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
