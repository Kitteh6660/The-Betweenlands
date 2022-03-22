package thebetweenlands.common.capability.base;

import net.minecraft.entity.player.ServerPlayerEntity;

public class EntityCapabilityTracker {
	private final EntityCapability<?, ?, ?> entityCapability;
	private final ServerPlayerEntity watcher;

	private boolean trackerReady = false;
	private int lastUpdate = 0;
	private boolean dirty = false;

	public EntityCapabilityTracker(EntityCapability<?, ?, ?> entityCapability, ServerPlayerEntity watcher) {
		this.entityCapability = entityCapability;
		this.watcher = watcher;
	}

	/**
	 * Called when the tracker is added
	 */
	public void add() {
		this.entityCapability.addTracker(this);
	}

	/**
	 * Called when the tracker is removed
	 */
	public void remove() {
		this.entityCapability.removeTracker(this);
	}

	/**
	 * Marks the data as dirty
	 */
	public void setChanged() {
		this.dirty = true;
	}

	/**
	 * Returns the watcher
	 * @return
	 */
	public ServerPlayerEntity getWatcher() {
		return this.watcher;
	}

	/**
	 * Returns the entity capability
	 * @return
	 */
	public EntityCapability<?, ?, ?> getEntityCapability() {
		return this.entityCapability;
	}

	/**
	 * Updates the tracker
	 */
	public void update() {
		if(this.lastUpdate < this.entityCapability.getTrackingTime()) {
			this.lastUpdate++;
		} else {
			this.trackerReady = true;
		}

		if(this.trackerReady && this.dirty) {
			this.lastUpdate = 0;
			this.trackerReady = false;
			this.entityCapability.sendPacket(this.watcher);
			this.dirty = false;
		}
	}
}
