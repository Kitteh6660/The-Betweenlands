package thebetweenlands.common.item.armor;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.armor.ModelAncientArmor;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.registries.ItemRegistry;

public class AncientArmorItem extends ItemBLArmor {
	
	public static final ResourceLocation TEXTURE = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/armor/ancient.png");
	public static final ResourceLocation TEXTURE_AQUA = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/armor/ancient_aqua.png");
	public static final ResourceLocation TEXTURE_CRIMSON = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/armor/ancient_crimson.png");
	public static final ResourceLocation TEXTURE_GREEN = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/armor/ancient_green.png");

	@OnlyIn(Dist.CLIENT)
	private static ModelAncientArmor model;
	
	public AncientArmorItem(EquipmentSlotType slot, Properties properties) {
		super(BLArmorMaterial.ANCIENT, slot, properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tooltip.bl.ancient_armor.usage"));
		if(stack.getDamageValue() == stack.getMaxDamage()) {
			tooltip.add(new TranslationTextComponent("tooltip.bl.tool.broken", stack.getDisplayName()));
		}
		//Item
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		CircleGemType gem = CircleGemHelper.getGem(stack);
		switch (gem) {
		case AQUA:
			return TEXTURE_AQUA.toString();
		case CRIMSON:
			return TEXTURE_CRIMSON.toString();
		case GREEN:
			return TEXTURE_GREEN.toString();
		default:
			return TEXTURE.toString();
		}
	}

	/*@Override
	public int getColor(ItemStack itemStack) {
		return 0xFFFFFFFF;
	}*/

	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BipedModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel defaultModel) {
		if(model == null) {
			model = (armorSlot == EquipmentSlotType.LEGS ? new ModelAncientArmor(0.5F) : new ModelAncientArmor(1.0F));
		}
		model.setVisibilities(armorSlot);
		return model;
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

	/*@Override
	public Multimap<Attribute, AttributeModifier> getItemAttributeModifiers(EquipmentSlotType equipmentSlot) {
		//ItemStack unspecific method can't check for damage, so just return no modifiers
		return HashMultimap.create();
	}*/

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		if(stack.getDamageValue() == stack.getMaxDamage()) {
			//Armor shouldn't give any reduction when fully damaged
			return HashMultimap.create();
		}
		//Returns default armor attributes
		return super.getAttributeModifiers(slot, stack);
	}

	@SubscribeEvent
	public static void onEntityMagicDamage(LivingHurtEvent event) {
		if(event.getSource().isMagic()) {
			float damage = 1;

			LivingEntity entityHit = event.getEntityLiving();

			ItemStack boots = entityHit.getItemBySlot(EquipmentSlotType.FEET);
			ItemStack legs = entityHit.getItemBySlot(EquipmentSlotType.LEGS);
			ItemStack chest = entityHit.getItemBySlot(EquipmentSlotType.CHEST);
			ItemStack helm = entityHit.getItemBySlot(EquipmentSlotType.HEAD);

			if (!boots.isEmpty() && boots.getItem() == ItemRegistry.ANCIENT_BOOTS.get() && boots.getDamageValue() < boots.getMaxDamage())
				damage -= 0.125D;
			if (!legs.isEmpty()  && legs.getItem() == ItemRegistry.ANCIENT_LEGGINGS.get() && legs.getDamageValue() < legs.getMaxDamage())
				damage -= 0.125D;
			if (!chest.isEmpty() && chest.getItem() == ItemRegistry.ANCIENT_CHESTPLATE.get() && chest.getDamageValue() < chest.getMaxDamage())
				damage -= 0.125D;
			if (!helm.isEmpty() && helm.getItem() == ItemRegistry.ANCIENT_HELMET.get() && helm.getDamageValue() < helm.getMaxDamage())
				damage -= 0.125D;

			event.setAmount(event.getAmount() * damage);
		}
	}
}
