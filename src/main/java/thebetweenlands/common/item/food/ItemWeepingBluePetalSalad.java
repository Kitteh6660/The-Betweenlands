package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemWeepingBluePetalSalad extends BLFoodItem {
	
	public ItemWeepingBluePetalSalad(Properties properties) {
		super(false, 0, 0, properties);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return new ItemStack(ItemRegistry.WEEDWOOD_BOWL.get());
	}
}
