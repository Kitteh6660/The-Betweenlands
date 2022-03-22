package thebetweenlands.common.block.structure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import thebetweenlands.common.tile.TileEntityMudTilesSpikeTrap;

public class BlockMudTilesSpikeTrap extends BlockSpikeTrap {

	public BlockMudTilesSpikeTrap() {
		super();
		setDefaultState(this.getBlockState().getBaseState().setValue(FACING, Direction.UP));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMudTilesSpikeTrap();
	}
}