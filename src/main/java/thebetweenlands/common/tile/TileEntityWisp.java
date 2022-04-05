package thebetweenlands.common.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityWisp extends TileEntity {
	
	public TileEntityWisp(TileEntityType<?> te) {
		super(te);
	}

	public long lastSpawn = 0;
}
