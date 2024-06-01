package thebetweenlands.api.entity;

import java.util.UUID;

import net.minecraft.core.Vec3i;

public interface IBLBoss {
	public UUID getBossInfoUuid();

	public default BossType getBossType() {
		return BossType.NORMAL_BOSS;
	}

	public default float getMiniBossTagSize(float partialTicks) {
		return 0.5F;
	}
	
	public default Vec3i getMiniBossTagOffset(float partialTicks) {
		return Vec3i.ZERO;
	}
}
