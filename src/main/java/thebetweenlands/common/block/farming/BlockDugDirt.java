package thebetweenlands.common.block.farming;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class BlockDugDirt extends BlockGenericDugSoil {
	public BlockDugDirt(boolean purified) {
		super(Material.GROUND, purified);
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

		if(!world.isClientSide()) {
			TileEntityDugSoil te = getTile(world, pos);

			if(te != null && !te.isComposted() && rand.nextInt(20) == 0) {
				if(this.isPurified(world, pos, state)) {
					world.setBlockState(pos, BlockRegistry.PURIFIED_SWAMP_DIRT.defaultBlockState());
				} else {
					world.setBlockState(pos, BlockRegistry.SWAMP_DIRT.defaultBlockState());
				}
			}
		}
	}

	@Override
	public BlockState getUnpurifiedDugSoil(World world, BlockPos pos, BlockState state) {
		return BlockRegistry.DUG_SWAMP_DIRT.defaultBlockState().setValue(COMPOSTED, state.getValue(COMPOSTED)).setValue(DECAYED, state.getValue(DECAYED));
	}
}
