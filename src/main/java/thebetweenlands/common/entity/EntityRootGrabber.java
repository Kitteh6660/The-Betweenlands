package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import io.netty.buffer.PacketBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.SpikeRenderer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleRootSpike;
import thebetweenlands.client.render.tile.RenderDecayPitHangingChain;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityRootGrabber extends Entity implements IEntityAdditionalSpawnData {
	public static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityRootGrabber.class, DataSerializers.FLOAT);
	public static final DataParameter<Boolean> RETRACT = EntityDataManager.createKey(EntityRootGrabber.class, DataSerializers.BOOLEAN);

	public static final byte EVENT_BROKEN = 40;
	public static final byte EVENT_HIT = 41;

	protected BlockPos origin;
	protected int delay;

	protected int maxAge = 12 * 20;

	protected int prevAttackTicks = 0;
	protected int attackTicks = 0;

	protected int prevRetractTicks = 0;
	protected int retractTicks = 0;

	protected boolean emergeSound = true;
	protected boolean retractSound = true;

	@Nullable
	protected LivingEntity grabbedEntity = null;

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public List<RootPart> modelParts;

	@OnlyIn(Dist.CLIENT)
	public static class RootPart {
		public float x, y, z;
		public float yaw, pitch;
		public float texWidth, texHeight;
		public float texU, texV;
		public ResourceLocation texture;
		
		public void render() {
			
		}
	}
	
	private boolean isChains = false;

	public EntityRootGrabber(World world, boolean isGears) {
		super(world);
		this.setSize(2, 2);
		this.noClip = true;
		this.isChains = isGears;
	}
	
	public EntityRootGrabber(World world) {
		this(world, false);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DAMAGE, 0.0F);
		this.entityData.define(RETRACT, false);
	}

	public float getDamage() {
		return this.dataManager.get(DAMAGE);
	}

	public void setDamage(float damage) {
		this.dataManager.set(DAMAGE, damage);

		if(damage >= 1.0F && !this.level.isClientSide()) {
			this.remove();
			this.world.setEntityState(this, EVENT_BROKEN);
		}
	}

	public void setPosition(BlockPos pos, int delay) {
		this.origin = pos;
		this.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		this.lastTickPosX = this.xOld = this.getX();
		this.lastTickPosY = this.yOld = this.getY();
		this.lastTickPosZ = this.zOld = this.getZ();

		this.delay = delay;
	}

	@OnlyIn(Dist.CLIENT)
	public void initRootModels() {
		if(this.modelParts == null) {
			this.modelParts = new ArrayList<>();

			int rings = 2 + this.world.rand.nextInt(2);

			for(int j = 0; j < rings; j++) {
				float radius = (this.width - 0.5F) / rings * j;
				int roots = this.isChains ? (2 + this.world.rand.nextInt(3)) : (5 + this.world.rand.nextInt(5));

				for(int i = 0; i < roots; i++) {
					float scale = 0.6F + this.random.nextFloat() * 0.2F;
					double angle = i * Math.PI * 2 / roots;
					
					Vector3d offset = new Vector3d(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
					
					RootPart part;
					
					if(!this.isChains) {
						final SpikeRenderer renderer = new SpikeRenderer(3, scale * 0.5F, scale, 1, this.random.nextLong(), -scale * 0.5F * 1.5F, 0, -scale * 0.5F * 1.5F).build(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL, Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(ParticleRootSpike.SPRITE.toString()));
						
						part = new RootPart() {
							@Override
							public void render() {
								renderer.render();
							}
						};

						part.texture = TextureMap.LOCATION_BLOCKS_TEXTURE;
						
						part.texWidth = renderer.getSprite().getMaxU() - renderer.getSprite().getMinU();
						part.texU = renderer.getSprite().getMinU();
						part.texHeight = renderer.getSprite().getMaxV() - renderer.getSprite().getMinV();
						part.texV = renderer.getSprite().getMinV();
						
						part.pitch = 30.0F;
					} else {
						final float[] yaws = new float[5];
						final float[] pitches = new float[5];
						
						for(int k = 0; k < 5; k++) {
							yaws[k] = (this.random.nextFloat() - 0.5F) * 360.0F;
							pitches[k] = (this.random.nextFloat() - 0.5F) * 40.0F;
						}
						
						part = new RootPart() {
							@Override
							public void render() {
								final float scale = 0.3F;
								
								GlStateManager.pushMatrix();
								GlStateManager.scale(scale, scale, scale);
								
								for(int w = 0; w < 5; w++) {
									GlStateManager.translate(0, 1, 0);
									GlStateManager.rotate(yaws[w], 0, 1, 0);
									GlStateManager.rotate(pitches[w], 1, 0, 0);
									GlStateManager.translate(0, -1, 0);
									
									RenderDecayPitHangingChain.CHAIN_MODEL.render(0.0625F);
									
									GlStateManager.translate(0, 1, 0);
								}
								
								GlStateManager.popMatrix();
							}
						};
						
						part.texture = RenderDecayPitHangingChain.CHAIN_TEXTURE;
						
						part.texWidth = 1;
						part.texU = 0;
						part.texHeight = 1;
						part.texV = 0;
						
						part.pitch = 15.0F;
					}
					
					part.x = (float)offset.x;
					part.y = (float)offset.y;
					part.z = (float)offset.z;
					part.yaw = -(float)Math.toDegrees(angle);
					
					this.modelParts.add(part);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getBrightnessForRender() {
		BlockPos.Mutable pos = new BlockPos.Mutable(MathHelper.floor(this.getX()), 0, MathHelper.floor(this.getZ()));

		if (this.world.isBlockLoaded(pos)) {
			pos.setY(MathHelper.floor(this.getY() + (double)this.getEyeHeight()));
			return this.world.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
	}

	public float getRootYOffset(float partialTicks) {
		float attackTicks = this.prevAttackTicks + (this.attackTicks - this.prevAttackTicks) * partialTicks;
		float retractTicks = this.prevRetractTicks + (this.retractTicks - this.prevRetractTicks) * partialTicks;

		float y;
		if(attackTicks < 5) {
			y = -2.5F + attackTicks / 5.0F;
		} else if(attackTicks >= this.delay) {
			y = -1.5F + Math.min(1.25F, (attackTicks - this.delay) / 0.5F);
		} else {
			y = -1.5F;
		}
		y = Math.max(-2.5F, y - retractTicks / 5.0F * 2.5F);
		return y;
	}

	@Override
	public void tick() {
		this.world.profiler.startSection("entityBaseTick");

		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();
		this.motionX = 0;
		this.motionZ = 0;
		this.lastTickPosX = this.getX();
		this.lastTickPosY = this.getY();
		this.lastTickPosZ = this.getZ();

		this.prevAttackTicks = this.attackTicks;
		this.prevRetractTicks = this.retractTicks;

		if(this.attackTicks >= this.delay) {
			if(this.attackTicks == this.delay) {
				if(!this.level.isClientSide()) {
					List<LivingEntity> targets = this.world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), e -> !e.getIsInvulnerable() && (e instanceof PlayerEntity == false || (!((PlayerEntity)e).isSpectator() && !((PlayerEntity)e).isCreative())));
					if(!targets.isEmpty()) {
						this.grabbedEntity = targets.get(this.random.nextInt(targets.size()));
						this.grabbedEntity.moveTo(this.getX(), this.getY() + 1, this.getZ(), this.grabbedEntity.yRot, this.grabbedEntity.xRot);
						this.grabbedEntity.motionX = 0;
						this.grabbedEntity.motionZ = 0;
						this.grabbedEntity.velocityChanged = true;
						if(this.grabbedEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)this.grabbedEntity).connection.setPlayerLocation(this.grabbedEntity.getX(), this.grabbedEntity.getY(), this.grabbedEntity.getZ(), this.grabbedEntity.yRot, this.grabbedEntity.xRot);
						}
					}
				} else {
					this.spawnExtendParticles();
					this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundRegistry.SPIRIT_TREE_SPIKE_TRAP, SoundCategory.HOSTILE, 1, 0.9F + this.random.nextFloat() * 0.2F, false);
				}
			}

			if(!this.level.isClientSide() && this.grabbedEntity != null && !this.dataManager.get(RETRACT)) {
				if(this.getBoundingBox().intersects(this.grabbedEntity.getBoundingBox())) {
					this.grabbedEntity.addEffect(new PotionEffect(ElixirEffectRegistry.ROOT_BOUND, 5, 0, true, false));
				} else {
					this.grabbedEntity = null;
				}
			}

			if(!this.level.isClientSide()) {
				if(this.grabbedEntity != null) {
					if(this.attackTicks >= this.delay + this.maxAge) {
						this.dataManager.set(RETRACT, true);
					}
				} else {
					if(this.attackTicks >= this.delay + 20) {
						this.dataManager.set(RETRACT, true);
					}
				}
			}

			if(this.dataManager.get(RETRACT)) {
				this.retractTicks++;

				if(!this.level.isClientSide() && this.getRootYOffset(1) <= -2.4F) {
					this.remove();
				}
			}
		}

		boolean retracting = this.dataManager.get(RETRACT);

		if(this.level.isClientSide() && (this.attackTicks <= 5 || retracting)) {
			this.spawnBlockDust();
			if(this.emergeSound && !retracting) {
				this.emergeSound = false;
				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundRegistry.SPIRIT_TREE_SPIKE_TRAP_EMERGE, SoundCategory.HOSTILE, 1, 0.9F + this.random.nextFloat() * 0.2F, false);
			}
			if(this.retractSound && retracting) {
				this.retractSound = false;
				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundRegistry.SPIRIT_TREE_SPIKE_TRAP_EMERGE, SoundCategory.HOSTILE, 1, 0.9F + this.random.nextFloat() * 0.2F, false);
			}
		}

		this.attackTicks++;

		this.firstUpdate = false;
		this.world.profiler.endSection();
	}

	@OnlyIn(Dist.CLIENT)
	protected void spawnExtendParticles() {
		if(!this.isChains) {
			for(int i = 0; i < 64; i++) {
				double dx = (this.random.nextDouble() * 2 - 1) * this.width / 2;
				double dy = this.height / 2.0D - 0.5D;
				double dz = (this.random.nextDouble() * 2 - 1) * this.width / 2;
				double mx = (this.random.nextDouble() - 0.5D) * 0.15D;
				double my = (this.random.nextDouble() - 0.5D) * 0.15D + 0.3D;
				double mz = (this.random.nextDouble() - 0.5D) * 0.15D;
				BlockPos pos = new BlockPos(this.getX() + dx, MathHelper.floor(this.getY() + dy), this.getZ() + dz);
				BlockState state = this.world.getBlockState(pos);
				if(!state.getBlock().isAir(state, this.world, pos)) {
					this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.getX() + dx, MathHelper.floor(this.getY() + dy) + 1 + this.random.nextDouble() * 0.5D, this.getZ() + dz, mx, my, mz, Block.getStateId(state));
				}
			}
	
			for(int i = 0; i < 8; i++) {
				double dx = (this.random.nextDouble() * 2 - 1) * this.width / 2;
				double dy = this.height / 2.0D - 0.5D;
				double dz = (this.random.nextDouble() * 2 - 1) * this.width / 2;
				double mx = (this.random.nextDouble() - 0.5D) * 0.2D;
				double my = (this.random.nextDouble() - 0.5D) * 0.2D + 0.4D;
				double mz = (this.random.nextDouble() - 0.5D) * 0.2D;
				ParticleRootSpike particle = (ParticleRootSpike) BLParticles.ROOT_SPIKE.spawn(this.world, this.getX() + dx, this.getY() + dy, this.getZ() + dz, ParticleArgs.get().withMotion(mx, my, mz).withScale(0.4F));
				particle.setUseSound(this.random.nextInt(3) == 0);
			}
		} else {
			this.spawnBlockDust();
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void spawnBlockDust() {
		for(int i = 0; i < 8; i++) {
			double dx = (this.random.nextDouble() * 2 - 1) * this.width / 2;
			double dy = this.height / 2.0D - 0.5D;
			double dz = (this.random.nextDouble() * 2 - 1) * this.width / 2;
			double mx = (this.random.nextDouble() - 0.5D) * 0.15D;
			double my = (this.random.nextDouble() - 0.5D) * 0.15D;
			double mz = (this.random.nextDouble() - 0.5D) * 0.15D;
			BlockPos pos = new BlockPos(this.getX() + dx, MathHelper.floor(this.getY() + dy), this.getZ() + dz);
			BlockState state = this.world.getBlockState(pos);
			if(!state.getBlock().isAir(state, this.world, pos)) {
				this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.getX() + dx, MathHelper.floor(this.getY() + dy) + 1, this.getZ() + dz, mx, my, mz, Block.getStateId(state));
			}
		}
	}
	
	public boolean isChains() {
		return this.isChains;
	}

	@Override
	public boolean handleWaterMovement() {
		return false;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean hitByEntity(Entity entity) {
		if(!this.level.isClientSide()) {
			if(entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative()) {
				this.setDamage(1.0F);
			} else {
				this.setDamage(this.getDamage() + 0.05F);
			}
			this.world.setEntityState(this, EVENT_HIT);
		}
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_BROKEN) {
			for(int i = 0; i < 128; i++) {
				double dx = (this.random.nextDouble() * 2 - 1) * this.width / 2.2F;
				double dy = (this.random.nextDouble() * 2 - 1) * this.height / 1.2F + this.height / 2;
				double dz = (this.random.nextDouble() * 2 - 1) * this.width / 2.2F;
				double mx = (this.random.nextDouble() - 0.5D) * 0.15D;
				double my = (this.random.nextDouble() - 0.5D) * 0.15D;
				double mz = (this.random.nextDouble() - 0.5D) * 0.15D;
				this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.getX() + dx, this.getY() + dy, this.getZ() + dz, mx, my, mz, Block.getStateId(BlockRegistry.LOG_SPIRIT_TREE.defaultBlockState()));
			}

			SoundType soundType = SoundType.WOOD;
			this.world.playSound(this.getX(), this.getY(), this.getZ(), soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
		} else if(id == EVENT_HIT) {
			for(int i = 0; i < 8; i++) {
				double dx = (this.random.nextDouble() * 2 - 1) * this.width / 4;
				double dy = (this.random.nextDouble() * 2 - 1) * this.height / 2 + this.height / 2;
				double dz = (this.random.nextDouble() * 2 - 1) * this.width / 4;
				double mx = (this.random.nextDouble() - 0.5D) * 0.15D;
				double my = (this.random.nextDouble() - 0.5D) * 0.15D;
				double mz = (this.random.nextDouble() - 0.5D) * 0.15D;
				this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.getX() + dx, this.getY() + dy, this.getZ() + dz, mx, my, mz, Block.getStateId(BlockRegistry.LOG_SPIRIT_TREE.defaultBlockState()));
			}

			SoundType soundType = SoundType.WOOD;
			this.world.playSound(this.getX(), this.getY(), this.getZ(), soundType.getHitSound(), SoundCategory.NEUTRAL, (soundType.getVolume() + 1.0F) / 8.0F, soundType.getPitch() * 0.5F, false);
		}
	}

	@Override
	public void writeSpawnData(PacketBuffer data) {
		data.writeLong(this.origin.toLong());
		data.writeInt(this.delay);
		data.writeInt(this.attackTicks);
		data.writeBoolean(this.isChains);
	}

	@Override
	public void readSpawnData(PacketBuffer data) {
		this.origin = BlockPos.of(data.readLong());
		this.delay = data.readInt();
		this.attackTicks = data.readInt();
		this.isChains = data.readBoolean();
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.delay = nbt.getInt("delay");
		this.origin = BlockPos.of(nbt.getLong("origin"));
		this.attackTicks = nbt.getInt("attackTicks");
		this.dataManager.set(DAMAGE, nbt.getFloat("damage"));
		this.isChains = nbt.getBoolean("isChains");
	}

	@Override
	public void save(CompoundNBT nbt) {
		nbt.putInt("delay", this.delay);
		nbt.setLong("origin", this.origin.toLong());
		nbt.putInt("attackTicks", this.attackTicks);
		nbt.putFloat("damage", this.dataManager.get(DAMAGE));
		nbt.putBoolean("isChains", this.isChains);
	}
}