package thebetweenlands.api.entity;

import java.util.UUID;

import net.minecraft.util.math.vector.Vector3d;

public interface IBLBoss {
	public UUID getBossInfoUuid();

	public default BossType getBossType() {
		return BossType.NORMAL_BOSS;
	}

	public default float getMiniBossTagSize(float partialTicks) {
		return 0.5F;
	}
	
	public default Vector3d getMiniBossTagOffset(float partialTicks) {
		return Vector3d.ZERO;
	}
}
