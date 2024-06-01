package thebetweenlands.api.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAnimatorRecipe {
	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(ItemStack stack);

	/**
	 * Returns the amount of required fuel
	 * @param stack
	 * @return
	 */
	public int getRequiredFuel(ItemStack stack);

	/**
	 * Returns the amount of required life crystal
	 * @param stack
	 * @return
	 */
	public int getRequiredLife(ItemStack stack);

	/**
	 * Returns the entity to be rendered when animating the item
	 * @param stack
	 * @return
	 */
	@Nullable
	@OnlyIn(Dist.CLIENT)
	public Entity getRenderEntity(ItemStack stack);

	/**
	 * Returns the resulting item when this recipe is finished
	 * @param stack
	 * @return
	 */
	@Nonnull
	public ItemStack getResult(ItemStack stack);

	/**
	 * Returns the entity that is spawned when this recipe is finished
	 * @param stack
	 * @return
	 */
	@Nullable
	public Class<? extends Entity> getSpawnEntityClass(ItemStack stack);

	/**
	 * Called when the item is animated. Can return the resulting ItemStack (overrides {@link IAnimatorRecipe#getResult()}).
	 * Also used to spawn entities from animator once animated
	 * @param world
	 * @param pos
	 * @param stack
	 * @return
	 */
	@Nonnull
	public ItemStack onAnimated(Level world, BlockPos pos, ItemStack stack);

	/**
	 * Use {@link #onRetrieved(Player, BlockPos, ItemStack)} instead
	 */
	@Deprecated
	public boolean onRetrieved(Level world, BlockPos pos, ItemStack stack);

	/**
	 * Called when the animator has finished animating and is right clicked.
	 * Return true if GUI should be opened on first click
	 * @param world
	 * @param pos
	 * @param stack
	 */
	public default boolean onRetrieved(Player player, BlockPos pos, ItemStack stack) {
		return this.onRetrieved(player.level(), pos, stack);
	}
	
	/**
	 * Returns whether the GUI should close when the animator has finished
	 * @param stack
	 * @return
	 */
	public boolean getCloseOnFinish(ItemStack stack);
}
