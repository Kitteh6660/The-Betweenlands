package thebetweenlands.common.handler;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnvilEventHandler {

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		//TODO: I don't think this is needed anymore
		/*if (event.getLeft() != null && event.getLeft().getItem() instanceof ItemBLShield) {
            if (event.getRight() != null && event.getLeft().getItem().isRepairable(event.getLeft(), event.getRight())) {
                if (event.getLeft().getTag() != null && event.getLeft().getTag().contains("damage")) {
                    int damage = event.getLeft().getTag().getInt("damage");
                    int repairPerItem = ((ItemBLShield) event.getLeft().getItem()).getMaxDamage() / 3;
                    if (damage == 0) {
                        ItemStack output = event.getLeft().copy();
                        if (event.getName() != null)
                            output.setStackDisplayName(event.getName());
                        event.setOutput(output);

                        event.setCost((int) (event.getName() != null ? event.getName().length() * 0.25D : 0));
                        event.setMaterialCost(0);
                    } else {
                        int amount = (int) Math.ceil(((double) damage / (double) repairPerItem) + .5d);
                        ItemStack output = event.getLeft().copy();
                        if (event.getName() != null)
                            output.setStackDisplayName(event.getName());
                        if (output.getTag() == null)
                            output.setTag(event.getLeft().getTag());
                        output.getTag().putInt("damage", damage - repairPerItem * amount < 0 ? 0 : damage - repairPerItem * amount);
                        event.setOutput(output);
                        int lengthDifference = event.getName() != null ? event.getName().length() - event.getLeft().getDisplayName().length() : 0;
                        event.setCost(amount + (int) (event.getName() != null ? lengthDifference * 0.25D : 0));
                        event.setMaterialCost(amount);
                    }
                }
            }
        }*/
	}
}
