package thebetweenlands.api.runechain.io.types;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface IRuneItemStackAccess {
	/**
	 * Returns the inventory slot that this access refers to. May be null if unknown or if no such inventory or slot exists.
	 * Access must not happen through the delegated inventory and may throw an exception.
	 * @return
	 */
	@Nullable
	public Pair<Container, Integer> getDelegatedSlot();

	/**
	 * Returns whether this access is valid.
	 * If false none of the other operations have an effect.
	 * @return
	 */
	public boolean isAccessValid();

	/**
	 * Returns the {@link ItemStack}.
	 * If {@link #isAccessValid()} is false an empty {@link ItemStack} is returned.
	 * @return
	 */
	public ItemStack get();

	/**
	 * Sets the {@link ItemStack}.
	 * If {@link #isAccessValid()} or {@link #mayPlace(ItemStack)} is false this has no effect and false is returned.
	 * @param stack
	 * @return
	 */
	public boolean set(ItemStack stack);

	/**
	 * Removes the given count from the {@link ItemStack}.
	 * If {@link #isAccessValid()} is false this has no effect and an empty {@link ItemStack} is returned.
	 * @param count
	 * @return
	 */
	public default ItemStack remove(int count) {
		ItemStack decreased = this.get().copy();
		ItemStack result = decreased.split(count);
		if(this.set(decreased)) {
			return result;
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Returns whether the given {@link ItemStack} is valid for this access.
	 * @param stack
	 * @return
	 */
	public boolean mayPlace(ItemStack stack);

	public default Container inventory() {
		return new RuneItemStackAccessInventory(this);
	}
}
