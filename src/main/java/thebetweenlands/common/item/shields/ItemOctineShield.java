package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.registries.KeyBindRegistry;

public class ItemOctineShield extends ItemBLShield {
	public ItemOctineShield() {
		super(BLMaterialRegistry.TOOL_OCTINE);
	}

	@Override
	public void onAttackBlocked(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		if(source.getImmediateSource() != null) {
			source.getImmediateSource().setFire(4);
		}
		super.onAttackBlocked(stack, attacked, damage, source);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.octine_shield"), 0));
	}
}
