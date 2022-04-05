package thebetweenlands.common.capability.swarmed;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.api.capability.ISwarmedCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class SwarmedCapability extends EntityCapability<SwarmedCapability, ISwarmedCapability, PlayerEntity> implements ISwarmedCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "swarmed");
	}

	@Override
	protected Capability<ISwarmedCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_SWARMED;
	}

	@Override
	protected Class<ISwarmedCapability> getCapabilityClass() {
		return ISwarmedCapability.class;
	}

	@Override
	protected SwarmedCapability getDefaultCapabilityImplementation() {
		return new SwarmedCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof PlayerEntity;
	}


	private float strength;
	private int hurtTimer;

	private int damageTimer;
	private float damage;

	private float lastYaw, lastPitch, lastYawDelta, lastPitchDelta;

	@Override
	public void setSwarmedStrength(float strength) {
		float newStrength = MathHelper.clamp(strength, 0, 1);
		if(newStrength != this.strength) {
			this.strength = newStrength;
			this.setChanged();
		}
	}

	@Override
	public float getSwarmedStrength() {
		return this.strength;
	}

	@Override
	public void setHurtTimer(int timer) {
		if(timer != this.hurtTimer) {
			this.hurtTimer = timer;
			this.setChanged();
		}
	}

	@Override
	public void setDamage(float damage) {
		this.damage = damage;
	}

	@Override
	public float getDamage() {
		return this.damage;
	}

	@Override
	public void setDamageTimer(int timer) {
		this.damageTimer = timer;
	}

	@Override
	public int getDamageTimer() {
		return this.damageTimer;
	}

	@Override
	public int getHurtTimer() {
		return this.hurtTimer;
	}

	@Override
	public void setLastRotations(float yaw, float pitch) {
		this.lastYaw = yaw;
		this.lastPitch = pitch;
	}

	@Override
	public float getLastYaw() {
		return this.lastYaw;
	}

	@Override
	public float getLastPitch() {
		return this.lastPitch;
	}

	@Override
	public void setLastRotationDeltas(float yaw, float pitch) {
		this.lastYawDelta = yaw;
		this.lastPitchDelta = pitch;
	}

	@Override
	public float getLastYawDelta() {
		return this.lastYawDelta;
	}

	@Override
	public float getLastPitchDelta() {
		return this.lastPitchDelta;
	}

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putFloat("strength", this.strength);
		nbt.putInt("hurtTimer", this.hurtTimer);
		nbt.putInt("damageTimer", this.damageTimer);
		nbt.putFloat("damage", this.damage);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.strength = nbt.getFloat("strength");
		this.hurtTimer = nbt.getInt("hurtTimer");
		this.damageTimer = nbt.getInt("damageTimer");
		this.damage = nbt.getFloat("damage");
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

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END && !event.player.level.isClientSide()) {
			ISwarmedCapability cap = (ISwarmedCapability) event.player.getCapability(CapabilityRegistry.CAPABILITY_SWARMED, null);

			if(cap != null) {
				if(cap.getHurtTimer() > 0) {
					cap.setHurtTimer(cap.getHurtTimer() - 1);
				}

				if(cap.getSwarmedStrength() > 0) {
					if(event.player.isInWater() || event.player.isOnFire()) {
						cap.setSwarmedStrength(0);
					} else if(event.player.swinging || (event.player.getY() - event.player.yOld) > 0.1f || event.player.isCrouching()) {
						cap.setSwarmedStrength(cap.getSwarmedStrength() - 0.01f);
						cap.setHurtTimer(5);
						event.player.setPose(Pose.STANDING);
					}

					float dYaw = MathHelper.wrapDegrees(event.player.yRot - cap.getLastYaw());
					float dPitch = MathHelper.wrapDegrees(event.player.xRot - cap.getLastPitch());
					float ddYaw = MathHelper.wrapDegrees(dYaw - cap.getLastYawDelta());
					float ddPitch = MathHelper.wrapDegrees(dPitch - cap.getLastPitchDelta());
					float ddRot = MathHelper.sqrt(ddYaw * ddYaw + ddPitch * ddPitch);

					if(ddRot > 30) {
						cap.setSwarmedStrength(cap.getSwarmedStrength() - (ddRot - 30) * 0.001f);
					}

					cap.setLastRotations(event.player.yRot, event.player.xRot);
					cap.setLastRotationDeltas(dYaw, dPitch);
				}

				if(cap.getSwarmedStrength() < 0.1f) {
					cap.setSwarmedStrength(cap.getSwarmedStrength() - 0.0005f);
				} else {
					cap.setDamageTimer(cap.getDamageTimer() + 1);

					if(cap.getDamageTimer() > 15 + (1.0f - cap.getSwarmedStrength()) * 75) {
						cap.setDamageTimer(0);

						event.player.hurt(new DamageSource("bl.swarm").bypassArmor(), cap.getDamage());
					}
				}
			}
		}
	}
}