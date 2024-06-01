package thebetweenlands.api.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.handler.FoodSicknessHandler;

public interface IFoodSicknessItem {
	/**
	 * Returns whether a player can get sick of the specified item stack
	 * @param player
	 * @param stack
	 * @return
	 */
	default boolean canGetSickOf(@Nullable Player player, ItemStack stack) {
		return player != null ? FoodSicknessHandler.isFoodSicknessEnabled(player.level()) : false;
	}

	/**
	 * Adds additional tooltips for the specified sickness
	 * @param stack
	 * @param sickness
	 * @param hatred
	 * @param advancedTooltips
	 * @param toolTip
	 * @return
	 */
	default void getSicknessTooltip(ItemStack stack, FoodSickness sickness, int hatred, boolean advancedTooltips, List<String> toolTip) {
		String debug = "";
		if(advancedTooltips) {
			debug = " (" + hatred + "/" + sickness.maxHatred + ")";
		}
		toolTip.add(I18n.get("tooltip.bl.food_sickness.state." + sickness.name().toLowerCase()) + debug);
	}
}
