package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.event.SplashPotionEvent;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.BLItemTier;
import thebetweenlands.common.item.tools.ItemBLShield;

public class ItemDentrothystShield extends ItemBLShield {
	
	public final EnumDentrothyst denType;

	public ItemDentrothystShield(EnumDentrothyst denTypeIn, Properties properties) {
		super(BLItemTier.DENTROTHYST, properties);
		this.denType = denTypeIn;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.dentrothyst_shield"), 0));
	}

	@SubscribeEvent
	public static void onSplashPotion(SplashPotionEvent event) {
		LivingEntity target = event.getTarget();
		if(target.isBlocking()) {
			ItemStack stack = target.getUseItem();
			if(!stack.isEmpty() && stack.getItem() instanceof ItemDentrothystShield) {
				if(!event.getPotionEffect().isBeneficial()) {
					event.setCanceled(true);
				}
			}
		}
	}
}
