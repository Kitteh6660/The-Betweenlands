package thebetweenlands.common.item.food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemMireScramble extends BLFoodItem {
	
	public ItemMireScramble(Properties properties) {
		super(false, 0, 0, properties);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return new ItemStack(ItemRegistry.WEEDWOOD_BOWL.get()); // EnumItemMisc.WEEDWOOD_BOWL.create(1);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, PlayerEntity player) {
		if (stack.getCount() != 0)
			player.inventory.addItemStackToInventory(getContainerItem(stack));
	}
}
