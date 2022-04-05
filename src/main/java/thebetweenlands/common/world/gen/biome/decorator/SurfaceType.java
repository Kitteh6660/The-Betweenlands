package thebetweenlands.common.world.gen.biome.decorator;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.terrain.CragrockBlock;
import thebetweenlands.common.registries.BlockRegistry;

public enum SurfaceType implements Predicate<BlockState> {
	GRASS(ImmutableList.of(
			BlockMatcher.forBlock(Blocks.GRASS),
			BlockMatcher.forBlock(Blocks.MYCELIUM),
			BlockMatcher.forBlock(BlockRegistry.SWAMP_GRASS.get()),
			BlockMatcher.forBlock(BlockRegistry.DEAD_GRASS.get())
			)),
	DIRT(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.SWAMP_DIRT.get()),
			BlockMatcher.forBlock(Blocks.DIRT),
			BlockMatcher.forBlock(BlockRegistry.MUD.get()),
			BlockMatcher.forBlock(BlockRegistry.COMPACTED_MUD.get()),
			BlockMatcher.forBlock(BlockRegistry.SLUDGY_DIRT.get()),
			BlockMatcher.forBlock(BlockRegistry.PEAT.get()),
			BlockMatcher.forBlock(BlockRegistry.COARSE_SWAMP_DIRT.get()),
			BlockMatcher.forBlock(BlockRegistry.SPREADING_SLUDGY_DIRT.get())
			)),
	SAND(ImmutableList.of(
			BlockMatcher.forBlock(Blocks.SAND),
			BlockMatcher.forBlock(BlockRegistry.SILT.get())
			)),
	WATER(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.SWAMP_WATER.get()),
			BlockMatcher.forBlock(Blocks.WATER),
			BlockMatcher.forBlock(Blocks.FLOWING_WATER)
			)),
	PEAT(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.PEAT.get())
			)),
	MIXED_GROUND(ImmutableList.of(BlockMatcher.forBlock(BlockRegistry.CRAGROCK)), GRASS, DIRT, SAND, PEAT),
	UNDERGROUND(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE.get()),
			BlockMatcher.forBlock(BlockRegistry.PITSTONE.get()),
			BlockMatcher.forBlock(BlockRegistry.LIMESTONE.get()),
			BlockMatcher.forBlock(BlockRegistry.OCTINE_ORE.get()),
			BlockMatcher.forBlock(BlockRegistry.SCABYST_ORE.get()),
			BlockMatcher.forBlock(BlockRegistry.SLIMY_BONE_ORE.get()),
			BlockMatcher.forBlock(BlockRegistry.SULFUR_ORE.get()),
			BlockMatcher.forBlock(BlockRegistry.SYRMORITE_ORE.get()),
			BlockMatcher.forBlock(BlockRegistry.VALONITE_ORE.get())
			)),
	GRASS_AND_DIRT(GRASS, DIRT),
	MIXED_GROUND_AND_UNDERGROUND(MIXED_GROUND, UNDERGROUND),
	MIXED_GROUND_OR_REPLACEABLE(ImmutableList.of(state -> state.getMaterial().isReplaceable()), MIXED_GROUND),
	CRAGROCK_MOSSY(ImmutableList.of(state -> state.getBlock() == BlockRegistry.MOSSY_CRAGROCK_TOP.get() && state.getBlock() == BlockRegistry.MOSSY_CRAGROCK_BOTTOM.get())),
	PLANT_DECORATION_SOIL(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.GIANT_ROOT.get())),
			GRASS_AND_DIRT, CRAGROCK_MOSSY);

	private final List<Predicate<BlockState>> matchers;
	private final SurfaceType types[];

	private SurfaceType(@Nullable List<Predicate<BlockState>> matchers, SurfaceType... types) {
		this.matchers = matchers;
		this.types = types;
	}

	private SurfaceType(SurfaceType... types) {
		this(null, types);
	}

	@Override
	public boolean apply(BlockState input) {
		if(input == null)
			return false;
		if(this.types != null && this.types.length > 0){
			for(SurfaceType type : this.types)
				if(type.apply(input))
					return true;
		}
		if(this.matchers != null) {
			for(Predicate<BlockState> matcher : this.matchers) {
				if(matcher.apply(input))
					return true;
			}
		}
		return false;
	}

	public boolean matches(World world, BlockPos pos) {
		return world.isLoaded(pos) && this.apply(world.getBlockState(pos));
	}

	public boolean matches(BlockState state) {
		return this.apply(state);
	}
}
