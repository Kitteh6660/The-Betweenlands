package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.item.misc.ItemBoneWayfinder;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeClearBoneWayfinder extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		boolean hasBoneWayfinder = false;
		for (int i = 0; i < crafter.getContainerSize(); ++i) {
			ItemStack stack = crafter.getItem(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.BONE_WAYFINDER) {
					if(hasBoneWayfinder) {
						return false;
					}
					hasBoneWayfinder = true;
				} else {
					//Don't allow other items in grid
					return false;
				}
			}
		}
		return hasBoneWayfinder;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack boneWayfinder = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getContainerSize(); ++i) {
			ItemStack stack = crafter.getItem(i);
			if(!stack.isEmpty() && stack.getItem() == ItemRegistry.BONE_WAYFINDER) {
				boneWayfinder = stack;
			}
		}
		boneWayfinder = boneWayfinder.copy();
		((ItemBoneWayfinder) boneWayfinder.getItem()).setBoundWaystone(boneWayfinder, null);
		return boneWayfinder;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 1;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < remaining.size() ;++i) {
			ItemStack stack = inv.getItem(i);
			remaining.set(i, ForgeHooks.getContainerItem(stack));
		}

		return remaining;
	}

}