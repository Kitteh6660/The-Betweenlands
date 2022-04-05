package thebetweenlands.common.world.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class SpiritTreeKillToken {
	public final BlockPos pos;
	public final float strength;

	public SpiritTreeKillToken(BlockPos position, float strength) {
		this.pos = position;
		this.strength = strength;
	}

	public CompoundNBT save() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putLong("pos", this.pos.asLong());
		nbt.putFloat("strength", this.strength);
		return nbt;
	}

	public static SpiritTreeKillToken load(CompoundNBT nbt) {
		return new SpiritTreeKillToken(BlockPos.of(nbt.getLong("pos")), nbt.getFloat("strength"));
	}
}
