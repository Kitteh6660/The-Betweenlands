package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.entity.mobs.EntityGreebling;

public class GreeblingSpawnEntry extends SurfaceSpawnEntry {
	private Direction facing = Direction.NORTH;

	public GreeblingSpawnEntry(int id, short weight) {
		super(id, EntityGreebling.class, EntityGreebling::new, weight);
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, BlockState spawnBlockState, BlockState surfaceBlockState) {
		if(super.canSpawn(world, chunk, pos, spawnBlockState, surfaceBlockState)) {
			List<Direction> facings = new ArrayList<>(4);
			facings.addAll(Arrays.asList(Direction.HORIZONTALS));

			Collections.shuffle(facings, world.rand);

			for(Direction facing : facings) {
				BlockPos offset = pos.offset(facing);
				if(world.isBlockLoaded(offset) && world.isEmptyBlock(offset) && world.isEmptyBlock(offset.below())) {
					this.facing = facing;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onSpawned(LivingEntity entity) {
		EntityGreebling greebling = (EntityGreebling) entity;
		greebling.setFacing(this.facing);
	}
}
