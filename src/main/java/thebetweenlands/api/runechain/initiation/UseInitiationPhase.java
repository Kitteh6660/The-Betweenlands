package thebetweenlands.api.runechain.initiation;

import javax.annotation.Nullable;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class UseInitiationPhase extends InitiationPhase {
	private final BlockPos block;
	private final Direction facing;
	private final Vector3d pos;

	public UseInitiationPhase(BlockPos block, Direction facing, Vector3d pos) {
		this.block = block;
		this.facing = facing;
		this.pos = pos;
	}

	public UseInitiationPhase() {
		this(null, null, null);
	}

	@Nullable
	public BlockPos getBlock() {
		return this.block;
	}

	@Nullable
	public Direction getFacing() {
		return this.facing;
	}

	@Nullable
	public Vector3d getPosition() {
		return this.pos;
	}
}