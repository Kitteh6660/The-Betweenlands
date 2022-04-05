package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.ai.attributes.Attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.common.entity.ai.EntityAIApproachItem;
import thebetweenlands.common.entity.ai.EntityAIPeatMummyCharge;
import thebetweenlands.common.entity.attributes.BooleanAttribute;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityPeatMummy extends EntityMob implements IEntityBL, IEntityScreenShake {
	public static final IAttribute SPAWN_LENGTH_ATTRIB = (new RangedAttribute(null, "bl.spawnLength", 100.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Spawning Length");
	public static final IAttribute SPAWN_OFFSET_ATTRIB = (new RangedAttribute(null, "bl.spawnOffset", 2.0D, -Integer.MAX_VALUE, Integer.MAX_VALUE)).setDescription("Spawning Y Offset");
	public static final IAttribute SPAWN_RANGE_ATTRIB = (new RangedAttribute(null, "bl.spawnRange", 8.0D, 0, Double.MAX_VALUE)).setDescription("Spawning Range");

	public static final IAttribute CHARGING_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.chargingCooldown", 160.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Cooldown");
	public static final IAttribute CHARGING_PREPARATION_SPEED_ATTRIB = (new RangedAttribute(null, "bl.chargingPreparationSpeed", 60.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Preparation Speed");
	public static final IAttribute CHARGING_TIME_ATTRIB = (new RangedAttribute(null, "bl.chargingTime", 320.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Time");
	public static final IAttribute CHARGING_SPEED_ATTRIB = (new RangedAttribute(null, "bl.chargingSpeed", 0.55D, 0, Double.MAX_VALUE)).setDescription("Charging Movement Speed");
	public static final IAttribute CHARGING_DAMAGE_MULTIPLIER_ATTRIB = (new RangedAttribute(null, "bl.chargingDamageMultiplier", 1.65D, 0, Double.MAX_VALUE)).setDescription("Charging Damage Multiplier");

	public static final IAttribute CARRY_SHIMMERSTONE = (new BooleanAttribute(null, "bl.carryShimmerstone", false)).setDescription("Whether this Peat Mummy carries a Shimmerstone");
	public static final IAttribute IS_BOSS = (new BooleanAttribute(null, "bl.isDreadfulPeatMummyBoss", false)).setDescription("Whether this Peat Mummy was spawned by a Dreadful Peat Mummy");

	private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

	private static final int BREAK_COUNT = 5;

	public static final float BASE_SPEED = 0.2F;
	public static final float BASE_DAMAGE = 6.0F;

	private static final DataParameter<Integer> SPAWNING_TICKS = EntityDataManager.<Integer>createKey(EntityPeatMummy.class, DataSerializers.INT);

	private int chargingPreparation;
	private static final DataParameter<Byte> CHARGING_STATE = EntityDataManager.<Byte>createKey(EntityPeatMummy.class, DataSerializers.BYTE);

	//Scream timer is only used for the screen shake and is client side only.
	private int prevScreamTimer;
	private int screamTimer;
	private boolean screaming = false;

	//Adjust to length of screaming sound
	private static final int SCREAMING_TIMER_MAX = 50;

	private float prevSpawningOffset = 0.0F;
	private float prevSpawningProgress = 0.0F;

	private static final List<Block> SPAWN_BLOCKS = new ArrayList<Block>();
	static {
		SPAWN_BLOCKS.add(BlockRegistry.MUD);
		SPAWN_BLOCKS.add(BlockRegistry.PEAT);
	}

	private List<EntityAIBase> activeTargetTasks;
	private List<EntityAIBase> inactiveTargetTasks;

	public EntityPeatMummy(World world) {
		super(world);
		this.experienceValue = 12;
		this.setSize(1.0F, 1.2F);
		this.setSpawningTicks(0);
	}

	@Override
	public void registerGoals() {
		this.goalSelector.addGoal(0, new EntityAISwimming(this));
		this.goalSelector.addGoal(1, new EntityAIApproachItem(this, ItemRegistry.SHIMMER_STONE, 80, 64, 1.9F, 1.5F) {
			@Override
			protected double getNearSpeed() {
				if(EntityPeatMummy.this.isCharging()) {
					return 1.0F;
				} else {
					return super.getNearSpeed();
				}
			}
			@Override
			protected double getFarSpeed() {
				if(EntityPeatMummy.this.isCharging()) {
					return 1.0F;
				} else {
					return super.getFarSpeed();
				}
			}
			@Override
			protected void onPickup() {
				EntityPeatMummy entity = EntityPeatMummy.this;
				if(entity.isCharging()) {
					entity.stopCharging();
				}
				entity.setCarryShimmerstone(true);
			}
		});
		this.goalSelector.addGoal(2, new EntityAIAttackMelee(this, 1.0D, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity attackTarget) {
				return 0.8D + attackTarget.width;
			}
		});
		this.goalSelector.addGoal(3, new EntityAIPeatMummyCharge(this));
		this.goalSelector.addGoal(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.goalSelector.addGoal(5, new EntityAIWatchClosest(this, PlayerEntity.class, 16.0F));
		this.goalSelector.addGoal(6, new EntityAIWander(this, 1D));
		this.goalSelector.addGoal(7, new EntityAILookIdle(this));

		this.activeTargetTasks = new ArrayList<EntityAIBase>();
		this.activeTargetTasks.add(new EntityAIHurtByTarget(this, false));
		this.activeTargetTasks.add(new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, true));

		this.inactiveTargetTasks = new ArrayList<EntityAIBase>();
		this.inactiveTargetTasks.add(new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, false) {
			@Override
			protected double getTargetDistance() {
				return EntityPeatMummy.this.getAttribute(SPAWN_RANGE_ATTRIB).getValue();
			}
		});
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(110.0D);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE);
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(40.0D);
		this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

		this.getAttributeMap().registerAttribute(CARRY_SHIMMERSTONE);
		this.getAttributeMap().registerAttribute(IS_BOSS);
		this.getAttributeMap().registerAttribute(SPAWN_LENGTH_ATTRIB);
		this.getAttributeMap().registerAttribute(SPAWN_OFFSET_ATTRIB);
		this.getAttributeMap().registerAttribute(SPAWN_RANGE_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_PREPARATION_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_TIME_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_DAMAGE_MULTIPLIER_ATTRIB);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		this.getEntityData().register(SPAWNING_TICKS, 0);
		this.getEntityData().register(CHARGING_STATE, (byte) 0);
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.getChargingState() == 1;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		nbt.putInt("spawningTicks", this.getSpawningTicks());
		nbt.putInt("chargingPreparation", this.chargingPreparation);
		nbt.putByte("chargingState", this.getEntityData().get(CHARGING_STATE));

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		if(nbt.contains("spawningTicks")) {
			this.setSpawningTicks(nbt.getInt("spawningTicks"));
		}
		if(nbt.contains("chargingPreparation")) {
			this.chargingPreparation = nbt.getInt("chargingPreparation");
		}
		if(nbt.contains("chargingState")) {
			this.getEntityData().set(CHARGING_STATE, nbt.getByte("chargingState"));
		}

		super.readEntityFromNBT(nbt);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		super.tick();

		this.prevSpawningOffset = this.getSpawningOffset();
		this.prevSpawningProgress = this.getSpawningProgress();

		if(!this.level.isClientSide()) {
			if(this.shouldUpdateSpawningAnimation()) {
				if(this.getSpawningTicks() == 0) {
					this.playSound(SoundRegistry.PEAT_MUMMY_EMERGE, 1.2F, 1.0F);
				}
				this.updateSpawningTicks();
			} else if(this.getSpawningTicks() > 0) {
				this.setSpawningFinished();
			}

			if(this.isInValidSpawn() && !this.isSpawningFinished()) {
				this.motionY = 0;
				this.motionX = 0;
				this.motionZ = 0;
				this.velocityChanged = true;

				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningTicks() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = new BlockPos(this.getX(), this.getY() - 1, this.getZ());
					BlockState blockState = this.world.getBlockState(pos);
					this.playSound(blockState.getBlock().getSoundType().getBreakSound(), this.random.nextFloat() * 0.3F + 0.3F, this.random.nextFloat() * 0.15F + 0.7F);
				}

				if(this.getAttackTarget() != null) {
					this.faceEntity(this.getAttackTarget(), 360, 360);
				}

				if(this.getSpawningTicks() == this.getSpawningLength() - 1) {
					this.setPosition(this.getX(), this.getY(), this.getZ());
				}
			} else {
				this.setSpawningFinished();
			}
		} else {
			if(this.getSpawningProgress() != 0.0F && this.getSpawningProgress() != 1.0F) {
				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningTicks() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = new BlockPos(this.getX(), this.getY() - 1, this.getZ());
					BlockState blockState = this.world.getBlockState(pos);
					double px = this.getX() + this.random.nextDouble() - 0.5F;
					double py = this.getY() + this.random.nextDouble() * 0.2 + 0.075;
					double pz = this.getZ() + this.random.nextDouble() - 0.5F;
					for (int i = 0, amount = this.random.nextInt(20) + 15; i < amount; i++) {
						double ox = this.random.nextDouble() * 0.1F - 0.05F;
						double oz = this.random.nextDouble() * 0.1F - 0.05F;
						double motionX = this.random.nextDouble() * 0.2 - 0.1;
						double motionY = this.random.nextDouble() * 0.25 + 0.1;
						double motionZ = this.random.nextDouble() * 0.2 - 0.1;
						this.world.addParticle(ParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(blockState));
					}
				}
			}
		}

		if(this.isSpawningFinished()) {
			this.prevScreamTimer = this.screamTimer;
			if(this.isPreparing() && this.screamTimer == 0) {
				this.screaming = true;
				this.screamTimer = 1;
			}
			if(this.screamTimer > 0) {
				this.screamTimer++;
			}
			if(this.screamTimer >= SCREAMING_TIMER_MAX || !this.isPreparing()) {
				this.screaming = false;
			} else {
				this.screaming = true;
			}
			if(!this.isPreparing()) {
				this.screamTimer = 0;
			}

			if(!this.level.isClientSide()) {
				if(this.isPreparing()){
					this.chargingPreparation++;
					if(this.getPreparationProgress() == 1.0F) {
						this.chargingPreparation = 0;
						this.setChargingState(2);
					}
				}

				if(this.isCharging()) {
					this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * this.getAttribute(CHARGING_DAMAGE_MULTIPLIER_ATTRIB).getValue());
					this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(CHARGING_SPEED_ATTRIB).getValue());
				} else {
					this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE);
					this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
				}
			}
		}
	}

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof EntitySwampHag)
			entity.setPosition(posX, posY + 0.5D + entity.getStepY(), posZ);
	}

	/**
	 * Returns whether the spawning animation should be started
	 * @return
	 */
	public boolean shouldUpdateSpawningAnimation() {
		return this.getAttackTarget() != null;
	}

	/**
	 * Returns whether the ground below the peat mummy is suitable for spawning
	 * @return
	 */
	public boolean isInValidSpawn() {
		int ebx = MathHelper.floor(this.getX());
		int eby = MathHelper.floor(this.getY());
		int ebz = MathHelper.floor(this.getZ());
		return inMud(ebx, eby, ebz);
	}

	@SuppressWarnings("deprecation")
	private boolean inMud(int ebx, int eby, int ebz) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for(int y = -MathHelper.ceil(this.getMaxSpawnOffset()); y < 0; y++) {
			for(int x = -1; x <= 1; x++) {
				for(int z = -1; z <= 1; z++) {
					BlockState blockState = this.world.getBlockState(pos.setPos(ebx + x, eby + y, ebz + z));
					Block cb = blockState.getBlock();
					if(!(y == -1 ? (SPAWN_BLOCKS.contains(cb)) : (cb.isOpaqueCube(blockState) || SPAWN_BLOCKS.contains(cb)))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity player) {
		if(this.isSpawningFinished()) {
			super.onCollideWithPlayer(player);
		}
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed() && this.isSpawningFinished() && this.getChargingState() == 0;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return !source.equals(DamageSource.IN_WALL) && super.hurt(source, damage);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && this.isInValidSpawn();
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if(this.isCharging()) {
			this.stopCharging();
		}
		if(this.isSpawningFinished()) {
			return super.attackEntityAsMob(entity);
		}
		return false;
	}

	@Override
	public void playLivingSound() {
		if(this.isSpawningFinished()) {
			super.playLivingSound();
		}
	}

	/**
	 * Starts the charging progress
	 */
	public void startCharging() {
		this.playSound(SoundRegistry.PEAT_MUMMY_CHARGE, 1.75F, (this.random.nextFloat() * 0.4F + 0.8F) * 0.8F);
		this.setChargingState(1);
	}

	/**
	 * Stops the charging progress
	 */
	public void stopCharging() {
		this.setChargingState(0);
		this.chargingPreparation = 0;
	}

	/**
	 * Returns the spawning offset
	 * @return
	 */
	public float getSpawningOffset() {
		return (float) ((-this.getMaxSpawnOffset() + this.getSpawningProgress() * this.getMaxSpawnOffset()));
	}

	/**
	 * Returns the interpolated spawning offset
	 * @param partialTicks
	 * @return
	 */
	public float getInterpolatedSpawningOffset(float partialTicks) {
		return this.prevSpawningOffset + (this.getSpawningOffset() - this.prevSpawningOffset) * partialTicks;
	}

	/**
	 * Returns the maximum spawning offset
	 * @return
	 */
	public double getMaxSpawnOffset() {
		return this.getAttribute(SPAWN_OFFSET_ATTRIB).getValue();
	}

	/**
	 * Sets the spawning ticks
	 * @param ticks
	 */
	public void setSpawningTicks(int ticks) {
		this.getEntityData().set(SPAWNING_TICKS, ticks);

		if(!this.level.isClientSide()) {
			if(this.isSpawningFinished()) {
				for(EntityAIBase task : this.inactiveTargetTasks) {
					this.targetSelector.removeTask(task);
				}
				for(int i = 0; i < this.activeTargetTasks.size(); i++) {
					this.targetSelector.addGoal(i, this.activeTargetTasks.get(i));
				}
			} else {
				for(EntityAIBase task : this.activeTargetTasks) {
					this.targetSelector.removeTask(task);
				}
				for(int i = 0; i < this.inactiveTargetTasks.size(); i++) {
					this.targetSelector.addGoal(i, this.inactiveTargetTasks.get(i));
				}
			}
		}
	}

	/**
	 * Returns the spawning ticks
	 * @return
	 */
	public int getSpawningTicks() {
		return this.getEntityData().get(SPAWNING_TICKS);
	}

	/**
	 * Returns the spawning animation duration
	 * @return
	 */
	public int getSpawningLength() {
		return (int) this.getAttribute(SPAWN_LENGTH_ATTRIB).getValue();
	}

	/**
	 * Returns the range at which a buried peat mummy detects a player
	 * @return
	 */
	public double getSpawningRange() {
		return this.getAttribute(SPAWN_RANGE_ATTRIB).getValue();
	}

	/**
	 * Returns the relative spawning progress
	 * @return
	 */
	public float getSpawningProgress() {
		if(this.getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / this.getSpawningLength() * this.getSpawningTicks();
	}

	/**
	 * Returns the interpolated relative spawning progress
	 * @param partialTicks
	 * @return
	 */
	public float getInterpolatedSpawningProgress(float partialTicks) {
		return this.prevSpawningProgress + (this.getSpawningProgress() - this.prevSpawningProgress) * partialTicks;
	}

	/**
	 * Increments the spawning ticks
	 */
	public void updateSpawningTicks() {
		int spawningTicks = this.getSpawningTicks();
		if(spawningTicks < this.getSpawningLength()) {
			this.setSpawningTicks(spawningTicks + 1);
		}
	}

	/**
	 * Resets the spawning ticks
	 */
	public void resetSpawningState() {
		this.setSpawningTicks(0);
	}

	/**
	 * Sets the spawning ticks to finished
	 */
	public void setSpawningFinished() {
		if(this.isSpawningFinished()) 
			return;

		this.setSpawningTicks(this.getSpawningLength());
	}

	/**
	 * Returns whether the spawning progress has finished
	 * @return
	 */
	public boolean isSpawningFinished() {
		return this.getSpawningTicks() == this.getSpawningLength();
	}

	/**
	 * Returns the maximum charge cooldown in ticks
	 * @return
	 */
	public int getMaxChargingCooldown() {
		return (int) this.getAttribute(CHARGING_COOLDOWN_ATTRIB).getValue();
	}

	/**
	 * Sets the charging state:
	 * - 0: Not charging
	 * - 1: Preparing
	 * - 2: Charging
	 * @param state
	 */
	public void setChargingState(int state) {
		this.getEntityData().set(CHARGING_STATE, (byte) state);
	}

	/**
	 * Returns the charging state
	 * @return
	 */
	public byte getChargingState() {
		return this.getEntityData().get(CHARGING_STATE);
	}

	/**
	 * Returns whether this entity is preparing for a charge attack
	 * @return
	 */
	public boolean isPreparing() {
		return this.getChargingState() == 1;
	}

	/**
	 * Returns whether this entity is charging
	 * @return
	 */
	public boolean isCharging() {
		return this.getChargingState() == 2;
	}

	/**
	 * Returns the relative charging prepartion progress
	 * @return
	 */
	public float getPreparationProgress() {
		return 1.0F / (int)this.getAttribute(CHARGING_PREPARATION_SPEED_ATTRIB).getValue() * this.chargingPreparation;
	}

	/**
	 * Returns whether the mummy is screaming
	 * @return
	 */
	public boolean isScreaming() {
		return this.screaming;
	}

	/**
	 * Returns the relative screaming progress
	 * @param delta
	 * @return
	 */
	public float getScreamingProgress(float delta) {
		return 1.0F / SCREAMING_TIMER_MAX * (this.prevScreamTimer + (this.screamTimer - this.prevScreamTimer) * delta);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.PEAT_MUMMY_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.PEAT_MUMMY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.PEAT_MUMMY_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.PEAT_MUMMY;
	}

	/**
	 * Sets whether the peat mummy is carrying a shimmer stone
	 * @param shimmerStone
	 */
	public void setCarryShimmerstone(boolean shimmerStone) {
		this.getAttribute(CARRY_SHIMMERSTONE).setBaseValue(shimmerStone ? 1 : 0);
	}

	/**
	 * Returns whether the Peat Mummy is holding a Shimmerstone
	 * @return
	 */
	public boolean doesCarryShimmerstone() {
		return this.getAttribute(CARRY_SHIMMERSTONE).getBaseValue() > 0;
	}

	/**
	 * Sets whether the peat mummy was spawned by a Dreadful Peat Mummy
	 * @param shimmerStone
	 */
	public void setBossMummy(boolean boss) {
		this.getAttribute(IS_BOSS).setBaseValue(boss ? 1 : 0);
	}

	/**
	 * Returns whether the Peat Mummy was spawned by a Dreadful Peat Mummy
	 * @return
	 */
	public boolean isBossMummy() {
		return this.getAttribute(IS_BOSS).getBaseValue() > 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return Minecraft.getInstance().player.isCreative() || this.getSpawningTicks() > 0 ? this.getBoundingBox() : ZERO_AABB;
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(this.isScreaming()) {
			double dist = this.getDistance(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if(dist >= 30.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(this.getScreamingProgress(partialTicks) * Math.PI) + 0.1F) * 0.15F * screamMult);
		} else {
			return 0.0F;
		}
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (!level.isClientSide() && this.level.random.nextInt(20) == 0) {
            EntitySwampHag hag = new EntitySwampHag(this.world);
            hag.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
            hag.onInitialSpawn(difficulty, (IEntityLivingData)null);
            this.world.addFreshEntity(hag);
            hag.startRiding(this);
        }
		return livingdata;
    }
    
    @Override
    protected int getExperiencePoints(PlayerEntity player) {
    	return !this.isBossMummy() ? super.getExperiencePoints(player) : 0;
    }
}