package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.function.Function;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

/**
 * Prevents entities from spawning in caves.
 */
public class SurfaceSpawnEntry extends BLSpawnEntry {
	private boolean canSpawnOnWater = false;
	private boolean canSpawnInWater = false;
	private Predicate<BlockState> surfaceBlockPredicate = new Predicate<BlockState>() {
		@Override
		public boolean apply(BlockState input) {
			return SurfaceType.MIXED_GROUND.matches(input);
		}
	};

	public SurfaceSpawnEntry(int id, Class<? extends MobEntity> entityType, Function<World, ? extends MobEntity> entityCtor) {
		super(id, entityType, entityCtor);
	}

	public SurfaceSpawnEntry(int id, Class<? extends MobEntity> entityType, Function<World, ? extends MobEntity> entityCtor, short weight) {
		super(id, entityType, entityCtor, weight);
	}

	public SurfaceSpawnEntry setSurfacePredicate(Predicate<BlockState> surfacePredicate) {
		this.surfaceBlockPredicate = surfacePredicate;
		return this;
	}

	public SurfaceSpawnEntry setCanSpawnOnWater(boolean spawnOnWater) {
		this.canSpawnOnWater = spawnOnWater;
		return this;
	}

	public SurfaceSpawnEntry setCanSpawnInWater(boolean spawnInWater) {
		this.canSpawnInWater = spawnInWater;
		return this;
	}

	@Override
	public void update(World world, BlockPos pos) {
		int caveHeight = WorldProviderBetweenlands.CAVE_START;
		if(pos.getY() <= caveHeight) {
			this.setWeight((short) 0);
		} else {
			this.setWeight(this.getBaseWeight());
		}
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, BlockState spawnBlockState, BlockState surfaceBlockState) {
		return !spawnBlockState.isNormalCube() &&
				((this.surfaceBlockPredicate.apply(surfaceBlockState) && !spawnBlockState.getMaterial().isLiquid()) ||
				(this.canSpawnInWater && spawnBlockState.getMaterial().isLiquid()) || 
				(this.canSpawnOnWater && surfaceBlockState.getMaterial().isLiquid() && !spawnBlockState.getMaterial().isLiquid()));
	}
}
