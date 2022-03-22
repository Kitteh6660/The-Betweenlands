package thebetweenlands.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.registries.ItemRegistry;

public class SlotDruidAltar extends Slot {

    public SlotDruidAltar(IInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
        index = slotIndex;
    }

    //TODO: Update code to account for the Flattening.
    @Override
    public boolean mayPlace(ItemStack stack) {
        //Player should not be able to put the talisman back in
        return index != 0 && (index > 0 && index <= 4 && ((stack.getItem() instanceof ItemSwampTalisman && stack.getItem() != ItemRegistry.SWAMP_TALISMAN_0.get() && stack.getItem() != ItemRegistry.SWAMP_TALISMAN_5.get()) || DruidAltarRecipe.isValidItem(stack)));
    }
}
