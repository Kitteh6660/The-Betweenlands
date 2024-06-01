package thebetweenlands.api.entity;

import java.util.List;

import net.minecraft.world.phys.AABB;

public interface IEntityCustomBlockCollisions {
	public void getCustomCollisionBoxes(AABB aabb, List<AABB> collisionBoxes);
}
