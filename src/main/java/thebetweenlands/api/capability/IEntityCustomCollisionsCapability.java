package thebetweenlands.api.capability;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public interface IEntityCustomCollisionsCapability {
	@FunctionalInterface
	public static interface EntityCollisionPredicate {
		public static EntityCollisionPredicate ALL = (entity, aabb, otherEntity, otherAabb) -> true;
		public static EntityCollisionPredicate NONE = (entity, aabb, otherEntity, otherAabb) -> false;

		public boolean isColliding(Entity entity, AABB aabb, Entity otherEntity, AABB otherAabb);
	}

	@FunctionalInterface
	public static interface BlockCollisionPredicate {
		public static EntityCollisionPredicate ALL = (entity, aabb, pos, state) -> true;
		public static EntityCollisionPredicate NONE = (entity, aabb, pos, state) -> false;

		public boolean isColliding(Entity entity, AABB aabb, MutableBlockPos pos, BlockState state, @Nullable AABB blockAabb);
	}

	@FunctionalInterface
	public static interface CollisionBoxHelper {
		public void getBlockCollisions(Entity entity, AABB aabb, EntityCollisionPredicate entityPredicate, BlockCollisionPredicate blockPredicate, List<AABB> collisionBoxes);
	}

	public void getCustomCollisionBoxes(CollisionBoxHelper collisionBoxHelper, AABB aabb, List<AABB> collisionBoxes);
	
	public boolean isPhasing();
	
	public double getViewObstructionCheckDistance();
	
	public double getViewObstructionDistance();
	
	public double getObstructionCheckDistance();
	
	public double getObstructionDistance();
}
