package thebetweenlands.api.runechain.io.types;

import java.util.function.DoubleSupplier;

import net.minecraft.util.math.BlockPos;

public class DynamicBlockTarget extends DynamicVectorTarget implements IBlockTarget {
	private BlockPos.Mutable pos = null;

	public DynamicBlockTarget(DoubleSupplier x, DoubleSupplier y, DoubleSupplier z) {
		super(x, y, z);
	}

	public DynamicBlockTarget(IVectorTarget pos) {
		super(() -> pos.x(), () -> pos.y(), () -> pos.z());
	}

	@Override
	public BlockPos block() {
		if(this.pos == null) {
			this.pos = new BlockPos.Mutable();
		}
		this.pos.set(this.x(), this.y(), this.z());
		return this.pos;
	}
}
