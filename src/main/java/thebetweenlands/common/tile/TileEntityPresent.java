package thebetweenlands.common.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntityPresent extends TileEntityLootInventory {
	
	public TileEntityPresent() {
		super(TileEntityRegistry.PRESENT.get(), 3, "container.bl.present");
	}
}
