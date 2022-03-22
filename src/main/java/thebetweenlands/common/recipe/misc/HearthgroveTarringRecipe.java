package thebetweenlands.common.recipe.misc;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.block.terrain.BlockHearthgroveLog;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

public class HearthgroveTarringRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		boolean tar = false;
		int hearthgroveLogs = 0;
		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == Item.getItemFromBlock(BlockRegistry.LOG_HEARTHGROVE)) {
					BlockState state = BlockRegistry.LOG_HEARTHGROVE.getStateFromMeta(stack.getMetadata());
					if(!state.getValue(BlockHearthgroveLog.TARRED)) {
						hearthgroveLogs++;
					} else {
						return false;
					}
				} else if(stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
					if(tar) {
						return false;
					}
					IFluidHandlerItem cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
					FluidStack drained = cap.drain(new FluidStack(FluidRegistry.TAR, Fluid.BUCKET_VOLUME), false);
					if(drained != null && drained.amount == Fluid.BUCKET_VOLUME) {
						tar = true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return tar && hearthgroveLogs > 0 && hearthgroveLogs <= 8;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		int tarredLogs = 0;
		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == Item.getItemFromBlock(BlockRegistry.LOG_HEARTHGROVE)) {
					tarredLogs++;
				}
			}
		}
		return new ItemStack(BlockRegistry.LOG_HEARTHGROVE, tarredLogs, BlockRegistry.LOG_HEARTHGROVE.getMetaFromState(BlockRegistry.LOG_HEARTHGROVE.defaultBlockState().setValue(BlockHearthgroveLog.LOG_AXIS, BlockLog.EnumAxis.Y).setValue(BlockHearthgroveLog.TARRED, true)));
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> ret = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			IFluidHandlerItem cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			if(cap != null && stack.getItem().hasContainerItem(stack)) {
				cap.drain(new FluidStack(FluidRegistry.TAR, Fluid.BUCKET_VOLUME), true);
				ret.set(i, cap.getContainer()); //Leave container handling up to the fluid handler
			}
		}
		return ret;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width*height >= 2;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}
}
