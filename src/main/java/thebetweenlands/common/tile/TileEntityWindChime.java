package thebetweenlands.common.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.environment.IPredictableEnvironmentEvent;
import thebetweenlands.api.environment.IPredictableEnvironmentEvent.State;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleVisionOrb;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class TileEntityWindChime extends TileEntity implements ITickableTileEntity {
	
	public int renderTicks;

	public int ticksUntilChimes;
	public int prevChimeTicks;
	public int chimeTicks;

	@OnlyIn(Dist.CLIENT)
	private ParticleBatch particleBatch;

	private int fadeOutTimer = 0;

	private IPredictableEnvironmentEvent predictedEvent;
	private int predictedTimeUntilActivation;
	private ResourceLocation predictedEventVision;

	private ResourceLocation attunedEvent;

	public int getMaxPredictionTime() {
		return this.attunedEvent != null ? 12000 : 6000;
	}

	@Nullable
	public ResourceLocation getAttunedEvent() {
		return this.attunedEvent;
	}

	public void setAttunedEvent(@Nullable ResourceLocation event) {
		this.attunedEvent = event;
		this.setChanged();
		if(this.level != null) {
			BlockState stat = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, stat, stat, 3);
		}
	}

	@Nullable
	public ResourceLocation cycleAttunedEvent() {
		BLEnvironmentEventRegistry registry = BetweenlandsWorldStorage.forWorld(this.level).getEnvironmentEventRegistry();

		List<IPredictableEnvironmentEvent> choices = new ArrayList<>();

		int currentAttunedIndex = -1;

		int i = 0;
		for(Entry<ResourceLocation, IEnvironmentEvent> entry : registry.getEvents().entrySet()) {
			if(entry.getValue() instanceof IPredictableEnvironmentEvent) {
				choices.add((IPredictableEnvironmentEvent) entry.getValue());

				if(this.attunedEvent != null && this.attunedEvent.equals(entry.getKey())) {
					currentAttunedIndex = i;
				}

				i++;
			}
		}

		ResourceLocation newAttunement;

		if(currentAttunedIndex >= 0) {
			if(currentAttunedIndex == choices.size() - 1) {
				newAttunement = null;
			} else {
				newAttunement = choices.get(currentAttunedIndex + 1).getEventName();
			}
		} else if(choices.size() > 0) {
			newAttunement = choices.get(0).getEventName();
		} else {
			newAttunement = null;
		}

		this.setAttunedEvent(newAttunement);

		return newAttunement;
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public ParticleBatch getParticleBatch() {
		return this.particleBatch;
	}

	public IPredictableEnvironmentEvent getPredictedEvent() {
		return this.predictedEvent;
	}

	public int getPredictedTimeUntilActivation() {
		return this.predictedTimeUntilActivation;
	}

	@Nullable
	public ResourceLocation getPredictedEventVision() {
		return this.predictedEventVision;
	}

	@Override
	public void tick() {
		if(this.level.isClientSide()) {
			this.renderTicks++;

			if(this.ticksUntilChimes > 0) {
				this.ticksUntilChimes--;

				if(this.ticksUntilChimes <= 0) {
					this.ticksUntilChimes = 0;
				} else if(this.ticksUntilChimes <= 4) {
					this.chimeTicks += 25;
				}
			}

			this.prevChimeTicks = this.chimeTicks;
			this.chimeTicks = Math.max(this.chimeTicks - 1, 0);
		}

		BLEnvironmentEventRegistry registry = BetweenlandsWorldStorage.forWorld(this.level).getEnvironmentEventRegistry();

		int maxPredictionTime = this.getMaxPredictionTime();

		int nextPrediction = Integer.MAX_VALUE;
		IPredictableEnvironmentEvent nextEvent = null;
		ResourceLocation[] nextEventVisions = null;

		for(IEnvironmentEvent event : registry.getEvents().values()) {
			if(event instanceof IPredictableEnvironmentEvent && (this.attunedEvent == null || this.attunedEvent.equals(event.getEventName()))) {
				IPredictableEnvironmentEvent predictable = (IPredictableEnvironmentEvent) event;

				ResourceLocation[] visions = predictable.getVisionTextures();

				if(visions != null) {
					int prediction = predictable.estimateTimeUntil(State.ACTIVE);

					if(prediction > 0 && prediction < nextPrediction && prediction < maxPredictionTime) {
						nextPrediction = prediction;
						nextEvent = predictable;
						nextEventVisions = visions;
					}
				}
			}
		}

		if(!this.level.isClientSide()) {
			if(this.predictedEvent != null && this.predictedEvent != nextEvent) {
				this.predictedEvent = nextEvent;
				this.predictedTimeUntilActivation = -1;
			} else if(this.predictedEvent == null) {
				this.predictedEvent = nextEvent;
			}
			
			if(this.predictedEvent == nextEvent && nextEvent != null) {
				if(this.predictedTimeUntilActivation == -1 || this.predictedTimeUntilActivation >= maxPredictionTime && nextPrediction < maxPredictionTime) {
					this.triggerAdvancement();
				} else if(this.predictedTimeUntilActivation >= maxPredictionTime * 0.4f && nextPrediction < maxPredictionTime * 0.4f) {
					this.triggerAdvancement();
				} else if(this.predictedTimeUntilActivation >= maxPredictionTime * 0.2f && nextPrediction < maxPredictionTime * 0.2f) {
					this.triggerAdvancement();
				}

				this.predictedTimeUntilActivation = nextPrediction;
			} else {
				this.predictedTimeUntilActivation = -1;
			}
		} else {
			this.updateParticles(maxPredictionTime, nextPrediction, nextEvent, nextEventVisions);
		}
	}

	private void triggerAdvancement() {
		AxisAlignedBB aabb = new AxisAlignedBB(this.getBlockPos()).inflate(16);
		for(ServerPlayerEntity player : this.level.getEntitiesOfClass(ServerPlayerEntity.class, aabb, EntityPredicates.NO_SPECTATORS)) {
			if(player.distanceToSqr(this.getBlockPos()) <= 256) {
				AdvancementCriterionRegistry.WIND_CHIME_PREDICTION.trigger(player);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void updateParticles(int maxPredictionTime, int nextPrediction, IPredictableEnvironmentEvent nextEvent, ResourceLocation[] nextEventVisions) {
		if(this.predictedEvent != null && this.predictedEvent != nextEvent && this.fadeOutTimer < 20) {
			this.fadeOutTimer++;

			if(this.fadeOutTimer >= 20) {
				this.fadeOutTimer = 0;
				this.predictedEvent = nextEvent;
				this.predictedTimeUntilActivation = -1;

				this.predictedEventVision = nextEventVisions != null && nextEventVisions.length >= 1 ? nextEventVisions[this.level.random.nextInt(nextEventVisions.length)] : null;

				if(this.predictedEventVision != null) {
					this.particleBatch = ParticleVisionOrb.createParticleBatch(() -> this.predictedEventVision);
				} else {
					this.particleBatch = null;
				}
			}
		} else if(this.predictedEvent == null) {
			this.predictedEvent = nextEvent;

			if(nextEvent != null) {
				this.predictedEventVision = nextEventVisions != null && nextEventVisions.length >= 1 ? nextEventVisions[this.level.random.nextInt(nextEventVisions.length)] : null;

				if(this.predictedEventVision != null) {
					this.particleBatch = ParticleVisionOrb.createParticleBatch(() -> this.predictedEventVision);
				} else {
					this.particleBatch = null;
				}
			} else {
				this.predictedEventVision = null;
				this.particleBatch = null;
			}
		}

		if(this.predictedEvent == nextEvent && nextEvent != null) {
			if(this.predictedTimeUntilActivation == -1 || this.predictedTimeUntilActivation >= maxPredictionTime && nextPrediction < maxPredictionTime) {
				this.playChimes(nextEvent);
			} else if(this.predictedTimeUntilActivation >= maxPredictionTime * 0.4f && nextPrediction < maxPredictionTime * 0.4f) {
				this.playChimes(nextEvent);
			} else if(this.predictedTimeUntilActivation >= maxPredictionTime * 0.2f && nextPrediction < maxPredictionTime * 0.2f) {
				this.playChimes(nextEvent);
			}

			this.predictedTimeUntilActivation = nextPrediction;

			if(this.particleBatch != null) {
				Entity view = Minecraft.getInstance().getCameraEntity();

				if(view != null && view.distanceToSqr(this.getBlockPos()) < 256) {
					double cx = this.worldPosition.getX() + 0.5f;
					double cy = this.worldPosition.getY() + 0.2f;
					double cz = this.worldPosition.getZ() + 0.5f;

					double rx = this.level.random.nextFloat() - 0.5f;
					double ry = this.level.random.nextFloat() - 0.5f;
					double rz = this.level.random.nextFloat() - 0.5f;
					double len = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);
					rx /= len;
					ry /= len;
					rz /= len;

					int size = this.level.random.nextInt(3);
					rx *= 0.6f + size * 0.1f;
					ry *= 0.6f + size * 0.1f;
					rz *= 0.6f + size * 0.1f;

					ParticleVisionOrb particle = (ParticleVisionOrb) BLParticles.WIND_CHIME_VISION
							.create(this.level, cx + rx, cy + ry, cz + rz, ParticleFactory.ParticleArgs.get()
									.withData(cx, cy, cz, 150)
									.withMotion(0, 0, 0)
									.withColor(1.0f, 1.0f, 1.0f, 0.85f)
									.withScale(0.85f));

					particle.setAlphaFunction(() -> 1.0f - this.fadeOutTimer / 20.0f);

					BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
				}
			}
		} else {
			this.predictedTimeUntilActivation = -1;
		}

		if(this.particleBatch != null) {
			BatchedParticleRenderer.INSTANCE.updateBatch(this.particleBatch);
		}
	}

	private void playChimes(IPredictableEnvironmentEvent event) {
		this.ticksUntilChimes = 15;

		SoundEvent chimes = event.getChimesSound();

		if(chimes != null) {
			this.level.playLocalSound(this.getBlockPos().getX() + 0.5f, this.getBlockPos().getY() + 0.5f, this.getBlockPos().getZ() + 0.5f, chimes, SoundCategory.BLOCKS, 2, 1, false);
		}

		this.level.playLocalSound(this.getBlockPos().getX() + 0.5f, this.getBlockPos().getY() + 0.5f, this.getBlockPos().getZ() + 0.5f, SoundRegistry.CHIMES_WIND, SoundCategory.BLOCKS, 2, 1, false);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		if(nbt.contains("attunedEvent", Constants.NBT.TAG_STRING)) {
			this.setAttunedEvent(new ResourceLocation(nbt.getString("attunedEvent")));
		} else {
			this.setAttunedEvent(null);
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt = super.save(nbt);
		if(this.attunedEvent != null) {
			nbt.putString("attunedEvent", this.attunedEvent.toString());
		}
		return nbt;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		if(this.attunedEvent != null) {
			nbt.putString("attunedEvent", this.attunedEvent.toString());
		}
		return new SUpdateTileEntityPacket(this.getBlockPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getTag();
		if(nbt.contains("attunedEvent", Constants.NBT.TAG_STRING)) {
			this.setAttunedEvent(new ResourceLocation(nbt.getString("attunedEvent")));
		} else {
			this.setAttunedEvent(null);
		}
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		if(this.attunedEvent != null) {
			nbt.putString("attunedEvent", this.attunedEvent.toString());
		}
		return nbt;
	}
}
