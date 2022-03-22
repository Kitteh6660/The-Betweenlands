package thebetweenlands.common.tile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IBlessingCapability;
import thebetweenlands.api.capability.ILastKilledCapability;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.EntityBLLightningBolt;
import thebetweenlands.common.entity.FalseXPOrbEntity;
import thebetweenlands.common.entity.EntityResurrection;
import thebetweenlands.common.handler.PlayerRespawnHandler;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.PlayerUtil;

public class TileEntitySimulacrum extends TileEntityRepeller implements ITickable {
	public static enum Effect {
		NONE("none", 0),
		RANDOM("random", 1),
		THEM("them", 2),
		IMITATION("imitation", 3),
		WEAKNESS("weakness", 4),
		RESURRECTION("resurrection", 5),
		SANCTUARY("sanctuary", 6),
		FERTILITY("fertility", 7),
		ATTRACTION("attraction", 8),
		WISP("wisp", 9),
		WISDOM("wisdom", 10),
		BLESSING("blessing", 11);

		public final String name;
		public final int id;

		private Effect(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public static Effect byId(int id) {
			for(Effect effect : Effect.values()) {
				if(effect.id == id) {
					return effect;
				}
			}
			return Effect.NONE;
		}
	}

	private static final Method m_getAmbientSound;
	private static final Method m_getDeathSound;
	private static final Method m_getHurtSound;

	static {
		m_getAmbientSound = ReflectionHelper.findMethod(MobEntity.class, "getAmbientSound", "func_184639_G");
		m_getAmbientSound.setAccessible(true);

		m_getDeathSound = ReflectionHelper.findMethod(LivingEntity.class, "getDeathSound", "func_184615_bR");
		m_getDeathSound.setAccessible(true);

		m_getHurtSound = ReflectionHelper.findMethod(LivingEntity.class, "getHurtSound", "func_184601_bQ", DamageSource.class);
		m_getHurtSound.setAccessible(true);
	}

	private final MobSpawnerLogicBetweenlands mireSnailSpawner = new MobSpawnerLogicBetweenlands() {
		{
			this.setNextEntityName("thebetweenlands:mire_snail");
			this.setDelayRange(600, 1200);
			this.setSpawnInAir(false);
			this.setParticles(false);
			this.setCheckRange(24);
			this.setMaxSpawnCount(1);
		}

		@Override
		public void broadcastEvent(int eventID) { }

		@Override
		public World getSpawnerWorld() {
			return TileEntitySimulacrum.this.world;
		}

		@Override
		public int getSpawnerX() {
			return TileEntitySimulacrum.this.getPos().getX();
		}

		@Override
		public int getSpawnerY() {
			return TileEntitySimulacrum.this.getPos().getY();
		}

		@Override
		public int getSpawnerZ() {
			return TileEntitySimulacrum.this.getPos().getZ();
		}

		@Override
		protected void spawnParticles() { }

		@Override
		public MobSpawnerLogicBetweenlands setNextEntityName(String name) {
			super.setNextEntityName(name);
			TileEntitySimulacrum te = TileEntitySimulacrum.this;
			if(te != null && te.world != null) {
				BlockState blockState = te.world.getBlockState(te.pos);
				te.world.sendBlockUpdated(te.pos, blockState, blockState, 3);
			}
			return this;
		}

		@Override
		public MobSpawnerLogicBetweenlands setNextEntity(String name) {
			super.setNextEntity(name);
			TileEntitySimulacrum te = TileEntitySimulacrum.this;
			if(te != null && te.world != null) {
				BlockState blockState = te.world.getBlockState(te.pos);
				te.world.sendBlockUpdated(te.pos, blockState, blockState, 3);
			}
			return this;
		}

		@Override
		public MobSpawnerLogicBetweenlands setNextEntity(WeightedSpawnerEntity entity) {
			super.setNextEntity(entity);
			TileEntitySimulacrum te = TileEntitySimulacrum.this;
			if(te != null && te.world != null) {
				BlockState blockState = te.world.getBlockState(te.pos);
				te.world.sendBlockUpdated(te.pos, blockState, blockState, 3);
			}
			return this;
		}

		@Override
		public MobSpawnerLogicBetweenlands setEntitySpawnList(List<WeightedSpawnerEntity> entitySpawnList) {
			super.setEntitySpawnList(entitySpawnList);
			TileEntitySimulacrum te = TileEntitySimulacrum.this;
			if(te != null && te.world != null) {
				BlockState blockState = te.world.getBlockState(te.pos);
				te.world.sendBlockUpdated(te.pos, blockState, blockState, 3);
			}
			return this;
		}
	};

