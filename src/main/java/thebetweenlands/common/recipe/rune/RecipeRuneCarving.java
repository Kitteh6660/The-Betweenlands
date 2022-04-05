package thebetweenlands.common.recipe.rune;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.inventory.InventoryRuneletCrafting;

public class RecipeRuneCarving extends IForgeRegistryEntry.Impl<IRecipe<?>> implements IRecipe<InventoryRuneletCrafting> {
	
	private final IRecipe recipe;

	public RecipeRuneCarving(IRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		return inv instanceof InventoryRuneletCrafting && this.recipe.matches(inv, worldIn);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return this.recipe.getCraftingResult(inv);
	}

	@Override
	public boolean canFit(int width, int height) {
		return this.recipe.canFit(width, height);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.recipe.getRecipeOutput();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.recipe.getIngredients();
	}

	@Override
	public String getGroup() {
		return this.recipe.getGroup();
	}

	@Override
	public boolean isDynamic() {
		return this.recipe.isDynamic();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return this.recipe.getRemainingItems(inv);
	}
}
