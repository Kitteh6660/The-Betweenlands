package thebetweenlands.api.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IRenamableItem {
	
	public default boolean canRename(Player player, InteractionHand hand, ItemStack stack, String name) {
		return true;
	}

	public default void setRename(Player player, InteractionHand hand, ItemStack stack, String name) {
		stack.setHoverName(Component.literal(name));
	}

	public default void clearRename(Player player, InteractionHand hand, ItemStack stack, String name) {
		stack.resetHoverName();
	}
}
