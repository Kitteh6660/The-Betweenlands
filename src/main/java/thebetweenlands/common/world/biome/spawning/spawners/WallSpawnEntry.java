package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;

public class WallSpawnEntry extends BLSpawnEntry {
	public WallSpawnEntry(int id, Class<? extends MobEntity> entityType, Function<World, ? extends MobEntity> entityCtor, short weight) {
		super(id, entityType, entityCtor, weight);
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, BlockState blockState, BlockState surfaceBlockState) {
		return blockState.isNormalCube();
	}
}
