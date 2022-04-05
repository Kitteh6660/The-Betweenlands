package thebetweenlands.common.entity.ai.gecko;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import thebetweenlands.common.entity.mobs.EntityGecko;

public class GeckoHideFromRainGoal extends GeckoHideGoal {
	
	public GeckoHideFromRainGoal(EntityGecko gecko, double speed) {
		super(gecko, speed, speed);
	}

	@Override
	protected boolean shouldFlee() {
		return this.gecko.level.isRainingAt(new BlockPos(this.gecko.blockPosition()));
	}

	@Override
	protected Vector3d getFleeingCausePosition() {
		return null;
	}
}
