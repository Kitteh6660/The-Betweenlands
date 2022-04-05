package thebetweenlands.common.entity.mobs;


import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import io.netty.buffer.PacketBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleEntitySwirl;
import thebetweenlands.client.render.particle.entity.ParticleLightningArc;
import thebetweenlands.common.block.misc.BlockOctine;
import thebetweenlands.common.entity.EntityProximitySpawner;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawHatchling extends EntityProximitySpawner implements IEntityAdditionalSpawnData {
	private static final byte EVENT_HATCH_PARTICLES = 100;
	private static final byte EVENT_FLOAT_UP_PARTICLES = 101;
	private static final byte EVENT_NEW_SPAWN = 102;
	
	public static final int MAX_EATING_COOLDOWN = 3000; // set to whatever time between hunger cycles 3000 = 2.5 minutes
	public static final int MIN_EATING_COOLDOWN = 0;
	public static final int MAX_RISE = 40;
	public static final int MIN_RISE = 0; 
	public static final int MAX_FOOD_NEEDED = 8; // amount of times needs to be fed
	NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
	public float feederRotation, prevFeederRotation, headPitch, prevHeadPitch;
	public int prevHatchAnimation, hatchAnimation, riseCount, prevRise, prevTransformTick, flapArmsCount, blinkCount;
	public boolean flapArms = false;
	private Direction facing = Direction.NORTH;
	
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityChiromawHatchling.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Boolean> HATCHED = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_RISING = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_HUNGRY = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> EATING_COOLDOWN = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.INT);
	private static final DataParameter<Integer> FOOD_COUNT = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.INT);
	private static final DataParameter<Boolean> IS_CHEWING = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TRANSFORM = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> TRANSFORM_COUNT = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.INT);
	private static final DataParameter<Integer> HATCH_COUNT = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.INT);
	private static final DataParameter<ItemStack> FOOD_CRAVED = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Boolean> ELECTRIC = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_WILD = EntityDataManager.defineId(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);

	public EntityChiromawHatchling(World world) {
		super(world);
		setSize(0.75F, 1F);
		this.facing = Direction.Plane.HORIZONTAL[world.rand.nextInt(4)];
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		this.entityData.define(HATCHED, false);
		this.entityData.define(IS_RISING, false);
		this.entityData.define(IS_HUNGRY, false);
		this.entityData.define(EATING_COOLDOWN, 0);
		this.entityData.define(FOOD_COUNT, 0);
		this.entityData.define(IS_CHEWING, false);
		this.entityData.define(TRANSFORM, false);
		this.entityData.define(TRANSFORM_COUNT, 0);
		this.entityData.define(HATCH_COUNT, 0);
		this.entityData.define(FOOD_CRAVED, ItemStack.EMPTY);
		this.entityData.define(ELECTRIC, false);
		this.entityData.define(IS_WILD, false);
	}

	@Override
	public void tick() {
		super.tick();
		/*
		//TODO Insta hatch
		if (!level.isClientSide()) {
			this.setHasHatched(true);
			this.setIsTransforming(true);
			this.setTransformCount(60);
			this.setEatingCooldown(0);
			this.setAmountEaten(MAX_FOOD_NEEDED);
		}
		 */	

		//Wild Eggs
		if (!level.isClientSide() && getIsWild()) {
			this.setHasHatched(true);
			this.setEatingCooldown(0);
			this.setAmountEaten(MAX_FOOD_NEEDED);
		}

		// STAGE 1
		if (!getHasHatched()) {
			if (!level.isClientSide()) {
				if (tickCount %200 == 0) { // 200 = 10 seconds (no need to count this every second)
					if (level.getBlockState(getPosition().below()).getBlock() instanceof BlockOctine)
						setHatchTick(getHatchTick() + 1); // increment whilst on an octine block.
				}
				if (getHatchTick() >= 60) { // how many increments before hatching 60 = 10 minutes
					level.setEntityState(this, EVENT_HATCH_PARTICLES);
					setIsHungry(true);
					setHasHatched(true);
					level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCH, SoundCategory.BLOCKS, 1F, 1F);
				}
			}

			if (level.isClientSide()) {
				if (getHatchTick() >= 1) { // animation
					prevHatchAnimation = hatchAnimation;
					hatchAnimation++;
				}
				if(getElectricBoogaloo())
					spawnLightningArcs();
			}
		}

		// STAGE 2
		if (getHasHatched()) {
			prevFeederRotation = feederRotation;
			prevHeadPitch = headPitch;
			prevTransformTick = getTransformCount();

			if (level.isClientSide()) {
				if(!getIsTransforming())
					checkFeeder();
				else {
					if(getOwner() != null)
						lookAtFeeder(getOwner(), 30F);
					}

				if (getRising() && getRiseCount() >= MAX_RISE) {
					if (!getIsHungry())
						if (headPitch < 40)
							headPitch += 8;
					if (getIsHungry())
						if (headPitch > 0)
							headPitch -= 8;
				}

				if (!getRising() && getRiseCount() < MAX_RISE)
					headPitch = getRiseCount();

				if (getAmountEaten() >= MAX_FOOD_NEEDED && !getIsChewing())
					; // TODO maybe else something to show this is ready to transform/transforming
				
				if (getElectricBoogaloo())
					spawnLightningArcs();

				if (getIsChewing())
					if (getTransformCount() < 60)
						spawnEatingParticles();

				if (!getIsHungry() && getRiseCount() >= MAX_RISE && !getIsChewing()) {
					if(!flapArms && flapArmsCount <= 0) {
						if(rand.nextInt(200) == 0) {
							flapArms = true;
							flapArmsCount = 30;
						}
					}
					if(blinkCount <= 0)
						if(rand.nextInt(200) == 0)
							blinkCount =  rand.nextBoolean() ? 10 : 5;
				}

				if (flapArmsCount >= 0)
					flapArmsCount--;

				if(flapArms && flapArmsCount <= 0)
					flapArms = false;
				
				if (blinkCount >= 0)
					blinkCount--;

			}

			prevRise = getRiseCount();
			if (!getRising() && getRiseCount() > MIN_RISE) {
				setRiseCount(getRiseCount() - 4);
			} else if (getRising() && getRiseCount() < MAX_RISE) {
				setRiseCount(getRiseCount() + 4);
			}
			
			if (!level.isClientSide()) {
				checkArea();
					
				if (!getIsHungry()) {
					setEatingCooldown(getEatingCooldown() - 1);
					if (getEatingCooldown() <= MAX_EATING_COOLDOWN && getEatingCooldown() > MAX_EATING_COOLDOWN - 60 && !getIsChewing())
						setIsChewing(true);
					if (getEatingCooldown() < MAX_EATING_COOLDOWN - 60 && getIsChewing())
						setIsChewing(false);
					if (getEatingCooldown() <= MIN_EATING_COOLDOWN && getAmountEaten() < MAX_FOOD_NEEDED) {
						setIsHungry(true);
						setFoodCraved(chooseNewFoodFromLootTable());
					}
				}

				if (getIsTransforming()) {
					if (getTransformCount() == 1)
						level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCHLING_TRANSFORM, SoundCategory.NEUTRAL, 1F, 1F);
					if (getTransformCount() <= 60) {
						setTransformCount(getTransformCount() + 1);
						level.setEntityState(this, EVENT_FLOAT_UP_PARTICLES);
						}
					if(getOwner() != null)
						lookAtFeeder(getOwner(), 30F);
				}

				if (!isDead && getRiseCount() >= MAX_RISE) {
					if(getIsHungry() && tickCount %20 == 0) {
						level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_LONG, SoundCategory.NEUTRAL, 1F, 1F + rand.nextFloat() * 0.125F - rand.nextFloat() * 0.125F);
					}
					if (getAmountEaten() >= MAX_FOOD_NEEDED && getEatingCooldown() <= 0) {
						if (!getIsTransforming())
							setIsTransforming(true);
						if (!isDead && getTransformCount() >= 60) {
							Entity spawn = getEntitySpawned();
							if (spawn != null) {
								if (!spawn.isDead) { // just in case
									level.setEntityState(this, EVENT_NEW_SPAWN);
									level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.NEUTRAL, 1F, 1F + (level.rand.nextFloat() - level.rand.nextFloat()) * 0.8F);
									level.addFreshEntity(spawn);
								}
								remove();
							}
						}
					}
				}
			}
		}
		
		this.yRot = this.facing.getHorizontalAngle();
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		if (getIsHungry() && getRiseCount() < MAX_RISE)
			return SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_SHORT;
		if (!getHasHatched())
			return SoundRegistry.CHIROMAW_HATCHLING_INSIDE_EGG;
		return SoundRegistry.CHIROMAW_HATCHLING_LIVING;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_HATCH_PARTICLES) {
			for (int count = 0; count <= 100; ++count) {
				BLParticles.ITEM_BREAKING.spawn(world, this.getX() + (world.rand.nextDouble() - 0.5D), this.getY() + 2D + world.rand.nextDouble(), this.getZ() + (world.rand.nextDouble() - 0.5D), ParticleArgs.get().withData(new ItemStack(ItemRegistry.CHIROMAW_EGG)));
			}
		}
		
		if(id == EVENT_FLOAT_UP_PARTICLES) {
			ParticleArgs<?> args = ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
			if(getElectricBoogaloo())
				args.withColor(0.420F, 0.565F, 0.553F, 1); //lightning
			else
				args.withColor(0.227F, 0.317F, 0.294F, 1); //normal

			args.withScale(0.5F + rand.nextFloat() * 0.5f);
			ParticleEntitySwirl particle = (ParticleEntitySwirl) BLParticles.CHIROMAW_TRANSFORM_SWIRL.create(this.world, this.getX(), this.getY() + 2.6D, this.getZ(), args);
			particle.setOffset(0, -1.3D, 0);
			particle.setTargetOffset(0, 1.3D, 0);
			particle.updateTarget();
			Minecraft.getInstance().effectRenderer.addEffect(particle);
		}
		
		if(id == EVENT_NEW_SPAWN) {
			int leafCount = 40;
			float x = (float) (posX);
			float y = (float) (posY + 1.1F);
			float z = (float) (posZ);
			while (leafCount-- > 0) {
				float dx = level.rand.nextFloat() * 1 - 0.5f;
				float dy = level.rand.nextFloat() * 1f - 0.1F;
				float dz = level.rand.nextFloat() * 1 - 0.5f;
				float mag = 0.08F + level.rand.nextFloat() * 0.07F;
				if(getElectricBoogaloo())
					BLParticles.CHIROMAW_TRANSFORM_LIGHTNING.spawn(level, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
				else
					BLParticles.CHIROMAW_TRANSFORM.spawn(level, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnEatingParticles() {
		double angle = Math.toRadians(feederRotation + yRot);
		double offSetX = -Math.sin(angle) * 0.35D;
		double offSetZ = Math.cos(angle) * 0.35D;
		BLParticles.ITEM_BREAKING.spawn(world, this.getX() + (float) offSetX + (world.rand.nextDouble() * 0.25D - 0.125D) , this.getY() + 0.75F, this.getZ() + (float) offSetZ + (world.rand.nextDouble() * 0.25D - 0.125D), ParticleArgs.get().withData(this.getFoodCraved()));
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnLightningArcs() {
		if(level.rand.nextInt(2) == 0) {
			float ox = (level.rand.nextFloat() - 0.5f) * 2;
			float oy = (level.rand.nextFloat() - 0.5f) * 2;
			float oz = (level.rand.nextFloat() - 0.5f) * 2;
			
			ParticleLightningArc particle = (ParticleLightningArc) BLParticles.LIGHTNING_ARC.create(this.world, this.getX(), this.getY() + 0.5F + getTransformCount() * 0.02F, this.getZ(), 
					ParticleArgs.get()
					.withMotion(this.motionX, this.motionY, this.motionZ)
					.withColor(0.3f, 0.5f, 1.0f, 0.9f)
					.withData(new Vector3d(this.getX() + ox, this.getY() + oy, this.getZ() + oz)));
			particle.setLighting(false);
			
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);
		}
	}

	protected Entity checkFeeder() {
		Entity entity = null;
			List<PlayerEntity> list = level.getEntitiesOfClass(PlayerEntity.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);
				if (entity != null)
					if (entity instanceof PlayerEntity)
						if (!isDead && getRiseCount() >= MAX_RISE)
							lookAtFeeder(entity, 30F);
			}

			if (entity == null && getRiseCount() > MIN_RISE)
				feederRotation = updateFeederRotation(feederRotation, 0F, 30F);

		return entity;
	}

	@Override
	protected Entity checkArea() {
		Entity entity = null;
		if (!level.isClientSide()) {
			List<PlayerEntity> list = level.getEntitiesOfClass(PlayerEntity.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);
				if (entity != null) {
					if (entity instanceof PlayerEntity) {
						if (canSneakPast() && entity.isCrouching())
							return null;
						else if (checkSight() && !canSee(entity))
							return null;
						else {
							if(!getRising())
								setRising(true);
						}

					}
				}
			}
			if (entity == null && getRiseCount() > MIN_RISE && !getIsTransforming()) {
				if (getRising())
					setRising(false);
			}
		}
		return entity;
	}

	@Override
	protected void performPreSpawnaction(@Nullable Entity targetEntity, Entity entitySpawned) {}

	public void lookAtFeeder(Entity entity, float maxYawIncrease) {
		double distanceX = entity.getX() - posX;
		double distanceZ = entity.getZ() - posZ;
		float angle = (float) (MathHelper.atan2(distanceZ, distanceX) * (180D / Math.PI)) - 90.0F;
		feederRotation = updateFeederRotation(feederRotation, angle - this.yRot, maxYawIncrease);
	}

	private float updateFeederRotation(float angle, float targetAngle, float maxIncrease) {
		float f = MathHelper.wrapDegrees(targetAngle - angle);
		if (f > maxIncrease)
			f = maxIncrease;
		if (f < -maxIncrease)
			f = -maxIncrease;
		return angle + f;
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.swing(hand);
		if(!getIsTransforming() && getHasHatched()) {
			if (!stack.isEmpty() && !checkFoodEqual(stack, getFoodCraved())) {
				level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCHLING_NO, SoundCategory.NEUTRAL, 1F, 1F);
					return false;
			}
			if (!stack.isEmpty() && getIsHungry()) {
				
				if (checkFoodEqual(stack, getFoodCraved())) {
					if (!player.isCreative()) {
						stack.shrink(1);
						if (stack.getCount() <= 0)
							player.setItemInHand(hand, ItemStack.EMPTY);
					}
					setEatingCooldown(MAX_EATING_COOLDOWN);
					setAmountEaten(getAmountEaten() + 1);
					level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCHLING_EAT, SoundCategory.NEUTRAL, 1F, 1F);
					setIsHungry(false);
					return true;
				}
			}
		}
		return super.processInteract(player, hand);
	}

	private boolean checkFoodEqual(ItemStack stack, ItemStack foodCraved) {
		if(stack.getItem() == foodCraved.getItem() && stack.getDamageValue() == foodCraved.getDamageValue()) {

			if(stack.getItem() instanceof ItemMob) {
				ResourceLocation cravedEntity = ((ItemMob)foodCraved.getItem()).getCapturedEntityId(foodCraved);
				ResourceLocation stackEntity = ((ItemMob)stack.getItem()).getCapturedEntityId(stack);
				
				if(!Objects.equals(cravedEntity, stackEntity)) {
					return false;
				}
			}
			
			return true;
		}
		return false;
	}

	protected ResourceLocation getFoodCravingLootTable() {
		return LootTableRegistry.CHIROMAW_HATCHLING;
	}

	public ItemStack chooseNewFoodFromLootTable() {
		LootTable lootTable = level.getLootTableManager().getLootTableFromLocation(getFoodCravingLootTable());
		if (lootTable != null) {
			LootContext.Builder lootBuilder = (new LootContext.Builder((ServerWorld) this.world)).withLootedEntity(this);
			List<ItemStack> loot = lootTable.generateLootForPools(world.rand, lootBuilder.build());
			if (!loot.isEmpty()) {
				Collections.shuffle(loot); // mix it up a bit
				return loot.get(0);
			}
		}
		return new ItemStack(ItemRegistry.SNAIL_FLESH_RAW); // to stop null;
	}

	@Override
	public ITextComponent getName() {
		if (getElectricBoogaloo()) {
			return I18n.get("entity.thebetweenlands.chiromaw_hatchling_lightning.name");
		}
		return super.getName();
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!level.isClientSide())
			moveTo(posX, posY, posZ, 0F, 0.0F); // stahp random rotating on spawn with an egg mojang pls
		return livingdata;
	}

	@Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
		if(!getHasHatched() && getHatchTick() < 1)
			setElectricBoogaloo(true);
		else
			super.onStruckByLightning(lightningBolt);
    }

	public void setElectricBoogaloo(boolean electric) {
		entityData.set(ELECTRIC, electric);
	}

    public boolean getElectricBoogaloo() {
        return entityData.get(ELECTRIC);
    }

	private void setHasHatched(boolean hatched) {
		entityData.set(HATCHED, hatched);
	}

    public boolean getHasHatched() {
        return entityData.get(HATCHED);
    }

	private void setRising(boolean rise) {
		entityData.set(IS_RISING, rise);
	}

    public boolean getRising() {
        return entityData.get(IS_RISING);
    }

	private void setRiseCount(int riseCountIn) {
		this.riseCount = riseCountIn;
	}

	public int getRiseCount() {
		return this.riseCount;
	}

	private void setAmountEaten(int foodIn) {
		entityData.set(FOOD_COUNT, foodIn);
	}

	private int getAmountEaten() {
		return entityData.get(FOOD_COUNT);
	}

	private void setEatingCooldown(int cooldown) {
		entityData.set(EATING_COOLDOWN, cooldown);
	}

	public int getEatingCooldown() {
		return entityData.get(EATING_COOLDOWN);
	}

	private void setIsHungry(boolean hungry) {
		entityData.set(IS_HUNGRY, hungry);
	}

	public boolean getIsHungry() {
		return entityData.get(IS_HUNGRY);
	}

	private void setIsChewing(boolean chewing) {
		entityData.set(IS_CHEWING, chewing);
	}

	public boolean getIsChewing() {
		return entityData.get(IS_CHEWING);
	}

	private void setIsTransforming(boolean transform) {
		entityData.set(TRANSFORM, transform);
	}

	public boolean getIsTransforming() {
		return entityData.get(TRANSFORM);
	}
	
	private void setTransformCount(int transformCountIn) {
		entityData.set(TRANSFORM_COUNT, transformCountIn);
	}

	public int getTransformCount() {
		return entityData.get(TRANSFORM_COUNT);
	}

	private void setHatchTick(int hatchCount) {
		entityData.set(HATCH_COUNT, hatchCount);
		
	}

	public int getHatchTick() {
		return entityData.get(HATCH_COUNT);
	}

	public void setFoodCraved(ItemStack itemStack) {
		entityData.set(FOOD_CRAVED, itemStack);
		
	}

	public ItemStack getFoodCraved() {
		return entityData.get(FOOD_CRAVED);
	}

	public void setIsWild(boolean wild) {
		entityData.set(IS_WILD, wild);
	}

	public boolean getIsWild() {
		return entityData.get(IS_WILD);
	}

	@Override
	public void onKillCommand() {
		remove();
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return true;
    }

	@Override
    public boolean canBeCollidedWith() {
		if(getIsTransforming())
			return false;
        return true;
    }

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return 5F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	@Override
	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).inflate(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal());
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		MobEntity entity = null;
		if (getIsWild()) {
			entity = new EntityChiromaw(level);
		} else {
			entity = new EntityChiromawTame(level);
			((EntityChiromawTame) entity).setOwnerId(getOwnerId());
			if (hasCustomName())
				entity.setCustomNameTag(getCustomNameTag());
			if (getElectricBoogaloo())
				((EntityChiromawTame) entity).setElectricBoogaloo(true);
		}
		if (entity != null) {
			entity.moveTo(posX, posY + 1F, posZ, feederRotation + yRot, 0.0F);
			entity.rotationYawHead = entity.yRot;
			entity.renderYawOffset = entity.yRot;
			((MobEntity) entity).setMoveForward(0.1F);
		}
		return entity;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 1;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		if (getOwnerId() == null)
			nbt.putString("OwnerUUID", "");
		else
			nbt.putString("OwnerUUID", getOwnerId().toString());

		nbt.putBoolean("Hatched", getHasHatched());
		nbt.putInt("HatchTick", getHatchTick());
		nbt.putBoolean("Rising", getRising());
		nbt.putInt("RisingCount", getRiseCount());
		nbt.putBoolean("Hungry", getIsHungry());
		nbt.putInt("FoodEaten", getAmountEaten());
		nbt.putInt("EatingCooldown", getEatingCooldown());
		nbt.putBoolean("Transforming", getIsTransforming());
		nbt.putInt("TransformCount", getTransformCount());
		nbt.putInt("Facing", this.facing.ordinal());
		nbt.putBoolean("Electric", getElectricBoogaloo());
		nbt.putBoolean("Wild", getIsWild());

		CompoundNBT nbtFood = new CompoundNBT();
		getFoodCraved().save(nbtFood);
		nbt.put("Items", nbtFood);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		String s;
		if (nbt.contains("OwnerUUID", 8))
			s = nbt.getString("OwnerUUID");
		else {
			String s1 = nbt.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(getServer(), s1);
		}
		if (!s.isEmpty()) {
			try {
				setOwnerId(UUID.fromString(s));
			} catch (Throwable e) {}
		}

		setHasHatched(nbt.getBoolean("Hatched"));
		setHatchTick(nbt.getInt("HatchTick"));
		setRising(nbt.getBoolean("Rising"));
		setRiseCount(nbt.getInt("RisingCount"));
		setIsHungry(nbt.getBoolean("Hungry"));
		setAmountEaten(nbt.getInt("FoodEaten"));
		setEatingCooldown(nbt.getInt("EatingCooldown"));
		setIsTransforming(nbt.getBoolean("Transforming"));
		setTransformCount(nbt.getInt("TransformCount"));
		this.facing = Direction.VALUES[nbt.getInt("Facing")];
		setElectricBoogaloo(nbt.getBoolean("Electric"));
		setIsWild(nbt.getBoolean("Wild"));

		CompoundNBT nbtFood = (CompoundNBT) nbt.getTag("Items");
		ItemStack stack = new ItemStack(ItemRegistry.SNAIL_FLESH_RAW);
		if(nbtFood != null)
			stack = new ItemStack(nbtFood);
		setFoodCraved(stack);
	}

	@Nullable
	public UUID getOwnerId() {
		return entityData.get(OWNER_UNIQUE_ID).orNull();
	}

	public void setOwnerId(@Nullable UUID uuid) {
		entityData.set(OWNER_UNIQUE_ID, Optional.fromNullable(uuid));
	}

	@Nullable
	public LivingEntity getOwner() {
		try {
			UUID uuid = getOwnerId();
			return uuid == null ? null : level.getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public boolean isOwner(LivingEntity entityIn) {
		return entityIn == getOwner();
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		buf.writeInt(this.facing.ordinal());
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		this.facing = Direction.VALUES[buf.readInt()];
	}
}