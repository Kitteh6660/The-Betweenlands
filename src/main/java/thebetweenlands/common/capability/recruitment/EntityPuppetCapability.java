package thebetweenlands.common.capability.recruitment;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityPuppetCapability extends EntityCapability<EntityPuppetCapability, IPuppetCapability, LivingEntity> implements IPuppetCapability, ISerializableCapability {
	
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "puppet");
	}

	@Override
	protected Capability<IPuppetCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_PUPPET;
	}

	@Override
	protected Class<IPuppetCapability> getCapabilityClass() {
		return IPuppetCapability.class;
	}

	@Override
	protected EntityPuppetCapability getDefaultCapabilityImplementation() {
		return new EntityPuppetCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof LivingEntity;
	}


	private Entity puppeteer;
	private UUID puppeteerUUID;
	private int remainingTicks;
	private boolean stay;
	private boolean guard;
	private BlockPos guardHome;

	@Nullable
	private UUID ringUUID;
	private int recruitmentCost;

	@Override
	public void setPuppeteer(Entity puppeteer) {
		this.puppeteerUUID = puppeteer == null ? null : puppeteer.getUUID();
		this.puppeteer = puppeteer;
		this.setChanged();
	}

	@Override
	public boolean hasPuppeteer() {
		return this.puppeteerUUID != null;
	}

	@Override
	public Entity getPuppeteer() {
		if(this.puppeteerUUID == null) {
			this.puppeteer = null;
		} else if(this.puppeteer == null || !this.puppeteer.isAlive() || !this.puppeteer.getUUID().equals(this.puppeteerUUID)) {
			this.puppeteer = null;
			for(Entity entity : this.getEntity().level.getEntitiesOfClass(Entity.class, this.getEntity().getBoundingBox().inflate(24.0D, 24.0D, 24.0D))) {
				if(entity.getUUID().equals(this.puppeteerUUID)) {
					this.puppeteer = entity;
					break;
				}
			}
		}
		return this.puppeteer;
	}

	@Override
	public void setRemainingTicks(int ticks) {
		this.remainingTicks = ticks;
	}

	@Override
	public int getRemainingTicks() {
		return this.remainingTicks;
	}

	@Override
	public void setStay(boolean stay) {
		this.stay = stay;
		this.setChanged();
	}

	@Override
	public boolean getStay() {
		return this.stay;
	}

	@Override
	public void setGuard(boolean guard, @Nullable BlockPos pos) {
		this.guard = guard;
		this.guardHome = pos;
		this.setChanged();
	}

	@Override
	public boolean getGuard() {
		return this.guard;
	}

	@Override
	public BlockPos getGuardHome() {
		return this.guardHome;
	}

	@Override
	public void setRingUuid(@Nullable UUID uuid) {
		this.ringUUID = uuid;
	}

	@Override
	@Nullable
	public UUID getRingUuid() {
		return this.ringUUID;
	}

	@Override
	public void setRecruitmentCost(int cost) {
		this.recruitmentCost = cost;
	}

	@Override
	public int getRecruitmentCost() {
		return this.recruitmentCost;
	}

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putInt("ticks", this.remainingTicks);
		if(this.puppeteerUUID != null) {
			nbt.putUUID("puppeteer", this.puppeteerUUID);
		}
		nbt.putBoolean("stay", this.stay);
		if(this.ringUUID != null) {
			nbt.putUUID("ring", this.ringUUID);
		}
		nbt.putInt("recruitmentCost", this.recruitmentCost);
		nbt.putBoolean("guard", this.guard);
		if(this.guardHome != null) {
			nbt.putLong("guardHome", this.guardHome.asLong());
		}
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.remainingTicks = nbt.getInt("ticks");
		if(nbt.hasUUID("puppeteer")) {
			this.puppeteerUUID = nbt.getUUID("puppeteer");
		} else {
			this.puppeteerUUID = null;
		}
		this.stay = nbt.getBoolean("stay");
		if(nbt.hasUUID("ring")) {
			this.ringUUID = nbt.getUUID("ring");
		} else {
			this.ringUUID = null;
		}
		this.recruitmentCost = nbt.getInt("recruitmentCost");
		this.guard = nbt.getBoolean("guard");
		if(nbt.contains("guardHome", Constants.NBT.TAG_LONG)) {
			this.guardHome = BlockPos.of(nbt.getLong("guardHome"));
		} else {
			this.guardHome = null;
		}
	}

	@Override
	public void writeTrackingDataToNBT(CompoundNBT nbt) {
		if(this.puppeteerUUID != null) {
			nbt.putUUID(null, puppeteerUUID);
		}
		nbt.putBoolean("stay", this.stay);
		nbt.putBoolean("guard", this.guard);
	}

	@Override
	public void readTrackingDataFromNBT(CompoundNBT nbt) {
		if(nbt.hasUUID("puppeteer")) {
			this.puppeteerUUID = nbt.getUUID("puppeteer");
		} else {
			this.puppeteerUUID = null;
			this.puppeteer = null;
		}
		this.stay = nbt.getBoolean("stay");
		this.guard = nbt.getBoolean("guard");
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}