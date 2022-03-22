package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.function.Function;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityGecko;

public class SporelingSpawnEntry extends TreeSpawnEntry {
	public SporelingSpawnEntry(int id, Class<? extends MobEntity> entityType, Function<World, ? extends MobEntity> entityCtor, short weight) {
		super(id, entityType, entityCtor, weight);
	}

	@Override
	public void onSpawned(LivingEntity entity) {
		if(entity.isEntityAlive() && entity.world.rand.nextInt(10) == 0) {
			EntityGecko gecko = new EntityGecko(entity.world);
			gecko.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.world.rand.nextFloat() * 360, 0);
			if(!entity.world.containsAnyLiquid(gecko.getBoundingBox()) && entity.world.getCollisionBoxes(gecko, gecko.getBoundingBox()).isEmpty()) {
				gecko.onInitialSpawn(entity.world.getDifficultyForLocation(new BlockPos(entity.getX(), entity.getY(), entity.getZ())), null);
				entity.world.spawnEntity(gecko);
				entity.startRiding(gecko);
			}
		}
	}
}
