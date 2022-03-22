package thebetweenlands.client.audio;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import thebetweenlands.common.entity.mobs.EntityFortressBoss;
import thebetweenlands.common.registries.SoundRegistry;

public class FortressBossIdleSound extends EntitySound<EntityFortressBoss> {
	public FortressBossIdleSound(EntityFortressBoss boss) {
		super(SoundRegistry.FORTRESS_BOSS_LIVING, SoundCategory.HOSTILE, boss, LivingEntity::isEntityAlive);
		repeat = false;
	}
}
