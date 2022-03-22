package thebetweenlands.common.block.structure;

import net.minecraft.block.DoorBlock;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;

public abstract class BlockDoorBetweenlands extends DoorBlock implements ICustomItemBlock, IStateMappedBlock
{
	public BlockDoorBetweenlands(Properties properties) {
		super(properties);
	}
}
