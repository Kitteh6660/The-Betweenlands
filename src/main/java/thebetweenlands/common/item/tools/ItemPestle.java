package thebetweenlands.common.item.tools;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPestle extends Item implements IAnimatorRepairable {

    public ItemPestle() {
        setMaxDamage(128);
        maxStackSize = 1;
        setCreativeTab(BLCreativeTabs.GEARS);
        addPropertyOverride(new ResourceLocation("remaining"), (stack, worldIn, entityIn) -> {
            if(hasTag(stack) && stack.getTag().getBoolean("active"))
                return 1;
            return 0;
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add(TranslationHelper.translateToLocal("tooltip.bl.pestle"));
        list.add(TranslationHelper.translateToLocal("tooltip.bl.pestle.remaining", Math.round(100F - 100F / getMaxDamage() * getDamage(stack)), (getMaxDamage() - getDamage(stack))));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int tick, boolean map) {
        if (!stack.hasTag())
            stack.setTag(new CompoundNBT());
    }

    @Override
    public void onCreated(ItemStack stack, World world, PlayerEntity player) {
        stack.setTag(new CompoundNBT());
    }

    private boolean hasTag(ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
            return false;
        }
        return true;
    }

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 4;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 4;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 12;
	}
}