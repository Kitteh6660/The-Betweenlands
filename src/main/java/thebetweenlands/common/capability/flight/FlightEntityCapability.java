package thebetweenlands.common.capability.flight;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IFlightCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class FlightEntityCapability extends EntityCapability<FlightEntityCapability, IFlightCapability, PlayerEntity> implements IFlightCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "flight");
	}

	@Override
	protected Capability<IFlightCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_FLIGHT;
	}

	@Override
	protected Class<IFlightCapability> getCapabilityClass() {
		return IFlightCapability.class;
	}

	@Override
	protected FlightEntityCapability getDefaultCapabilityImplementation() {
		return new FlightEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}

	private boolean ring = false;
	private boolean flying = false;
	public int flightTime = 0;

	@Override
	public boolean isFlying() {
		return this.flying;
	}

	@Override
	public void setFlying(boolean flying) {
		this.flying = flying;
		this.setChanged();
	}

	@Override
	public int getFlightTime() {
		return this.flightTime;
	}

	@Override
	public void setFlightTime(int ticks) {
		this.flightTime = ticks;
		this.setChanged();
	}

	@Override
	public void setFlightRing(boolean ring) {
		this.ring = ring;
		this.setChanged();
	}

	@Override
	public boolean getFlightRing() {
		return this.ring;
	}

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putBoolean("flying", this.flying);
		nbt.putInt("time", this.flightTime);
		nbt.putBoolean("ring", this.ring);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.flying = nbt.getBoolean("flying");
		this.flightTime = nbt.getInt("time");
		this.ring = nbt.getBoolean("ring");
	}

	@Override
	public void writeTrackingDataToNBT(CompoundNBT nbt) {
		this.save(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(CompoundNBT nbt) {
		this.load(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}
