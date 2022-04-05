package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemNettleSoup extends BLFoodItem {
	
	public ItemNettleSoup(Properties properties) {
		super(false, 0, 0, properties);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return new ItemStack(ItemRegistry.WEEDWOOD_BOWL.get());
	}

	/*@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entity) {
		if (stack.getCount() != 0) {
			player.inventory.add(getContainerItem(stack));
		}
	}*/
}
