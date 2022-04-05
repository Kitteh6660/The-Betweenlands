package thebetweenlands.common.capability.lastkilled;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thebetweenlands.api.capability.ILastKilledCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class LastKilledCapability extends EntityCapability<LastKilledCapability, ILastKilledCapability, PlayerEntity> implements ILastKilledCapability, ISerializableCapability {
	
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "last_killed");
	}

	@Override
	protected Capability<ILastKilledCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_LAST_KILLED;
	}

	@Override
	protected Class<ILastKilledCapability> getCapabilityClass() {
		return ILastKilledCapability.class;
	}

	@Override
	protected LastKilledCapability getDefaultCapabilityImplementation() {
		return new LastKilledCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}



	private ResourceLocation lastKilled;

	@Override
	public ResourceLocation getLastKilled() {
		return this.lastKilled;
	}

	@Override
	public void setLastKilled(ResourceLocation key) {
		this.lastKilled = key;
		this.setChanged();
	}

	@Override
	public void save(CompoundNBT nbt) {
		if(this.lastKilled != null) {
			nbt.putString("lastKilled", this.lastKilled.toString());
		}
	}

	@Override
	public void load(CompoundNBT nbt) {
		if(nbt.contains("lastKilled", Constants.NBT.TAG_STRING)) {
			this.lastKilled = new ResourceLocation(nbt.getString("lastKilled"));
		} else {
			this.lastKilled = null;
		}
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
		return 20;
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();

		if(source instanceof EntityDamageSource) {
			Entity attacker = ((EntityDamageSource) source).getEntity();

			if(attacker != null) {
				ILastKilledCapability cap = (ILastKilledCapability) attacker.getCapability(CapabilityRegistry.CAPABILITY_LAST_KILLED, null);

				if(cap != null) {
					cap.setLastKilled(ForgeRegistries.ENTITIES.getKey(event.getEntityLiving().getType()));
				}
			}
		}
	}
}