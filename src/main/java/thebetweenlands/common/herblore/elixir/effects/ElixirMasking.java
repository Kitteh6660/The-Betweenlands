package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class ElixirMasking extends ElixirEffect {
	
	public ElixirMasking(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
		this.setType(EffectType.BENEFICIAL);
	}

	public boolean canSeeBy(LivingEntity target, LivingEntity watcher) {
		if(this.isActive(target)) {
			int strength = this.getStrength(target);
			double minDist = 28.0D - Math.min(20.0D / 4.0D * strength, 21.0D);
			if(target.distanceTo(watcher) < minDist) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
}
