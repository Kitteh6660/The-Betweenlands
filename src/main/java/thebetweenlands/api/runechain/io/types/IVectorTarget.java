package thebetweenlands.api.runechain.io.types;

import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface IVectorTarget {
	public Vec3 vec();

	public default double x() {
		return this.vec().x;
	}

	public default double y() {
		return this.vec().y;
	}

	public default double z() {
		return this.vec().z;
	}

	/**
	 * Whether the target is dynamic, i.e. can change its position over time
	 * @return
	 */
	public default boolean isDynamic() {
		return true;
	}
}
