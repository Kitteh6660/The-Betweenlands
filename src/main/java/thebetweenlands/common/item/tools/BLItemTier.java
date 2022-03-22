package thebetweenlands.common.item.tools;

import java.util.function.Supplier;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import thebetweenlands.common.registries.ItemRegistry;

public enum BLItemTier implements IItemTier {
	
	WEEDWOOD(0, 80, 2.0F, 0.0F, 0, () -> { return Ingredient.of(ItemRegistry.WEEDWOOD_PLANKS.get()); } ),
	BONE(1, 320, 4.0F, 1.0F, 0, () -> { return Ingredient.of(ItemRegistry.SLIMY_BONE.get()); } ),
	LURKER_SKIN(1, 600, 5.0F, 1.0F, 0, () -> { return Ingredient.of(ItemRegistry.LURKER_SKIN.get()); } ),
	DENTROTHYST(1, 80, 7.0F, 1.0F, 0, () -> { return Ingredient.of(ItemRegistry.DENTROTHYST_FLUID_VIAL.get()); } ),
	OCTINE(2, 900, 6.0F, 2.0F, 0, () -> { return Ingredient.of(ItemRegistry.OCTINE_INGOT.get()); } ),
	SYRMORITE(2, 900, 6.0F, 2.0F, 0, () -> { return Ingredient.of(ItemRegistry.SYRMORITE_INGOT.get()); } ),
	VALONITE(3, 2500, 8.0F, 3.0F, 0, () -> { return Ingredient.of(ItemRegistry.VALONITE_SHARD.get()); } ),
	LOOT(3, 7500, 2.0F, 0.5F, 0, () -> { return Ingredient.of(ItemRegistry.LOOT_SCRAPS.get()); } ),
	LEGEND(6, 10000, 16.0F, 6.0F, 0, () -> { return Ingredient.of(ItemRegistry.ANCIENT.get()); } );

	private final int level;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final LazyValue<Ingredient> repairIngredient;
	
	private BLItemTier(int harvestLvl, int usesIn, float speedIn, float damageIn, int enchIn, Supplier<Ingredient> ingredientIn) {
		this.level = harvestLvl;
		this.uses = usesIn;
		this.speed = speedIn;
		this.damage = damageIn;
		this.enchantmentValue = enchIn;
		this.repairIngredient = new LazyValue<>(ingredientIn);
    }
	
	@Override
	public int getUses() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAttackDamageBonus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEnchantmentValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Ingredient getRepairIngredient() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
