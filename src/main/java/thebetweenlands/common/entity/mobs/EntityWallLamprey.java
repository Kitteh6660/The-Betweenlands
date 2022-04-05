package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityWallLamprey extends EntityMovingWallFace implements IMob {
	public static final byte EVENT_START_THE_SUCC = 80;

	private static final DataParameter<Boolean> HIDDEN = EntityDataManager.defineId(EntityWallLamprey.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Float> LOOK_X = EntityDataManager.defineId(EntityWallLamprey.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> LOOK_Y = EntityDataManager.defineId(EntityWallLamprey.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> LOOK_Z = EntityDataManager.defineId(EntityWallLamprey.class, DataSerializers.FLOAT);

	private float prevHiddenPercent = 1.0F;
	private float hiddenPercent = 1.0F;

	private Vector3d prevHeadLook = Vector3d.ZERO;
	private Vector3d headLook = Vector3d.ZERO;

	private boolean clientHeadLookChanged = false;

	private int suckTimer = 0;

	@OnlyIn(Dist.CLIENT)
	private TextureAtlasSprite wallSprite;

	public EntityWallLamprey(World world) {
		super(world);
		this.lookMoveSpeedMultiplier = 15.0F;
		this.experienceValue = 7;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		this.entityData.define(HIDDEN, true);
		this.entityData.define(LOOK_X, 0.0F);
		this.entityData.define(LOOK_Y, 0.0F);
		this.entityData.define(LOOK_Z, 0.0F);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();

		this.targetSelector.addGoal(0, new EntityAIHurtByTargetImproved(this, false));
		this.targetSelector.addGoal(1, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, 0, true, false, null).setUnseenMemoryTicks(120));

		this.goalSelector.addGoal(0, new AITrackTargetLamprey(this, true, 28.0D));
		this.goalSelector.addGoal(1, new AIAttackMelee(this, 1, true));
		this.goalSelector.addGoal(2, new AISuck(this));
		this.goalSelector.addGoal(3, new AISpit(this, 3.0F));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.08D);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);

		if(LOOK_X.equals(key) || LOOK_Y.equals(key) || LOOK_Z.equals(key)) {
			this.clientHeadLookChanged = true;
		}
	}
	
	@Override
	protected boolean isTravelBlocked() {
		return super.isTravelBlocked() || this.isSucking();
	}

	@Override
	public void tick() {
		this.prevHeadLook = this.headLook;

		super.tick();

		if(this.clientHeadLookChanged) {
			this.headLook = new Vector3d(this.entityData.get(LOOK_X), this.entityData.get(LOOK_Y), this.entityData.get(LOOK_Z));
			this.clientHeadLookChanged = false;
		}

		if(!this.level.isClientSide()) {
			LivingEntity attackTarget = this.getAttackTarget();

			this.entityData.set(HIDDEN, attackTarget == null);

			if(attackTarget != null) {
				this.setHeadLook(attackTarget.getPositionEyes(1).subtract(this.getPositionEyes(1)));
			} else {
				this.setHeadLook(new Vector3d(this.getFacing().getDirectionVec()));
			}
		} else {
			this.prevHiddenPercent = this.hiddenPercent;

			if(this.entityData.get(HIDDEN)) {
				if(this.hiddenPercent < 1.0F) {
					this.hiddenPercent += 0.01F;
					if(this.hiddenPercent > 1.0F) {
						this.hiddenPercent = 1.0F;
					}
				}
			} else {
				if(this.hiddenPercent > 0.0F) {
					this.hiddenPercent -= 0.04F;
					if(this.hiddenPercent < 0.0F) {
						this.hiddenPercent = 0.0F;
					}
				}
			}

			this.updateWallSprite();
		}

		if(this.isSucking()) {
			this.suckTimer--;

			if(!this.level.isClientSide()) {
				List<Entity> affectedEntities = (List<Entity>)this.world.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(6.0F, 6.0F, 6.0F));
				
				for(Entity e : affectedEntities) {
					float dst = e.getDistance(this);
					
					if(e == this || dst > 6.0F || !this.canSee(e) || e instanceof IEntityBL) {
						continue;
					}
					
					Vector3d vec = new Vector3d(this.getX() - e.getX(), this.getY() - e.getY(), this.getZ() - e.getZ());
					vec = vec.normalize();
					
					float mod = (float) Math.pow(1.0F - dst / 6.0F, 1.3D);
					
					if(e instanceof PlayerEntity) {
						if(((PlayerEntity)e).isActiveItemStackBlocking()) mod *= 0.18F;
					}
					
					e.motionX += vec.x * 0.1F * mod;
					e.motionY += vec.y * 0.215F * mod;
					e.motionZ += vec.z * 0.1F * mod;
					
					e.velocityChanged = true;
				}
			} else {
				Vector3d fwd = this.getHeadLook(1);
				Vector3d up = new Vector3d(this.getFacingUp().getDirectionVec());
				Vector3d right = fwd.cross(up);

				Vector3d front = this.getFrontCenter().add(fwd.scale(0.3D)).add(up.scale(-0.3D));

				for(int i = 0; i < 3; i++) {
					Random rnd = this.world.rand;

					Vector3d vec = fwd.scale(rnd.nextFloat() * 5).add(up.scale((rnd.nextFloat() - 0.5F) * 1.2F)).add(right.scale((rnd.nextFloat() - 0.5F) * 1.2F));

					float rx = (float)vec.x;
					float ry = (float)vec.y;
					float rz = (float)vec.z;

					vec = vec.normalize();

					this.world.addParticle(ParticleTypes.SMOKE_NORMAL, front.x + rx, front.y + ry, front.z + rz, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void updateWallSprite() {
		this.wallSprite = null;

		BlockPos pos = this.getPosition();

		BlockState state = this.world.getBlockState(pos);
		state = state.getActualState(this.world, pos);

		if(state.isFullCube()) {
			this.wallSprite = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
		}
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public TextureAtlasSprite getWallSprite() {
		return this.wallSprite;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		boolean hasAttacked = false;

		IDecayCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
		if(cap != null && cap.isDecayEnabled()) {
			float attackDamage = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
			
			if(EntityAIAttackOnCollide.useStandardAttack(this, entity, attackDamage / 3.0F, !this.isSucking())) {
				hasAttacked = true;

				DecayStats stats = cap.getDecayStats();

				stats.addDecayAcceleration(attackDamage * 2.0F);
			}
		} else {
			hasAttacked = super.attackEntityAsMob(entity);
		}

		if(hasAttacked) {
			this.playSound(SoundRegistry.WALL_LAMPREY_ATTACK, 1, 1);
		}

		return hasAttacked;
	}
	
	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.WALL_LAMPREY;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, Direction facing, Direction facingUp) {
		return this.isValidBlockForMovement(pos, this.world.getBlockState(pos)) && this.isValidBlockForMovement(pos.offset(facingUp.getOpposite()), this.world.getBlockState(pos.offset(facingUp.getOpposite())));
	}

	@Override
	public int checkAnchorAt(BlockPos anchor, Direction facing, Direction facingUp, int checks) {
		int violations = super.checkAnchorAt(anchor, facing, facingUp, checks);

		//Check "below" (relative to facingUp) for entities
		if((checks & AnchorChecks.ENTITIES) != 0) {
			if(!this.world.getEntitiesOfClass(EntityWallFace.class, this.getBoundingBox().offset(anchor.subtract(this.getAnchor()).offset(facingUp.getOpposite())).expand(facing.getStepX() * this.getPeek(), facing.getStepY() * this.getPeek(), facing.getStepZ() * this.getPeek()), e -> e != this).isEmpty()) {
				violations |= AnchorChecks.ENTITIES;
			}
		}

		return violations;
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, BlockState state) {
		return state.canOcclude() && state.isNormalCube() && state.isFullCube() && state.getBlockHardness(this.world, pos) > 0 && (state.getMaterial() == Material.ROCK || state.getMaterial() == Material.WOOD);
	}

	@Override
	public Vector3d getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	public float getLampreyHiddenPercent(float partialTicks) {
		return 1 - (1 - this.easeInOut(this.prevHiddenPercent + (this.hiddenPercent - this.prevHiddenPercent) * partialTicks)) * this.getHoleDepthPercent(partialTicks);
	}

	private float easeInOut(float percent) {
		float sq = percent * percent;
		return sq / (2.0f * (sq - percent) + 1.0f);
	}

	public void setHeadLook(Vector3d look) {
		look = look.normalize();
		Vector3d curr = this.headLook;
		if(Math.abs(curr.x - look.x) >= 0.01F || Math.abs(curr.y - look.y) >= 0.01F || Math.abs(curr.z - look.z) >= 0.01F) {
			if(!this.level.isClientSide()) {
				this.entityData.set(LOOK_X, (float) look.x);
				this.entityData.set(LOOK_Y, (float) look.y);
				this.entityData.set(LOOK_Z, (float) look.z);
			}
			this.headLook = look;
		}
	}

	public Vector3d getHeadLook(float partialTicks) {
		return new Vector3d(
				this.prevHeadLook.x + (this.headLook.x - this.prevHeadLook.x) * partialTicks,
				this.prevHeadLook.y + (this.headLook.y - this.prevHeadLook.y) * partialTicks,
				this.prevHeadLook.z + (this.headLook.z - this.prevHeadLook.z) * partialTicks
				);
	}

	public float[] getRelativeHeadLookAngles(float partialTicks) {
		Vector3d headLook = this.getHeadLook(partialTicks);

		Vector3d fwdAxis = new Vector3d(this.getFacing().getDirectionVec());
		Vector3d upAxis = new Vector3d(this.getFacingUp().getDirectionVec());
		Vector3d rightAxis = fwdAxis.cross(upAxis);

		double fwd = fwdAxis.dotProduct(headLook);
		double up = upAxis.dotProduct(headLook);
		double right = rightAxis.dotProduct(headLook);

		return new float[] {(float)Math.toDegrees(Math.atan2(right, fwd)), (float)Math.toDegrees(Math.atan2(fwd, up)) * (float)Math.signum(fwd) - 90.0F};
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_START_THE_SUCC) {
			this.startSucking();
		}
	}

	public void startSucking() {
		if(!this.level.isClientSide()) {
			this.world.setEntityState(this, EVENT_START_THE_SUCC);
			this.world.playLocalSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.WALL_LAMPREY_SUCK, SoundCategory.HOSTILE, 0.8F, this.level.random.nextFloat() * 0.3F + 0.8F);
		}
		this.suckTimer = 30 + this.level.random.nextInt(20);
	}

	public boolean isSucking() {
		return this.suckTimer > 0;
	}

	public void startSpit(float spitDamage) {
		Entity target = this.getAttackTarget();
		if(target != null) {
			Direction facing = this.getFacing();

			EntitySludgeWallJet jet = new EntitySludgeWallJet(this.world, this);
			jet.setPosition(this.getX() + facing.getStepX() * (this.width / 2 + 0.1F), this.getY() + this.height / 2.0F + facing.getStepY() * (this.height / 2 + 0.1F), this.getZ() + facing.getStepZ() * (this.width / 2 + 0.1F));

			double dx = target.getX() - jet.getX();
			double dy = target.getBoundingBox().minY + (double)(target.height / 3.0F) - jet.getY();
			double dz = target.getZ() - jet.getZ();
			double dist = (double)MathHelper.sqrt(dx * dx + dz * dz);
			jet.shoot(dx, dy + dist * 0.2D, dz, 1, 1);

			this.world.addFreshEntity(jet);
		}
	}

	public static class AITrackTargetLamprey extends AITrackTarget<EntityWallLamprey> {
		public AITrackTargetLamprey(EntityWallLamprey entity, boolean stayInRange, double maxRange) {
			super(entity, stayInRange, maxRange);
		}

		public AITrackTargetLamprey(EntityWallLamprey entity) {
			super(entity);
		}

		@Override
		protected boolean canMove() {
			return !this.entity.isSucking();
		}
	}

	protected static class AISuck extends EntityAIBase {
		protected final EntityWallLamprey entity;
		protected int minCooldown;
		protected int maxCooldown;

		protected int cooldown = 0;

		public AISuck(EntityWallLamprey entity) {
			this(entity, 50, 140);
		}

		public AISuck(EntityWallLamprey entity, int minCooldown, int maxCooldown) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.setMutexBits(0);
		}

		@Override
		public boolean canUse() {
			return this.entity.getFacing() != Direction.DOWN && !this.entity.isSucking() && !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() &&
					this.entity.getSensing().canSee(this.entity.getAttackTarget()) && this.entity.getDistance(this.entity.getAttackTarget()) < 6.0F;
		}

		@Override
		public void start() {
			this.cooldown = 20 + this.entity.rand.nextInt(40);
		}

		@Override
		public void updateTask() {
			if(!this.entity.isSucking()) {
				if(this.cooldown <= 0) {
					this.cooldown = this.minCooldown + this.entity.rand.nextInt(this.maxCooldown - this.minCooldown + 1);
					this.entity.startSucking();
				}
				this.cooldown--;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return this.canUse();
		}
	}

	protected static class AISpit extends EntityAIBase {
		protected final EntityWallLamprey entity;
		protected int minCooldown;
		protected int maxCooldown;

		protected int cooldown = 0;

		protected float spitDamage;

		public AISpit(EntityWallLamprey entity, float spitDamage) {
			this(entity, spitDamage, 50, 170);
		}

		public AISpit(EntityWallLamprey entity, float spitDamage, int minCooldown, int maxCooldown) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.spitDamage = spitDamage;
			this.setMutexBits(0);
		}

		protected boolean isInRange(LivingEntity target) {
			final Vector3d down = new Vector3d(0, -1, 0);
			Vector3d dir = target.getDeltaMovement().subtract(this.entity.getDeltaMovement()).normalize();
			return Math.acos(down.dotProduct(dir)) < 0.733D /*~42°*/;
		}

		@Override
		public boolean canUse() {
			return this.entity.getFacing() == Direction.DOWN && !this.entity.isSucking() && !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() &&
					this.entity.getSensing().canSee(this.entity.getAttackTarget()) && this.isInRange(this.entity.getAttackTarget());
		}

		@Override
		public void start() {
			this.cooldown = 20 + this.entity.rand.nextInt(40);
		}

		@Override
		public void updateTask() {
			if(!this.entity.isSucking()) {
				if(this.cooldown <= 0) {
					this.cooldown = this.minCooldown + this.entity.rand.nextInt(this.maxCooldown - this.minCooldown + 1);
					this.entity.startSpit(this.getSpitDamage());
				}
				this.cooldown--;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return this.canUse();
		}

		protected float getSpitDamage() {
			return this.spitDamage;
		}
	}
}
