package thebetweenlands.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;

public abstract class EntityProximitySpawner extends EntityCreature implements IEntityBL {

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(5.0D);
	}

	public EntityProximitySpawner(World world) {
		super(world);
	}
	/**
	 * Amount to extend proximity area in XZ axis
	 *
	 * @return amount to expand proximity box all around in the x & z axis.
	 */
	protected abstract float getProximityHorizontal();
	
	/**
	 * Amount to extend proximity area in Y axis
	 *
	 * @return amount to expand proximity box all around in the y axis.
	 */
	protected abstract float getProximityVertical();

	/**
	 * Test if entity can sneak past
	 *
	 * @return true to allow player to sneak past. false to deny it.
	 */
	protected abstract boolean canSneakPast();

	/**
	 * Test if entity needs line of sight to activate
	 *
	 * @return true for yes. false for no.
	 */
	protected abstract boolean checkSight();

	/**
	 * Which entity should be spawned on activation
	 *
	 * @return an Entity or null.
	 */
	@Nullable
	protected abstract Entity getEntitySpawned();

	/**
	 * How Many Entities should be spawned
	 *
	 * @return an int amount.
	 */
	protected abstract int getEntitySpawnCount();

	/**
	 * Test if spawner is just a single use 
	 *
	 * @return true to set dead after spawn. false to deny it.
	 */
	protected abstract boolean isSingleUse();

	/**
	 * How many spawns this does (NYI)
	 *
	 * @return amount of uses.
	 */
	protected abstract int maxUseCount();
	
	/**
	 * Action to happen just before entity spawns
	 *
	 * Can be used for setting Spawned Entities' position or attributes etc
	 * By default sets the spawned entity to the same pos as the proximity spawner was.
	 * Override to change.
	 */

	protected void performPreSpawnaction(@Nullable Entity targetEntity, @Nullable Entity entitySpawned) {
		if(entitySpawned != null)
			entitySpawned.setPosition(getPosition().getX() + 0.5F, getPosition().getY(), getPosition().getZ() + 0.5F);
	}

	/**
	 * Action to happen just after entity spawns
	 *
	 * Entity can be null
	 */

	protected void performPostSpawnaction(@Nullable Entity targetEntity, @Nullable Entity entitySpawned) { }
	
	/**
	 * The Proximity box used
	 *
	 * @return an AxisAlignedBB for the proximity area.
	 */

	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal());
	}

	/**
	 * Generic area checking code and spawning.
	 *
	 * @return returns a null :( - bad modder.
	 */
	@Nullable
	protected Entity checkArea() {
		if (!level.isClientSide() && level.getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				Entity entity = list.get(entityCount);
				if (entity != null)
					if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isSpectator() && !((PlayerEntity) entity).isCreative()) {
						if (canSneakPast() && entity.isCrouching())
							return null;
						else if (checkSight() && !canEntityBeSeen(entity))
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
							if (!isDead && isSingleUse())
								remove();
						}
					}
			}
		}
		return null;
	}

	@Override
    protected boolean canDespawn() {
        return false;
    }
}