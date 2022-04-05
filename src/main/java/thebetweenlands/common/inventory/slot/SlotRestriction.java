package thebetweenlands.common.inventory.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import thebetweenlands.common.inventory.container.ContainerBLDualFurnace;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class SlotRestriction extends Slot {
	
	private ItemStack item;
	private int maxItems;
	private Container container;

	public SlotRestriction(IInventory inventory, int slotIndex, int x, int y, ItemStack item, int maxItems, Container container) {
		super(inventory, slotIndex, x, y);
		this.item = item;
		this.maxItems = maxItems;
		this.container = container;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.getItem() == item.getItem() && stack.getDamageValue() == item.getDamageValue()) {
			return true;
		}
		return false;
	}
	
    @Override
	public int getMaxStackSize()
    {
        return maxItems;
    }

	@Override
	public void set(ItemStack stack) {
		super.set(stack);
		if (!stack.isEmpty() && container instanceof ContainerBLDualFurnace && stack.getItem() == ItemRegistry.LIMESTONE_FLUX.get() && Minecraft.getInstance().getEffectiveSide().isServer()) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			if(server != null) {
				PlayerList manager = server.getPlayerList();
				if (manager != null) {
					for (ServerPlayerEntity entityPlayerMP : manager.getPlayers()) {
						if (entityPlayerMP.containerMenu == container && container.stillValid(entityPlayerMP) && container.getCanCraft(entityPlayerMP)) {
							AdvancementCriterionRegistry.FLUX_ADDED.trigger(entityPlayerMP);
						}
					}
				}
			}
		}
	}
}
