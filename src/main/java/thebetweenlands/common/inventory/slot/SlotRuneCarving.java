package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.inventory.InventoryRuneCarveResult;

public class SlotRuneCarving extends SlotPassthroughCraftingOutput {

	private final Slot aspectSource;
	
	private int amountCrafted;
	
	public SlotRuneCarving(PlayerEntity player, InventoryCrafting craftingMatrix, IInventory resultInventory,
			int slotIndex, int xPosition, int yPosition, SlotPassthroughCraftingInput itemSource, Slot aspectSource) {
		super(player, craftingMatrix, resultInventory, slotIndex, xPosition, yPosition, itemSource);
		this.aspectSource = aspectSource;
	}

	@Override
	public ItemStack removeItem(int amount) {
        if(this.hasItem()) {
            this.amountCrafted += Math.min(amount, this.getStack().getCount());
        }
        return super.removeItem(amount);
    }

	@Override
    protected void onCrafting(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        super.onCrafting(stack, amount);
	}

	@Override
    protected void onSwapCraft(int count) {
        this.amountCrafted += count;
        super.onSwapCraft(count);
    }
	
	@Override
	protected void onCrafting(ItemStack stack) {
		super.onCrafting(stack);
		
		if(this.amountCrafted > 0) {
			InventoryRuneCarveResult inv = (InventoryRuneCarveResult) this.inventory;
			
			IAspectType type = inv.getAspectTypeUsed();
			
			if(type != null) {
				AspectContainer container = ItemAspectContainer.fromItem(this.aspectSource.getStack(), AspectManager.get(this.player.world));
				
				container.drain(type, inv.getAspectAmountUsed() * this.amountCrafted);
				
				this.craftMatrix.eventHandler.detectAndSendChanges();
			}
		}
		
		this.amountCrafted = 0;
	}
	
	@Override
	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		this.onCrafting(stack);
		
		this.source.onTake(this.player, this.craftMatrix.removeItem(0, 1));
		
		return stack;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isEnabled() {
		return this.hasItem();
	}
}
