package thebetweenlands.common.item.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.entity.mobs.EntitySwampHag;

public class ItemHagHacker extends ItemLootSword {
	
	public ItemHagHacker(IItemTier itemTier, int damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
		this.addInstantKills(EntitySwampHag.class);
		//this.setMaxDamage(32);
	}
	
	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return true;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlot) {
		Multimap<Attribute, AttributeModifier> multimap = HashMultimap.<Attribute, AttributeModifier>create();
		
		if(equipmentSlot == EquipmentSlotType.MAINHAND) {
			multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", -3.0D, Operation.ADDITION));
		}
		return multimap;
	}
}