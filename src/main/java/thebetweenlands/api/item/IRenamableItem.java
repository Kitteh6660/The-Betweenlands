package thebetweenlands.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface IRenamableItem {
	public default boolean canRename(PlayerEntity player, Hand hand, ItemStack stack, String name) {
		return true;
	}

	public default void setRename(PlayerEntity player, Hand hand, ItemStack stack, String name) {
		stack.setStackDisplayName(name);
	}

	public default void clearRename(PlayerEntity player, Hand hand, ItemStack stack, String name) {
		stack.clearCustomName();
	}
}
