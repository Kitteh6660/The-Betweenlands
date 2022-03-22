package thebetweenlands.common.item.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemRuneDoorKey extends Item {
	public ItemRuneDoorKey() {
		setMaxStackSize(1);
		setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flag) {
		//put some blurb here
	}

}
