package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class ProcessedEntityCollisionBox<T extends Entity> extends AABB {
	private final T entity;

	public ProcessedEntityCollisionBox(T entity, BlockPos pos) {
		super(pos);
		this.entity = entity;
	}

	public ProcessedEntityCollisionBox(T entity, BlockPos pos1, BlockPos pos2) {
		super(pos1, pos2);
		this.entity = entity;
	}

	public ProcessedEntityCollisionBox(T entity, double x1, double y1, double z1, double x2, double y2, double z2) {
		super(x1, y1, z1, x2, y2, z2);
		this.entity = entity;
	}

	public ProcessedEntityCollisionBox(T entity, Vec3 min, Vec3 max) {
		super(min, max);
		this.entity = entity;
	}

	public ProcessedEntityCollisionBox(T entity, AABB other) {
		super(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
		this.entity = entity;
	}

	public T getEntity() {
		return this.entity;
	}

	@Nullable
	public abstract AABB process(@Nullable Entity other, AABB otherAabb);
}
