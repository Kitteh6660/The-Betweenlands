package thebetweenlands.api.recipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

public interface IPurifierRecipe {
	/**
	 * Returns the output for the specified item stack
	 * @param input
	 * @return
	 */
	@MethodsReturnNonnullByDefault
	public ItemStack getOutput(ItemStack input);

	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(ItemStack stack);
}
