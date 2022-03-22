package thebetweenlands.common.item.tools;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;

public class ItemOctinePickaxe extends BLPickaxeItem {
	
	public ItemOctinePickaxe(IItemTier tier, int damage, float speed, Properties properties) {
		super(tier, damage, speed, properties);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if(attacker.level.random.nextFloat() < ItemOctineSword.getOctineToolFireChance(stack, target, attacker)) {
			target.setSecondsOnFire(5);
		}
		return super.hurtEnemy(stack, target, attacker);
	}
}
