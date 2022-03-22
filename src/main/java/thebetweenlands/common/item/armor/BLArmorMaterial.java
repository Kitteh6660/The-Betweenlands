package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.function.Supplier;

public enum BLArmorMaterial implements IArmorMaterial
{
	//ArmorMaterial
	BL_CLOTH("bl_cloth", 12, new int[]{1, 2, 3, 1}, 10, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> { return Ingredient.of(ItemRegistry.LURKER_SKIN.get()); }),
	LURKER_SKIN("lurker_skin", 12, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> { return Ingredient.of(ItemRegistry.LURKER_SKIN.get()); }),
	SLIMY_BONE("slimy_bone", 6, new int[]{1, 3, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> { return Ingredient.of(ItemRegistry.SLIMY_BONE.get()); }),
	SYRMORITE("syrmorite", 16, new int[]{2, 5, 6, 2}, 8, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> { return Ingredient.of(ItemRegistry.SYRMORITE_INGOT.get()); }),
	VALONITE("valonite", 35, new int[]{3, 6, 8, 3}, 14, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> { return Ingredient.of(ItemRegistry.VALONITE_SHARD.get()); }),
	RUBBER("rubber", 10, new int[]{1, 0, 0, 0}, 14, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> { return Ingredient.of(ItemRegistry.RUBBER_BALL.get()); }),
	LEGEND("legend", 66, new int[]{6, 12, 16, 6}, 22, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 2.0F, () -> { return Ingredient.of(ItemRegistry.ANCIENT_REMNANT.get()); }),
	ANCIENT("ancient", 35, new int[]{3, 6, 8, 3}, 20, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 1.0F, () -> { return Ingredient.of(ItemRegistry.ANCIENT_REMNANT.get()); });
	
	private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] slotProtections;
	private final int enchantmentValue;
	private final SoundEvent sound;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyValue<Ingredient> repairIngredient;

	private BLArmorMaterial(String materialName, int durabilityIn, int[] defenseIn, int enchantmentIn, SoundEvent soundIn, float toughnessIn, float knockbackResIn, Supplier<Ingredient> ingredientIn) {
		this.name = materialName;
		this.durabilityMultiplier = durabilityIn;
		this.slotProtections = defenseIn;
		this.enchantmentValue = enchantmentIn;
		this.sound = soundIn;
		this.toughness = toughnessIn;
		this.knockbackResistance = knockbackResIn;
		this.repairIngredient = new LazyValue<>(ingredientIn);
	}

	public int getDurabilityForSlot(EquipmentSlotType slotIn) {
		return HEALTH_PER_SLOT[slotIn.getIndex()] * this.durabilityMultiplier;
	}

	public int getDefenseForSlot(EquipmentSlotType slotIn) {
		return this.slotProtections[slotIn.getIndex()];
	}

	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	public SoundEvent getEquipSound() {
		return this.sound;
	}

	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@OnlyIn(Dist.CLIENT)
	public String getName() {
		return this.name;
	}

	public float getToughness() {
		return this.toughness;
	}

	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}

}
