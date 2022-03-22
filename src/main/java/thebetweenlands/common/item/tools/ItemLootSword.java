package thebetweenlands.common.item.tools;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemLootSword extends BLSwordItem {
	
    private List<Class<? extends LivingEntity>> instantKills = new ArrayList<>();

    public ItemLootSword(IItemTier itemTier, int damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
        //setCreativeTab(BLCreativeTabs.SPECIALS);
    }

    @SafeVarargs
	public final ItemLootSword addInstantKills(Class<? extends LivingEntity>... instantKills) {
        this.instantKills.addAll(Arrays.asList(instantKills));
        return this;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity attacked, LivingEntity attacker) {
        if (!this.instantKills.isEmpty() && this.instantKills.contains(attacked.getClass())) {
        	int maxCorrosion = this.getMaxCorrosion(stack);
        	int corrosion = this.getCorrosion(stack);
        	float corrosionMultiplier = 1.0F - (corrosion > maxCorrosion / 2.0F ? ((corrosion - maxCorrosion / 2.0F) / (float)(maxCorrosion / 2.0F)) : 0);
            attacked.hurt(DamageSource.indirectMagic(attacker, attacker), attacked.getMaxHealth() * corrosionMultiplier);
        }
        return super.hurtEnemy(stack, attacked, attacker);
    }
    
    @Override
    public boolean isRepairableByAnimator(ItemStack stack) {
    	return false;
    }
    
    @Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
