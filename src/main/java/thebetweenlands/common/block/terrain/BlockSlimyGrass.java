package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import thebetweenlands.common.registries.BlockRegistry;
import java.util.Random;

public class BlockSlimyGrass extends Block
{

	public BlockSlimyGrass(Properties properties) {
		super(properties);
		/*
		 * super(Material.GRASS); setHardness(0.5F); setSoundType(SoundType.PLANT);
		 * setHarvestLevel("shovel", 0); setCreativeTab(BLCreativeTabs.BLOCKS);
		 */
		// setBlockName("thebetweenlands.slimyGrass");
		// setTickRandomly(true);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if (!world.isClientSide()) {
			if (world.getMaxLocalRawBrightness(pos.above()) < 4 && world.getLightEmission(pos.above()) > 2) {
				world.setBlockAndUpdate(pos, BlockRegistry.SLIMY_DIRT.get().defaultBlockState());
			} else if (world.getLightEmission(pos.above()) >= 9) {
				for (int l = 0; l < 4; ++l) {
					BlockPos target = pos.offset(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

					if (world.getBlockState(target).getBlock() == Blocks.DIRT && world.getMaxLocalRawBrightness(target.above()) >= 4) {
						world.setBlockAndUpdate(target, BlockRegistry.SLIMY_GRASS.get().defaultBlockState());
					}
				}
			}
		}
	}
}
