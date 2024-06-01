package thebetweenlands.api.entity;

import net.minecraft.world.entity.player.Player;

public interface IEntityPreventUnmount {
	/**
	 * Returns whether player controlled unmounting should be blocked
	 * @param rider
	 * @return
	 */
	public boolean isUnmountBlocked(Player rider);
	
	/**
	 * Returns whether the unmount status bar text should be prevented
	 * @param rider
	 * @return
	 */
	public default boolean shouldPreventStatusBarText(Player rider) {
		return true;
	}
	
	/**
	 * Called when player controlled unmounting was blocked.
	 * This may not necessarily be called during the player's update.
	 * @param rider
	 */
	public default void onUnmountBlocked(Player rider) {
		
	}
}
