package thebetweenlands.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.potion.Effect;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.event.SplashPotionEvent;

public final class CommonHooks {
	private CommonHooks() { }

	/**
	 * Called before a splash potion calls affectEntity in applySplash
	 * @param potion
	 * @param target
	 * @param effect
	 * @return True to cancel
	 */
	public static boolean onSplashAffectEntity(PotionEntity potion, LivingEntity target, Effect effect) {
		SplashPotionEvent event = new SplashPotionEvent(potion, target, effect, true);
		MinecraftForge.EVENT_BUS.post(event);
		return event.isCanceled();
	}

	/**
	 * Called before a splash potion calls addEffect in applySplash
	 * @param potion
	 * @param target
	 * @param effect
	 * @return True to cancel
	 */
	public static boolean onSplashAddPotionEffect(PotionEntity potion, LivingEntity target, Effect effect) {
		SplashPotionEvent event = new SplashPotionEvent(potion, target, effect, false);
		MinecraftForge.EVENT_BUS.post(event);
		return event.isCanceled();
	}
}
