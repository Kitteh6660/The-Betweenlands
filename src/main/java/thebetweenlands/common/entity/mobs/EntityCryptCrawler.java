package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityCryptCrawler extends EntityMob implements IEntityBL {
	protected static final int MUTEX_BLOCKING  = 0b01000;
	protected static final int MUTEX_ATTACKING = 0b10000;
	
	private static final byte EVENT_SHIELD_BLOCKED = 80;
	
	private static final DataParameter<Boolean> IS_BIPED = EntityDataManager.defineId(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.defineId(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_CHIEF = EntityDataManager.defineId(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_BLOCKING = EntityDataManager.<Boolean>createKey(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	
	public float standingAngle, prevStandingAngle;

	protected boolean recentlyBlockedAttack;
	
	public EntityCryptCrawler(World world) {
		super(world);
		setSize(0.5F, 1.0F); //Width must be < 1 otherwise path finder will not path through one block wide gaps!!
		stepHeight = 1F;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_STANDING, false);
		this.entityData.define(IS_BIPED, false);
		this.entityData.define(IS_CHIEF, false);
		this.entityData.define(IS_BLOCKING, false);
	}

	public boolean isStanding() {
		return entityData.get(IS_STANDING);
	}

	private void setIsStanding(boolean standing) {
		entityData.set(IS_STANDING, standing);
	}

	public boolean isBiped() {
		return entityData.get(IS_BIPED);
	}

	public void setIsBiped(boolean standing) {
		entityData.set(IS_BIPED, standing);
	}

	public boolean isChief() {
		return entityData.get(IS_CHIEF);
	}

	public void setIsChief(boolean chief) {
		entityData.set(IS_CHIEF, chief);
	}

	public boolean isBlocking() {
		return entityData.get(IS_BLOCKING);
	}

	private void setIsBlocking(boolean blocking) {
		entityData.set(IS_BLOCKING, blocking);
	}

	@Override
	protected void registerGoals() {
		tasks.addGoal(1, new EntityAISwimming(this));
		tasks.addGoal(2, new EntityCryptCrawler.AIShieldCharge(this)); //Shield charge AI interrupts shield block AI and attack AI
		tasks.addGoal(3, new EntityCryptCrawler.AIShieldBlock(this));
		tasks.addGoal(4, new EntityCryptCrawler.AICryptCrawlerAttack(this));
		tasks.addGoal(5, new EntityAIWander(this, 1D));
		tasks.addGoal(6, new EntityAIWatchClosest(this, PlayerEntity.class, 8.0F));
		tasks.addGoal(7, new EntityAILookIdle(this));
		
		targetTasks.addGoal(0, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, 3, true, true, null).setUnseenMemoryTicks(120));
		targetTasks.addGoal(3, new EntityAIHurtByTarget(this, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		updateAttributes();
	}

	protected void updateAttributes() {
		if (level != null && !level.isClientSide()) {
			if (isChief()) {
				setSize(0.98F, 1.9F);
				experienceValue = 20;
				getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28D);
				getAttribute(Attributes.MAX_HEALTH).setBaseValue(100D);
				getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.25D);
				getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20.0D);
				getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
			}
			if (!isChief() && isBiped()) {
				setSize(0.75F, 1.5F);
				experienceValue = 10;
				getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.29D);
				getAttribute(Attributes.MAX_HEALTH).setBaseValue(30D);
				getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.5D);
				getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20.0D);
				getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
			}
			if (!isChief() && !isBiped()) {
				setSize(0.95F, 1F);
				experienceValue = 5;
				getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.31D);
				getAttribute(Attributes.MAX_HEALTH).setBaseValue(20D);
				getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.75D);
				getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20.0D);
				getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CRYPT_CRAWLER_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.CRYPT_CRAWLER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CRYPT_CRAWLER_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CRYPT_CRAWLER;
	}

	@Override
	protected float getSoundPitch() {
		if(isChief())
			return super.getSoundPitch() * 0.5F;
		return super.getSoundPitch();
	}
	
	@Override
	public void onLivingUpdate() {
		if(this.isBlocking() && !this.getHeldItemOffhand().isEmpty() && this.getHeldItemOffhand().getItem().isShield(this.getHeldItemOffhand(), this)) {
			this.setActiveHand(Hand.OFF_HAND);
			
			//"Fix" for janky item pose
			if(this.level.isClientSide()) {
				this.activeItemStack = this.getHeldItemOffhand();
			}
		} else if(this.isHandActive() && this.getActiveHand() == Hand.OFF_HAND) {
			this.stopActiveHand();
		}
		
		if (level.isClientSide()) {
			if (isChief())
				setSize(1.2F, 1.9F);
			if (!isChief() && isBiped())
				setSize(0.75F, 1.5F);
			if (!isChief() && !isBiped())
				setSize(0.95F - standingAngle * 0.2F, 1F + standingAngle * 0.75F);
		}

		if (!level.isClientSide() && !isBiped()) {
			if (getAttackTarget() != null) {
				double distance = getDistance(getAttackTarget().getX(), getAttackTarget().getBoundingBox().minY, getAttackTarget().getZ());

				setIsStanding(distance <= 3.0D);
			}
	
			if (getAttackTarget() == null) {
				setIsStanding(false);
			}
		}

		if (level.isClientSide() && !isBiped()) {
			prevStandingAngle = standingAngle;

			if (standingAngle > 0 && !isStanding()) {
				standingAngle -= 0.1F;
			}

			if (isStanding() && standingAngle <= 1F) {
				standingAngle += 0.1F;
			}
			
			if (standingAngle < 0 && !isStanding()) {
				standingAngle = 0F;
			}

			if (isStanding() && standingAngle > 1F) {
				standingAngle = 1F;
			}
			
			standingAngle = MathHelper.clamp(standingAngle, 0, 1);
		}

		super.onLivingUpdate();
	}

    @OnlyIn(Dist.CLIENT)
    public float smoothedStandingAngle(float partialTicks) {
        return prevStandingAngle + (standingAngle - prevStandingAngle) * partialTicks;
    }

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return EntityCryptCrawler.class != entity;
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
    public boolean isNotColliding() {
        return !level.containsAnyLiquid(getBoundingBox()) && level.getBlockCollisions(this, getBoundingBox()).isEmpty() && level.checkNoEntityCollision(getBoundingBox(), this);
    }

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if(!this.isBlocking() && this.canSee(entity)) {
			boolean hasHitTarget = entity.hurt(DamageSource.causeMobDamage(this), (float) ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));

			if (hasHitTarget) {
				if(!getMainHandItem().isEmpty())
					getMainHandItem().getItem().hitEntity(getMainHandItem(), (LivingEntity) entity, this);
				
				//entity.addVelocity(-MathHelper.sin(yRot * 3.141593F / 180.0F) * 0.5F, 0.2D, MathHelper.cos(yRot * 3.141593F / 180.0F) * 0.5F);
				
				if (!level.isClientSide())
					level.playSound((PlayerEntity) null, posX, posY, posZ, SoundRegistry.CRYPT_CRAWLER_LIVING, SoundCategory.HOSTILE, 1F, 0.5F);
			}
			return hasHitTarget;
		}
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (tickCount < 40 && source == DamageSource.IN_WALL) {
			return false;
		}
		
		boolean wasAttackBlocked = super.hurt(source, amount);
		
		if(this.isBlocking() && !wasAttackBlocked) {
			recentlyBlockedAttack = true;
			
			//Play shield block sound to all listeners. For some reason shield block sound from item doesn't seem to work.
			this.world.setEntityState(this, EVENT_SHIELD_BLOCKED);
		}
		
		return wasAttackBlocked;
	}
	
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_SHIELD_BLOCKED) {
			this.world.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.NEUTRAL, 1.0F, 0.8F + this.level.random.nextFloat() * 0.4F, false);
		}
	}
	
	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (rand.nextInt(3) == 0) { // TODO re-enable for both types
			setIsBiped(true);
			if (rand.nextInt(3) == 0)
				setIsChief(true);
		}

		if (isBiped()) {
			if (rand.nextFloat() < 0.05F)
				setLeftHanded(true);
			else
				setLeftHanded(false);
			setRandomEquipment();
		}
		updateAttributes();
		setHealth(getMaxHealth());
		return livingdata;
	}

	protected void setRandomEquipment() {
		if (!isChief()) {
			int randomWeapon = rand.nextInt(5);
			int randomShield = rand.nextInt(3);

			switch (randomWeapon) {
			case 0:
				setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemRegistry.WIGHTS_BANE));
				break;
			case 1:
				setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemRegistry.WEEDWOOD_SWORD));
				break;
			case 2:
				setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemRegistry.BONE_SWORD));
				break;
			case 3:
				setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemRegistry.BONE_AXE));
				break;
			case 4:
				setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemRegistry.WEEDWOOD_AXE));
				break;
			}

			if (!getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
				switch (randomShield) {
				case 0:
					setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
					break;
				case 1:
					setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(ItemRegistry.WEEDWOOD_SHIELD));
					break;
				case 2:
					setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(ItemRegistry.BONE_SHIELD));
					break;
				}
			}
		}
		else {
			setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(ItemRegistry.SYRMORITE_SHIELD));
			setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemRegistry.OCTINE_SWORD));
		}
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putBoolean("is_biped", isBiped());
		nbt.putBoolean("is_chief", isChief());
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		setIsBiped(nbt.getBoolean("is_biped"));
		setIsChief(nbt.getBoolean("is_chief"));
	}

	@Override
	public float getAIMoveSpeed() {
		//Half move speed when blocking
		return (this.isBlocking() ? 0.5F : 1.0F) * super.getAIMoveSpeed();
	}
	
	static class AICryptCrawlerAttack extends EntityAIAttackMelee {
		public AICryptCrawlerAttack(EntityCryptCrawler crypt_crawler) {
			super(crypt_crawler, 1.2D, false);
			this.setMutexBits(this.getMutexBits() | MUTEX_ATTACKING);
		}

		@Override
		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return (double) (3.0F + attackTarget.width);
		}
	}
	
	static class AIShieldCharge extends EntityAIBase {
		private EntityCryptCrawler crawler;
		
		private int chargeCooldown;
		private int chargeCooldownMax;
		
		private int chargeTimer = 0;
		
		public AIShieldCharge(EntityCryptCrawler crawler) {
			this.crawler = crawler;
			setMutexBits(3 | MUTEX_BLOCKING | MUTEX_ATTACKING);
		}
		
		protected boolean isHoldingShield() {
			if(crawler != null) {
				ItemStack heldItem = crawler.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
				if (heldItem.isEmpty()) {
					return false;
				} else if(!heldItem.getItem().isShield(heldItem, this.crawler)) {
					return false;
				}
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public boolean canUse() {
			if(!crawler.isChief()) {
				return false;
			}
			
			ItemStack heldItem = crawler.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
			if (heldItem.isEmpty()) {
				return false;
			} else if(!heldItem.getItem().isShield(heldItem, this.crawler)) {
				return false;
			}
			
			if(crawler.getAttackTarget() != null) {
				if(chargeCooldownMax < 0) {
					chargeCooldownMax = 80 + crawler.rand.nextInt(90);
				}
				
				chargeCooldown++;
				
				return chargeCooldown > chargeCooldownMax;
			}
			
			return false;
		}
		
		@Override
		public void start() {
			this.chargeCooldown = 0;
			this.chargeCooldownMax = -1;
		}
		
		@Override
		public boolean canContinueToUse() {
			return this.isHoldingShield() && this.chargeTimer < 60;
		}
		
		@Override
		public void stop() {
			crawler.setIsBlocking(false);
			this.chargeTimer = 0;
		}
		
		@Override
		public void updateTask() {
			this.chargeTimer++;
			
			if(!crawler.isBlocking()) {
				crawler.setIsBlocking(true);
			}
			
			if(this.chargeTimer < 20) {
				crawler.setSneaking(true);
			} else {
				crawler.setSneaking(false);
			}
			
			LivingEntity target = this.crawler.getAttackTarget();
			if(target != null) {
				this.crawler.faceEntity(target, 15, 15);
			}
		}
	}

	static class AIShieldBlock extends EntityAIBase {
		private EntityCryptCrawler crawler;
		private LivingEntity target;
		private int blockingCount;
		private int blockingCountMax;
		private int meleeBlockingCounter;
		private int meleeBlockingCounterMax;
		
		private int blockingCooldownCounter;
		private int blockingCooldownCounterMax = -1;
		
		public AIShieldBlock(EntityCryptCrawler crawler) {
			this.crawler = crawler;
			setMutexBits(MUTEX_BLOCKING);
		}

		protected boolean isInMeleeRange() {
			return crawler != null && target != null && crawler.getDistanceSq(target) < 9.0D;
		}
		
		protected boolean isHoldingShield() {
			if(crawler != null) {
				ItemStack heldItem = crawler.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
				if (heldItem.isEmpty()) {
					return false;
				} else if(!heldItem.getItem().isShield(heldItem, this.crawler)) {
					return false;
				}
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public boolean canUse() {
			if(!this.isHoldingShield()) {
				return false;
			}

			target = crawler.getAttackTarget();

			if(target != null && !crawler.isSwingInProgress && crawler.hurtResistantTime <= Math.max(0, crawler.maxHurtResistantTime - 5)) {
				blockingCooldownCounter++;
				
				if(blockingCooldownCounterMax < 0) {
					blockingCooldownCounterMax = 4 + crawler.rand.nextInt(5);
				}
				
				return blockingCooldownCounter > blockingCooldownCounterMax;
			}
			
			return false;
		}

		@Override
		public boolean canContinueToUse() {
			return this.isHoldingShield() && !crawler.recentlyBlockedAttack && blockingCount != -1 && crawler.hurtResistantTime <= Math.max(0, crawler.maxHurtResistantTime - 5) && !crawler.isSwingInProgress &&
					!(blockingCount > blockingCountMax || (meleeBlockingCounterMax >= 0 && meleeBlockingCounter > meleeBlockingCounterMax));
		}

		@Override
		public void start() {
			blockingCount = 0;
			blockingCountMax = 40 + crawler.rand.nextInt(40);
			meleeBlockingCounterMax = -1;
			meleeBlockingCounter = 0;
			
			//Reset the recently blocked state
			crawler.recentlyBlockedAttack = false;
			
			blockingCooldownCounter = 0;
			blockingCooldownCounterMax = -1;
		}
		
		@Override
		public void stop() {
			crawler.setIsBlocking(false);
			blockingCount = -1;
			meleeBlockingCounterMax = -1;
			meleeBlockingCounter = 0;
		}

		@Override
	    public void updateTask() {
			if(!crawler.isBlocking()) {
				crawler.setIsBlocking(true);
			}
			
			this.blockingCount++;
			
			if(this.isInMeleeRange()) {
				if(meleeBlockingCounterMax < 0) {
					meleeBlockingCounterMax = 10 + crawler.rand.nextInt(20);
				}
				meleeBlockingCounter++;
			} else {
				meleeBlockingCounterMax = -1;
				meleeBlockingCounter = 0;
			}
	    }
	}
}