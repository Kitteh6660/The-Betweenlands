package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;

public abstract class EntityWallFace extends CreatureEntity implements  IEntityBL {
	
	private static final DataParameter<Direction> FACING = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.DIRECTION);
	private static final DataParameter<Direction> FACING_UP = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.DIRECTION);
	private static final DataParameter<BlockPos> ANCHOR = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.BLOCK_POS);

	private int targetFacingTimeout = 40;
	private Direction targetFacing;
	private Direction targetFacingUp;

	private int targetAnchorTimeout = 40;
	private BlockPos targetAnchor;

	private static final DataParameter<Boolean> MOVING = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> MOVE_SPEED = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.FLOAT);
	private static final DataParameter<Direction> MOVE_FACING = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.DIRECTION);
	private static final DataParameter<Direction> MOVE_FACING_UP = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.DIRECTION);
	private static final DataParameter<BlockPos> MOVE_ANCHOR = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Byte> MOVE_REASON = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> ANCHORED = EntityDataManager.defineId(EntityWallFace.class, DataSerializers.BOOLEAN);

	protected final LookHelper lookHelper;

	protected float lookMoveSpeedMultiplier = 1.0F;

	private float lastMoveProgress = 0;
	private float moveProgress = 0;

	protected float peek = 0.25F;

	public static enum MoveReason {
		POSITION, LOOK, POSITION_AND_LOOK
	}

	public EntityWallFace(World world) {
		super(world);
		this.lookHelper = new LookHelper(this);
		this.moveControl = new MovementController(this);
		this.setSize(0.9F, 0.9F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		this.entityData.define(FACING, Direction.NORTH);
		this.entityData.define(FACING_UP, Direction.UP);
		this.entityData.define(ANCHOR, BlockPos.ZERO);

		this.entityData.define(MOVING, false);
		this.entityData.define(MOVE_SPEED, 1.0F);
		this.entityData.define(MOVE_FACING, Direction.NORTH);
		this.entityData.define(MOVE_FACING_UP, Direction.UP);
		this.entityData.define(MOVE_ANCHOR, BlockPos.ZERO);
		this.entityData.define(MOVE_REASON, (byte) 0);

		this.entityData.define(ANCHORED, true);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld level, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData livingdata, @Nullable CompoundNBT tag) {
		BlockPos anchor = new BlockPos(this);
		Direction[] randomFacing = this.findRandomValidFacingAt(anchor);
		if(randomFacing == null) {
			randomFacing = new Direction[] {Direction.NORTH, Direction.UP};
		}
		this.setPositionToAnchor(anchor, randomFacing[0], randomFacing[1]);
		return super.finalizeSpawn(level, difficulty, reason, livingdata, tag);
	}

	@Override
	public boolean getCanSpawnHere() {
		BlockState surfaceState = this.level.getBlockState((new BlockPos(this)).below());
		return surfaceState.canEntitySpawn(this) && this.findRandomValidFacingAt(new BlockPos(this)) != null;
	}
	
	@Override
	public boolean isNotColliding() {
		return true;
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return this.isEntityInvulnerable(source) ? false : super.hurt(source, amount);
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source == DamageSource.IN_WALL || super.isEntityInvulnerable(source);
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return EntityAIAttackOnCollide.useStandardAttack(this, target);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.5F;
	}

	@Override
	protected boolean canDropLoot() {
		return true;
	}

	@Override
	public LookHelper getLookHelper() {
		return this.lookHelper;
	}

	@Override
	public MoveHelper getMoveHelper() {
		return (MoveHelper) this.moveControl;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if(this.isServerWorld() && !this.isMovementBlocked()) {
			this.lookHelper.onUpdateLook();
		}
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
		//No knockback
	}

	@Override
	protected void setSize(float width, float height) {
		super.setSize(width, height);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getBoundingBox();
	}

	@Override
	protected boolean isMovementBlocked() {
		return this.isMoving();
	}

	@Override
	public void setAIMoveSpeed(float speedIn) {
		this.entityData.set(MOVE_SPEED, speedIn);
	}

	@Override
	public float getAIMoveSpeed() {
		if(this.isMoving() && this.getMoveReason() == MoveReason.LOOK) {
			return this.entityData.get(MOVE_SPEED) * this.lookMoveSpeedMultiplier;
		}
		return this.entityData.get(MOVE_SPEED);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getBrightnessForRender() {
		if(!this.isAnchored()) {
			return super.getBrightnessForRender();
		}

		Direction facing = this.getFacing();
		BlockPos.Mutable pos = new BlockPos.Mutable(MathHelper.floor(this.getX()) + facing.getStepX(), 0, MathHelper.floor(this.getZ()) + facing.getStepZ());

		if (this.level.isBlockLoaded(pos)) {
			pos.setY(MathHelper.floor(this.getY() + (double)this.getEyeHeight()) + facing.getStepY());
			return this.level.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
	}

	@Override
	public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
		if (stack.isEmpty()) {
			return null;
		} else {
			ItemEntity ItemEntity = new ItemEntity(this.level, this.getX(), this.getY() + (double)offsetY, this.getZ(), stack);

			Direction facing = this.getFacing();
			Vector3d dropPos = this.getFrontCenter().add(facing.getStepX() * ItemEntity.width, facing.getStepY() * ItemEntity.height, facing.getStepZ() * ItemEntity.width);

			ItemEntity.setPosition(dropPos.x, dropPos.y, dropPos.z);

			ItemEntity.setDefaultPickupDelay();
			if(this.captureDrops) {
				this.capturedDrops.add(ItemEntity);
			} else {
				this.level.addFreshEntity(ItemEntity);
			}
			return ItemEntity;
		}
	}

	@Override
	public boolean canSee(Entity entity) {
		Vector3d frontCenter = this.getFrontCenter();
		return this.level.rayTraceBlocks(frontCenter, new Vector3d(entity.getX(), entity.getY() + (double)entity.getEyeHeight(), entity.getZ()), false, true, false) == null;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);

		nbt.putInt("facing", this.getFacing().getIndex());
		nbt.putInt("facingUp", this.getFacingUp().getIndex());
		nbt.setLong("anchor", this.getAnchor().toLong());
		nbt.putBoolean("anchored", this.isAnchored());
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);

		if(nbt.contains("facing", Constants.NBT.TAG_INT)) {
			this.entityData.set(FACING, Direction.byIndex(nbt.getInt("facing")));
		}
		if(nbt.contains("facingUp", Constants.NBT.TAG_INT)) {
			this.entityData.set(FACING_UP, Direction.byIndex(nbt.getInt("facingUp")));
		}
		if(nbt.contains("anchor", Constants.NBT.TAG_LONG)) {
			this.entityData.set(ANCHOR, BlockPos.of(nbt.getLong("anchor")));
		}
		if(!nbt.contains("anchored", Constants.NBT.TAG_BYTE)) {
			this.setAnchored(true);
		} else {
			this.setAnchored(nbt.getBoolean("anchored"));
		}
	}

	@Override
	public void tick() {
		boolean isAnchored = this.entityData.get(ANCHORED);

		if(isAnchored) {
			this.fallDistance = 0;
			this.onGround = true;
			this.setNoGravity(true);

			double px = this.getX(), py = this.getY(), pz = this.getZ();

			super.tick();

			this.getX() = this.xOld = this.lastTickPosX = px;
			this.getY() = this.yOld = this.lastTickPosY = py;
			this.getZ() = this.zOld = this.lastTickPosZ = pz;

			this.motionX = this.motionY = this.motionZ = 0;
		} else {
			this.setNoGravity(false);

			super.tick();
		}

		this.updatePositioning(isAnchored);

		this.updateMovement();
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		if(this.isAnchored()) {
			this.collided = this.collidedHorizontally = this.collidedVertically = true;
		} else {
			super.move(type, x, y, z);
		}
	}

	@Override
	public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {
		if(!this.isAnchored()) {
			super.moveToBlockPosAndAngles(pos, rotationYawIn, rotationPitchIn);
		}
	}

	@Override
	public void moveRelative(float strafe, float up, float forward, float friction) {
		if(!this.isAnchored()) {
			super.moveRelative(strafe, up, forward, friction);
		}
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		this.fallDistance = 0;
	}

	@Override
	public boolean canTrample(World world, Block block, BlockPos pos, float fallDistance) {
		return false;
	}

	protected void updatePositioning(boolean isAnchored) {
		Direction facing = this.getFacing();
		Direction facingUp = this.getFacingUp();

		if(isAnchored) {
			if(facing == Direction.UP || facing == Direction.DOWN) {
				this.prevRotationPitch = this.xRot = facing == Direction.UP ? -90.0F : 90.0F;
				this.prevRenderYawOffset = this.renderYawOffset = facingUp.getHorizontalAngle() + (facing == Direction.DOWN ? 0.0F : 180.0F);
			} else {
				this.prevRotationPitch = this.xRot = 0;
				this.prevRenderYawOffset = this.renderYawOffset = facing.getHorizontalAngle();
			}
		} else {
			if(this.onGround && Math.abs(this.xRot + 90.0f) > 1) {
				if(this.xRot > -90.0f) {
					this.xRot -= 25.0f;
					if(this.xRot < -90.0f) {
						this.xRot = -90.0f;
					}
				} else {
					this.xRot += 25.0f;
					if(this.xRot > -90.0f) {
						this.xRot = -90.0f;
					}
				}
			}
		}

		if(!this.level.isClientSide()) {
			if(!this.isMoving()) {
				if(this.targetFacingTimeout > 0) {
					this.targetFacingTimeout--;
				} else {
					this.targetFacingUp = null;
					this.targetFacing = null;
				}

				if(this.targetAnchorTimeout > 0) {
					this.targetAnchorTimeout--;
				} else {
					this.targetAnchor = null;
				}

				if(!this.isTravelBlocked() && !this.isMoving() && (this.targetFacing != null || this.targetAnchor != null)) {
					Direction targetFacing = this.targetFacing != null ? this.targetFacing : facing;
					Direction targetFacingUp = this.targetFacingUp != null ? this.targetFacingUp : facingUp;
					BlockPos targetAnchor = this.targetAnchor != null ? this.targetAnchor : this.getAnchor();

					boolean isLookDifferent = facing != targetFacing || facingUp != targetFacingUp;
					boolean isPositionDifferent = !this.getAnchor().equals(targetAnchor);

					if(isLookDifferent || isPositionDifferent) {
						if(this.checkAnchorAt(targetAnchor, targetFacing, targetFacingUp, AnchorChecks.ALL) == 0) {
							this.entityData.set(MOVING, true);
							this.entityData.set(MOVE_FACING, targetFacing);
							this.entityData.set(MOVE_FACING_UP, targetFacingUp);
							this.entityData.set(MOVE_ANCHOR, targetAnchor);

							if(isPositionDifferent && isLookDifferent) {
								this.setMoveReason(MoveReason.POSITION_AND_LOOK);
							} else if(isPositionDifferent) {
								this.setMoveReason(MoveReason.POSITION);
							} else if(isLookDifferent) {
								this.setMoveReason(MoveReason.LOOK);
							}

							this.targetFacing = null;
							this.targetFacingUp = null;
							this.targetAnchor = null;
						}
					}
				}
			}

			int violatedChecks = this.checkAnchorHere(AnchorChecks.ALL);
			if(violatedChecks != 0) {
				this.fixUnsuitablePosition(violatedChecks);
			}
		}

		if(!this.isMoving() && isAnchored) {
			Vector3d offset = this.getOffset(1);
			Vector3d position = this.getCenter().add(offset);
			this.setPosition(position.x, position.y - this.height / 2.0D, position.z);
		}
	}
	
	protected boolean isTravelBlocked() {
		return this.isMovementBlocked();
	}

	protected void updateMovement() {
		this.lastMoveProgress = this.moveProgress;

		if(this.isMoving()) {
			float movementProgress = this.getMovementProgress(1);
			if(movementProgress < 0.5F) {
				Vector3d offset = this.getOffset(movementProgress);
				Vector3d position = this.getCenter().add(offset);
				this.setPosition(position.x, position.y - this.height / 2.0D, position.z);
			} else {
				this.entityData.set(ANCHOR, this.entityData.get(MOVE_ANCHOR));
				this.entityData.set(FACING, this.entityData.get(MOVE_FACING));
				this.entityData.set(FACING_UP, this.entityData.get(MOVE_FACING_UP));
				this.setAnchored(true);
				Vector3d offset = this.getOffset(movementProgress);
				Vector3d position = this.getCenter().add(offset);
				double px = this.getX();
				double py = this.getY();
				double pz = this.getZ();
				this.setPosition(position.x, position.y - this.height / 2.0D, position.z);
				if((this.getX() - px) * (this.getX() - px) + (this.getY() - py) * (this.getY() - py) + (this.getZ() - pz) * (this.getZ() - pz) >= 1.0D) {
					this.setPositionAndUpdate(this.getX(), this.getY(), this.getZ());
				}
			}
			if(this.moveProgress >= 1.0F) {
				this.entityData.set(MOVING, false);
				this.lastMoveProgress = this.moveProgress = 0;
			} else {
				this.moveProgress += 0.05F * (this.getAIMoveSpeed() + 0.05F);
			}
		} else {
			this.moveProgress = this.lastMoveProgress = 0;
		}
	}

	public float getPeek() {
		return this.peek;
	}

	private float getHalfMovementProgressFromRegular(float movementProgress) {
		float halfProgress;
		if(movementProgress < 0.5F) {
			halfProgress = (0.5F - movementProgress) / 0.5F;
		} else {
			halfProgress = (movementProgress - 0.5F) / 0.5F;
		}
		return halfProgress;
	}

	public float getHalfMovementProgress(float partialTicks) {
		return this.getHalfMovementProgressFromRegular(this.getMovementProgress(partialTicks));
	}

	public float getMovementProgress(float partialTicks) {
		return MathHelper.clamp(this.lastMoveProgress + (this.moveProgress - this.lastMoveProgress) * partialTicks, 0, 1);
	}

	public Vector3d getOffset(float movementProgress) {
		float offsetLength = this.getHalfMovementProgressFromRegular(movementProgress);
		return new Vector3d(this.getFacing().getDirectionVec()).scale(this.getPeek() + (this.getFacing().getAxis().isHorizontal() ? (this.getBlockWidth() - this.width) : (this.getBlockHeight() - this.height)) / 2.0D).scale(offsetLength);
	}

	public int getBlockWidth() {
		return MathHelper.ceil(this.width);
	}

	public int getBlockHeight() {
		return MathHelper.ceil(this.height);
	}

	public Direction getFacing() {
		return this.entityData.get(FACING);
	}

	public Direction getFacingUp() {
		return this.entityData.get(FACING_UP);
	}

	public BlockPos getAnchor() {
		return this.entityData.get(ANCHOR);
	}

	public boolean isMoving() {
		return this.entityData.get(MOVING);
	}

	public Vector3d getCenter() {
		return new Vector3d(this.getAnchor()).add(this.getBlockWidth() / 2.0D, this.getBlockHeight() / 2.0D, this.getBlockWidth() / 2.0D);
	}

	public Vector3d getFrontCenter() {
		Direction facing = this.getFacing();
		Vector3d center = this.getCenter();
		return center.add(this.getOffset(this.getMovementProgress(1))).add(facing.getStepX() * this.width / 2.0F, facing.getStepY() * this.height / 2.0F, facing.getStepZ() * this.width / 2.0F);
	}

	public boolean isAnchored() {
		return this.entityData.get(ANCHORED);
	}

	public void setAnchored(boolean anchored) {
		this.entityData.set(ANCHORED, anchored);
	}

	public void stopMovement() {
		this.entityData.set(MOVING, false);
		this.lastMoveProgress = this.moveProgress = 0;
	}

	public Direction[] getFacingForLookDir(Vector3d lookDir) {
		Direction[] facing = new Direction[2];
		Direction dir = Direction.getNearest((float)lookDir.x, (float)lookDir.y, (float)lookDir.z);
		facing[0] = dir;
		if(dir == Direction.DOWN || dir == Direction.UP) {
			facing[1] = Direction.getNearest((float)lookDir.x, 0, (float)lookDir.z);
			if(dir == Direction.UP) {
				facing[1] = facing[1].getOpposite();
			}
		} else {
			facing[1] = Direction.UP;
		}
		return facing;
	}

	public void setPositionToAnchor(BlockPos anchor, Direction facing, Direction facingUp) {
		this.entityData.set(ANCHOR, anchor);
		this.entityData.set(FACING, facing);
		this.entityData.set(FACING_UP, facingUp);

		this.entityData.set(MOVING, false);
		this.lastMoveProgress = this.moveProgress = 0;

		this.setAnchored(true);
		this.stopMovement();

		this.updatePositioning(true);
	}

	public MoveReason getMoveReason() {
		switch(this.entityData.get(MOVE_REASON)) {
		default:
		case 0:
			return MoveReason.POSITION;
		case 1:
			return MoveReason.LOOK;
		case 2:
			return MoveReason.POSITION_AND_LOOK;
		}
	}

	private void setMoveReason(MoveReason type) {
		switch(type) {
		default:
		case POSITION:
			this.entityData.set(MOVE_REASON, (byte) 0);
			break;
		case LOOK:
			this.entityData.set(MOVE_REASON, (byte) 1);
			break;
		case POSITION_AND_LOOK:
			this.entityData.set(MOVE_REASON, (byte) 2);
			break;
		}
	}

	public static class AnchorChecks {
		/**
		 * Checks whether the blocks around the anchor are valid
		 */
		public static final int ANCHOR_BLOCKS = 0b001;

		/**
		 * Checks whether the blocks at the entity's face at the anchor are valid
		 */
		public static final int FACE_BLOCKS = 0b010;

		/**
		 * Checks whether the entities around the anchor and the entity's face at the anchor are valid
		 */
		public static final int ENTITIES = 0b100;

		public static final int BLOCKS = ANCHOR_BLOCKS | FACE_BLOCKS;
		public static final int ALL = BLOCKS | ENTITIES;
	}

	@Nullable
	protected Direction[] findRandomValidFacingAt(BlockPos anchor) {
		List<Direction> forwardFacings = new ArrayList<>();
		forwardFacings.addAll(Arrays.asList(Direction.VALUES));
		Collections.shuffle(forwardFacings, this.rand);
		
		List<Direction> horizontalFacings = new ArrayList<>();
		horizontalFacings.addAll(Arrays.asList(Direction.Plane.HORIZONTAL));
		Collections.shuffle(horizontalFacings, this.rand);
		
		for(Direction forwardFacing : forwardFacings) {
			if(forwardFacing.getAxis() == Direction.Axis.Y) {
				for(Direction horizontalFacing : horizontalFacings) {
					if(this.checkAnchorAt(anchor, forwardFacing, horizontalFacing, AnchorChecks.ALL) == 0) {
						return new Direction[] {forwardFacing, horizontalFacing};
					}
				}
			} else {
				if(this.checkAnchorAt(anchor, forwardFacing, Direction.UP, AnchorChecks.ALL) == 0) {
					return new Direction[] {forwardFacing, Direction.UP};
				}
			}
		}
		
		return null;
	}
	
	public int checkAnchorAt(Vector3d pos, Vector3d lookDir, int checks) {
		Direction[] facing = this.getFacingForLookDir(lookDir);
		BlockPos anchor = new BlockPos(pos.x - (this.getBlockWidth() / 2), pos.y - (this.getBlockHeight() / 2), pos.z - (this.getBlockWidth() / 2));
		return this.checkAnchorAt(anchor, facing[0], facing[1], checks);
	}

	public int checkAnchorAt(BlockPos anchor, Direction facing, Direction facingUp, int checks) {
		int violations = 0;

		if((checks & AnchorChecks.ENTITIES) != 0) {
			if(!this.level.getEntitiesOfClass(EntityWallFace.class, this.getBoundingBox().move(anchor.subtract(this.getAnchor())).expandTowards(facing.getStepX() * this.getPeek(), facing.getStepY() * this.getPeek(), facing.getStepZ() * this.getPeek()), e -> e != this).isEmpty()) {
				violations |= AnchorChecks.ENTITIES;
			}
		}

		BlockPos.Mutable pos = new BlockPos.Mutable();

		if((checks & AnchorChecks.ANCHOR_BLOCKS) != 0) {
			outer: for(int xo = 0; xo < this.getBlockWidth(); xo++) {
				for(int yo = 0; yo < this.getBlockHeight(); yo++) {
					for(int zo = 0; zo < this.getBlockWidth(); zo++) {
						pos.set(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + zo);
						if(!this.canResideInBlock(pos, facing, facingUp)) {
							violations |= AnchorChecks.ANCHOR_BLOCKS;
							break outer;
						}
					}
				}
			}
		}

		if((checks & AnchorChecks.FACE_BLOCKS) != 0) {
			if(facing == Direction.UP || facing == Direction.DOWN) {
				int y = facing == Direction.UP ? this.getBlockHeight() : -1;
				outer: for(int xo = 0; xo < this.getBlockWidth(); xo++) {
					for(int zo = 0; zo < this.getBlockWidth(); zo++) {
						for(int yo = 0; yo < MathHelper.ceil(this.getPeek()); yo++) {
							pos.set(anchor.getX() + xo, anchor.getY() + y + facing.getStepY() * yo, anchor.getZ() + zo);
							if(!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			} else if(facing == Direction.NORTH || facing == Direction.SOUTH) {
				int z = facing == Direction.NORTH ? -1 : this.getBlockWidth();
				outer: for(int xo = 0; xo < this.getBlockWidth(); xo++) {
					for(int yo = 0; yo < this.getBlockHeight(); yo++) {
						for(int zo = 0; zo < MathHelper.ceil(this.getPeek()); zo++) {
							pos.set(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + z + facing.getStepZ() * zo);
							if(!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			} else if(facing == Direction.WEST || facing == Direction.EAST) {
				int x = facing == Direction.WEST ? -1 : this.getBlockWidth();
				outer: for(int zo = 0; zo < this.getBlockWidth(); zo++) {
					for(int yo = 0; yo < this.getBlockHeight(); yo++) {
						for(int xo = 0; xo < MathHelper.ceil(this.getPeek()); xo++) {
							pos.set(anchor.getX() + x + facing.getStepX() * xo, anchor.getY() + yo, anchor.getZ() + zo);
							if(!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			}
		}

		return violations;
	}

	protected int checkAnchorHere(int checks) {
		return this.checkAnchorAt(this.getAnchor(), this.getFacing(), this.getFacingUp(), checks);
	}

	public abstract boolean canResideInBlock(BlockPos pos, Direction facing, Direction facingUp);

	public abstract boolean canMoveFaceInto(BlockPos pos, Direction facing, Direction facingUp);

	protected void fixUnsuitablePosition(int violatedChecks) {

	}

	public static final class LookHelper extends EntityLookHelper {
		private final EntityWallFace face;

		private int lookingMode = 0;

		private double x, y, z;

		private LookHelper(EntityWallFace entity) {
			super(entity);
			this.face = entity;
		}

		@Override
		public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
			this.x = entityIn.getX();

			if (entityIn instanceof LivingEntity) {
				this.y = entityIn.getY() + (double)entityIn.getEyeHeight();
			} else {
				this.y = (entityIn.getBoundingBox().minY + entityIn.getBoundingBox().maxY) / 2.0D;
			}

			this.z = entityIn.getZ();
			this.lookingMode = 1;
		}

		@Override
		public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.lookingMode = 1;
		}

		public void setLookDirection(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.lookingMode = 2;
		}

		@Override
		public boolean getIsLooking() {
			return this.lookingMode != 0;
		}

		@Override
		public double getLookPosX() {
			return this.x;
		}

		@Override
		public double getLookPosY() {
			return this.y;
		}

		@Override
		public double getLookPosZ() {
			return this.z;
		}

		@Override
		public void onUpdateLook() {
			if(this.lookingMode == 1) {
				Vector3d center = this.face.getCenter();
				Direction[] facing = this.face.getFacingForLookDir(new Vector3d(this.x - center.x, this.y - center.y, this.z - center.z));
				this.face.targetFacingTimeout = 30 + this.face.level.random.nextInt(30);
				this.face.targetFacing = facing[0];
				this.face.targetFacingUp = facing[1];
				this.setSpeed(1);
			} else if(this.lookingMode == 2) {
				Direction[] facing = this.face.getFacingForLookDir(new Vector3d(this.x, this.y, this.z));
				this.face.targetFacingTimeout = 30 + this.face.level.random.nextInt(30);
				this.face.targetFacing = facing[0];
				this.face.targetFacingUp = facing[1];
				this.setSpeed(1);
			}
			this.lookingMode = 0;
		}

		public void setSpeed(double speed) {
			if(!this.face.isMoving() && this.face.targetAnchor == null) {
				this.face.setAIMoveSpeed((float)(speed * this.face.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
			}
		}
	}

	public static class MoveHelper extends EntityMoveHelper {
		private final EntityWallFace face;

		private MoveHelper(EntityWallFace entity) {
			super(entity);
			this.face = entity;
		}

		@Override
		public void onUpdateMoveHelper() {
			if(this.action == EntityMoveHelper.Action.STRAFE && this.moveStrafe != 0) {
				Vector3i horDir = this.face.getFacing().getDirectionVec().cross(this.face.getFacingUp().getDirectionVec());
				int strafeDir = -(int)Math.signum(this.moveStrafe);
				this.face.targetAnchorTimeout = 30 + this.entity.level.rand.nextInt(30);
				this.face.targetAnchor = this.face.getAnchor().add(horDir.getX() * strafeDir, horDir.getY() * strafeDir, horDir.getZ() * strafeDir);
				this.setSpeed(this.speed);
			} else if(this.action == EntityMoveHelper.Action.MOVE_TO) {
				this.face.targetAnchorTimeout = 30 + this.entity.level.rand.nextInt(30);
				this.face.targetAnchor = new BlockPos(this.getX() - this.face.getBlockWidth() / 2.0D, this.getY() - this.face.getBlockHeight() / 2.0D, this.getZ() - this.face.getBlockWidth() / 2.0D);
				this.setSpeed(this.speed);
			}
			this.action = EntityMoveHelper.Action.WAIT;
		}

		public void setSpeed(double speed) {
			this.face.setAIMoveSpeed((float)(speed * this.entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
		}
	}
}
