package thebetweenlands.api.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IRingOfGatheringMinion {
	/**
	 * Called when the entity is being returned from the ring.
	 * This entity is not yet spawned in the world. If the entity needs to be
	 * spawned in the world then it needs to be done in this method.
	 * @param user Entity using the ring to return this entity
	 * @param nbt NBT returned by {@link #returnToRing(UUID)}
	 * @return
	 */
	public boolean returnFromRing(Entity user, CompoundNBT nbt);

	/**
	 * Called when the entity should be teleported back to the user
	 * @param user
	 * @return
	 */
	public default void returnToCall(Entity user) {
		((Entity) this).setPos(user.getX(), user.getY(), user.getZ());
	}

	/**
	 * Called when the entity is returned to the ring.
	 * No additional data of the entity is saved, only the NBT that is returned
	 * by this method.
	 * @param userId
	 * @return
	 */
	public default CompoundNBT returnToRing(UUID userId) {
		return new CompoundNBT();
	}

	@Nullable
	public UUID getRingOwnerId();

	public default boolean shouldReturnOnDeath(boolean isOwnerLoggedIn) {
		return true;
	}

	public default boolean shouldReturnOnUnload(boolean isOwnerLoggedIn) {
		//Don't kill if player has logged out, causing the chunks to unload
		return isOwnerLoggedIn;
	}

	public default boolean shouldReturnOnCall() {
		if(this instanceof TameableEntity && ((TameableEntity)this).isOrderedToSit()) {
			return false;
		}
		return true;
	}

	/**
	 * Whether this entity can only be returned by an animator.
	 * Default is true if entity is dead.
	 * @return
	 */
	public default boolean isRespawnedByAnimator() {
		return !((Entity) this).isAlive();
	}

	public default int getAnimatorLifeCrystalCost() {
		return 24;
	}

	public default int getAnimatorSulfurCost() {
		return 16;
	}
}