	private Effect effect = Effect.NONE;
	private Effect secondaryEffect = Effect.NONE;

	private boolean isActive = false;
	private String customName = "";

	private int soundCooldown = 0;

	private TileEntityRepeller sourceRepeller;

	private boolean readFromNbt = false;

	@Override
	public void setWorld(World worldIn) {
		super.setWorld(worldIn);

		//Prevent spawner from spawning immediately after placement
		if(!this.readFromNbt) { 
			this.mireSnailSpawner.resetTimer();
		}
	}

	@Override
	public void update() {
		if(this.isActive()) {
			this.updateEffects(this.effect);
			this.updateEffects(this.secondaryEffect);
		}		
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound = super.save(compound);

		compound.putInt("effectId", this.effect.id);
		compound.putInt("secondaryEffectId", this.effect.id);
		compound.putBoolean("isActive", this.isActive);
		compound.putString("customName", this.customName);

		this.mireSnailSpawner.save(compound);

		return compound;
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.readFromNBT(compound);

		this.readFromNbt = true;

		this.effect = Effect.byId(compound.getInt("effectId"));
		this.secondaryEffect = Effect.byId(compound.getInt("secondaryEffectId"));
		this.isActive = compound.getBoolean("isActive");
		this.customName = compound.getString("customName");

		this.mireSnailSpawner.readFromNBT(compound);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getPos(), 1, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("effectId", this.effect.id);
		nbt.putInt("secondaryEffectId", this.secondaryEffect.id);
		return nbt;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);

		CompoundNBT nbt = pkt.getNbtCompound();
		this.effect = Effect.byId(nbt.getInt("effectId"));
		this.secondaryEffect = Effect.byId(nbt.getInt("secondaryEffectId"));
	}

	@Override
	public void setChanged() {
		super.setChanged();
		BlockState state = this.world.getBlockState(this.pos);
		this.world.sendBlockUpdated(this.pos, state, state, 2);
	}

