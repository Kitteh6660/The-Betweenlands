package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromaw extends EntityFlyingMob implements IEntityBL {
	private static final DataParameter<Boolean> IS_HANGING = EntityDataManager.defineId(EntityChiromaw.class, DataSerializers.BOOLEAN);

	public EntityChiromaw(World world) {
		super(world);
		setSize(0.7F, 0.9F);
		setIsHanging(false);

		this.moveControl = new FlightMoveHelper(this);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new EntityAISwimming(this));
		this.goalSelector.addGoal(1, new EntityAIAttackMelee(this, 1.0D, true));
		this.goalSelector.addGoal(2, new EntityAIFlyingWander(this, 0.5D));
		this.targetSelector.addGoal(1, new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, true).setUnseenMemoryTicks(160));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		this.entityData.define(IS_HANGING, false);
	}

	@Override
	public void tick() {
		super.tick();

		if (!level.isClientSide() && world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			remove();
		}

		if (this.isJumping && this.isInWater()) {
			//Moving out of water
			this.getMoveHelper().setMoveTo(this.getX(), this.getY() + 1, this.getZ(), 1.0D);
		}

		if (this.getIsHanging()) {
			this.motionX = this.motionY = this.motionZ = 0.0D;
			this.getY() = (double) MathHelper.floor(this.getY()) + 1.0D - (double) this.height;
		}

		if (motionY < 0.0D && this.getAttackTarget() == null) {
			motionY *= 0.25D;
		}

		if(level.getBlockState(getPosition().below()).isSideSolid(level, getPosition().below(), Direction.UP)) {
			getMoveHelper().setMoveTo(this.getX(), this.getY() + 1, this.getZ(), 1.0D);
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if (getIsHanging()) {
			if (!this.level.isClientSide()) {
				this.moveControl.setWantedPosition(this.getX(), this.getY() + 0.5D, this.getZ(), 0);

				if (this.random.nextInt(250) == 0 || !this.world.getBlockState(new BlockPos(this.getX(), this.getY() + 1, this.getZ())).isNormalCube()) {
					setIsHanging(false);
					this.world.levelEvent(null, 1025, this.getPosition(), 0);
				} else if (this.getAttackTarget() != null) {
					setIsHanging(false);
					this.world.levelEvent(null, 1025, this.getPosition(), 0);
				}
			}
		} else {
			if (this.getAttackTarget() == null) {
				if (!this.level.isClientSide() && this.random.nextInt(20) == 0 && world.getBlockState(new BlockPos(this.getX(), this.getY() + 1, this.getZ())).isNormalCube()) {
					setIsHanging(true);
				}
			}
		}
	}

	public boolean getIsHanging() {
		return entityData.get(IS_HANGING);
	}

	public void setIsHanging(boolean hanging) {
		entityData.set(IS_HANGING, hanging);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CHIROMAW;
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
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(10.0D);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.095D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
}
