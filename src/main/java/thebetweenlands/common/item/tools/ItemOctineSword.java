package thebetweenlands.common.item.tools;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;

public class ItemOctineSword extends BLSwordItem {
	
	public ItemOctineSword(IItemTier itemTier, int damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if(attacker.level.random.nextFloat() < ItemOctineSword.getOctineToolFireChance(stack, target, attacker)) {
			target.setSecondsOnFire(5);
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	public static float getOctineToolFireChance(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return CircleGemHelper.getGem(stack) == CircleGemType.CRIMSON ? 0.5F : 0.25F;
	}
}
