package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.BiomeColorHelper;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockTintedLeaves extends BlockLeavesBetweenlands implements ITintedBlock {
	@Override
	public int getColorMultiplier(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	}
}
