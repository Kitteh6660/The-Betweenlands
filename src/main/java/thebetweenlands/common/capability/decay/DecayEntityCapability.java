package thebetweenlands.common.capability.decay;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.GameruleRegistry;

public class DecayEntityCapability extends EntityCapability<DecayEntityCapability, IDecayCapability, PlayerEntity> implements IDecayCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "decay");
	}

	@Override
	protected Capability<IDecayCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_DECAY;
	}

	@Override
	protected Class<IDecayCapability> getCapabilityClass() {
		return IDecayCapability.class;
	}

	@Override
	protected DecayEntityCapability getDefaultCapabilityImplementation() {
		return new DecayEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}






	private DecayStats decayStats = new DecayStats(this);
	private int removedHealth = 0;
	
	@Override
	public DecayStats getDecayStats() {
		return this.decayStats;
	}
	
	@Override
	public int getRemovedHealth() {
		return this.removedHealth;
	}

	@Override
	public void setRemovedHealth(int removedHealth) {
		this.removedHealth = removedHealth;
	}
	
	@Override
	public float getMaxPlayerHealth(int decayLevel) {
		return Math.min(20f + BetweenlandsConfig.GENERAL.decayMinHealth - decayLevel, 20f);
	}

	@Override
	public float getMaxPlayerHealthPercentage(int decayLevel) {
		return BetweenlandsConfig.GENERAL.decayMinHealthPercentage + (1.0f - BetweenlandsConfig.GENERAL.decayMinHealthPercentage) * (Math.min(26f - decayLevel, 20f) - 6f) / (20.0f - 6f);
	}
	
	@Override
	public boolean isDecayEnabled() {
		return this.getEntity().level.getDifficulty() != Difficulty.PEACEFUL &&
				GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_DECAY) &&
				BetweenlandsConfig.GENERAL.useDecay &&
				(this.getEntity().level.dimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId || BetweenlandsConfig.GENERAL.decayDimensionListSet.contains(this.getEntity().level.dimension())) && 
				!this.getEntity().isCreative() && 
				!this.getEntity().abilities.invulnerable;
	}

	@Override
	public void save(CompoundNBT nbt) {
		this.decayStats.writeNBT(nbt);
		nbt.putInt("removedHealth", this.removedHealth);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.decayStats.readNBT(nbt);
		this.removedHealth = nbt.getInt("removedHealth");
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
}
