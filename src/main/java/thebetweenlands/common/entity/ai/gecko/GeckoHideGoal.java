package thebetweenlands.common.entity.ai.gecko;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.*;

public abstract class GeckoHideGoal extends Goal {
	
	private final Comparator<BlockPos> closest = (a, b) -> {
		double aLength = a.getDistance(0, 0, 0);
		double bLength = b.getDistance(0, 0, 0);
		return aLength < bLength ? -1 : aLength > bLength ? 1 : 0;
	};

	protected final EntityGecko gecko;

	private double farSpeed;

	private double nearSpeed;

	private Path path;

	private PathNavigator navigator;

	private boolean bushBound;

	private BlockPos target;

	public GeckoHideGoal(EntityGecko gecko, double farSpeed, double nearSpeed) {
		this.gecko = gecko;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		navigator = gecko.getNavigation();
		setMutexBits(1);
	}

	@Nullable
	protected abstract Vector3d getFleeingCausePosition();

	protected abstract boolean shouldFlee();

	@Override
	public boolean canUse() {
		if (gecko.isHiding()) {
			return false;
		}
		if(!shouldFlee()) {
			return false;
		}
		Vector3d fleeingCausePos = this.getFleeingCausePosition();
		target = findNearBush();
		if (target == null) {
			Vector3d target = fleeingCausePos != null ? RandomPositionGenerator.findRandomTargetBlockAwayFrom(gecko, 16, 7, fleeingCausePos) : RandomPositionGenerator.findRandomTargetBlockAwayFrom(gecko, 16, 7, gecko.getDeltaMovement());
			if (target != null) {
				this.target = new BlockPos(target);
			}
		} else {
			bushBound = true;
		}
		if (target == null) {
			return false;
		} else if (!bushBound && fleeingCausePos != null && fleeingCausePos.distanceToSqr(target.getX(), target.getY(), target.getZ()) < fleeingCausePos.distanceToSqr(gecko.getDeltaMovement())) {
			return false;
		} else {
			path = navigator.getPathToPos(target);
			if (doesGeckoNeighborBush(target)) {
				gecko.setHidingBush(new BlockPos(target));
				gecko.startHiding();
				return false;
			}
			if (path != null) {
				PathPoint finalPathPoint = path.getFinalPathPoint();
				return finalPathPoint.x == target.getX() && finalPathPoint.y == target.getY() && finalPathPoint.z == target.getZ() || bushBound && doesPathDestinationNeighborBush(target, path);
			}
			return false;
		}
	}

	private boolean doesGeckoNeighborBush(BlockPos target) {
		BlockPos geckoPos = new BlockPos(gecko.position());
		for (Direction facing : Direction.values()) {
			if (target.offset(facing).equals(geckoPos)) {
				return true;
			}
		}
		return false;
	}

	private boolean doesPathDestinationNeighborBush(BlockPos target, Path path) {
		for (Direction facing : Direction.values()) {
			BlockPos nearTarget = new BlockPos(target.offset(facing));
			PathPoint finalPathPoint = path.getFinalPathPoint();
			if (finalPathPoint.x == nearTarget.getX() && finalPathPoint.y == nearTarget.getY() && finalPathPoint.z == nearTarget.getZ()) {
				return true;
			}
		}
		return false;
	}

	private BlockPos findNearBush() {
		final int radius = 8;
		BlockPos center = new BlockPos(gecko.position());
		Random rand = gecko.getRandom();
		List<BlockPos> bushes = new ArrayList<>();
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius / 2; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					BlockState state = gecko.level.getBlockState(pos);
					if (state.getBlock() == BlockRegistry.WEEDWOOD_BUSH.get() && gecko.level.isBlockNormalCube(pos.below(), false) && 
							gecko.level.getEntitiesOfClass(EntityGecko.class, new AxisAlignedBB(pos), e -> e != gecko).isEmpty()) {
						bushes.add(pos.subtract(center));
					}
				}
			}
		}
		if (bushes.size() == 0) {
			return null;
		}
		Collections.sort(bushes, closest);
		double targetDistance = bushes.get(0).distSqr(Vector3i.ZERO);
		final double epsilon = 1e-8;
		for (int i = 0, bushCount = bushes.size(); i < bushCount; i++) {
			boolean end = i == bushCount - 1;
			if (Math.abs(bushes.get(i).distSqr(Vector3i.ZERO) - targetDistance) > epsilon || end) {
				return bushes.get(rand.nextInt(end ? i + 1 : i)).add(center);
			}
		}
		throw new ConcurrentModificationException("I'm not sure how I feel about this...");
	}

	@Override
	public boolean canContinueToUse() {
		if(target != null && !gecko.level.getEntitiesOfClass(EntityGecko.class, new AxisAlignedBB(target), e -> e != gecko).isEmpty()) {
			return false;
		}
		return !navigator.noPath();
	}

	@Override
	public void start() {
		navigator.createPath(path, farSpeed);
		gecko.setHidingBush(target);
	}

	@Override
	public void stop() {
		if (bushBound && path.isFinished()) {
			gecko.startHiding();

		}
		bushBound = false;
	}

	@Override
	public void updateTask() {
		Vector3d fleeingCausePos = this.getFleeingCausePosition();
		if (fleeingCausePos != null && gecko.getDeltaMovement().squareDistanceTo(fleeingCausePos) < 49) {
			gecko.getNavigation().setSpeed(nearSpeed);
		} else {
			gecko.getNavigation().setSpeed(farSpeed);
		}
	}
}
