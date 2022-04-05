package thebetweenlands.common.handler;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.common.block.terrain.BlockHearthgroveLog;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

public class FuelHandler {
	@SubscribeEvent
	public static void onFuelEvent(FurnaceFuelBurnTimeEvent event) {
		ItemStack stack = event.getItemStack();
		if(stack.getItem() instanceof ItemMisc && stack.getDamageValue() == EnumItemMisc.WEEDWOOD_STICK.getID()) {
			event.setBurnTime(100);
		}
		else if(stack.getItem() instanceof ItemMisc && stack.getDamageValue() == EnumItemMisc.SULFUR.getID()) {
			event.setBurnTime(1600);
		}
		else if(stack.getItem() instanceof ItemMisc && stack.getDamageValue() == EnumItemMisc.UNDYING_EMBER.getID()) {
			event.setBurnTime(20000);
		}
		else if(stack.getItem() == Item.getItemFromBlock(BlockRegistry.LOG_HEARTHGROVE)) {
			BlockState state = BlockRegistry.LOG_HEARTHGROVE.getStateFromMeta(stack.getMetadata());
			if(state.getValue(BlockHearthgroveLog.TARRED)) {
				event.setBurnTime(4800);
			} else {
				event.setBurnTime(800);
			}
		}
	}
}