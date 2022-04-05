package thebetweenlands.common.capability.recruitment;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.api.capability.ProtectionShield;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityPuppeteerCapability extends EntityCapability<EntityPuppeteerCapability, IPuppeteerCapability, PlayerEntity> implements IPuppeteerCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "puppeteer");
	}

	@Override
	protected Capability<IPuppeteerCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_PUPPETEER;
	}

	@Override
	protected Class<IPuppeteerCapability> getCapabilityClass() {
		return IPuppeteerCapability.class;
	}

	@Override
	protected EntityPuppeteerCapability getDefaultCapabilityImplementation() {
		return new EntityPuppeteerCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}



	private int activatingTicks;
	private int activatingEntityId = -1;
	private Entity activatingEntity;

	private final ProtectionShield shield = new ProtectionShield() {
		@Override
		public void setActive(int index, boolean active) {
			super.setActive(index, active);
			EntityPuppeteerCapability.this.setChanged();
		}

		@Override
		public void unpackActiveData(int packedData) {
			super.unpackActiveData(packedData);
			EntityPuppeteerCapability.this.setChanged();
		}
	};
	private int shieldRotationTicks = 0;
	private int prevShieldRotationTicks = 0;

	@Override
	public List<Entity> getPuppets() {
		return this.getEntity().level.getEntitiesOfClass(Entity.class, this.getEntity().getBoundingBox().inflate(24.0D, 24.0D, 24.0D), entity -> {
			IPuppetCapability cap = (IPuppetCapability) entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
			return cap != null && cap.getPuppeteer() == this.getEntity();
		});
	}

	@Override
	public void setActivatingEntity(Entity entity) {
		this.activatingEntityId = entity == null ? -1 : entity.getEntityId();
		this.activatingEntity = entity;
		this.setChanged();
	}

	@Override
	public Entity getActivatingEntity() {
		if(this.activatingEntityId < 0) {
			this.activatingEntity = null;
		} else if(this.activatingEntity == null || !this.activatingEntity.isAlive() || this.activatingEntity.getEntityId() != this.activatingEntityId) {
			this.activatingEntity = this.getEntity().level.getEntityByID(this.activatingEntityId);
		}
		return this.activatingEntity;
	}

	@Override
	public int getActivatingTicks() {
		return this.activatingTicks;
	}

	@Override
	public void setActivatingTicks(int ticks) {
		this.activatingTicks = ticks;
	}

	@Override
	public ProtectionShield getShield() {
		return this.shield;
	}

	@Override
	public void updateShield() {
		for(int i = 0; i <= 19; i++) {
			if(this.shield.getAnimationTicks(i) == 0 && this.getEntity().level.random.nextInt(50) == 0)
				this.shield.setAnimationTicks(i, 40);
			if(this.shield.getAnimationTicks(i) > 0) {
				this.shield.setAnimationTicks(i, this.shield.getAnimationTicks(i) - 1);
				if(this.shield.getAnimationTicks(i) == 20)
					this.shield.setAnimationTicks(i, 0);
			}
		}

		this.prevShieldRotationTicks = this.shieldRotationTicks;
		if(this.shield.hasShield()) {
			this.shieldRotationTicks++;
			this.setChanged();
		}
	}

	@Override
	public int getShieldRotationTicks() {
		return this.shieldRotationTicks;
	}

	@Override
	public int getPrevShieldRotationTicks() {
		return this.prevShieldRotationTicks;
	}

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putInt("shieldActiveData", this.shield.packActiveData());
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.shield.unpackActiveData(nbt.getInt("shieldActiveData"));
	}

	@Override
	public void writeTrackingDataToNBT(CompoundNBT nbt) {
		nbt.putInt("activatingEntityId", this.activatingEntityId);
		if(this.shield.hasShield()) {
			nbt.putInt("shieldRotationTicks", this.shieldRotationTicks);
			nbt.putInt("shieldActiveData", this.shield.packActiveData());
		}
	}

	@Override
	public void readTrackingDataFromNBT(CompoundNBT nbt) {
		this.activatingEntityId = nbt.getInt("activatingEntityId");
		this.shieldRotationTicks = nbt.getInt("shieldRotationTicks");
		this.shield.unpackActiveData(nbt.getInt("shieldActiveData"));
	}

	@Override
	public int getTrackingTime() {
		return 5;
	}
}