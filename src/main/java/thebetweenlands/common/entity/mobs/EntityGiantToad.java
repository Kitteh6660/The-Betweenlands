package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.Hand;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.common.entity.EntityTameableBL;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGiantToad extends EntityTameableBL implements IEntityBL, IRingOfGatheringMinion {
	private static final DataParameter<Byte> DW_SWIM_STROKE = EntityDataManager.defineId(EntityGiantToad.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> DW_TAMED = EntityDataManager.defineId(EntityGiantToad.class, DataSerializers.BOOLEAN);

	private int temper = 0;
	private int ticksOnGround = 0;
	private int strokeTicks = 0;
	private boolean prevOnGround;
	private ControlledAnimation leapingAnim = new ControlledAnimation(4);
	private ControlledAnimation swimmingAnim = new ControlledAnimation(8);
	private ControlledAnimation waterStanceAnim = new ControlledAnimation(4);

	public EntityGiantToad(World world) {
		super(world);
		this.setPathPriority(PathNodeType.WATER, 0.0F);
		this.setSize(1.6F, 1.5F);
		this.stepHeight = 1.0F;
		this.experienceValue = 8;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new EntityAISwimming(this));
		this.goalSelector.addGoal(1, new EntityAIPanic(this, 1.0D));
		this.goalSelector.addGoal(2, new EntityAIWander(this, 1.0D));
		this.goalSelector.addGoal(3, new EntityAIWatchClosest(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(4, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.05D);
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(60.0D);
		this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DW_SWIM_STROKE, (byte) 0);
		this.entityData.define(DW_TAMED, false);
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putInt("Temper", this.temper);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.temper = nbt.getInt("Temper");
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public void tick() {
		prevOnGround = onGround;

		//Extend AABB so that the player doesn't suffocate in blocks
		if (this.isBeingRidden()) {
			this.setEntityBoundingBox(this.getBoundingBox().setMaxY(this.getBoundingBox().minY + this.height + this.getControllingPassenger().height));
		}
		super.tick();
		this.setEntityBoundingBox(this.getBoundingBox().setMaxY(this.getBoundingBox().minY + this.height));

		if (this.onGround) {
			this.ticksOnGround++;
		} else {
			this.ticksOnGround = 0;
		}
		if (!this.level.isClientSide()) {
			if (this.strokeTicks > 0) {
				this.strokeTicks--;
				this.entityData.set(DW_SWIM_STROKE, (byte) 1);
			} else {
				this.entityData.set(DW_SWIM_STROKE, (byte) 0);
			}
		}
		if (!level.isClientSide()) {
			if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}
			this.setAir(20);
			Path path = getNavigation().getPath();
			if (path != null && !path.isFinished() && !this.isMovementBlocked()) {
				if (this.inWater) {
					int index = path.getCurrentPathIndex();
					if (index < path.getCurrentPathLength()) {
						PathPoint nextHopSpot = path.getPathPointFromIndex(index);
						float x = (float) (nextHopSpot.x - posX);
						float z = (float) (nextHopSpot.z - posZ);
						float angle = (float) (Math.atan2(z, x));
						float distance = (float) Math.sqrt(x * x + z * z);
						if (distance > 1) {
							if (this.strokeTicks == 0) {
								double speedMultiplier = (Math.min(distance, 4.0F) / 4.0F * 0.8F + 0.2F);
								motionX += speedMultiplier * 0.8F * MathHelper.cos(angle);
								motionZ += speedMultiplier * 0.8F * MathHelper.sin(angle);
								this.world.setEntityState(this, (byte) 8);
								this.strokeTicks = 40;
							} else if (this.collidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						} else {
							path.incrementPathIndex();
						}
					}
				} else if (onGround) {
					int index = path.getCurrentPathIndex();
					if (index < path.getCurrentPathLength()) {
						PathPoint nextHopSpot = path.getPathPointFromIndex(index);
						float x = (float) (nextHopSpot.x - posX);
						float z = (float) (nextHopSpot.z - posZ);
						float angle = (float) (Math.atan2(z, x));
						float distance = (float) Math.sqrt(x * x + z * z);
						if (distance > 1) {
							if (this.ticksOnGround > 20) {
								double speedMultiplier = (Math.min(distance, 2.0F) / 2.0F * 0.8F + 0.2F);
								motionY += speedMultiplier * 0.6 + MathHelper.clamp((nextHopSpot.y - this.getY()) / 6.0D, 0.0D, 0.3D);
								motionX += speedMultiplier * 0.5 * MathHelper.cos(angle);
								motionZ += speedMultiplier * 0.5 * MathHelper.sin(angle);
								ForgeHooks.onLivingJump(this);
							} else if (this.collidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						} else {
							path.incrementPathIndex();
						}
					}
				}
			}
			if (this.isBeingRidden()) {
				List<LivingEntity> targets = this.world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.6D, 0.6D, 0.6D));
				LivingEntity closestTarget = null;
				float lastAngDiff = 0.0F;
				Entity controllingPassenger = this.getControllingPassenger();
				for (LivingEntity target : targets) {
					if (target.getRevengeTarget() == controllingPassenger || (controllingPassenger instanceof LivingEntity && ((LivingEntity) controllingPassenger).getRevengeTarget() == target)) {
						float x = (float) (target.getX() - posX);
						float z = (float) (target.getZ() - posZ);
						float angle = (float) (Math.atan2(z, x));
						float angDiff = (float) Math.abs(this.yRot % 360.0F - Math.toDegrees(angle) % 360.0F + 90) % 360.0F;
						float angDiffWrapped = Math.min(angDiff, Math.abs(360.0F - angDiff));
						//Only attack mobs in front of the toad (+-50 deg.)
						if (angDiffWrapped <= 50 && (angDiffWrapped < lastAngDiff || closestTarget == null)) {
							closestTarget = target;
							lastAngDiff = angDiffWrapped;
						}
					}
				}
				if (closestTarget != null) {
					DamageSource damageSource = new EntityDamageSourceIndirect("mob", this, controllingPassenger);
					float attackDamage = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
					if (closestTarget.hurt(damageSource, attackDamage)) {
						boolean doesJump = true;
						//Random chance for the target to attack back
						if (this.random.nextInt(35) == 0) {
							if (closestTarget.attackEntityAsMob(this))
								doesJump = false;
						}
						float x = (float) (closestTarget.getX() - posX);
						float z = (float) (closestTarget.getZ() - posZ);
						if (doesJump && ((this.onGround && this.ticksOnGround >= 5) || (this.inWater && this.strokeTicks == 0))) {
							float angle = (float) (Math.atan2(z, x));
							if (!this.inWater)
								motionY += 0.4;
							motionX += 0.5 * MathHelper.cos(angle);
							motionZ += 0.5 * MathHelper.sin(angle);
							if (this.inWater) {
								this.strokeTicks = 20;
								this.world.setEntityState(this, (byte) 8);
							} else
								ForgeHooks.onLivingJump(this);
							this.onGround = false;
						}
						closestTarget.knockBack(this, attackDamage / 2.5F, -x, -z);
					}
				}
			}
		}

		if (this.level.isClientSide()) {
			waterStanceAnim.updateTimer();
			if (this.inWater) {
				waterStanceAnim.increaseTimer();
			} else {
				waterStanceAnim.decreaseTimer();
			}

			leapingAnim.updateTimer();
			if (this.inWater || onGround || prevOnGround) {
				leapingAnim.decreaseTimer();
			} else {
				leapingAnim.increaseTimer();
			}

			this.swimmingAnim.updateTimer();
			if (this.entityData.get(DW_SWIM_STROKE) == 1) {
				if (this.strokeTicks < 20)
					this.strokeTicks++;
			} else {
				this.strokeTicks = 0;
			}

			if (this.inWater && this.entityData.get(DW_SWIM_STROKE) == 1 && this.strokeTicks < 12) {
				this.swimmingAnim.increaseTimer();
			} else {
				this.swimmingAnim.decreaseTimer();
			}
		}
	}

	public float getLeapProgress(float partialRenderTicks) {
		return leapingAnim.getAnimationProgressSinSqrt(partialRenderTicks);
	}

	public float getSwimProgress(float partialRenderTicks) {
		return Math.min((1F - (float) Math.pow(1 - this.swimmingAnim.getAnimationFraction(partialRenderTicks), 2)) * 2.5F * this.swimmingAnim.getAnimationFraction(partialRenderTicks), 1.0F);
	}

	public float getWaterStanceProgress(float partialRenderTicks) {
		return waterStanceAnim.getAnimationProgressSinSqrt(partialRenderTicks);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GIANT_TOAD_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.GIANT_TOAD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GIANT_TOAD_DEATH;
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand) {
		boolean holdsEquipment = hand == Hand.MAIN_HAND && !player.getItemInHand(hand).isEmpty() && (player.getItemInHand(hand).getItem() instanceof IEquippable || player.getItemInHand(hand).getItem() == ItemRegistry.AMULET_SLOT);
		if (holdsEquipment)
			return true;
		boolean holdsWings = EnumItemMisc.DRAGONFLY_WING.isItemOf(player.getItemInHand(hand));
		if (!this.isBeingRidden() && this.isTamed() && (!holdsWings || this.getHealth() >= this.getMaxHealth()) && !player.isCrouching() && !this.level.isClientSide()) {
			player.startRiding(this);
			return true;
		} else if (holdsWings) {
			if (!this.isTamed()) {
				if (!this.level.isClientSide()) {
					this.temper += this.random.nextInt(4) + 1;
					if (this.temper >= 30 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
						this.world.setEntityState(this, (byte) 7);
						this.setTamedBy(player);
						this.temper = 0;
					} else {
						this.world.setEntityState(this, (byte) 6);
					}
					if (!player.isCreative()) {
						player.getItemInHand(hand).shrink(1);
						if (player.getItemInHand(hand).getCount() <= 0)
							player.setItemInHand(hand, ItemStack.EMPTY);
					}
				}
				return true;
			}
			if (this.getHealth() < this.getMaxHealth()) {
				if (!this.level.isClientSide()) {
					this.world.setEntityState(this, (byte) 6);
					this.heal(4.0F);
					player.getItemInHand(hand).shrink(1);
					if (player.getItemInHand(hand).getCount() <= 0)
						player.setItemInHand(hand, ItemStack.EMPTY);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canBePushed() {
		return !this.isBeingRidden();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		Entity entity = source.getTrueSource();
		return !(this.isBeingRidden() && this.getControllingPassenger() != null && this.getControllingPassenger().equals(entity)) && super.hurt(source, damage);
	}

	@Override
	protected boolean isMovementBlocked() {
		return this.isBeingRidden() || super.isMovementBlocked();
	}

	@Override
	public void travel(float strafing,float up,  float forward) {
		Entity controllingPassenger = this.getControllingPassenger();
		if (this.isBeingRidden() && controllingPassenger != null && controllingPassenger instanceof LivingEntity) {
			this.prevRotationYaw = this.yRot = controllingPassenger.yRot;
			this.xRot = controllingPassenger.xRot * 0.5F;
			this.setRotation(this.yRot, this.xRot);
			this.rotationYawHead = this.renderYawOffset = this.yRot;
			strafing = ((LivingEntity) controllingPassenger).xxa * 0.5F;
			forward = ((LivingEntity) controllingPassenger).zza;

			if (forward <= 0.0F) {
				forward *= 0.25F;
			}

			boolean onWaterSurface = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getY() + 1), MathHelper.floor(this.getZ()))).getMaterial().isLiquid();

			if (!this.inWater || !onWaterSurface) {
				if (this.onGround && forward != 0.0F) {
					if (!this.level.isClientSide()) {
						if (this.ticksOnGround > 4) {
							motionY += 0.5;
							motionX += forward / 1.5F * MathHelper.cos((float) Math.toRadians(this.yRot + 90));
							motionZ += forward / 1.5F * MathHelper.sin((float) Math.toRadians(this.yRot + 90));
							motionX += strafing / 2.0F * MathHelper.cos((float) Math.toRadians(this.yRot));
							motionZ += strafing / 2.0F * MathHelper.sin((float) Math.toRadians(this.yRot));
							ForgeHooks.onLivingJump(this);
						}
					}
				}
			} else {
				if (this.motionY < 0.0F) {
					if (this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getY() + 0.9D), MathHelper.floor(this.getZ()))).getMaterial().isLiquid()) {
						this.motionY *= 0.05F;
					}
				}
				if (this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getY() + 1.1D), MathHelper.floor(this.getZ()))).getMaterial().isLiquid()) {
					this.motionY += 0.02F;
				}

				if (!this.level.isClientSide()) {
					if (this.collidedHorizontally) {
						this.motionY += 0.2D;
						this.strokeTicks = 0;
					}
				}

				if (!this.level.isClientSide() && forward > 0.0F) {
					if (forward != 0.0F && this.strokeTicks == 0) {
						motionX += forward / 1.25F * MathHelper.cos((float) Math.toRadians(this.yRot + 90));
						motionZ += forward / 1.25F * MathHelper.sin((float) Math.toRadians(this.yRot + 90));
						motionX += strafing / 1.25F * MathHelper.cos((float) Math.toRadians(this.yRot));
						motionZ += strafing / 1.25F * MathHelper.sin((float) Math.toRadians(this.yRot));
						this.world.setEntityState(this, (byte) 8);
						this.strokeTicks = 20;
					}
				}
			}
			if (!this.level.isClientSide() && forward > 0.0F) {
				motionX += 0.05D * MathHelper.cos((float) Math.toRadians(this.yRot + 90));
				motionZ += 0.05D * MathHelper.sin((float) Math.toRadians(this.yRot + 90));
			}

			super.travel(0,  0,0);
		} else {
			this.jumpMovementFactor = 0.02F;
			super.travel(strafing, up, forward);
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void spawnToadParticles(boolean isHeart) {
		ParticleTypes enumparticletypes = isHeart ? ParticleTypes.HEART : ParticleTypes.SMOKE_NORMAL;
		for (int i = 0; i < 7; ++i) {
			double d0 = this.random.nextGaussian() * 0.02D;
			double d1 = this.random.nextGaussian() * 0.02D;
			double d2 = this.random.nextGaussian() * 0.02D;
			this.world.addParticle(enumparticletypes, this.getX() + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width, this.getY() + 0.5D + (double)(this.random.nextFloat() * this.height), this.getZ() + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 8) {
			this.strokeTicks = 0;
		}
		if (id == 7) {
			this.spawnToadParticles(true);
		} else if (id == 6) {
			this.spawnToadParticles(false);
			this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean canPassengerSteer() {
		//TODO: onGround only updates properly if this return false??
		return false;
	}
	
	@Override
	public void fall(float distance, float damageMultiplier) { 
		distance = Math.max(0, distance - 6.0F);
		super.fall(distance, damageMultiplier);
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TOAD;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}
	
	@Override
	public CompoundNBT returnToRing(UUID userId) {
		return this.save(new CompoundNBT());
	}

	@Override
	public boolean returnFromRing(Entity user, CompoundNBT nbt) {
		double prevX = this.getX();
		double prevY = this.getY();
		double prevZ = this.getZ();
		float prevYaw = this.yRot;
		float prevPitch = this.xRot;
		this.readFromNBT(nbt);
		this.moveTo(prevX, prevY, prevZ, prevYaw, prevPitch);
		if(!this.isEntityAlive()) {
			//Revivd by animator
			this.setHealth(this.getMaxHealth());
		}
		this.world.addFreshEntity(this);
		return true;
	}

	@Override
	public boolean shouldReturnOnUnload(boolean isOwnerLoggedIn) {
		return IRingOfGatheringMinion.super.shouldReturnOnUnload(isOwnerLoggedIn) && !this.isSitting();
	}

	@Override
	public UUID getRingOwnerId() {
		return this.getOwnerId();
	}
}

