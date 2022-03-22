package thebetweenlands.api.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO: Remove this file.
public interface IDecayFood {
	/**
	 * Returns the amount of healed decay when eating this item
	 * @param stack
	 * @return
	 */
	int getDecayHealAmount(ItemStack stack);

	/**
	 * Returns the saturation multiplier gained when eating this item
	 * @param stack
	 * @return
	 */
	default float getDecayHealSaturation(ItemStack stack) {
		return 0.2F;
	}
	
	/**
	 * Adds the decay healing hint to the tooltip
	 * @param stack
	 * @param worldIn
	 * @param list
	 * @param flag
	 */
	@OnlyIn(Dist.CLIENT)
	default void getDecayFoodTooltip(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flag) {
		list.add(I18n.get("tooltip.bl.decay_food", stack.getDisplayName()));
	}
}
