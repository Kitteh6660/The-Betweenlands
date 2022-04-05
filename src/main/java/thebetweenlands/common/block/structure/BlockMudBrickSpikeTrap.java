package thebetweenlands.common.block.structure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.tile.TileEntityMudBricksSpikeTrap;

public class BlockMudBrickSpikeTrap extends BlockSpikeTrap {

	public BlockMudBrickSpikeTrap(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new TileEntityMudBricksSpikeTrap();
	}
}