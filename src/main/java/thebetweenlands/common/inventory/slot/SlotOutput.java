package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

import javax.annotation.Nullable;

public class SlotOutput extends Slot {
	
    private Container container;

    public SlotOutput(IInventory inventoryIn, int index, int xPosition, int yPosition, Container container) {
        super(inventoryIn, index, xPosition, yPosition);
        this.container = container;
    }


    @Override
    public boolean mayPlace(@Nullable ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        if (container instanceof ContainerPurifier && stack.getItem() instanceof ICorrodible && thePlayer instanceof ServerPlayerEntity)
            AdvancementCriterionRegistry.PURIFY_TOOL.trigger((ServerPlayerEntity) thePlayer);
        return super.onTake(thePlayer, stack);
    }
}
