package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemLurkerSkinPatch extends Item 
{
	public ItemLurkerSkinPatch(Properties properties) {
		super(properties);
		//this.setCreativeTab(BLCreativeTabs.ITEMS);
		//this.setMaxStackSize(16);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.get("tooltip.bl.lurker_skin_patch"));
	}
}
