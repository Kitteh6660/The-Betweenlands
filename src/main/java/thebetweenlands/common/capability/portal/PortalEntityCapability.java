package thebetweenlands.common.capability.portal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class PortalEntityCapability extends EntityCapability<PortalEntityCapability, IPortalCapability, PlayerEntity> implements IPortalCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "portal");
	}

	@Override
	protected Capability<IPortalCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_PORTAL;
	}

	@Override
	protected Class<IPortalCapability> getCapabilityClass() {
		return IPortalCapability.class;
	}

	@Override
	protected PortalEntityCapability getDefaultCapabilityImplementation() {
		return new PortalEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}

	private boolean inPortal = false;
	private boolean wasTeleported = false;
	private int ticksUntilTeleport = 0;

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putBoolean("inPortal", this.inPortal);
		nbt.putInt("ticks", this.ticksUntilTeleport);
		nbt.putBoolean("wasTeleported", this.wasTeleported);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.inPortal = nbt.getBoolean("inPortal");
		this.ticksUntilTeleport = nbt.getInt("ticks");
		this.wasTeleported = nbt.getBoolean("wasTeleported");
	}

	@Override
	public boolean isInPortal() {
		return this.inPortal;
	}

	@Override
	public void setInPortal(boolean inPortal) {
		this.inPortal = inPortal;
	}

	@Override
	public int getTicksUntilTeleport() {
		return this.ticksUntilTeleport;
	}

	@Override
	public void setTicksUntilTeleport(int ticks) {
		this.ticksUntilTeleport = ticks;
	}

	@Override
	public boolean wasTeleported() {
		return this.wasTeleported;
	}

	@Override
	public void setWasTeleported(boolean wasTeleported) {
		this.wasTeleported = wasTeleported;
	}
}
