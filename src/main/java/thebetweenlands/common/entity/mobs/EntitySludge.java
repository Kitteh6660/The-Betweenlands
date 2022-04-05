package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.common.entity.attributes.BooleanAttribute;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

public class EntitySludge extends MobEntity implements IMob, IEntityBL {
	public static final DataParameter<Boolean> IS_ACTIVE = EntityDataManager.defineId(EntitySludge.class, DataSerializers.BOOLEAN);

	public static final IAttribute SLUDGE_TRAIL = (new BooleanAttribute(null, "bl.sludgeTrail", false)).setDescription("Whether this Sludge should leave a Sludge trail");
	
	private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

	private float squishAmount;
	private float squishFactor;
	private float prevSquishFactor;
	private boolean wasOnGround;

	public ControlledAnimation scale = new ControlledAnimation(5);

	protected int attackCooldown = 0;
	
	public EntitySludge(World worldIn) {
		super(worldIn);
		this.moveControl = new EntitySludge.SludgeMoveHelper(this);
		this.fireImmune = true;
		this.setSize(1.1F, 1.2F);
		this.experienceValue = 4;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new EntitySludge.AISludgeFloat(this));
		this.goalSelector.addGoal(1, new EntitySludge.AISludgeAttack(this));
		this.goalSelector.addGoal(2, new EntitySludge.AISludgeFaceRandom(this));
		this.goalSelector.addGoal(3, new EntitySludge.AISludgeHop(this));

