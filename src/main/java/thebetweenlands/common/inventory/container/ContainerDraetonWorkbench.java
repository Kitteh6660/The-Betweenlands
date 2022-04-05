package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public class ContainerDraetonWorkbench extends ContainerWorkbench {
	private final EntityDraeton draeton;
	private final int slot;

	public ContainerDraetonWorkbench(PlayerInventory playerInventory, EntityDraeton draeton, int slot) {
		super(playerInventory, draeton.getWorld(), BlockPos.ZERO);
		this.draeton = draeton;
		this.slot = slot;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		if(playerIn.getDistanceSq(draeton) <= 64.0D) {
			IInventory inv = this.draeton.getUpgradesInventory();
			if(this.slot >= 0 && this.slot < inv.getContainerSize()) {
				ItemStack stack = inv.getItem(this.slot);
				if(!stack.isEmpty() && this.draeton.isCraftingUpgrade(stack)) {
					return true;
				}
			}
		}
		return false;
	}
}
