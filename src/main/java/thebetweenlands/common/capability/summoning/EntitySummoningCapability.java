package thebetweenlands.common.capability.summoning;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.api.capability.ISummoningCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntitySummoningCapability extends EntityCapability<EntitySummoningCapability, ISummoningCapability, MonsterEntity> implements ISummoningCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "summoning");
	}

	@Override
	protected Capability<ISummoningCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_SUMMON;
	}

	@Override
	protected Class<ISummoningCapability> getCapabilityClass() {
		return ISummoningCapability.class;
	}

	@Override
	protected EntitySummoningCapability getDefaultCapabilityImplementation() {
		return new EntitySummoningCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}




	private boolean active;
	private int cooldownTicks;
	private int activeTicks;

	@Override
	public void setActive(boolean active) {
		this.active = active;
		this.setChanged();
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public int getCooldownTicks() {
		return this.cooldownTicks;
	}

	@Override
	public void setCooldownTicks(int ticks) {
		this.cooldownTicks = ticks;
	}

	@Override
	public int getActiveTicks() {
		return this.activeTicks;
	}

	@Override
	public void setActiveTicks(int ticks) {
		this.activeTicks = ticks;
		this.setChanged();
	}

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putInt("cooldown", this.cooldownTicks);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		this.cooldownTicks = nbt.getInt("cooldown");
	}

	@Override
	public void writeTrackingDataToNBT(CompoundNBT nbt) {
		nbt.putBoolean("active", this.active);
	}

	@Override
	public void readTrackingDataFromNBT(CompoundNBT nbt) {
		this.active = nbt.getBoolean("active");
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}