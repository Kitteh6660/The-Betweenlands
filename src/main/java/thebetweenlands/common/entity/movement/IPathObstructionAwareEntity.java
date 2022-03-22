package thebetweenlands.common.entity.movement;

import javax.annotation.Nullable;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public interface IPathObstructionAwareEntity {
	/**
	 * Called when the mob tries to move along the path but is obstructed
	 */
	public void onPathingObstructed(Direction facing);

	/**
	 * Returns how many ticks the mob can be stuck before the path is considered to be obstructed
	 * @return
	 */
	public default int getMaxStuckCheckTicks() {
		return 40;
	}
	
	/**
	 * Returns the pathing malus for building a bridge
	 * @param entity
	 * @param pos
	 * @param fallPathPoint
	 * @return
	 */
	public default float getBridgePathingMalus(MobEntity entity, BlockPos pos, @Nullable PathPoint fallPathPoint) {
		return -1.0f;
	}

	/**
	 * Returns teh pathing malus for the given {@link PathNodeType} and block position
	 * @param type
	 * @param pos
	 * @return
	 */
	public default float getPathingMalus(MobEntity entity, PathNodeType nodeType, BlockPos pos) {
		return entity.getPathPriority(nodeType);
	}
}
