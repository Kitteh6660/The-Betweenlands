package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

public class WorldGenLakeCavernSimulacrum extends WorldGenSimulacrum {
	
	public WorldGenLakeCavernSimulacrum() {
		super(ImmutableList.of(BlockRegistry.SIMULACRUM_LAKE_CAVERN), LootTableRegistry.LAKE_CAVERN_SIMULACRUM_OFFERINGS);
	}

	@Override
	protected boolean canGenerateHere(World world, Random rand, BlockPos pos) {
		if(world.getBlockState(pos.below()).getBlock() != BlockRegistry.PITSTONE) {
			return false;
		}

		for(Direction offset : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(offset, 3);

			if(world.getBlockState(offsetPos.below()).getMaterial() == Material.WATER || world.getBlockState(offsetPos.below(2)).getMaterial() == Material.WATER) {
				return true;
			}
		}

		return false;
	}
}
