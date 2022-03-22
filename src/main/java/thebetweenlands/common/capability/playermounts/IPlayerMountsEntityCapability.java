package thebetweenlands.common.capability.playermounts;

import java.util.List;

import net.minecraft.nbt.CompoundNBT;

public interface IPlayerMountsEntityCapability {
	public List<CompoundNBT> getQueuedPassengers();
}
