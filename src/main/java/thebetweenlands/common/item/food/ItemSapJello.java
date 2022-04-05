package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IDecayFood;

public class ItemSapJello extends BLFoodItem implements IDecayFood 
{
    public ItemSapJello(Properties properties) {
        super(false, 4, 0.2F, properties);
    }

    @Override
    public int getDecayHealAmount(ItemStack stack) {
        return 4;
    }
}
