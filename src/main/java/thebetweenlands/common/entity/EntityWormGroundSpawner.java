package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;
import thebetweenlands.common.entity.mobs.EntitySludgeWorm;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EntityWormGroundSpawner extends EntityCCGroundSpawner {

	public EntityWormGroundSpawner(World world) {
		super(world);
		setSize(3F, 0.5F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
	}

	@Override
	public boolean isSpawnEventActive(World world) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
        if(worldStorage.getEnvironmentEventRegistry().heavyRain.isActive())
            return true;
        return false;
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		if (!level.isClientSide()) {
			if(getCanBeRemovedSafely() && canBeRemovedNow())
				remove();
			if (level.getDifficulty() != EnumDifficulty.PEACEFUL) {
				if(isWorldSpawned() && !isSpawnEventActive(level))
					return null;
				List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, proximityBox());
				if(list.stream().filter(e -> e instanceof EntitySludgeWorm).count() >= 4)
					return null;
				for (int entityCount = 0; entityCount < list.size(); entityCount++) {
					Entity entity = list.get(entityCount);
					if (entity != null)
						if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isSpectator() && !((PlayerEntity) entity).isCreative()) {
							if (canSneakPast() && entity.isCrouching())
								return null;
							else if (checkSight() && !canEntityBeSeen(entity) || getCanBeRemovedSafely())
								return null;
							else {
								for (int count = 0; count < getEntitySpawnCount(); count++) {
									Entity spawn = getEntitySpawned();
									if (spawn != null) {
										performPreSpawnaction(entity, spawn);
										if (!spawn.isDead) // just in case of pre-emptive removal
											level.spawnEntity(spawn);
										performPostSpawnaction(entity, spawn);
									}
								}
							}
						}
				}
			}
		}
		return null;
	}

	@Override
    public boolean canBeRemovedNow() {
    	AxisAlignedBB dead_zone = getBoundingBox().grow(0D, 1D, 0D).offset(0D, -0.5D, 0D);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, dead_zone);
		if(list.stream().filter(e -> e instanceof EntitySludgeWorm).count() >= 1)
			return false;
        return true;
    }

	@Override
	protected Entity getEntitySpawned() {
		MobEntity worm = null;
		int rand = level.rand.nextInt(5);

		switch (rand) {
		case 0:
			worm = new EntityLargeSludgeWorm(level);
			break;
		case 1:
		case 2:
			worm  = new EntitySludgeWorm(level);
			break;
		case 3:
		case 4:
			worm  = new EntityTinySludgeWorm(level);
			break;
		}

		if(worm != null)
			((MobEntity) worm).onInitialSpawn(level.getDifficultyForLocation(getPosition()), null);
		return worm;
	}
	
	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(isWorldSpawned())
			setSpawnCount(getSpawnCount() + 1);
		level.playSound((PlayerEntity)null, getPosition(), getDigSound(), SoundCategory.HOSTILE, 0.5F, 1.0F);
		entitySpawned.setPosition(getPosition().getX() + 0.5F, getPosition().getY() - 0.0F, getPosition().getZ() + 0.5F);
	}

	@Override
	protected int maxUseCount() {
		return 8;
	}
}