package thebetweenlands.common.block;

import net.minecraft.block.BlockState;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class SoilHelper {
	
	//TODO: Replace the list with JSON tags.
	public static boolean canSustainPlant(BlockState state) {
		return SurfaceType.GRASS.matches(state) || SurfaceType.DIRT.matches(state) || state.getBlock() instanceof BlockGenericDugSoil || state.is(BLBlockTags.BETWEENLANDS_SOIL);
	}

	public static boolean canSustainUnderwaterPlant(BlockState state) {
		return state.getBlock() == BlockRegistry.MUD.get() || canSustainPlant(state);
	}

	public static boolean canSustainCrop(BlockState state) {
		return state.getBlock() instanceof BlockGenericDugSoil && (state.getValue(BlockGenericDugSoil.COMPOSTED) || state.getValue(BlockGenericDugSoil.DECAYED));
	}
}
