package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IDecayFood;

public class ItemSapJello extends BLFoodItem implements IDecayFood 
{
    public ItemSapJello(Properties properties) {
        super(properties);
    }

    @Override
    public int getDecayHealAmount(ItemStack stack) {
        return 4;
    }
}
