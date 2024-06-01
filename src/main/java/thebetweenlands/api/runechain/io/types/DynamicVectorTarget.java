package thebetweenlands.api.runechain.io.types;

import java.util.function.DoubleSupplier;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class DynamicVectorTarget implements IVectorTarget {
	private final DoubleSupplier x, y, z;

	public DynamicVectorTarget(DoubleSupplier x, DoubleSupplier y, DoubleSupplier z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public DynamicVectorTarget(Entity entity) {
		this(() -> entity.getX(), () -> entity.getY(), () -> entity.getZ());
	}
	
	@Override
	public double x() {
		return this.x.getAsDouble();
	}

	@Override
	public double y() {
		return this.y.getAsDouble();
	}

	@Override
	public double z() {
		return this.z.getAsDouble();
	}

	@Override
	public Vec3 vec() {
		return new Vec3(this.x.getAsDouble(), this.y.getAsDouble(), this.z.getAsDouble());
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
