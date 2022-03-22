package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Container;

public class ContainerItemNaming extends Container {
	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}
}
