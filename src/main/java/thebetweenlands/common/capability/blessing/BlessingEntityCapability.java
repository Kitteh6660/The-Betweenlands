package thebetweenlands.common.capability.blessing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.IBlessingCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class BlessingEntityCapability extends EntityCapability<BlessingEntityCapability, IBlessingCapability, PlayerEntity> implements IBlessingCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "blessing");
	}

	@Override
	protected Capability<IBlessingCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_BLESSING;
	}

	@Override
	protected Class<IBlessingCapability> getCapabilityClass() {
		return IBlessingCapability.class;
	}

	@Override
	protected BlessingEntityCapability getDefaultCapabilityImplementation() {
		return new BlessingEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}






	private BlockPos location;
	private int dimension;

	@Override
	public boolean isBlessed() {
		return this.location != null;
	}

	@Override
	public BlockPos getBlessingLocation() {
		return this.location;
	}

	@Override
	public int getBlessingDimension() {
		return this.dimension;
	}

	@Override
	public void setBlessed(int dimension, BlockPos location) {
		this.location = location;
		this.dimension = dimension;
		this.setChanged();
	}

	@Override
	public void clearBlessed() {
		this.location = null;
		this.setChanged();
	}

	@Override
	public void save(CompoundNBT nbt) {
		if(this.location != null) {
			nbt.putInt("x", this.location.getX());
			nbt.putInt("y", this.location.getY());
			nbt.putInt("z", this.location.getZ());
		}
		nbt.putInt("dimension", this.dimension);
	}

	@Override
	public void load(CompoundNBT nbt) {
		if(nbt.contains("x", Constants.NBT.TAG_INT) && nbt.contains("y", Constants.NBT.TAG_INT) && nbt.contains("z", Constants.NBT.TAG_INT)) {
			this.location = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
		} else {
			this.location = null;
		}
		this.dimension = nbt.getInt("dimension");
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
		return 10;
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == Phase.START && !event.player.level.isClientSide()) {
			IBlessingCapability cap = (IBlessingCapability) event.player.getCapability(CapabilityRegistry.CAPABILITY_BLESSING, null);

			if(cap != null && cap.isBlessed() && cap.getBlessingLocation() != null && event.player.level.dimension() == cap.getBlessingDimension()) {
				event.player.addEffect(ElixirEffectRegistry.EFFECT_BLESSED.createEffect(205, 0));
			}
		}
	}
}
