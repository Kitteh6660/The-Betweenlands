package thebetweenlands.common.item.food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IDecayFood;

public class ItemSapBall extends BLFoodItem implements IDecayFood {
	
	public ItemSapBall(Properties properties) {
		super(false, 0, 0, properties);
		//setAlwaysEdible();
	}

	@Override
	public int getDecayHealAmount(ItemStack stack) {
		return 2;
	}

	@Override
	public boolean canGetSickOf(PlayerEntity player, ItemStack stack) {
		return false;
	}
}
