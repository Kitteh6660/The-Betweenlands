package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import io.netty.buffer.PacketBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Direction;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability.BlockCollisionPredicate;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability.EntityCollisionPredicate;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityCustomBlockCollisions;
import thebetweenlands.api.entity.IEntityWithLootModifier;
import thebetweenlands.client.render.model.SpikeRenderer;
import thebetweenlands.client.render.model.entity.ModelBoulderSprite;
import thebetweenlands.common.handler.CustomEntityCollisionsHandler;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class EntityBoulderSprite extends EntityMob implements IEntityCustomBlockCollisions, IEntityAdditionalSpawnData, IEntityWithLootModifier, IEntityBL {
	public static final byte EVENT_STEP = 40;

	protected static final UUID ROLLING_ATTACK_MODIFIER_ATTRIBUTE_UUID = UUID.fromString("6c403225-c522-4d69-aa2c-e7c67463a8c7");

	protected static final DataParameter<Float> ROLL_SPEED = EntityDataManager.defineId(EntityBoulderSprite.class, DataSerializers.FLOAT);

	protected Direction hideoutEntrance = null;
	protected BlockPos hideout = null;

	protected boolean isAiHiding = false;

	private float prevRollAnimationInAirWeight = 0.0F;
	private float prevRollAnimation = 0.0F;
	private float prevRollAnimationWeight = 0.0F;
	private float rollAnimationInAirWeight = 0.0F;
	private float rollAnimationSpeed = 0.0F;
	private float rollAnimation = 0.0F;
	private float rollAnimationWeight = 0.0F;

	protected boolean rollSoundPlayed = false;

	protected double rollingSpeed = 0;
	protected int rollingTicks = 0;
	protected int rollingAccelerationTime = 0;
	protected int rollingDecelerationTime = 0;
	protected int rollingDuration = 0;
	protected Vector3d rollingDir = null;

	protected long stalactitesSeed = 0;
	protected int numStalactites = 0;
	protected int[] stalactiteHeights;

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public List<SpikeRenderer> stalactites;

	public EntityBoulderSprite(World worldIn) {
		super(worldIn);
		this.experienceValue = 10;
		this.setSize(0.9F, 1.2F);
		this.setStalactitesSeed(worldIn.rand.nextLong());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ROLL_SPEED, 0.0F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(50);
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(28);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4);
	}

	@Override
	protected void registerGoals() {
		this.targetSelector.addGoal(0, new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, false) {
			@Override
			public double getTargetDistance() {
				return 12.0D;
			}
		});
		this.targetSelector.addGoal(1, new EntityAIHurtByTarget(this, true) {
			@Override
			public void start() {
				if(EntityBoulderSprite.this.getAttackTarget() != EntityBoulderSprite.this.getRevengeTarget()) {
					//Cancel hiding
					EntityBoulderSprite.this.setHideout(null);
				}
				super.start();
			}
		});

		this.goalSelector.addGoal(0, new AIRollTowardsTargetFromHideout(this, 8, 1.2D));
		this.goalSelector.addGoal(1, new AIMoveToHideout(this, 1.5D));
		this.goalSelector.addGoal(2, new AIHide(this, 0.8D));
		this.goalSelector.addGoal(3, new AIFindRandomHideoutFlee(this, 8));
		this.goalSelector.addGoal(4, new AIRollTowardsTarget(this));
		this.goalSelector.addGoal(5, new EntityAIAttackMelee(this, 1, true) {
			@Override
			public boolean canUse() {
				return !EntityBoulderSprite.this.isHiddenOrInWall() && super.canUse();
			}

			@Override
			public boolean canContinueToUse() {
				return !EntityBoulderSprite.this.isHiddenOrInWall() && super.canContinueToUse();
			}
		});
		this.goalSelector.addGoal(6, new EntityAIWander(this, 0.9D));
		this.goalSelector.addGoal(7, new AIFindRandomHideout(this, 8, 10));
	}

	protected void setStalactitesSeed(long seed) {
		this.stalactitesSeed = seed;

		Random seededRng = new Random();
		seededRng.setSeed(this.stalactitesSeed);

		if(seededRng.nextInt(2) == 0) {
			this.numStalactites = 2 + seededRng.nextInt(5);
		} else {
			this.numStalactites = 0;
		}

		this.stalactiteHeights = new int[this.numStalactites];
		for(int i = 0; i < this.numStalactites; i++) {
			this.stalactiteHeights[i] = 1 + seededRng.nextInt(3);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void initStalactiteModels() {
		if(this.stalactites == null) {
			this.stalactites = new ArrayList<>();

			Random seededRng = new Random();
			seededRng.setSeed(this.stalactitesSeed);

			if(this.numStalactites > 0) {
				TextureMap altas = Minecraft.getInstance().getTextureMapBlocks();

				for(int i = 0; i < numStalactites; i++) {
					Vector3d offset = new Vector3d(-0.08D + seededRng.nextDouble() * 0.5D - 0.25D, 0.5D, -0.15D + seededRng.nextDouble() * 0.5D - 0.25D);
					float scale = (0.2F + seededRng.nextFloat() * 0.4F) * 0.6F;
					SpikeRenderer renderer = new SpikeRenderer(this.stalactiteHeights[i], scale, scale, scale, seededRng.nextLong(), offset.x, offset.y, offset.z)
							.build(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL, altas.getAtlasSprite(ModelBoulderSprite.StalactitesModelRenderer.SPRITE_BOTTOM.toString()), altas.getAtlasSprite(ModelBoulderSprite.StalactitesModelRenderer.SPRITE_MID.toString()));
					this.stalactites.add(renderer);
				}
			}
		}
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeLong(this.stalactitesSeed);
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		this.setStalactitesSeed(buffer.readLong());
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);

		if(this.hideout != null) {
			nbt.setLong("hideout", this.hideout.toLong());
		}

		if(this.hideoutEntrance != null) {
			nbt.putString("hideoutEntrance", this.hideoutEntrance.getName());
		}

		nbt.setDouble("rollingSpeed", this.rollingSpeed);
		nbt.putInt("rollingTicks", this.rollingTicks);
		nbt.putInt("rollingAccelerationTime", this.rollingAccelerationTime);
		nbt.putInt("rollingDecelerationTime", this.rollingDecelerationTime);
		nbt.putInt("rollingDuration", this.rollingDuration);

		if(this.rollingDir != null) {
			nbt.setDouble("rollingDirX", this.rollingDir.x);
			nbt.setDouble("rollingDirY", this.rollingDir.y);
			nbt.setDouble("rollingDirZ", this.rollingDir.z);
		}

		nbt.setLong("stalactitesSeed", this.stalactitesSeed);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);

		if(nbt.contains("hideout", Constants.NBT.TAG_LONG)) {
			this.hideout = BlockPos.of(nbt.getLong("hideout"));
		}

		if(nbt.contains("hideoutEntrance", Constants.NBT.TAG_STRING)) {
			this.hideoutEntrance = Direction.byName(nbt.getString("hideoutEntrance"));
		}

		this.rollingSpeed = nbt.getDouble("rollingSpeed");
		this.rollingTicks = nbt.getInt("rollingTicks");
		this.rollingAccelerationTime = nbt.getInt("rollingAccelerationTime");
		this.rollingDecelerationTime = nbt.getInt("rollingDecelerationTime");
		this.rollingDuration = nbt.getInt("rollingDuration");

		if(nbt.contains("rollingDirX", Constants.NBT.TAG_DOUBLE) && nbt.contains("rollingDirY", Constants.NBT.TAG_DOUBLE) && nbt.contains("rollingDirZ", Constants.NBT.TAG_DOUBLE)) {
			this.rollingDir = new Vector3d(nbt.getDouble("rollingDirX"), nbt.getDouble("rollingDirY"), nbt.getDouble("rollingDirZ"));
		}

		this.setStalactitesSeed(nbt.getLong("stalactitesSeed"));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if(!this.isHiddenOrInWall()) {
			return SoundRegistry.BOULDER_SPRITE_LIVING;
		}
		return null;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.BOULDER_SPRITE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.BOULDER_SPRITE_DEATH;
	}

	protected void playRollSound() {
		this.world.playLocalSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.BOULDER_SPRITE_ROLL, SoundCategory.HOSTILE, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		float distanceWalked = this.distanceWalkedOnStepModified;

		if(!this.isRolling()) {
			if(this.isHiddenOrInWall()) {
				distanceWalked = this.distanceWalkedOnStepModified + 0.9F;
			} else {
				distanceWalked = this.distanceWalkedOnStepModified + 0.5F;
			}
			this.playSound(SoundEvents.BLOCK_STONE_HIT, 0.6F, 1.0F);
		} else {
			distanceWalked = this.distanceWalkedOnStepModified + 0.7F;
			this.playSound(SoundEvents.BLOCK_STONE_HIT, 0.35F, 1.0F);
			this.playSound(SoundEvents.BLOCK_GRAVEL_BREAK, 0.08F, 1.0F);
		}

		this.distanceWalkedOnStepModified = distanceWalked;

		this.world.setEntityState(this, EVENT_STEP);
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_STEP && this.isHiddenOrInWall()) {
			BlockState state = this.world.getBlockState(this.getPosition());
			if(!state.getBlock().isAir(state, this.world, this.getPosition())) {
				int stateId = Block.getStateId(state);
				for(int i = 0; i < 24; i++) {
					double dx = this.random.nextDouble() - 0.5D;
					double dy = this.random.nextDouble();
					double dz = this.random.nextDouble() - 0.5D;
					this.world.addParticle(ParticleTypes.BLOCK_DUST, this.getX() + this.motionX + dx, this.getY() + this.motionY + dy, this.getZ() + this.motionZ + dz, dx * 0.2D, dy * 0.2D, dz * 0.2D, stateId);
				}
			}
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.BOULDER_SPRITE;
	}

	@Override
	public Map<String, Float> getLootModifiers(LootContext context, boolean isEntityProperty) {
		int stalactiteBlocks = this.stalactiteHeights != null ? IntStream.of(this.stalactiteHeights).sum() : 0;
		return ImmutableMap.<String, Float>builder()
				.put("stalactites", (float) this.numStalactites)
				.put("stalactite_blocks", (float) stalactiteBlocks)
				.build();
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) { }

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getBrightnessForRender() {
		if(this.isEntityInsideOpaqueBlock()) {
			AxisAlignedBB renderAABB = this.getBoundingBox().inflate(0.1D, 0.1D, 0.1D);
			Iterable<BlockPos.Mutable> it = BlockPos.Mutable.getAllInBoxMutable(new BlockPos(renderAABB.minX, this.getY() + this.getEyeHeight(), renderAABB.minZ), new BlockPos(renderAABB.maxX, renderAABB.maxY, renderAABB.maxZ));
			int brightestBlock = 0;
			int brightestSky = 0;
			for(BlockPos.Mutable pos : it) {
				int brightness = this.getBrightnessForRenderAt(pos);
				int brightnessBlock = ((brightness >> 4) & 0b11111111);
				int brightnessSky = ((brightness >> 20) & 0b11111111);
				if(brightnessBlock > brightestBlock) {
					brightestBlock = brightnessBlock;
				}
				if(brightnessSky > brightestSky) {
					brightestSky = brightnessSky;
				}
			}
			return (brightestSky << 20) | (brightestBlock << 4);
		} else {
			return super.getBrightnessForRender();
		}
	}

	private int getBrightnessForRenderAt(BlockPos.Mutable pos) {
		if(this.world.isBlockLoaded(pos)) {
			return this.world.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.isRolling() || this.isHiddenOrInWall() ? null : this.getBoundingBox();
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return this.isRolling() || this.isHiddenOrInWall() ? null : this.getBoundingBox();
	}

	@Override
	public void getCustomCollisionBoxes(AxisAlignedBB aabb, List<AxisAlignedBB> collisionBoxes) {
		collisionBoxes.clear();
		final int floor = MathHelper.floor(aabb.minY) + 1;
		CustomEntityCollisionsHandler.HELPER.getBlockCollisions(this, aabb, EntityCollisionPredicate.ALL, new BlockCollisionPredicate() {
			@Override
			public boolean isColliding(Entity entity, AxisAlignedBB aabb, BlockPos.Mutable pos, BlockState state, @Nullable AxisAlignedBB blockAabb) {
				return !EntityBoulderSprite.this.isHiddenOrInWall() || pos.getY() < floor || !EntityBoulderSprite.this.isValidHideoutBlock(pos);
			}
		}, collisionBoxes);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		LivingEntity attacker = source.getImmediateSource() instanceof LivingEntity ? (LivingEntity)source.getImmediateSource() : null;
		if(attacker != null && attacker.getActiveHand() != null) {
			ItemStack item = attacker.getItemInHand(attacker.getActiveHand());
			if(!item.isEmpty() && item.getItem() instanceof ItemPickaxe) {
				amount *= 3.0F;
			}
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if(this.isHiddenOrInWall()) {
			return false;
		}
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source == DamageSource.IN_WALL || super.isEntityInvulnerable(source);
	}

	@Override
	public void tick() {
		double prevMotionX = this.motionX;
		double prevMotionZ = this.motionZ;

		if(this.isEntityInsideOpaqueBlock()) {
			this.setSize(this.width, 0.95F);
		} else {
			this.setSize(this.width, 1.2F);
		}

		if(this.isRolling()) {
			this.stepHeight = 1.1F;
		} else {
			this.stepHeight = 0.6F;
		}

		super.tick();

		if(!this.level.isClientSide()) {
			if(this.getHideout() != null && !this.isValidHideoutBlock(this.getHideout())) {
				this.setHideout(null);
			}

			if(this.getAIMoveSpeed() > 0.3F && this.zza != 0) {
				this.entityData.set(ROLL_SPEED, 0.05F + (this.getAIMoveSpeed() - 0.3F) / 3.0F);
				if(!this.rollSoundPlayed) {
					this.playRollSound();
				}
				this.rollSoundPlayed = true;
			} else {
				this.entityData.set(ROLL_SPEED, 0.0F);
				this.rollSoundPlayed = false;
			}

			ModifiableAttributeInstance attackAttribute = this.getAttribute(Attributes.ATTACK_DAMAGE);

			if(this.isRolling()) {
				if(this.collidedHorizontally) {
					boolean pg = this.onGround;
					double pmx = this.motionX;
					double pmy = this.motionY;
					double pmz = this.motionZ;
					double px = this.getX();
					double py = this.getY();
					double pz = this.getZ();

					this.move(MoverType.SELF, MathHelper.clamp(prevMotionX, -4, 4), 0, 0);

					boolean cx = Math.abs(this.getX() - px) < Math.abs(MathHelper.clamp(prevMotionX, -4, 4)) / 2.0D;

					this.onGround = pg;
					this.motionX = pmx;
					this.motionY = pmy;
					this.motionZ = pmz;
					this.setPosition(px, py, pz);

					this.move(MoverType.SELF, 0, 0, MathHelper.clamp(prevMotionZ, -4, 4));

					boolean cz = Math.abs(this.getZ() - pz) < Math.abs(MathHelper.clamp(prevMotionZ, -4, 4)) / 2.0D;

					this.onGround = pg;
					this.motionX = pmx;
					this.motionY = pmy;
					this.motionZ = pmz;
					this.setPosition(px, py, pz);

					this.onRollIntoWall(cx, cz, MathHelper.clamp(prevMotionX, -4, 4), MathHelper.clamp(prevMotionZ, -4, 4));
				}

				attackAttribute.removeModifier(ROLLING_ATTACK_MODIFIER_ATTRIBUTE_UUID);
				attackAttribute.applyModifier(new AttributeModifier(ROLLING_ATTACK_MODIFIER_ATTRIBUTE_UUID, "Rolling attack modifier", Math.min(Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 20.0F, 10.0F), 0));

				Vector3d motionDir = new Vector3d(this.motionX, 0, this.motionZ).normalize();
				List<Entity> collidingEntities = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().inflate(0.35D, 0, 0.35D), EntitySelectors.getTeamCollisionPredicate(this));
				for(Entity collidingEntity : collidingEntities) {
					if(collidingEntity.hurtResistantTime <= 0) {
						double dot = motionDir.dotProduct(collidingEntity.getDeltaMovement().subtract(this.getDeltaMovement()));
						if(dot >= -0.5D && dot <= Math.sqrt(this.width * this.width) * 0.5D) {
							if(this.attackEntityAsMob(collidingEntity) && collidingEntity instanceof LivingEntity) {
								((LivingEntity) collidingEntity).knockBack(this, (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 3.0F, -this.motionX, -this.motionZ);

								if(collidingEntity instanceof ServerPlayerEntity) {
									AdvancementCriterionRegistry.ROLLED_OVER_BY_BOULDER_SPRITE.trigger((ServerPlayerEntity) collidingEntity);
								}
							}
						}
					}
				}
			} else {
				attackAttribute.removeModifier(ROLLING_ATTACK_MODIFIER_ATTRIBUTE_UUID);
			}

			if(this.rollingTicks > 0 && this.rollingDir != null) {
				double speed;
				if(this.rollingDuration - this.rollingTicks < this.rollingAccelerationTime) {
					speed = 0.5D + (this.rollingSpeed - 0.5D) / this.rollingAccelerationTime * (this.rollingDuration - this.rollingTicks);
				} else if(this.rollingTicks < this.rollingDecelerationTime) {
					speed = 0.5D + (this.rollingSpeed - 0.5D) / this.rollingDecelerationTime * this.rollingTicks;
				} else {
					speed = this.rollingSpeed;
				}

				this.getMoveHelper().setMoveTo(this.getX() + this.rollingDir.x, this.getY(), this.getZ() + this.rollingDir.z, speed);

				this.rollingTicks--;
			}
		} else {
			if(this.isEntityAlive() && this.isRolling()) {
				this.setRollSpeed(this.entityData.get(ROLL_SPEED));
			}

			this.updateRollAnimationState();

			if(this.onGround) {
				double particleTiming = 0.75D;
				if(this.prevRollAnimation % 1 < particleTiming && this.rollAnimation % 1 >= particleTiming) {
					BlockPos posBelow = this.getPosition().below();
					BlockState stateBelow = this.world.getBlockState(posBelow);
					if(!stateBelow.getBlock().isAir(stateBelow, this.world, posBelow)) {
						int stateId = Block.getStateId(stateBelow);
						int betweenstoneId = Block.getStateId(BlockRegistry.BETWEENSTONE.defaultBlockState());
						for(int i = 0; i < 28; i++) {
							double dx = this.random.nextDouble() - 0.5D;
							double dz = this.random.nextDouble() - 0.5D;
							this.world.addParticle(ParticleTypes.BLOCK_DUST, this.getX() + this.motionX + dx, this.getY() - 0.2D, this.getZ() + this.motionZ + dz, dx * 0.3D, 0.3D, dz * 0.3D, stateId);
						}
						for(int i = 0; i < 12; i++) {
							double dx = this.random.nextDouble() - 0.5D;
							double dz = this.random.nextDouble() - 0.5D;
							this.world.addParticle(ParticleTypes.BLOCK_DUST, this.getX() + this.motionX + dx, this.getY() - 0.2D, this.getZ() + this.motionZ + dz, dx * 0.3D, 0.3D, dz * 0.3D, betweenstoneId);
						}
					}
				}
			}
		}
	}

	protected void onRollIntoWall(boolean cx, boolean cz, double mx, double mz) {
		if(this.onGround) {
			if(this.motionY <= 3.0D) {
				this.motionY += Math.min(Math.sqrt(mx * mx + mz * mz) * 3, 0.7F);
				this.velocityChanged = true;
			}

			if(cx && Math.abs(this.motionX) <= 3.0D) {
				this.motionX -= mx * 4;
				this.velocityChanged = true;
			}

			if(cz && Math.abs(this.motionZ) <= 3.0D) {
				this.motionZ -= mz * 4;
				this.velocityChanged = true;
			}

			if(cx) {
				this.bumpWall(Direction.getNearest((float) mx, 0, 0));
			}

			if(cz) {
				this.bumpWall(Direction.getNearest(0, 0, (float) mz));
			}
		}

		this.stopRolling();
	}

	protected void bumpWall(Direction dir) {
		if(ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
			for(int so = -1; so <= 1; so++) {
				for(int yo = 0; yo <= 1; yo++) {
					BlockPos pos = this.getPosition().offset(dir).add(dir.getStepX() == 0 ? so : 0, yo, dir.getStepZ() == 0 ? so : 0);
					if(this.world.isEmptyBlock(pos.offset(dir.getOpposite()))) {
						BlockState hitState = this.world.getBlockState(pos);
						float hardness = hitState.getBlockHardness(this.world, pos);
						if(!hitState.getBlock().isAir(hitState, this.world, pos) && hardness >= 0 && hardness <= 2.5F && this.random.nextInt(yo + so + 2) == 0
								&& hitState.getBlock().canEntityDestroy(hitState, this.world, pos, this)
								&& ForgeEventFactory.onEntityDestroyBlock(this, pos, hitState)) {
							this.world.levelEvent(2001, pos, Block.getStateId(hitState));
							this.world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
						}
					}
				}
			}
		}
	}

	public void startRolling(int duration, int accelerationTime, int decelerationTime, Vector3d dir, double rollingSpeed) {
		this.rollingTicks = duration;
		this.rollingAccelerationTime = accelerationTime;
		this.rollingDecelerationTime = decelerationTime;
		this.rollingDuration = duration;
		this.rollingDir = dir;
		this.rollingSpeed = rollingSpeed + 1.5D;
	}

	public int getRollingTicks() {
		return this.rollingTicks;
	}

	public void stopRolling() {
		this.rollingTicks = Math.min(this.rollingDecelerationTime, this.rollingTicks);
	}

	public boolean isRolling() {
		return this.entityData.get(ROLL_SPEED) > 0.04F;
	}

	public void setRollSpeed(float speed) {
		this.rollAnimationSpeed = speed;
	}

	protected void updateRollAnimationState() {
		this.prevRollAnimationInAirWeight = this.rollAnimationInAirWeight;
		this.prevRollAnimation = this.rollAnimation;
		this.prevRollAnimationWeight = this.rollAnimationWeight;

		if(this.rollAnimationSpeed > 0) {
			if(!this.onGround) {
				this.rollAnimationInAirWeight = Math.min(1, this.rollAnimationInAirWeight + 0.2F);
			} else {
				this.rollAnimationInAirWeight = Math.max(0, this.rollAnimationInAirWeight - 0.2F);
			}

			if(this.rollAnimationSpeed < 0.04F) {
				double p = this.rollAnimation % 1;
				double incr = Math.pow((1 - (this.rollAnimation % 1)) * this.rollAnimationSpeed, 0.65D);
				this.rollAnimation += incr;
				this.rollAnimationWeight = (float) Math.max(0, this.rollAnimationWeight - incr / (1 - (this.rollAnimation % 1)) / 4);
				if(this.rollAnimation % 1 < p) {
					this.prevRollAnimation = this.rollAnimation = 0;
					this.prevRollAnimationWeight = this.rollAnimationWeight = 0;
					this.rollAnimationSpeed = 0;
					this.rollAnimationInAirWeight = 0;
				}
			} else {
				this.rollAnimation += this.rollAnimationSpeed;
				this.rollAnimationWeight = Math.min(1, this.rollAnimationWeight + 0.1F);
				this.rollAnimationSpeed *= 0.5F;
			}
		}
	}

	public float getRollAnimation(float partialTicks) {
		return this.prevRollAnimation + (this.rollAnimation - this.prevRollAnimation) * partialTicks;
	}

	public float getRollAnimationWeight(float partialTicks) {
		return this.prevRollAnimationWeight + (this.rollAnimationWeight - this.prevRollAnimationWeight) * partialTicks;
	}

	public float getRollAnimationInAirWeight(float partialTicks) {
		return this.prevRollAnimationInAirWeight + (this.rollAnimationInAirWeight - this.prevRollAnimationInAirWeight) * partialTicks;
	}

	public boolean isHiddenOrInWall() {
		return this.isAiHiding || this.isEntityInsideOpaqueBlock();
	}

	public void setHideout(@Nullable BlockPos pos) {
		this.hideout = pos;
	}

	@Nullable
	public BlockPos getHideout() {
		return this.hideout;
	}

	public void setHideoutEntrance(@Nullable Direction entrance) {
		this.hideoutEntrance = entrance;
	}

	@Nullable
	protected Direction getHideoutEntrance() {
		return this.hideoutEntrance;
	}

	protected boolean isValidHideoutBlock(BlockPos pos) {
		return SurfaceType.UNDERGROUND.matches(this.world.getBlockState(pos));
	}

	protected static class AIRollTowardsTarget extends EntityAIBase {
		protected final EntityBoulderSprite entity;

		protected int cooldown = 18;
		protected Vector3d rollDir;

		public AIRollTowardsTarget(EntityBoulderSprite entity) {
			this.entity = entity;
			this.setMutexBits(3);
		}

		@Override
		public boolean canUse() {
			if(this.cooldown-- <= 0) {
				return this.entity.isEntityAlive() && this.entity.getAttackTarget() != null && this.entity.getRollingTicks() <= 0 && this.entity.onGround && this.entity.getAttackTarget().isEntityAlive() && this.entity.getSensing().canSee(this.entity.getAttackTarget());
			}
			return false;
		}

		@Override
		public void start() {
			Entity target = this.entity.getAttackTarget();
			if(target != null) {
				this.rollDir = new Vector3d(target.getX() - this.entity.getX(), 0, target.getZ() - this.entity.getZ()).normalize();
				this.entity.startRolling(160, 35, 15, this.rollDir, 1.8D);
			}
		}

		@Override
		public void stop() {
			this.cooldown = 20 + this.entity.getRandom().nextInt(26);
		}

		@Override
		public boolean canContinueToUse() {
			//Keep task active while rolling to block other movement tasks
			if(this.entity.getRollingTicks() > 0) {
				Entity target = this.entity.getAttackTarget();
				if(target != null) {
					double overshoot = this.rollDir.dotProduct(new Vector3d(this.entity.getX() - target.getX(), 0, this.entity.getZ() - target.getZ()));
					if(overshoot >= 2) {
						this.entity.stopRolling();
					}
				}
				return true;
			}
			return false;
		}
	}

	protected static class AIMoveToHideout extends EntityAIBase {
		protected final EntityBoulderSprite entity;
		protected double speed;

		protected List<Direction> potentialEntrances = new ArrayList<>();

		protected BlockPos targetHideout;
		protected Direction targetEntrance;
		protected BlockPos target;
		protected Path path;

		protected int delayCounter;
		protected int pathingFails;

		protected double approachSpeedFar;
		protected double approachSpeedNear;

		protected double lastFinalPositionDistSq;
		protected int stuckCounter;

		protected boolean finished;

		public AIMoveToHideout(EntityBoulderSprite entity, double speed) {
			this.entity = entity;
			this.speed = this.approachSpeedFar = this.approachSpeedNear = speed;
			this.setMutexBits(3 | 0b10000);
		}

		@Override
		public boolean canUse() {
			if(this.entity.isEntityAlive() && this.entity.getHideout() != null && !this.entity.isHiddenOrInWall()) {
				Direction entrance;
				if(this.entity.getHideoutEntrance() == null) {
					if(this.potentialEntrances.isEmpty()) {
						for(Direction dir : Direction.Plane.HORIZONTAL) {
							BlockPos offset = this.entity.getHideout().offset(dir);
							PathNodeType node = this.entity.getNavigation().getNodeProcessor().getPathNodeType(this.entity.world, offset.getX(), offset.getY(), offset.getZ());
							if(node == PathNodeType.OPEN || node == PathNodeType.WALKABLE) {
								this.potentialEntrances.add(dir);
							}
						}
						if(this.potentialEntrances.isEmpty()) {
							return false;
						}
						Collections.sort(this.potentialEntrances, (f1, f2) -> 
						Double.compare(
								this.entity.getHideout().offset(f2).distanceSq(this.entity.getX(), this.entity.getY(), this.entity.getZ()),
								this.entity.getHideout().offset(f1).distanceSq(this.entity.getX(), this.entity.getY(), this.entity.getZ())
								));
					}
					entrance = this.potentialEntrances.remove(this.potentialEntrances.size() - 1);
				} else {
					entrance = this.entity.getHideoutEntrance();
				}
				BlockPos entrancePos = this.entity.getHideout().offset(entrance);
				this.path = this.entity.getNavigation().getPathToPos(entrancePos);
				if(this.path != null && this.path.getFinalPathPoint().x == entrancePos.getX() && this.path.getFinalPathPoint().y == entrancePos.getY() && this.path.getFinalPathPoint().z == entrancePos.getZ()) {
					this.entity.setHideoutEntrance(entrance);
					this.target = entrancePos;
					this.targetEntrance = entrance;
					this.targetHideout = this.entity.getHideout();
					return true;
				}
			}
			return false;
		}

		@Override
		public void start() {
			this.entity.getNavigation().setPath(this.path, this.speed);
			this.approachSpeedFar = this.approachSpeedNear = this.entity.getAIMoveSpeed();
		}

		@Override
		public void updateTask() {
			if(this.delayCounter-- <= 0) {
				double dist = this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D);

				this.delayCounter = 4 + this.entity.getRandom().nextInt(7);

				if(dist > 1024.0D) {
					this.delayCounter += 10;
				} else if(dist > 256.0D) {
					this.delayCounter += 5;
				}

				if(!this.entity.getNavigation().tryMoveToXYZ(this.target.getX(), this.target.getY(), this.target.getZ(), this.speed)) {
					this.delayCounter += 15;
					this.pathingFails++;
				}
			}

			double dstSq = this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D);

			if(this.entity.getNavigation().noPath()) {
				if(this.path.isFinished()) {
					this.entity.getMoveHelper().setMoveTo(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D, this.approachSpeedNear / this.entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					this.entity.getLookHelper().setLookPosition(this.target.getX() - this.targetEntrance.getStepX() + 0.5D, this.target.getY() + this.entity.getEyeHeight(), this.target.getZ() - this.targetEntrance.getStepZ() + 0.5D, 30, 30);

					this.approachSpeedNear = this.approachSpeedNear * 0.9D + Math.min((dstSq + 0.2D) / 4.0D, 0.4D / 4.0D) * 0.1D;

					if(this.lastFinalPositionDistSq == 0) {
						this.lastFinalPositionDistSq = dstSq;
					} else {
						if(dstSq > this.lastFinalPositionDistSq - 0.05D) {
							this.stuckCounter += this.entity.getRandom().nextInt(3) + 1;
						} else {
							this.lastFinalPositionDistSq = dstSq;
						}
						if(this.stuckCounter >= 80) {
							this.finished = true;
						}
					}

					if(this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D) < 0.015D && this.entity.getAIMoveSpeed() <= 0.1D) {
						this.finished = true;
					}
				} else {
					this.finished = true;
				}
			} else {
				if(dstSq <= this.entity.getAIMoveSpeed() * 5 * 5) {
					double decay = (this.entity.getAIMoveSpeed() * 5 * 5 - dstSq) / (this.entity.getAIMoveSpeed() * 5 * 5) * 0.33D;

					this.approachSpeedNear = this.approachSpeedFar = this.approachSpeedFar * (1 - decay) + Math.min(0.6D / 4.0D, this.speed / 4.0D) * decay;
					this.entity.getNavigation().setSpeed(this.approachSpeedFar / this.entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
				} else {
					this.approachSpeedFar = this.approachSpeedNear = this.entity.getAIMoveSpeed();
				}

				if(this.entity.getNavigation().getPath() != this.path) {
					this.finished = true;
				}
			}
		}

		@Override
		public void stop() {
			this.potentialEntrances.clear();
			this.path = null;
			this.entity.getNavigation().clearPath();
			this.pathingFails = 0;
			this.finished = false;
			this.lastFinalPositionDistSq = 0;
			this.target = null;
			this.targetEntrance = null;
			this.stuckCounter = 0;
		}

		@Override
		public boolean canContinueToUse() {
			return this.entity.isEntityAlive() && this.entity.getHideout() != null && this.targetHideout != null && this.targetHideout.equals(this.entity.getHideout()) && this.pathingFails < 3 && !this.finished;
		}
	}

	protected static class AIHide extends EntityAIBase {
		protected final EntityBoulderSprite entity;
		protected double speed;

		protected BlockPos hideout;
		protected Direction entrance;

		public AIHide(EntityBoulderSprite entity, double speed) {
			this.entity = entity;
			this.speed = speed;
			this.setMutexBits(3 | 0b10000);
		}

		@Override
		public boolean canUse() {
			if(this.entity.isEntityAlive() && this.entity.getHideout() != null && this.entity.getHideoutEntrance() != null && this.entity.isValidHideoutBlock(this.entity.getHideout())) {
				BlockPos entrance = this.entity.getHideout().offset(this.entity.getHideoutEntrance());
				if(entrance.distanceSqToCenter(this.entity.getX(), this.entity.getY(), this.entity.getZ()) <= 0.33D) {
					this.hideout = this.entity.getHideout();
					this.entrance = this.entity.getHideoutEntrance();
					return true;
				}
			}
			return false;
		}

		@Override
		public void updateTask() {
			this.entity.isAiHiding = true;
			BlockPos hideoutPos = this.hideout.offset(this.entrance.getOpposite());
			double dstSq = hideoutPos.distanceSqToCenter(this.entity.getX(), this.entity.getY(), this.entity.getZ());
			if(dstSq >= 0.5D) {
				this.entity.getMoveHelper().setMoveTo(hideoutPos.getX() + 0.5D, hideoutPos.getY(), hideoutPos.getZ() + 0.5D, this.speed);
			}
		}

		@Override
		public void stop() {
			this.entity.isAiHiding = false;
			this.entity.setHideout(null);
		}

		@Override
		public boolean canContinueToUse() {
			return this.entity.isEntityAlive() && this.entity.getHideout() == this.hideout && this.hideout.getY() >= MathHelper.floor(this.entity.getY()) && this.entity.isValidHideoutBlock(this.hideout);
		}
	}

	protected static class AIRollTowardsTargetFromHideout extends EntityAIBase {
		protected final EntityBoulderSprite entity;
		protected double rollSpeed;
		protected int chance;

		protected Vector3d rollDir;
		protected boolean finished = false;

		public AIRollTowardsTargetFromHideout(EntityBoulderSprite entity, int chance, double rollSpeed) {
			this.entity = entity;
			this.rollSpeed = rollSpeed;
			this.chance = chance;
			this.setMutexBits(3 | 0b10000);
		}

		@Override
		public boolean canUse() {
			return this.entity.isHiddenOrInWall() && this.entity.isEntityAlive() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() && Math.abs(this.entity.getY() - this.entity.getAttackTarget().getY()) <= 3 && this.entity.getRandom().nextInt(this.chance) == 0;
		}

		@Override
		public void start() {
			this.finished = false;
		}

		@Override
		public void updateTask() {
			Entity target = this.entity.getAttackTarget();

			if(target != null) {
				if(this.entity.getRollingTicks() <= 0) {
					if(!this.canUse()) {
						this.finished = true;
					} else {
						this.rollDir = new Vector3d(target.getX() - this.entity.getX(), 0, target.getZ() - this.entity.getZ()).normalize();
						this.entity.startRolling(80, 10, 10, this.rollDir, this.rollSpeed);
						this.entity.isAiHiding = false;
						this.entity.setHideout(null);
					}
				} else if(this.rollDir != null && target != null) {
					double overshoot = this.rollDir.dotProduct(new Vector3d(this.entity.getX() - target.getX(), 0, this.entity.getZ() - target.getZ()));
					if(overshoot >= 2) {
						this.entity.stopRolling();
					}
				} else {
					this.entity.stopRolling();
				}
			} else {
				this.entity.stopRolling();
			}
		}

		@Override
		public boolean canContinueToUse() {
			return !this.finished;
		}
	}

	protected static class AIFindRandomHideout extends EntityAIBase {
		protected final EntityBoulderSprite entity;

		protected int chance;
		protected int range;

		public AIFindRandomHideout(EntityBoulderSprite entity, int range, int chance) {
			this.entity = entity;
			this.range = range;
			this.chance = chance;
			this.setMutexBits(3 | 0b10000);
		}

		@Override
		public boolean canUse() {
			return this.entity.isEntityAlive() && this.entity.onGround && this.entity.getRandom().nextInt(this.chance) == 0;
		}

		@Override
		public void start() {
			BlockPos.Mutable pos = new BlockPos.Mutable();
			for(int i = 0; i < 32; i++) {
				pos.setPos(this.entity.getX() + this.entity.getRandom().nextInt(this.range * 2) - this.range, this.entity.getY(), this.entity.getZ() + this.entity.getRandom().nextInt(this.range * 2) - this.range);
				if(this.entity.isValidHideoutBlock(pos) && (!this.entity.level.isEmptyBlock(pos.above()) || this.entity.getRandom().nextInt(10) == 0)) {
					boolean hasPotentialEntrance = false;
					for(Direction facing : Direction.Plane.HORIZONTAL) {
						if(this.entity.level.isEmptyBlock(pos.offset(facing))) {
							hasPotentialEntrance = true;
							break;
						}
					}
					if(hasPotentialEntrance) {
						this.entity.setHideout(pos.toImmutable());
						this.entity.setHideoutEntrance(null);
						break;
					}
				}
			}
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}
	}

	protected static class AIFindRandomHideoutFlee extends AIFindRandomHideout {
		public AIFindRandomHideoutFlee(EntityBoulderSprite entity, int range) {
			super(entity, range, 2);
			this.setMutexBits(0b10000);
		}

		@Override
		public boolean canUse() {
			return this.entity.isEntityAlive() && this.entity.getHealth() <= this.entity.getMaxHealth() / 3 && this.entity.getRandom().nextInt(this.chance) == 0;
		}
	}
}
