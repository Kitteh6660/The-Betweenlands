package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.BLMaterialRegistry;


public class ItemAncientGreatsword extends ItemGreatsword {
	
	public ItemAncientGreatsword(IItemTier tier, int damage, float speed, Properties properties) {
		super(tier, damage, speed, properties);
	}
	
	/*public ItemAncientGreatsword() {
		super(BLMaterialRegistry.TOOL_VALONITE);
	}*/

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (stack.getDamageValue() == stack.getMaxDamage()) {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.tool.broken", stack.getDisplayName()), 0));
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the sword break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		if(slot == EquipmentSlotType.MAINHAND && stack.getDamageValue() == stack.getMaxDamage()) {
			Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
			map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", -1, Operation.ADDITION));
			return map;
		}
		return super.getAttributeModifiers(slot, stack);
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(BLMaterialRegistry.TOOL_LOOT);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(BLMaterialRegistry.TOOL_LOOT);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(BLMaterialRegistry.TOOL_LOOT);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(BLMaterialRegistry.TOOL_LOOT);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
