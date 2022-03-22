package thebetweenlands.common.item.food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemWightHeart extends BLFoodItem {
	public ItemWightHeart() {
		super(0, 0.0F, false);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemStackIn = playerIn.getItemInHand(handIn);
		if (playerIn.getHealth() < playerIn.getMaxHealth()) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStackIn);
		} else {
			return new ActionResult<ItemStack>(ActionResultType.FAIL, itemStackIn);
		}
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player) {
		super.onFoodEaten(stack, world, player);
		player.heal(8.0F);
	}

	@Override
	public boolean canGetSickOf(PlayerEntity player, ItemStack stack) {
		return false;
	}
}
