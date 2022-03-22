package thebetweenlands.common.item.herblore;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.item.IDiscoveryProvider;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;

import javax.annotation.Nullable;

public class ItemManualHL extends Item implements IDiscoveryProvider<ItemStack>{
    public ItemManualHL() {
        setMaxStackSize(1);
        this.setCreativeTab(BLCreativeTabs.ITEMS);
    }
    
    @Override
    public DiscoveryContainer<ItemStack> getContainer(ItemStack stack) {
        if(stack != null) {
            if(stack.getTag() == null)
                stack.setTag(new CompoundNBT());
            return new DiscoveryContainer<ItemStack>(this, stack).updateFromNBT(stack.getTag(), false);
        }
        return null;
    }

    @Override
    public ActionResult<ItemStack> use( World world, PlayerEntity player, Hand hand) {
        player.openGui(TheBetweenlands.instance, CommonProxy.GUI_HL, world, hand == Hand.MAIN_HAND ? 0 : 1, 0, 0);
        return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public void saveContainer(ItemStack stack, DiscoveryContainer<ItemStack> container) {
        if(stack != null) {
            if(stack.getTag() == null)
                stack.setTag(new CompoundNBT());
            stack.setTag(container.save(stack.getTag()));
        }
    }
}
