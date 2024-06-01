package thebetweenlands.api.runechain.initiation;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class UseInitiationPhase extends InitiationPhase {
	private final BlockPos block;
	private final Direction facing;
	private final Vec3i pos;

	public UseInitiationPhase(BlockPos block, Direction facing, Vec3i pos) {
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
	public Vec3i getPosition() {
		return this.pos;
	}
}