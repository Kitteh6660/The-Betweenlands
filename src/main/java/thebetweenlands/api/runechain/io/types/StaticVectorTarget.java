package thebetweenlands.api.runechain.io.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class StaticVectorTarget implements IVectorTarget {
	private final double x, y, z;
	private Vec3 vec = null;

	public StaticVectorTarget(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public StaticVectorTarget(Vec3 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public StaticVectorTarget(BlockPos pos) {
		this.x = pos.getX() + 0.5f;
		this.y = pos.getY() + 0.5f;
		this.z = pos.getZ() + 0.5f;
	}
	
	public StaticVectorTarget(IVectorTarget pos) {
		this.x = pos.x();
		this.y = pos.y();
		this.z = pos.z();
	}
	
	@Override
	public double x() {
		return this.x;
	}

	@Override
	public double y() {
		return this.y;
	}

	@Override
	public double z() {
		return this.z;
	}

	@Override
	public Vec3 vec() {
		if(this.vec == null) {
			this.vec = new Vec3(this.x, this.y, this.z);
		}
		return this.vec;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}
}
