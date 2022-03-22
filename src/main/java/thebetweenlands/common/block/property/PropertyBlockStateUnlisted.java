package thebetweenlands.common.block.property;

import net.minecraft.block.BlockState;

public class PropertyBlockStateUnlisted extends UnlistedPropertyHelper<BlockState> {
	
	public PropertyBlockStateUnlisted(String name) {
		super(name);
	}

	@Override
	public Class<BlockState> getType() {
		return BlockState.class;
	}
}