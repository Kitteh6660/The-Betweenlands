package thebetweenlands.common.tile;

import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntityType;
import thebetweenlands.common.registries.TileEntityRegistry;

public class BLSignTileEntity extends SignTileEntity {

	public BLSignTileEntity() {
		super();
	}
	
	@Override
	public TileEntityType<?> getType() {
		return TileEntityRegistry.BL_SIGN.get();
	}
	
}
