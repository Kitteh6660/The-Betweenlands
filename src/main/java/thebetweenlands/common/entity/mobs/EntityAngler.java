package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityAngler extends MobEntity implements IEntityBL {
    private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityAngler.class, DataSerializers.BOOLEAN);

    public EntityAngler(EntityType<? extends MobEntity> entity, World world) {
        super(entity, world);
        setSize(0.8F, 0.7F);
        moveHelper = new EntityAngler.AnglerMoveHelper(this);
		setPathPriority(PathNodeType.WALKABLE, -8.0F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.WATER, 16.0F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAIAttackMelee(this, 0.7D, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity attackTarget) {
                return 0.75D + attackTarget.width;
            }
        });
        tasks.addTask(1, new EntityAIMoveTowardsRestriction(this, 0.4D));
        tasks.addTask(2, new EntityAIWander(this, 0.5D, 20));
        tasks.addTask(3, new EntityAIWatchClosest(this, PlayerEntity.class, 6.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        targetTasks.addTask(0, new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, 0, true, true, null));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_LEAPING, false);
    }

    public boolean isLeaping() {
        return dataManager.get(IS_LEAPING);
    }

    private void setIsLeaping(boolean leaping) {
        dataManager.set(IS_LEAPING, leaping);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
        getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(12.0D);
        getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.FISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.FISH_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.ANGLER;
    }

    @Override
    public boolean getCanSpawnHere() {
        return world.getDifficulty() != EnumDifficulty.PEACEFUL && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }

    public boolean isGrounded() {
        return !isInWater() && world.isEmptyBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY + 1), MathHelper.floor(posZ))) && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY - 1), MathHelper.floor(posZ))).getBlock().isCollidable();
    }

	@Override
    protected PathNavigate createNavigator(World world){
        return new PathNavigateSwimmer(this, world);
    }

    @Override
    protected boolean isValidLightLevel() {
        return true;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

	@Override
	public void onLivingUpdate() {
		if (level.isClientSide()) {
			if (isInWater()) {
				Vector3d vec3d = getLook(0.0F);
				for (int i = 0; i < 2; ++i)
					level.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX + (rand.nextDouble() - 0.5D) * (double) width - vec3d.x * 1.5D, posY + rand.nextDouble() * (double) height - vec3d.y * 1.5D, posZ + (rand.nextDouble() - 0.5D) * (double) width - vec3d.z * 1.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			}
		}

		if (inWater) {
			setAir(300);
		} else if (onGround) {
			motionY += 0.5D;
			motionX += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
			motionZ += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
			yRot = rand.nextFloat() * 360.0F;
			if(isLeaping())
				setIsLeaping(false);
			onGround = false;
			isAirBorne = true;
			if(level.getGameTime()%5==0)
				level.playSound((PlayerEntity) null, posX, posY, posZ, SoundEvents.ENTITY_GUARDIAN_FLOP, SoundCategory.HOSTILE, 1F, 1F);
				damageEntity(DamageSource.DROWN, 0.5F);
		}

		super.onLivingUpdate();
	}

	@Override
	public void tick() {
		if(!level.isClientSide()) {
		if(getAttackTarget() != null && !level.containsAnyLiquid(getAttackTarget().getBoundingBox())) {
			Double distance = getPosition().getDistance((int) getAttackTarget().getX(), (int) getAttackTarget().getY(), (int) getAttackTarget().getZ());
			if (distance > 1.0F && distance < 6.0F) // && getAttackTarget().getBoundingBox().maxY >= getBoundingBox().minY && getAttackTarget().getBoundingBox().minY <= getBoundingBox().maxY && rand.nextInt(3) == 0)
				if (isInWater() && level.isEmptyBlock(new BlockPos((int) posX, (int) posY + 1, (int) posZ))) {
					if(!isLeaping()) {
						setIsLeaping(true);
						level.playSound((PlayerEntity) null, posX, posY, posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.HOSTILE, 1F, 2F);
					}
					double distanceX = getAttackTarget().getX() - posX;
					double distanceZ = getAttackTarget().getZ() - posZ;
					float distanceSqrRoot = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ);
					motionX = distanceX / distanceSqrRoot * 0.5D * 0.900000011920929D + motionX * 0.70000000298023224D;
					motionZ = distanceZ / distanceSqrRoot * 0.5D * 0.900000011920929D + motionZ * 0.70000000298023224D;
					motionY = 0.4D;
					}
			}
		}
		super.tick();
	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if (isServerWorld()) {
			if (isInWater()) {
				moveRelative(strafe, up,  forward, 0.1F);
				move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.8999999761581421D;
				motionY *= 1D;
				motionZ *= 0.8999999761581421D;

				if (getAttackTarget() == null)
					motionY -= 0.005D;
			} else {
				super.travel(strafe, up, forward);
			}
		} else {
			super.travel(strafe, up, forward);
		}
	}

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (super.attackEntityAsMob(entity)) {
            playSound(SoundRegistry.ANGLER_ATTACK, 1, 1);
            return true;
        }
        return false;
    }

	@Override
    public boolean isNotColliding() {
		 return level.checkNoEntityCollision(getBoundingBox(), this) && level.getCollisionBoxes(this, getBoundingBox()).isEmpty();
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    //AIs

    static class AnglerMoveHelper extends EntityMoveHelper {
        private final EntityAngler angler;

        public AnglerMoveHelper(EntityAngler angler) {
            super(angler);
            this.angler = angler;
        }

        @Override
		public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO && !angler.getNavigator().noPath()) {
                double d0 = posX - angler.getX();
                double d1 = posY - angler.getY();
                double d2 = posZ - angler.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double) MathHelper.sqrt(d3);
                d1 = d1 / d3;
                float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                angler.yRot = limitAngle(angler.yRot, f, 90.0F);
                angler.renderYawOffset = angler.yRot;
                float f1 = (float) (speed * angler.getEntityAttribute(Attributes.MOVEMENT_SPEED).getAttributeValue());
                angler.setAIMoveSpeed(angler.getAIMoveSpeed() + (f1 - angler.getAIMoveSpeed()) * 0.125F);
                double d4 = Math.sin((double) (angler.tickCount + angler.getEntityId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (angler.yRot * 0.017453292F));
                double d6 = Math.sin((double) (angler.yRot * 0.017453292F));
                angler.motionX += d4 * d5;
                angler.motionZ += d4 * d6;
                d4 = Math.sin((double) (angler.tickCount + angler.getEntityId()) * 0.75D) * 0.05D;
                angler.motionY += d4 * (d6 + d5) * 0.25D;
                angler.motionY += (double) angler.getAIMoveSpeed() * d1 * 0.1D;
                EntityLookHelper entitylookhelper = angler.getLookHelper();
                double d7 = angler.getX() + d0 / d3 * 2.0D;
                double d8 = (double) angler.getEyeHeight() + angler.getY() + d1 / d3;
                double d9 = angler.getZ() + d2 / d3 * 2.0D;
                double d10 = entitylookhelper.getLookPosX();
                double d11 = entitylookhelper.getLookPosY();
                double d12 = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking()) {
                    d10 = d7;
                    d11 = d8;
                    d12 = d9;
                }

                angler.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
            } else {
                angler.setAIMoveSpeed(0.0F);
            }
        }
    }

}
