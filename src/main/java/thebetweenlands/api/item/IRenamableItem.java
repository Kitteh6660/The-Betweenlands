package thebetweenlands.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;

public interface IRenamableItem {
	
	public default boolean canRename(PlayerEntity player, Hand hand, ItemStack stack, String name) {
		return true;
	}

	public default void setRename(PlayerEntity player, Hand hand, ItemStack stack, String name) {
		stack.setHoverName(new TextComponent(name));
	}

	public default void clearRename(PlayerEntity player, Hand hand, ItemStack stack, String name) {
		stack.resetHoverName();
	}
}
