package thebetweenlands.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IExtendedReach {

    /**
     * Returns the reach in blocks that the item will be able to hit
     * @return
     */
    double getReach();

    default void onLeftClick(Player player, ItemStack stack) {
    	
    }
}

