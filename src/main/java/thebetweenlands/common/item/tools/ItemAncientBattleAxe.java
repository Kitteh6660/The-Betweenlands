package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item.Properties;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemAncientBattleAxe extends ItemGreataxe {
	
	public ItemAncientBattleAxe(IItemTier itemTier, int damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
		//this.setMaxDamage(itemTier.getUses());
	}

	/*public ItemAncientBattleAxe() {
		this(BLMaterialRegistry.TOOL_VALONITE);
	}*/

	@Override
	protected double getBlockBreakReach(LivingEntity entity, ItemStack stack) {
		return stack.getDamageValue() == stack.getMaxDamage() ? 0.0D : 3.0D;
	}

	@Override
	protected double getBlockBreakHalfAngle(LivingEntity entity, ItemStack stack) {
		return stack.getDamageValue() == stack.getMaxDamage() ? 0.0D : 55.0D;
	}

	@Override
	protected float getSwingSpeedMultiplier(LivingEntity entity, ItemStack stack) {
		return 0.225F;
	}

	@Override
	protected double getAoEReach(LivingEntity entityLiving, ItemStack stack) {
		return 2.2D;
	}

	@Override
	public double getReach() {
		return 3.5D;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
		if(equipmentSlot == EquipmentSlotType.MAINHAND && stack.getDamageValue() == stack.getMaxDamage()) {
			Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
			map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", -1, Operation.MULTIPLY_BASE));
			return map;
		}

		Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

		if(equipmentSlot == EquipmentSlotType.MAINHAND) {
			multimap.removeAll(Attributes.ATTACK_SPEED.getRegistryName());
			multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", -3.0D, Operation.ADDITION));
		}

		return multimap;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if(stack.getDamageValue() == stack.getMaxDamage()) {
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
