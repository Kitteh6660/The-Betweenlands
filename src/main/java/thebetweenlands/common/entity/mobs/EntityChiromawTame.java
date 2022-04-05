package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IPullerEntity;
import thebetweenlands.api.entity.IPullerEntityProvider;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.EntityTameableBL;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.ai.EntityAISitBL;
import thebetweenlands.common.entity.draeton.DraetonPhysicsPart;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.entity.draeton.EntityPullerChiromaw;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.entity.movement.PathNavigateFlyingBL;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.network.serverbound.MessageChiromawDoubleJump;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawTame extends EntityTameableBL implements IRingOfGatheringMinion, IPullerEntityProvider<EntityPullerChiromaw> {
	private static final byte EVENT_DOUBLE_JUMP = 80;

	private static final DataParameter<Boolean> ATTACKING = EntityDataManager.defineId(EntityChiromawTame.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ELECTRIC = EntityDataManager.defineId(EntityChiromawTame.class, DataSerializers.BOOLEAN);

	public int doubleJumpTicks;
	public int prevWingFlapTicks, wingFlapTicks;
	public int prevRaiseWingTicks, raiseWingsTicks;

	public EntityChiromawTame(World world) {
		super(world);
		setSize(0.7F, 0.9F);
		moveControl = new FlightMoveHelper(this);
	}

	@Override
	protected void registerGoals() {
		tasks.addGoal(0, new EntityAISwimming(this));
		this.aiSit = new EntityAISitBL(this);
		tasks.addGoal(1, this.aiSit);
		tasks.addGoal(2, new EntityChiromawTame.AIBarbAttack(this));
		tasks.addGoal(3, new EntityChiromawTame.AIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		tasks.addGoal(4, new EntityAIAvoidEntity<>(this, EntityMob.class, 4.0F, 0.75D, 0.75D));
		tasks.addGoal(5, new EntityAIAttackMelee(this, 0.5D, true));
		tasks.addGoal(6, new EntityAIFlyingWander(this, 0.5D));
		tasks.addGoal(7, new EntityAIWatchClosest(this, PlayerEntity.class, 6.0F));
		tasks.addGoal(8, new EntityAILookIdle(this));
		targetTasks.addGoal(1, new EntityChiromawTame.AIChiromawOwnerHurtByTarget(this));
		targetTasks.addGoal(2, new EntityChiromawTame.AIChiromawOwnerHurtTarget(this));
		targetTasks.addGoal(3, new EntityAIHurtByTarget(this, true, new Class[0]));
		targetTasks.addGoal(4, new EntityAINearestAttackableTarget<>(this, EntityMob.class, true));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ATTACKING, false);
		this.entityData.define(ELECTRIC, false);
	}

	@Override
	public ITextComponent getName() {
		if (getElectricBoogaloo()) {
			return I18n.get("entity.thebetweenlands.chiromaw_tame_lightning.name");
		}
		return super.getName();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isAttacking() {
		return entityData.get(ATTACKING);
	}

	public void setAttacking(boolean attacking) {
		entityData.set(ATTACKING, attacking);
	}

	public void setElectricBoogaloo(boolean electric) {
		entityData.set(ELECTRIC, electric);
	}

    public boolean getElectricBoogaloo() {
        return entityData.get(ELECTRIC);
    }

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putBoolean("Electric", getElectricBoogaloo());
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		setElectricBoogaloo(nbt.getBoolean("Electric"));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(50.0D);
		getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32.0D);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FLYING_FIEND_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundRegistry.FLYING_FIEND_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FLYING_FIEND_DEATH;
	}

	@Override
	public void tick() {
		if(this.aiSit != null) {
			this.aiSit.setSitting(this.isSitting());
		}
		
		super.tick();

		Entity riding = this.getRidingEntity();
		
		if(this.doubleJumpTicks > 1) {
			this.doubleJumpTicks--;
		}
		
		this.prevWingFlapTicks = this.wingFlapTicks;
		if(this.wingFlapTicks > 1) {
			this.wingFlapTicks--;
		}
		
		if(riding == null || riding.onGround) {
			this.doubleJumpTicks = 0;
		}
		
		if (riding == null) {
			if (isJumping && isInWater()) {
				getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
				this.setSitting(false);
			}

			if (isSitting()) {
				motionX = motionY = motionZ = 0.0D;
				if (!level.isClientSide()) {
					BlockState state = this.world.getBlockState(this.getPosition().above());
					BlockFaceShape shape = state.getBlockFaceShape(this.world, this.getPosition().above(), Direction.DOWN);
					
					if(shape != BlockFaceShape.SOLID && shape != BlockFaceShape.MIDDLE_POLE_THICK) {
						this.setSitting(false);
					}
				}
			} else if (level.getBlockState(getPosition().below()).isSideSolid(level, getPosition().below(), Direction.UP)) {
				getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
			}

			if (motionY < 0.0D && getAttackTarget() == null) {
				motionY *= 0.25D;
			}
			
			this.prevRaiseWingTicks = this.raiseWingsTicks;
			if(this.raiseWingsTicks > 0) {
				this.raiseWingsTicks--;
			}
		} else {
			this.prevRaiseWingTicks = this.raiseWingsTicks;

			if(!riding.onGround && this.raiseWingsTicks < 3) {
				this.raiseWingsTicks++;
			} else if(riding.onGround && this.raiseWingsTicks > 0) {
				this.raiseWingsTicks--;
				
				if(this.raiseWingsTicks == 0) {
					this.wingFlapTicks = 0;
				}
			}
		}
	}

	@Override
	public boolean shouldAttackEntity(LivingEntity entityTarget, LivingEntity entityTarget2) {
		if(this.isSitting()) {
			return false;
		}
		if (!(entityTarget instanceof EntityCreeper) && !(entityTarget instanceof EntityGhast)) {
			if (entityTarget instanceof EntityChiromawTame || entityTarget instanceof EntityPullerChiromaw) {
				if (((EntityTameableBL) entityTarget).getOwner() != null && getOwner() !=null && ((EntityTameableBL) entityTarget).getOwner() == getOwner()) {
					return false;
				}
			}
			return entityTarget instanceof PlayerEntity && entityTarget2 instanceof PlayerEntity && !((PlayerEntity) entityTarget2).canAttackPlayer((PlayerEntity) entityTarget) ? false : !(entityTarget instanceof EntityHorse) || !((EntityHorse) entityTarget).isTame();
		} else {
			return false;
		}
	}

	@Override
    public void setRevengeTarget(@Nullable LivingEntity entity) {
    	super.setRevengeTarget(entity);
    	if (entity instanceof EntityChiromawTame || entity instanceof EntityPullerChiromaw)
			if (((EntityTameableBL) entity).getOwner() != null && getOwner() !=null && ((EntityTameableBL) entity).getOwner() == getOwner())
				setRevengeTarget(null);
    }

	@Override
	public boolean attackEntityAsMob(Entity entity) { 
		boolean hasHit = entity.hurt(DamageSource.causeMobDamage(this), (float) ((int) getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
		if (hasHit)
			applyEnchantments(this, entity);
		return hasHit;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(this.isRiding() && (source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN))) {
			return false;
		}
		if(source.getTrueSource() == this) {
			return false;
		}
		if(source.getImmediateSource() instanceof EntityBLArrow && ((EntityBLArrow) source.getImmediateSource()).getArrowType() == EnumArrowType.CHIROMAW_BARB) {
				return false;
		}
		if(this.getRidingEntity() != null && source.getTrueSource() == this.getRidingEntity() && !this.canRiderInteract()) {
			return false;
		}
		if(super.hurt(source, damage)) {
			if(this.isSitting()) {
				this.setSitting(false);
			}
			return true;
		}
		return false;
	}

	@Override
	protected PathNavigate createNavigator(World world) {
		return new PathNavigateFlyingBL(this, world, 2);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		//Don't wander around when sitting
		if(this.isSitting() && !this.level.isClientSide()) {
			return;
		}
		
		if (isInWater()) {
			moveRelative(strafe, vertical, forward, 0.02F);
			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= 0.800000011920929D;
			motionY *= 0.800000011920929D;
			motionZ *= 0.800000011920929D;
		} else if (isInLava()) {
			moveRelative(strafe, vertical, forward, 0.02F);
			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= 0.5D;
			motionY *= 0.5D;
			motionZ *= 0.5D;
		} else {
			float f = 0.91F;

			if (onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(getBoundingBox().minY) - 1, MathHelper.floor(posZ));
				BlockState underState = world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, world, underPos, this) * 0.91F;
			}

			float f1 = 0.16277136F / (f * f * f);
			moveRelative(strafe, vertical, forward, onGround ? 0.1F * f1 : 0.02F);
			f = 0.91F;

			if (onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(getBoundingBox().minY) - 1, MathHelper.floor(posZ));
				BlockState underState = world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, world, underPos, this) * 0.91F;
			}

			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= (double) f;
			motionY *= (double) f;
			motionZ *= (double) f;
		}

		prevLimbSwingAmount = limbSwingAmount;
		double d1 = posX - xOld;
		double d0 = posZ - zOld;
		float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

		if (f2 > 1.0F) {
			f2 = 1.0F;
		}

		limbSwingAmount += (f2 - limbSwingAmount) * 0.4F;
		limbSwing += limbSwingAmount;
	}

	@Override
	public boolean isOnLadder() {
		return false;
	}

	@Override
	public void setSitting(boolean sitting) {
		super.setSitting(sitting);
		
		if(this.aiSit != null) {
			this.aiSit.setSitting(sitting);
		}
	}
	
	@Override
	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		boolean holdsEquipment = hand == Hand.MAIN_HAND && !stack.isEmpty() && (stack.getItem() instanceof IEquippable || stack.getItem() == ItemRegistry.AMULET_SLOT);
		if (holdsEquipment)
			return true;
		if (!stack.isEmpty()) {
			if (stack.getItem() == ItemRegistry.SNAIL_FLESH_RAW) {
				if (getHealth() < getMaxHealth()) {
					if (!level.isClientSide()) {
						heal(5.0F);
						if (!player.isCreative()) {
							stack.shrink(1);
							if (stack.getCount() <= 0)
								player.setItemInHand(hand, ItemStack.EMPTY);
						}
					} else {
						playTameEffect(true);
					}

					return true;
				}
			}
			if (stack.getItem() == ItemRegistry.NET)
				return stack.getItem().itemInteractionForEntity(stack, player, this, Hand.MAIN_HAND);
		}

		if (isOwner(player) && hand == Hand.MAIN_HAND && !(this instanceof IPullerEntity)) {
			yRot = player.yRot;
			
			if (!level.isClientSide()) {
				setAttackTarget((LivingEntity) null);

				Entity riding = getRidingEntity();

				if (riding == null) {
					if (!player.isBeingRidden()) { // stops multiple mounting you
						if(isSitting()) {
							this.setSitting(false);
						}
						level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_LAND, SoundCategory.NEUTRAL, 0.25F, 1.5F);
						startRiding(player, true);
					}
				} else {
					boolean canUnmount = this.world.getBlockCollisions(this, this.getBoundingBox()).isEmpty();
					
					if(canUnmount) {
						level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_RELEASE, SoundCategory.NEUTRAL, 0.5F, 1F);
						dismountRidingEntity();
					} else if(player instanceof ServerPlayerEntity) {
						((ServerPlayerEntity) player).displayClientMessage(new TranslationTextComponent("chat.chiromaw_tame.obstructed"), true);
					}
					
					if(!this.isSitting()) {
						if(canUnmount) {
							List<BlockPos> sitPositions = new ArrayList<>();
							
							for(int yo = 2; yo <= 4; yo++) {
								for(int xo = -1; xo <= 1; xo++) {
									for(int zo = -1; zo <= 1; zo++) {
										BlockPos pos = new BlockPos(riding.getPosition().add(xo, yo, zo));
										
										if(this.world.isEmptyBlock(pos)) {
											BlockState state = this.world.getBlockState(pos.above());
											BlockFaceShape shape = state.getBlockFaceShape(this.world, pos.above(), Direction.DOWN);
											
											if(shape == BlockFaceShape.SOLID || shape == BlockFaceShape.MIDDLE_POLE_THICK) {
												sitPositions.add(pos);
											}
										}
									}
								}
							}
							
							if(!sitPositions.isEmpty()) {
								this.setSitting(true);
								this.isJumping = false;
								
								Collections.sort(sitPositions, Comparator.comparingDouble(pos -> this.getDistanceSq(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f)));
								
								BlockPos sitPosition = sitPositions.get(0);
								
								this.setPosition(sitPosition.getX() + 0.5f, sitPosition.getY() + 1 - this.height, sitPosition.getZ() + 0.5f);
								this.navigator.clearPath();
								this.motionX = this.motionY = this.motionZ = 0;
								this.velocityChanged = true;
								this.setMoveForward(0);
								this.setMoveStrafing(0);
								this.setMoveVertical(0);
							}
						}
					} else {
						this.setSitting(false);
					}
				}
			}
			return true;
		}

		return super.processInteract(player, hand);
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		boolean result = super.startRiding(entity, force);
		if(entity instanceof PlayerEntity && this.world instanceof ServerWorld) {
			((ServerWorld) this.world).getEntityTracker().sendToTracking(this, new SPacketSetPassengers(entity));
		}
		return result;
	}

	@Override
	public void dismountEntity(Entity entity) {
		super.dismountEntity(entity);
		if(entity instanceof PlayerEntity && this.world instanceof ServerWorld) {
			((ServerWorld) this.world).getEntityTracker().sendToTracking(this, new SPacketSetPassengers(entity));
		}
	}

	@Override
	public void updateRidden() {
		super.updateRidden();
	}

	@Override
	public double getStepY() {
		if(getRidingEntity() !=null && getRidingEntity() instanceof PlayerEntity)
			return height * 0.5D;
		return 0.0D;
	}

	@Override
	public boolean canRiderInteract() {
		return this.getRidingEntity() != null && this.getRidingEntity().isCrouching();
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_DOUBLE_JUMP) {
			this.prevWingFlapTicks = this.wingFlapTicks = this.doubleJumpTicks = 20;
		}
	}

	public void performDoubleJump(PlayerEntity player) {
		if(this.doubleJumpTicks == 0 && (!this.level.isClientSide() || !player.onGround)) {
			player.jump();
			player.fallDistance = -2;

			this.prevWingFlapTicks = this.wingFlapTicks = this.doubleJumpTicks = 20;
			
			if(!this.level.isClientSide()) {
				this.world.setEntityState(this, EVENT_DOUBLE_JUMP);
				this.world.playLocalSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.CHIROMAW_MATRIARCH_FLAP, SoundCategory.NEUTRAL, 1, 1);
			} else {
				TheBetweenlands.networkWrapper.sendToServer(new MessageChiromawDoubleJump(this));
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onInputUpdate(InputUpdateEvent event) {
		if(event.getMovementInput().jump && !event.getEntityPlayer().isJumping) {
			List<Entity> passengers = event.getEntityPlayer().getPassengers();
			for(Entity entity : passengers) {
				if(entity instanceof EntityChiromawTame) {
					((EntityChiromawTame) entity).performDoubleJump(event.getEntityPlayer());
					return;
				}
			}
		}
	}

	class AIFollowOwner extends EntityAIBase {
		private final EntityChiromawTame chiromaw;
		private LivingEntity owner;
		World world;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public AIFollowOwner(EntityChiromawTame chiromawIn, double followSpeedIn, float minDistIn, float maxDistIn) {
			chiromaw = chiromawIn;
			world = chiromawIn.level;
			followSpeed = followSpeedIn;
			petPathfinder = chiromawIn.getNavigation();
			minDist = minDistIn;
			maxDist = maxDistIn;
			setMutexBits(3);

			if (!(chiromawIn.getNavigation() instanceof PathNavigateFlyingBL)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		@Override
		public boolean canUse() {
			if(chiromaw.isSitting()) {
				return false;
			}
			
			LivingEntity entitylivingbase = chiromaw.getOwner();

			if (entitylivingbase == null)
				return false;
			else if (entitylivingbase instanceof PlayerEntity && ((PlayerEntity) entitylivingbase).isSpectator())
				return false;
			else if (chiromaw.getDistanceSq(entitylivingbase) < (double) (minDist * minDist))
				return false;
			else {
				owner = entitylivingbase;
				return true;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return !petPathfinder.noPath() && chiromaw.getDistanceSq(owner) > (double) (maxDist * maxDist);
		}

		@Override
		public void start() {
			timeToRecalcPath = 0;
			oldWaterCost = chiromaw.getPathPriority(PathNodeType.WATER);
			chiromaw.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		@Override
		public void stop() {
			owner = null;
			petPathfinder.clearPath();
			chiromaw.setPathPriority(PathNodeType.WATER, oldWaterCost);
		}

		private boolean isEmptyBlock(BlockPos pos) {
			BlockState iblockstate = world.getBlockState(pos);
			return iblockstate.getMaterial() == Material.AIR ? true : !iblockstate.isFullCube();
		}

		@Override
		public void updateTask() {
			chiromaw.getLookHelper().setLookPositionWithEntity(owner, 10.0F, (float) chiromaw.getVerticalFaceSpeed());

			if (--timeToRecalcPath <= 0) {
				timeToRecalcPath = 10;

				if (!petPathfinder.tryMoveToEntityLiving(owner, followSpeed)) {
					if (!chiromaw.getLeashed()) {
						if (chiromaw.getDistanceSq(owner) >= 144.0D) {
							int i = MathHelper.floor(owner.getX()) - 2;
							int j = MathHelper.floor(owner.getZ()) - 2;
							int k = MathHelper.floor(owner.getBoundingBox().minY);

							for (int l = 0; l <= 4; ++l) {
								for (int i1 = 0; i1 <= 4; ++i1) {
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && isTeleportFriendlyBlock(i, j, k, l, i1)) {
										chiromaw.moveTo((double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), chiromaw.yRot, chiromaw.xRot);
										petPathfinder.clearPath();
										return;
									}
								}
							}
						}
					}
				}
			}
		}

		protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset) {
			BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
			BlockState iblockstate = world.getBlockState(blockpos);
			return iblockstate.getBlockFaceShape(world, blockpos, Direction.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(chiromaw) && world.isEmptyBlock(blockpos.above()) && world.isEmptyBlock(blockpos.above(2));
		}
	}

	class AIChiromawOwnerHurtByTarget extends EntityAITarget {
		EntityChiromawTame chiromaw;
		LivingEntity attacker;
		private int timestamp;

		public AIChiromawOwnerHurtByTarget(EntityChiromawTame theDefendingChiromaw) {
			super(theDefendingChiromaw, false);
			chiromaw = theDefendingChiromaw;
			setMutexBits(1);
		}

		@Override
		public boolean canUse() {
			LivingEntity entity = chiromaw.getOwner();
			if (entity == null)
				return false;
			else {
				attacker = entity.getRevengeTarget();
				int i = entity.getRevengeTimer();
				return i != timestamp && isSuitableTarget(attacker, false) && chiromaw.shouldAttackEntity(attacker, entity);
			}
		}

		@Override
		public void start() {
			taskOwner.setAttackTarget(attacker);
			LivingEntity entity = chiromaw.getOwner();
			if (entity != null)
				timestamp = entity.getRevengeTimer();
			super.start();
		}
	}

	class AIChiromawOwnerHurtTarget extends EntityAITarget {
		EntityChiromawTame chiromaw;
		LivingEntity target;
		private int timestamp;

		public AIChiromawOwnerHurtTarget(EntityChiromawTame chirowmawIn) {
			super(chirowmawIn, false);
			chiromaw = chirowmawIn;
			setMutexBits(1);
		}

		@Override
		public boolean canUse() {
			LivingEntity entitylivingbase = chiromaw.getOwner();
			if (entitylivingbase == null)
				return false;
			else {
				target = entitylivingbase.getRevengeTarget();
				int i = entitylivingbase.getRevengeTimer();
				return i != timestamp && isSuitableTarget(target, false) && chiromaw.shouldAttackEntity(target, entitylivingbase);
			}
		}

		@Override
		public void start() {
			taskOwner.setAttackTarget(target);
			LivingEntity entitylivingbase = chiromaw.getOwner();
			if (entitylivingbase != null)
				timestamp = entitylivingbase.getRevengeTimer();
			super.start();
		}
	}

	class AIBarbAttack extends EntityAIBase {
		EntityChiromawTame chiromaw;
		public int attackTimer;

		public AIBarbAttack(EntityChiromawTame chirowmawIn) {
			chiromaw = chirowmawIn;
		}

		@Override
		public boolean canUse() {
			return chiromaw.getAttackTarget() != null && !chiromaw.isSitting();
		}

		@Override
		public void start() {
			this.attackTimer = 0;
		}

		@Override
		public void stop() {
			chiromaw.setAttacking(false);
		}

		@Override
		public void updateTask() {
			LivingEntity target = chiromaw.getAttackTarget();
			if (!chiromaw.level.isClientSide() && target != null) {
				World world = chiromaw.level;
				if (target.getDistanceSq(chiromaw) < 576 && target.getDistanceSq(chiromaw) > 25 && chiromaw.canSee(target)) {
					++this.attackTimer;
					if (attackTimer == 20) {
						EntityBLArrow arrow = new EntityBLArrow(world, chiromaw);
						arrow.setType(chiromaw.getElectricBoogaloo()? EnumArrowType.CHIROMAW_SHOCK_BARB : EnumArrowType.CHIROMAW_BARB);
						
						double targetX = target.getX() + target.motionX - chiromaw.getX();
						double targetY = target.getY() + target.getEyeHeight() - chiromaw.getY() + chiromaw.getEyeHeight();
						double targetZ = target.getZ() + target.motionZ - chiromaw.getZ();
						
						arrow.shoot(targetX, targetY, targetZ, 1.2F, 0.0F);
						
						float g = -0.03f;

						float vy0 = 0.35f;

						float tmax = -vy0 / g;

						float s = vy0 * tmax + 0.5f * g * tmax * tmax;

						float h = (float)(target.getY() + 0.5f - this.chiromaw.getY());

						float fall = h - s;
						
						if(fall < 0) {
							float tmin = MathHelper.sqrt(fall * 2 / g);

							float t = tmax + tmin;

							float dx = (float)(target.getX() + (this.chiromaw.rand.nextFloat() - 0.5f) * 2.2f - this.chiromaw.getX()) + (float)target.motionX * t * 0.75f;
							float dz = (float)(target.getZ() + (this.chiromaw.rand.nextFloat() - 0.5f) * 2.2f - this.chiromaw.getZ()) + (float)target.motionZ * t * 0.75f;

							float len = MathHelper.sqrt(dx*dx + dz*dz);

							dx /= len;
							dz /= len;

							float speed = len / t * 1.5f /*constant adjustment for MC physics*/;

							arrow.motionX = dx * speed;
							arrow.motionY = vy0;
							arrow.motionZ = dz * speed;
						}
						
						world.addFreshEntity(arrow);
						
						chiromaw.level.playSound(null, chiromaw.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.NEUTRAL, 0.5F, 1F + (chiromaw.level.rand.nextFloat() - chiromaw.level.rand.nextFloat()) * 0.8F);
						attackTimer = -20;
					}

				} else if (this.attackTimer > 0) {
					--this.attackTimer;
				}
				chiromaw.setAttacking(this.attackTimer > 10);
			}
		}
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

	@Override
	public EntityPullerChiromaw createPuller(EntityDraeton draeton, DraetonPhysicsPart puller) {
		return new EntityPullerChiromaw(draeton.world, draeton, puller);
	}
}