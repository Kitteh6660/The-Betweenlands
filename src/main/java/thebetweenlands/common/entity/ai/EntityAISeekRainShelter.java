package thebetweenlands.common.entity.ai;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class EntityAISeekRainShelter extends EntityAIBase {
	private final EntityCreature creature;
	private double shelterX;
	private double shelterY;
	private double shelterZ;
	private final double movementSpeed;
	private final World world;

	public EntityAISeekRainShelter(EntityCreature creature, double movementSpeed) {
		this.creature = creature;
		this.movementSpeed = movementSpeed;
		this.world = creature.world;
		this.setMutexBits(1);
	}

	@Override
	public boolean canUse() {
		BlockPos pos = new BlockPos(this.creature.getX(), this.creature.getBoundingBox().minY, this.creature.getZ());
		if (!this.world.isRainingAt(pos) && !this.world.isRainingAt(pos.above())) {
			return false;
		} else {
			Vector3d vec3d = this.findPossibleShelter();

			if (vec3d == null) {
				return false;
			} else {
				this.shelterX = vec3d.x;
				this.shelterY = vec3d.y;
				this.shelterZ = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		return !this.creature.getNavigation().noPath();
	}

	@Override
	public void start() {
		this.creature.getNavigation().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
	}

	@Nullable
	private Vector3d findPossibleShelter() {
		Random random = this.creature.getRandom();
		BlockPos pos = new BlockPos(this.creature.getX(), this.creature.getBoundingBox().minY, this.creature.getZ());

		for (int i = 0; i < 10; ++i) {
			BlockPos offsetPos = pos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

			if (!this.world.isRainingAt(offsetPos) && this.world.isEmptyBlock(offsetPos)) {
				return new Vector3d((double)offsetPos.getX() + 0.5D, (double)offsetPos.getY() + 0.5D, (double)offsetPos.getZ() + 0.5D);
			}
		}

		return null;
	}
}