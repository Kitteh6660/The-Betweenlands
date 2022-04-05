package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;

public class SlotPassthroughCraftingOutput extends CraftingResultSlot {
	
	protected final SlotPassthroughCraftingInput source;

	protected final PlayerEntity player;
	protected final CraftingInventory craftMatrix;

	public SlotPassthroughCraftingOutput(PlayerEntity player, CraftingInventory craftingMatrix, IInventory resultInventory, int slotIndex, int xPosition, int yPosition, SlotPassthroughCraftingInput source) {
		super(player, craftingMatrix, resultInventory, slotIndex, xPosition, yPosition);
		this.player = player;
		this.craftMatrix = craftingMatrix;
		this.source = source;
	}

	@Override
	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		//Same as super.onTake, but calls this.source.onTake when crafting matrix items are removed

		this.onCrafting(stack);
		ForgeHooks.setCraftingPlayer(thePlayer);
		NonNullList<ItemStack> remainingItems = CraftingManager.getRemainingItems(this.craftMatrix, thePlayer.level);
		ForgeHooks.setCraftingPlayer(null);

		for(int i = 0; i < remainingItems.size(); ++i) {
			ItemStack currentStack = this.craftMatrix.getItem(i);
			ItemStack remainingStack = remainingItems.get(i);

			if(!currentStack.isEmpty()) {
				this.source.onTake(this.player, this.craftMatrix.removeItem(i, 1));
				currentStack = this.craftMatrix.getItem(i);
			}

			if(!remainingStack.isEmpty()) {
				if(currentStack.isEmpty()) {
					this.craftMatrix.setItem(i, remainingStack);
				} else if(ItemStack.areItemsEqual(currentStack, remainingStack) && ItemStack.areItemStackTagsEqual(currentStack, remainingStack)) {
					remainingStack.inflate(currentStack.getCount());
					this.craftMatrix.setItem(i, remainingStack);
				} else if(!this.player.inventory.add(remainingStack)) {
					this.player.drop(remainingStack, false);
				}
			}
		}

		return stack;
	}
}
