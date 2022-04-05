package thebetweenlands.common.entity.ai.puppet;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityAIGuardHome extends Goal {
	
	private final CreatureEntity entity;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private final double movementSpeed;
	private final int maxDist;

	public EntityAIGuardHome(CreatureEntity entity, double speed, int maxDist) {
		this.entity = entity;
		this.movementSpeed = speed;
		this.maxDist = maxDist;
		this.setMutexBits(3);
	}

	@Override
	public boolean canUse() {
		IPuppetCapability cap = this.entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

		if(cap != null && cap.hasPuppeteer() && cap.getGuard()) {
			BlockPos homePos = cap.getGuardHome();

			if(homePos == null || homePos.distSqr(this.entity.getX(), this.entity.getY(), this.entity.getZ()) < this.maxDist * this.maxDist) {
				return false;
			} else {
				boolean hadHome = this.entity.hasHome();
				BlockPos prevHome = this.entity.getHomePosition();
				float prevRange = this.entity.getMaximumHomeDistance();

				this.entity.setHomePosAndDistance(homePos, this.maxDist);

				Vector3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 16, 7, new Vector3d(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D));

				if(hadHome) {
					this.entity.setHomePosAndDistance(prevHome, (int)prevRange);
				} else {
					this.entity.detachHome();
				}

				if (vec3d == null) {
					return false;
				} else {
					this.movePosX = vec3d.x;
					this.movePosY = vec3d.y;
					this.movePosZ = vec3d.z;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return !this.entity.getNavigation().noPath();
	}

	@Override
	public void start() {
		this.entity.getNavigation().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
	}
}