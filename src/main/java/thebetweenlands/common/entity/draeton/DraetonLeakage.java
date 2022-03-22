package thebetweenlands.common.entity.draeton;

import net.minecraft.util.math.vector.Vector3d;

public class DraetonLeakage {
	public final Vector3d pos, dir;
	
	public int age;
	
	public DraetonLeakage(Vector3d pos, Vector3d dir, int age) {
		this.pos = pos;
		this.dir = dir;
		this.age = age;
	}
}
