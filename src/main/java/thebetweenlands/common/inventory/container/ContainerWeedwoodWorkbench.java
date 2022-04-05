package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.inventory.InventoryCustomCraftResult;
import thebetweenlands.common.inventory.InventoryCustomCrafting;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class ContainerWeedwoodWorkbench extends ContainerWorkbench {
	private final TileEntityWeedwoodWorkbench tile;

	public ContainerWeedwoodWorkbench(PlayerInventory playerInventory, TileEntityWeedwoodWorkbench tile) {
		super(playerInventory, tile.getWorld(), tile.getPos());
		this.tile = tile;

		this.slots.clear();
		this.inventoryItemStacks.clear();

		this.craftMatrix = new InventoryCustomCrafting(this, tile, tile.getCraftingGrid(), 3, 3, "container.bl.weedwood_workbench");
		this.craftMatrix.startOpen(playerInventory.player);

		this.craftResult = new InventoryCustomCraftResult(tile, null);

		//Result
		this.this.addSlot(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

		//Crafting matrix
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				this.this.addSlot(new Slot(this.craftMatrix, x + y * 3, 30 + x * 18, 17 + y * 18));
			}
		}

		//Player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; ++x) {
			this.this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
		}

		tile.onCraftMatrixChanged();
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		BlockPos pos = this.tile.getPos();
		if(playerIn.world.getBlockEntity(pos) != this.tile) {
			return false;
		} else {
			return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);

		this.craftMatrix.stopOpen(playerIn);
	}
}
