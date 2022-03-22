package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.inventory.InventoryItem;

public class ContainerDraetonPouch extends ContainerPouch {
	private final EntityDraeton draeton;
	private final int index;

	public ContainerDraetonPouch(PlayerEntity player, PlayerInventory playerInventory, InventoryItem itemInventory, EntityDraeton draeton, int index) {
		super(player, playerInventory, itemInventory);
		this.draeton = draeton;
		this.index = index;
		draeton.openStorage(player, index);
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		this.draeton.closeStorage(playerIn, this.index);
	}
}