	public void setActive(boolean active) {
		this.isActive = active;
		BlockState state = this.world.getBlockState(this.pos);
		this.world.sendBlockUpdated(this.pos, state, state, 2);
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setCustomName(String name) {
		this.customName = name;
	}

	public String getCustomName() {
		return this.customName;
	}

	public Effect getEffect() {
		return this.effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
		this.setSecondaryEffect(Effect.NONE);
		this.setChanged();
	}

	public Effect getSecondaryEffect() {
		return this.secondaryEffect;
	}

	public void setSecondaryEffect(Effect effect) {
		this.secondaryEffect = effect;
		this.setChanged();
	}

	private void updateEffects(Effect effect) {
		switch(effect) {
		case RANDOM:
			if(!this.level.isClientSide() && this.world.getGameTime() % 20 == 0 && this.world.rand.nextInt(200) == 0) {
				this.setSecondaryEffect(Effect.values()[this.world.rand.nextInt(Effect.values().length)]);
			}
			break;
		case THEM:
			if(this.level.isClientSide() && this.world.getGameTime() % 20 == 0 && this.world.rand.nextInt(5) == 0) {
				this.spawnThem();
			}
			break;
		case IMITATION:
			if(this.level.isClientSide() && this.world.getGameTime() % 20 == 0 && --this.soundCooldown <= 0) {
				this.soundCooldown = this.world.rand.nextInt(30) + 30;
				this.playImitationSound();
			}
			break;
		case SANCTUARY:
			this.setRadiusState(3);

			TileEntityRepeller repeller = getClosestActiveTile(TileEntityRepeller.class, this, this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 18.0D, null, null);

			if(repeller != null && repeller.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) > repeller.getRadius(1) * repeller.getRadius(1)) {
				repeller = null;
			}

			if(repeller instanceof TileEntitySimulacrum) {
				this.sourceRepeller = repeller = ((TileEntitySimulacrum) repeller).sourceRepeller;
			} else {
				this.sourceRepeller = repeller;
			}

			if(this.sourceRepeller != null && (this.sourceRepeller.isInvalid() || !this.world.isBlockLoaded(this.sourceRepeller.getPos()))) {
				this.sourceRepeller = null;

				if(repeller == this.sourceRepeller) {
					repeller = null;
				}
			}

			int prevFuel = 0;

			if(repeller != null) {
				this.running = repeller.running;
				this.hasShimmerstone = repeller.hasShimmerstone;
				prevFuel = this.fuel = repeller.fuel;
			} else {
				this.running = false;
				this.hasShimmerstone = false;
				this.fuel = 0;
			}

			super.update();

			if(repeller != null) {
				if(this.fuel < prevFuel) {
					repeller.fuel -= (prevFuel - this.fuel);
					repeller.setChanged();
					this.setChanged();
				}

				this.running = repeller.running;
				this.hasShimmerstone = repeller.hasShimmerstone;
				this.fuel = repeller.fuel;
			}

			break;
		case FERTILITY:

			if(!this.level.isClientSide() && BetweenlandsWorldStorage.forWorld(this.world).getEnvironmentEventRegistry().heavyRain.isActive()) {
				this.mireSnailSpawner.updateSpawner();
			}

			break;
		case WISP:

			if(!this.level.isClientSide() && this.world.isEmptyBlock(this.pos.above()) && this.world.getGameTime() % 200 == 0 && BetweenlandsWorldStorage.forWorld(this.world).getEnvironmentEventRegistry().auroras.isActive()) {
				this.world.setBlockState(this.pos.above(), BlockRegistry.WISP.defaultBlockState());
			}

			break;
		case WISDOM:

			if(!this.level.isClientSide() && this.world.getGameTime() % 160 == 0 && this.world.getEntitiesOfClass(FalseXPOrbEntity.class, new AxisAlignedBB(this.pos).grow(16.0D)).isEmpty()) {
				PlayerEntity player = this.world.getClosestPlayer(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 8.0D, false);

				if(player != null) {
					int xp = this.world.rand.nextInt((int)Math.min(Math.abs(this.world.rand.nextGaussian() * Math.min(player.experienceTotal, 1200)), 2400) + 1);

					if(xp < player.experienceTotal) {
						UUID playerUuid = player.getUUID();

						int negativeXp = xp;

						float multiplier = 1.0f + MathHelper.clamp((float)this.world.rand.nextGaussian(), -0.5f, 0.5f) - 0.025f;

						xp = (int)(xp * multiplier);

						List<Entity> orbs = new ArrayList<>();

						for(int i = 0; i < 32; i++) {
							int negativeOrbXp = 0;
							if(negativeXp > 0) {
								if(i != 31) {
									negativeOrbXp = this.world.rand.nextInt(negativeXp / 8 + 1) + 1;
									negativeXp -= negativeOrbXp;
								} else {
									negativeOrbXp = negativeXp;
								}
							}

							int orbXp = 0;
							if(xp > 0) {
								if(i != 31) {
									orbXp = this.world.rand.nextInt(xp / 8 + 1) + 1;
									xp -= orbXp;
								} else {
									orbXp = xp;
								}
							}

							Entity negativeOrb = null;
							Entity orb = null;

							if(this.world.rand.nextBoolean()) {
								if(negativeOrbXp > 0) negativeOrb = new FalseXPOrbEntity(this.world, this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D, -negativeOrbXp, playerUuid);
								if(orbXp > 0) orb = new FalseXPOrbEntity(this.world, this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D, orbXp, playerUuid);
							} else {
								orb = new FalseXPOrbEntity(this.world, this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D, orbXp, playerUuid);
								if(orbXp > 0) if(negativeOrbXp > 0) negativeOrb = new FalseXPOrbEntity(this.world, this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D, -negativeOrbXp, playerUuid);
							}

							if(orb != null) {
								orbs.add(orb);
							}

							if(negativeOrb != null) {
								orbs.add(negativeOrb);
							}
						}

						Collections.shuffle(orbs);

						float stepH = (float)Math.PI * 2 / (float)orbs.size() * 6;
						float stepV = (float)Math.PI * 2 / (float)orbs.size() / 4;

						for(int i = 0; i < orbs.size(); i++) {
							double hc = Math.cos(stepH * i);
							double hs = Math.sin(stepH * i);

							double vc = Math.cos(stepV * i);
							double vs = Math.sin(stepV * i);

							double dx = hs * vc;
							double dy = vs;
							double dz = hc * vc;

							Entity orb = orbs.get(i);

							orb.motionX = dx * 0.25f;
							orb.motionY = 0.1f + dy * 0.35f;
							orb.motionZ = dz * 0.25f;

							orb.moveTo(orb.getX() + dx * 0.65f, orb.getY() + dy * 2f, orb.getZ() + dz * 0.65f, 0, 0);

							this.world.spawnEntity(orb);
						}
					}
				}
			}
			break;
		case BLESSING:
			if(this.world.getGameTime() % 4 == 0) {
				PlayerEntity player = this.world.getClosestPlayer(this.pos.getX() + 0.5, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 4, e -> {
					PlayerEntity p = (PlayerEntity) e;
					if(!p.isSpectator()) {
						IBlessingCapability cap = p.getCapability(CapabilityRegistry.CAPABILITY_BLESSING, null);
						return cap != null && (!cap.isBlessed() || cap.getBlessingDimension() != p.dimension || !this.pos.equals(cap.getBlessingLocation()));
					}
					return false;
				});

				if(player != null) {
					TileEntityOfferingTable offering = getClosestActiveTile(TileEntityOfferingTable.class, null, this.world, player.getX(), player.getY(), player.getZ(), 2.5f, null, stack -> !stack.isEmpty() && stack.getItem() == ItemRegistry.SPIRIT_FRUIT);

					if(offering != null) {
						if(!this.level.isClientSide() && this.world.rand.nextInt(40) == 0) {
							IBlessingCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_BLESSING, null);

							if(cap != null) {
								ItemStack stack = offering.getStack();
								stack.shrink(1);
								offering.setStack(stack);

								cap.setBlessed(player.dimension, this.pos);

								player.sendStatusMessage(new TranslationTextComponent("chat.simulacrum.blessed"), true);
							}
						} else if(this.level.isClientSide()) {
							this.spawnBlessingParticles(this.world.getGameTime() * 0.025f, offering.getPos().getX() + 0.5f, offering.getPos().getY() + 0.4f, offering.getPos().getZ() + 0.5f);
						}
					}
				}
			}

			break;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnBlessingParticles(float rot, float x, float y, float z) {
		float step = (float)Math.PI * 2 / 20;
		for(int i = 0; i < 20; i++) {
			float dx = (float)Math.cos(rot + step * i);
			float dz = (float)Math.sin(rot + step * i);

			BLParticles.CORRUPTED.spawn(this.world, x, y, z,
					ParticleArgs.get()
					.withMotion(dx * 0.05f, 0.2f, dz * 0.05f)
					.withData(80, true, 0.1f, true));
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnThem() {
		Entity viewer = Minecraft.getInstance().getRenderViewEntity();

		if(viewer != null && this.pos.distanceSq(viewer.getX(), viewer.getY(), viewer.getZ()) < 16 * 16 && FogHandler.hasDenseFog(this.world) && (FogHandler.getCurrentFogEnd() + FogHandler.getCurrentFogStart()) / 2 < 65.0F) {
			BlockPos pos = viewer.getPosition();

			if(SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(this.world.getBlockState(pos.below())) || SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(this.world.getBlockState(pos.below(2))) || SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(this.world.getBlockState(pos.below(3)))) {
				double xOff = this.world.rand.nextInt(50) - 25;
				double zOff = this.world.rand.nextInt(50) - 25;
				double sx = viewer.getX() + xOff;
				double sz = viewer.getZ() + zOff;
				double sy = pos.getY() + this.world.rand.nextFloat() * 0.75f;
				BLParticles.THEM.spawn(this.world, sx, sy, sz);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void playImitationSound() {
		Entity viewer = Minecraft.getInstance().getRenderViewEntity();

		if(viewer != null && this.pos.distanceSq(viewer.getX(), viewer.getY(), viewer.getZ()) < 16 * 16) {
			ILastKilledCapability cap = viewer.getCapability(CapabilityRegistry.CAPABILITY_LAST_KILLED, null);

			if(cap != null) {
				ResourceLocation key = cap.getLastKilled();

				if(key != null) {
					Entity entity = EntityList.createEntityByIDFromName(key, viewer.world);

					if(entity != null) {
						SoundEvent sound;

						int r = viewer.world.rand.nextInt(20);

						try {
							if(r <= 15) {
								sound = (SoundEvent) m_getAmbientSound.invoke(entity);
							} else if(r <= 19) {
								sound = (SoundEvent) m_getHurtSound.invoke(entity, DamageSource.GENERIC);
							} else {
								sound = (SoundEvent) m_getDeathSound.invoke(entity);
							}
						} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}

						if(sound != null) {
							this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, sound, SoundCategory.BLOCKS, 0.75f, 0.9f, false);
						}

						entity.remove();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends TileEntity> T getClosestActiveTile(Class<T> tileCls, @Nullable TileEntity exclude, World world, double x, double y, double z, double range, @Nullable Effect effect, @Nullable Predicate<ItemStack> offeringPredicate) {
		int sx = (MathHelper.floor(x - range) >> 4);
		int sz = (MathHelper.floor(z - range) >> 4);
		int ex = (MathHelper.floor(x + range) >> 4);
		int ez = (MathHelper.floor(z + range) >> 4);

		T closest = null;

		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = world.getChunkProvider().getLoadedChunk(cx, cz);

				if(chunk != null) {
					for(Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
						TileEntity tile = entry.getValue();

						if(tile != exclude && tileCls.isInstance(tile)) {
							double dstSq = entry.getKey().distanceSq(x, y, z);

							if(dstSq <= range * range && (closest == null || dstSq <= closest.getPos().distanceSq(x, y, z)) &&
									(effect == null || tile instanceof TileEntitySimulacrum == false || (((TileEntitySimulacrum) tile).getEffect() == effect && ((TileEntitySimulacrum) tile).isActive())) &&
									(offeringPredicate == null || tile instanceof TileEntityOfferingTable == false || offeringPredicate.test(((TileEntityOfferingTable) tile).getStack()))) {
								closest = (T) tile;
							}
						}
					}
				}
			}
		}

		return closest;
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		BlockPos pos = event.getPos();
		PlayerEntity player = event.getEntityPlayer();

		TileEntitySimulacrum simulacrum = getClosestActiveTile(TileEntitySimulacrum.class, null, player.world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 16.0D, Effect.WEAKNESS, null);

		if(simulacrum != null) {
			double dst = simulacrum.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

			float multiplier = (float) (0.075f + 0.925f * dst / 256.0D);

			event.setNewSpeed(event.getNewSpeed() * multiplier);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntityLiving();

		if(!entity.world.isClientSide()) {
			if(entity instanceof PlayerEntity == false && entity.world.rand.nextInt(4) == 0) {
				TileEntitySimulacrum simulacrum = getClosestActiveTile(TileEntitySimulacrum.class, null, entity.world, entity.getX(), entity.getY(), entity.getZ(), 16.0D, Effect.RESURRECTION, null);

				if(simulacrum != null) {
					entity.setDropItemsWhenDead(false);

					CompoundNBT nbt = new CompoundNBT();

					if(entity.writeToNBTAtomically(nbt)) {
						EntityResurrection resurrection = new EntityResurrection(entity.world, nbt, () -> !entity.isDead ? entity.getPositionVector() : null, 60 + entity.world.rand.nextInt(60));
						resurrection.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
						entity.world.spawnEntity(resurrection);
					}
				}
			} else if(entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;
				IBlessingCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_BLESSING, null);

				if(cap != null && cap.isBlessed()) {
					BlockPos location = cap.getBlessingLocation();

					if(location != null && cap.getBlessingDimension() == entity.dimension) {
						event.setCanceled(true);

						entity.setHealth(entity.getMaxHealth() * 0.5f);

						int droppedExperience = player.experienceTotal / 3;
						player.experience = 0;
						player.experienceLevel = 0;
						player.experienceTotal = 0;
						while(droppedExperience > 0) {
							int xp = EntityXPOrb.getXPSplit(droppedExperience);
							droppedExperience -= xp;
							EntityXPOrb xpOrb = new EntityXPOrb(player.world, player.getX(), player.getY(), player.getZ(), xp);
							xpOrb.delayBeforeCanPickup = 40;
							player.world.spawnEntity(xpOrb);
						}

						if(entity.world.rand.nextBoolean()) {
							BlockPos spawnPoint = PlayerRespawnHandler.getSpawnPointNearPos(entity.world, location, 8, false, 4, 0);

							if(spawnPoint != null) {
								if(entity.getDistanceSq(spawnPoint) > 24) {
									playThunderSounds(entity.world, entity.getX(), entity.getY(), entity.getZ());
									entity.world.spawnEntity(new EntityBLLightningBolt(entity.world, entity.getX(), entity.getY(), entity.getZ(), 1, false, true));
								}

								PlayerUtil.teleport(entity, spawnPoint.getX() + 0.5D, spawnPoint.getY(), spawnPoint.getZ() + 0.5D);

								playThunderSounds(entity.world, entity.getX(), entity.getY(), entity.getZ());
								entity.world.spawnEntity(new EntityBLLightningBolt(entity.world, entity.getX(), entity.getY(), entity.getZ(), 1, false, true));

								entity.addEffect(new EffectInstance(Effects.BLINDNESS, 60, 1));
							} else if(entity instanceof ServerPlayerEntity) {
								((ServerPlayerEntity) entity).sendStatusMessage(new TranslationTextComponent("chat.simulacrum.obstructed"), true);
							}
						} else {
							playThunderSounds(entity.world, entity.getX(), entity.getY(), entity.getZ());
							entity.world.spawnEntity(new EntityBLLightningBolt(entity.world, entity.getX(), entity.getY(), entity.getZ(), 1, false, true));
						}
						
						if(player instanceof ServerPlayerEntity) {
							AdvancementCriterionRegistry.REVIVED_BLESSED.trigger((ServerPlayerEntity) player);
						}

						cap.clearBlessed();
					}
				}
			}
		}
	}

	protected static void playThunderSounds(World world, double x, double y, double z) {
		world.playSound(null, x, y, z, SoundRegistry.RIFT_CREAK, SoundCategory.PLAYERS, 2, 1);
		world.playSound(null, x, y, z, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.75F, 0.75F);
	}
}
