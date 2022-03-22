package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.tile.TileEntityBLFurnace;

public class ContainerDraetonFurnace extends ContainerBLFurnace {
	private final EntityDraeton draeton;
	private final int index;
	
	public ContainerDraetonFurnace(PlayerEntity player, PlayerInventory inventory, TileEntityBLFurnace tile, EntityDraeton draeton, int index) {
		super(inventory, tile);
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
