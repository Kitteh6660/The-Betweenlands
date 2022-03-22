package thebetweenlands.common.item.food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class RottenFood extends Food {
	
	public RottenFood() {
		super();
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player) {
		super.onFoodEaten(stack, world, player);

		if (player != null) {
			player.addEffect(new EffectInstance(Effects.HUNGER, 200, 1));
			player.addEffect(new EffectInstance(Effects.POISON, 200, 1));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		ItemStack originalStack = this.getOriginalStack(stack);
		if (!originalStack.isEmpty() && originalStack.getItem() != Items.AIR) {
			return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", originalStack.getRarity().color + originalStack.getDisplayName() + TextFormatting.RESET).trim();
		}
		return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + "_empty.name").trim();
	}

	public void setOriginalStack(ItemStack stack, ItemStack originalStack) {
		stack.setTagInfo("originalStack", originalStack.save(new CompoundNBT()));
	}

	public ItemStack getOriginalStack(ItemStack stack) {
		return stack.getTag() != null ? new ItemStack(stack.getTag().getCompoundTag("originalStack")) : ItemStack.EMPTY;
	}

	@Override
	public boolean canGetSickOf(PlayerEntity player, ItemStack stack) {
		return false;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		ItemStack original = this.getOriginalStack(stack);
		if(!original.isEmpty()) {
			return original.getItem().getItemStackLimit(original);
		}
		return super.getItemStackLimit(stack);
	}
}
