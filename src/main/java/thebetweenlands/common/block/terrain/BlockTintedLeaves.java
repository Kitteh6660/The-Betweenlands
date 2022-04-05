package thebetweenlands.common.block.terrain;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.BiomeColors;
import thebetweenlands.common.block.ITintedBlock;

public class BlockTintedLeaves extends BlockLeavesBetweenlands implements ITintedBlock {
	
	public BlockTintedLeaves(Properties properties) {
		super(properties);
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getAverageFoliageColor(worldIn, pos) : FoliageColors.getDefaultColor();
	}
}
