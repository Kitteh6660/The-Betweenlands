package thebetweenlands.api.runechain.io.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class StaticBlockTarget extends StaticVectorTarget implements IBlockTarget {
	private BlockPos block = null;

	public StaticBlockTarget(double x, double y, double z) {
		super(x, y, z);
	}

	public StaticBlockTarget(Vec3 pos) {
		super(pos);
	}

	public StaticBlockTarget(BlockPos pos) {
		super(pos);
	}
	
	public StaticBlockTarget(IVectorTarget pos) {
		super(pos);
	}

	@Override
	public BlockPos block() {
		if(this.block == null) {
			this.block = new BlockPos((int)this.x(), (int)this.y(), (int)this.z());
		}
		return this.block;
	}
}
