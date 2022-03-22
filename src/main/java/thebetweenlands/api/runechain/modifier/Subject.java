package thebetweenlands.api.runechain.modifier;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

/**
 * A subject to be affected by rune effect modifiers. This subject can be various things, such as an entity, a block or even just a position.
 */
public class Subject 
{
	private final Vector3d position;
	private final BlockPos block;
	private final Entity entity;

	public Subject(@Nullable Vector3d position, @Nullable BlockPos block, @Nullable Entity entity) {
		this.position = position;
		this.block = block;
		this.entity = entity;
	}

	public Subject(Vector3d position) {
		this(position, null, null);
	}

	public Subject(BlockPos block) {
		this(null, block, null);
	}

	public Subject(Entity entity) {
		this(null, null, entity);
	}

	/**
	 * Returns the position described by this subject
	 * @return
	 */
	@Nullable
	public Vector3d getPosition() {
		if(this.position == null && this.entity != null) {
			return new Vector3d(this.entity.getX(), this.entity.getY() + this.entity.getBbHeight() * 0.5f, this.entity.getZ());
		}
		return this.position;
	}

	/**
	 * Returns the block described by this subject
	 * @return
	 */
	@Nullable
	public BlockPos getBlock() {
		return this.block;
	}

	/**
	 * Returns the entity described by this subject
	 * @return
	 */
	@Nullable
	public Entity getEntity() {
		return this.entity;
	}

	/**
	 * Returns whether this subject is still active
	 * @return
	 */
	public boolean isActive() {
		return this.position != null || this.block != null || (this.entity != null && this.entity.isAlive());
	}
}