		this.targetSelector.addGoal(0, new EntityAIFindEntityNearestPlayer(this));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().register(IS_ACTIVE, true);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setActive(this.level.random.nextInt(5) == 0 || !this.canHideIn(this.world.getBlockState(this.getPosition().below())));
		return super.onInitialSpawn(difficulty, livingdata);
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SLUDGE_TRAIL).setBaseValue(1);
		this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.5D);
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		super.writeEntityToNBT(compound);
		compound.putBoolean("wasOnGround", this.wasOnGround);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		super.readEntityFromNBT(compound);
		if(compound.contains("wasOnGround")) {
			this.wasOnGround = compound.getBoolean("wasOnGround");
		}
	}

	@Override
	public void tick() {
		if (!this.level.isClientSide() && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			this.isDead = true;
		}

		if(this.attackCooldown > 0) {
			this.attackCooldown--;
		}
		
		this.prevSquishFactor = this.squishFactor;
		this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;

		if (!this.level.isClientSide()) {
			if (getIsPlayerNearby(7, 3, 7, 7) || getAttackTarget() != null || this.level.random.nextInt(2200) == 0) {
				if (!this.isActive()) {
					this.setActive(true);
					this.motionY += 0.6;
				}
			}

			if(this.isActive()) {
				if(this.getAttribute(SLUDGE_TRAIL).getValue() == 1 && this.world.getClosestPlayer(this.getX(), this.getY(), this.getZ(), 16, false) != null) {
					BlockPos position = new BlockPos(this.getX(), this.getY(), this.getZ());;
					if (this.world.isEmptyBlock(position)) {
						this.createTrail(position);
					}
				}

				if (this.getAttackTarget() == null && this.onGround && this.level.random.nextInt(350) == 0 && !this.isInWater() && this.canHideIn(this.world.getBlockState(this.getPosition().below()))) {
					this.setActive(false);
				}
			} else if(this.isInWater() || !this.onGround) {
				this.setActive(true);
			} else {
				this.motionX = 0;
				this.motionY -= 0.1;
				this.motionZ = 0;
			}
		}

		super.tick();

		if(this.isActive()) {
			this.setNormalSize();
		} else {
			this.setSmallSize();
		}

		if (this.onGround && !this.wasOnGround) {
			//Particles
			this.squishAmount = -0.5F;
		} else if (!this.onGround && this.wasOnGround) {
			this.squishAmount = 1.0F;
		}

		this.wasOnGround = this.onGround;
		this.alterSquishAmount();

		//Update animation
		if (this.level.isClientSide()) {
			this.scale.updateTimer();
			if (this.isActive()) {
				this.scale.increaseTimer();
			} else {
				this.scale.decreaseTimer();
			}
		}
	}
	
	protected boolean canHideIn(BlockState state) {
		Material ground = state.getMaterial();
		return ground == Material.GROUND || ground == Material.SAND || ground == Material.GRASS;
	}

	protected void createTrail(BlockPos pos) {
		if(BlockRegistry.SLUDGE.canPlaceBlockAt(this.world, pos)) {
			BlockRegistry.SLUDGE.generateBlockTemporary(this.world, pos);
		}
	}
	
	protected void setSmallSize() {
		this.setSize(0.5F, 0.6F);
	}
	
	protected void setNormalSize() {
		this.setSize(1.1F, 1.2F);
	}
	
	public float getSquishFactor(float partialTicks) {
		return this.prevSquishFactor + (this.squishFactor - this.prevSquishFactor) * partialTicks;
	}

	protected void alterSquishAmount() {
		this.squishAmount *= 0.8F;
	}

	/**
	 * Gets the amount of time the slime needs to wait between jumps.
	 */
	protected int getJumpDelay() {
		return this.random.nextInt(20) + 10;
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || !this.isActive();
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity entityIn) {
		if(this.attackCooldown <= 0) {
			this.attackCooldown = 20;
			this.dealDamage(entityIn);
		}
	}

	protected void dealDamage(LivingEntity entityIn) {
		if (this.isActive() && this.canSee(entityIn) && this.getDistanceSq(entityIn) < 2.5D && entityIn.hurt(DamageSource.causeMobDamage(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue())) {
			this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
		}
	}

	@Override
	public int getVerticalFaceSpeed() {
		return 0;
	}

	/**
	 * Returns true if the slime makes a sound when it jumps (based upon the slime's size)
	 */
	protected boolean makesSoundOnJump() {
		return true;
	}

	@Override
	protected void jump() {
		this.motionY = 0.42D;
		this.isAirBorne = true;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SLUDGE;
	}
	
	protected SoundEvent getJumpSound() {
		return SoundEvents.ENTITY_SLIME_JUMP;
	}

	public void setActive(boolean active) {
		this.getEntityData().set(IS_ACTIVE, active);
	}

	public boolean isActive() {
		return this.getEntityData().get(IS_ACTIVE);
	}

	protected boolean getIsPlayerNearby(double distanceX, double distanceY, double distanceZ, double radius) {
		List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().inflate(distanceX, distanceY, distanceZ));
		for (Entity entityNeighbor : entities) {
			if (entityNeighbor instanceof PlayerEntity && this.getDistance(entityNeighbor) <= radius && (!((PlayerEntity) entityNeighbor).isCreative() && !((PlayerEntity) entityNeighbor).isSpectator() && this.getSensing().canSee(entityNeighbor)))
				return true;
		}
		return false;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return Minecraft.getInstance().player.isCreative() || this.isActive() ? this.getBoundingBox() : ZERO_AABB;
	}

	@Override
	public void knockBack(Entity entityIn, float strenght, double xRatio, double zRatio) {
		if(this.isActive()) {
			super.knockBack(entityIn, strenght, xRatio, zRatio);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(!this.isActive() && !source.isCreativePlayer()) {
			if (!this.level.isClientSide()) {
				this.setActive(true);
			}
			return false;
		}
		return super.hurt(source, amount);
	}

	static class AISludgeAttack extends EntityAIBase {
		private final EntitySludge sludge;
		private int growTieredTimer;

		public AISludgeAttack(EntitySludge slimeIn) {
			this.sludge = slimeIn;
			this.setMutexBits(2);
		}

		@Override
		public boolean canUse() {
			LivingEntity entitylivingbase = this.sludge.getAttackTarget();
			return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : !(entitylivingbase instanceof PlayerEntity) || !((PlayerEntity)entitylivingbase).capabilities.disableDamage);
		}

		@Override
		public void start() {
			this.growTieredTimer = 300;
			super.start();
		}

		@Override
		public boolean canContinueToUse() {
			LivingEntity entitylivingbase = this.sludge.getAttackTarget();
			return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (entitylivingbase instanceof PlayerEntity && ((PlayerEntity)entitylivingbase).capabilities.disableDamage ? false : --this.growTieredTimer > 0));
		}

		@Override
		public void updateTask() {
			LivingEntity target = this.sludge.getAttackTarget();
			if(target != null) {
				this.sludge.faceEntity(target, 10.0F, 10.0F);
				((EntitySludge.SludgeMoveHelper)this.sludge.getMoveHelper()).setDirection(this.sludge.yRot, true);
			}
		}
	}

	static class AISludgeFaceRandom extends EntityAIBase {
		private final EntitySludge sludge;
		private float chosenDegrees;
		private int nextRandomizeTime;

		public AISludgeFaceRandom(EntitySludge slimeIn) {
			this.sludge = slimeIn;
			this.setMutexBits(2);
		}

		@Override
		public boolean canUse() {
			return this.sludge.getAttackTarget() == null && (this.sludge.onGround || this.sludge.isInWater() || this.sludge.isInLava() || this.sludge.isPotionActive(MobEffects.LEVITATION));
		}

		@Override
		public void updateTask() {
			if (--this.nextRandomizeTime <= 0) {
				this.nextRandomizeTime = 40 + this.sludge.getRandom().nextInt(60);
				this.chosenDegrees = (float)this.sludge.getRandom().nextInt(360);
			}

			((EntitySludge.SludgeMoveHelper)this.sludge.getMoveHelper()).setDirection(this.chosenDegrees, false);
		}
	}

	static class AISludgeFloat extends EntityAIBase {
		private final EntitySludge sludge;

		public AISludgeFloat(EntitySludge slimeIn) {
			this.sludge = slimeIn;
			this.setMutexBits(5);
			((PathNavigateGround)slimeIn.getNavigation()).setCanSwim(true);
		}

		@Override
		public boolean canUse() {
			return this.sludge.isInWater() || this.sludge.isInLava();
		}

		@Override
		public void updateTask() {
			if (this.sludge.getRandom().nextFloat() < 0.8F) {
				this.sludge.getJumpHelper().setJumping();
				this.sludge.motionY += 0.01D;
			}

			((EntitySludge.SludgeMoveHelper)this.sludge.getMoveHelper()).setSpeed(1.2D);
		}
	}

	static class AISludgeHop extends EntityAIBase {
		private final EntitySludge slime;

		public AISludgeHop(EntitySludge slimeIn) {
			this.slime = slimeIn;
			this.setMutexBits(5);
		}

		@Override
		public boolean canUse() {
			return true;
		}

		@Override
		public void updateTask() {
			((EntitySludge.SludgeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.0D);
		}
	}

	static class SludgeMoveHelper extends EntityMoveHelper {
		private float yRot;
		private int jumpDelay;
		private final EntitySludge sludge;
		private boolean isAggressive;

		public SludgeMoveHelper(EntitySludge slimeIn) {
			super(slimeIn);
			this.sludge = slimeIn;
			this.yRot = 180.0F * slimeIn.yRot / (float)Math.PI;
		}

		public void setDirection(float yaw, boolean aggressive) {
			this.yRot = yaw;
			this.isAggressive = aggressive;
		}

		public void setSpeed(double speedIn) {
			this.speed = speedIn;
			this.action = EntityMoveHelper.Action.MOVE_TO;
		}

		@Override
		public void onUpdateMoveHelper() {
			if(!this.sludge.isActive()) {
				this.action = EntityMoveHelper.Action.WAIT;
				return;
			}

			this.entity.yRot = this.limitAngle(this.entity.yRot, this.yRot, 90.0F);
			this.entity.rotationYawHead = this.entity.yRot;
			this.entity.renderYawOffset = this.entity.yRot;

			if (this.action != EntityMoveHelper.Action.MOVE_TO) {
				this.entity.setMoveForward(0.0F);
			} else {
				this.action = EntityMoveHelper.Action.WAIT;

				if (this.entity.onGround) {
					this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));

					if (this.jumpDelay-- <= 0) {
						this.jumpDelay = this.sludge.getJumpDelay();

						if (this.isAggressive) {
							this.jumpDelay /= 3;
						}

						this.sludge.getJumpHelper().setJumping();

						if (this.sludge.makesSoundOnJump()) {
							this.sludge.playSound(this.sludge.getJumpSound(), this.sludge.getSoundVolume(), ((this.sludge.getRandom().nextFloat() - this.sludge.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
						}
					} else {
						this.sludge.xxa = 0.0F;
						this.sludge.zza = 0.0F;
						this.entity.setAIMoveSpeed(0.0F);
					}
				} else {
					this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
				}
			}
		}
	}
}