package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.ai.EntityAIMoveToDirect;
import thebetweenlands.common.entity.ai.EntityAITargetNonSneaking;
import thebetweenlands.common.entity.ai.EntityAIWightAttack;
import thebetweenlands.common.entity.ai.EntityAIWightBuffSwampHag;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.network.clientbound.MessageWightVolatileParticles;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityWight extends MobEntity implements IEntityBL {

    public static final Attribute VOLATILE_HEALTH_START_ATTRIB = (new RangedAttribute("bl.volatileHealthStart", 1.0D, 0.0D, 1.0D)).setDescription("Volatile Health Percentage Start");
    public static final Attribute VOLATILE_COOLDOWN_ATTRIB = (new RangedAttribute("bl.volatileCooldown", 400.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Volatile Cooldown");
    public static final Attribute VOLATILE_FLIGHT_SPEED_ATTRIB = (new RangedAttribute("bl.volatileFlightSpeed", 0.32D, 0.0D, 5.0D)).setDescription("Volatile Flight Speed");
    public static final Attribute VOLATILE_LENGTH_ATTRIB = (new RangedAttribute("bl.volatileLength", 600.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Volatile Length");
    public static final Attribute VOLATILE_MAX_DAMAGE_ATTRIB = (new RangedAttribute("bl.volatileMaxDamage", 20.0D, 0.0D, Double.MAX_VALUE)).setDescription("Volatile Max Damage");

    protected static final DataParameter<Boolean> HIDING_STATE_DW = EntityDataManager.defineId(EntityWight.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> VOLATILE_STATE_DW = EntityDataManager.defineId(EntityWight.class, DataSerializers.BOOLEAN);
    protected final EntityMoveHelper flightMoveHelper;
    protected final EntityMoveHelper groundMoveHelper;
    private int hidingAnimationTicks = 0;
    private int lastHidingAnimationTicks = 0;
    private int volatileCooldownTicks = (int) VOLATILE_COOLDOWN_ATTRIB.getDefaultValue() / 2 + 20;
    private int volatileTicks = 0;
    private float volatileReceivedDamage = 0.0F;
    private boolean canTurnVolatile = true;
    private boolean canTurnVolatileOnTarget = false;
    private boolean didTurnVolatileOnPlayer = false;
    private static final DataParameter<Integer> GROW_TIMER = EntityDataManager.defineId(EntityWight.class, DataSerializers.INT);
    public int growCount, prevGrowCount;

    public EntityWight(World world) {
        super(world);
        this.experienceValue = 10;
        setSize(0.7F, 2.2F);
        this.setPathPriority(PathNodeType.WATER, 0.2F);
        this.flightMoveHelper = new FlightMoveHelper(this) {
            @Override
            protected double getFlightSpeed() {
                return this.entity.getAttributeMap().getAttributeInstance(VOLATILE_FLIGHT_SPEED_ATTRIB).getValue();
            }
        };
        this.moveControl = this.groundMoveHelper = new EntityMoveHelper(this);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new EntityAITargetNonSneaking(this));
        this.targetSelector.addGoal(1, new EntityAIHurtByTarget(this, true));

        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new EntityAIWightAttack(this, 1.0D, false));
        this.goalSelector.addGoal(2, new EntityAIWightBuffSwampHag(this));
        this.goalSelector.addGoal(3, new EntityAIMoveToDirect<EntityWight>(this, this.getAttributeMap().getAttributeInstance(VOLATILE_FLIGHT_SPEED_ATTRIB).getValue()) {
            @Override
            protected Vector3d getTarget() {
                if (this.entity.volatileTicks >= 20) {
                    LivingEntity target = this.entity.getAttackTarget();
                    if (target != null) {
                        return new Vector3d(target.getX(), target.getY() + target.getEyeHeight() / 2.0D, target.getZ());
                    }
                }
                return null;
            }
        });
        this.goalSelector.addGoal(4, new EntityAIMoveTowardsRestriction(this, 0.4D));
        this.goalSelector.addGoal(5, new EntityAIWander(this, 0.3D));
        this.goalSelector.addGoal(9, new EntityAIFlyRandomly<EntityWight>(this) {
            @Override
            public boolean canUse() {
                return this.entity.isVolatile() && this.entity.volatileTicks >= 20 && this.entity.getAttackTarget() == null && super.canUse();
            }

            @Override
            protected double getFlightSpeed() {
                return 0.1D;
            }
        });
        this.goalSelector.addGoal(7, new EntityAIWatchClosest(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new EntityAILookIdle(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VOLATILE_STATE_DW, false);
        this.entityData.define(HIDING_STATE_DW, false);
        this.entityData.define(GROW_TIMER, 40);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.33D);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(76.0D);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(80.0D);

        this.getAttributeMap().registerAttribute(VOLATILE_HEALTH_START_ATTRIB);
        this.getAttributeMap().registerAttribute(VOLATILE_COOLDOWN_ATTRIB);
        this.getAttributeMap().registerAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB);
        this.getAttributeMap().registerAttribute(VOLATILE_LENGTH_ATTRIB);
        this.getAttributeMap().registerAttribute(VOLATILE_MAX_DAMAGE_ATTRIB);
    }

    @Override
	public void tick() {
		if (this.level.isClientSide()) {
			prevGrowCount = growCount;
			growCount = getGrowTimer();
			if (getGrowTimer() > 0 && getGrowTimer() < 40)
				if (this.tickCount % 4 == 0)
					this.spawnVolatileParticles(true);
		}

        if (!this.level.isClientSide()) {
			if (isInTar()) {
				if (getGrowTimer() > 0)
					setGrowTimer(Math.max(0, getGrowTimer() - 1));
				if (getGrowTimer() <= 0) {
					EntityTarBeast tar_beast = new EntityTarBeast(level);
					tar_beast.setPositionAndRotation(posX, posY, posZ, yRot, xRot);
					tar_beast.setGrowTimer(0);
					level.addFreshEntity(tar_beast);
					remove();
				}
			}

			if (!isInTar() && getGrowTimer() < 40)
				setGrowTimer(Math.min(40, getGrowTimer() + 1));

            if (this.getAttackTarget() == null) {
                this.setHiding(true);

                this.canTurnVolatileOnTarget = false;
            } else {
                this.setHiding(false);

                if (this.canTurnVolatile && !this.isVolatile() && !this.isRiding() && this.canPossess(this.getAttackTarget()) && this.canTurnVolatileOnTarget) {
                    if (this.volatileCooldownTicks > 0) {
                        this.volatileCooldownTicks--;
                    }

                    if (this.getHealth() <= this.getMaxHealth() * this.getAttribute(VOLATILE_HEALTH_START_ATTRIB).getValue() && this.volatileCooldownTicks <= 0) {
                        this.setVolatile(true);
                        this.didTurnVolatileOnPlayer = true;
                        this.volatileReceivedDamage = 0.0F;
                        this.volatileCooldownTicks = this.getMaxVolatileCooldown() + this.level.random.nextInt(this.getMaxVolatileCooldown()) + 20;
                        this.volatileTicks = 0;

                        TheBetweenlands.networkWrapper.sendToAllAround(new MessageWightVolatileParticles(this), new TargetPoint(this.dimension, this.getX(), this.getY(), this.getZ(), 32));
                        this.world.playLocalSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.WIGHT_ATTACK, SoundCategory.HOSTILE, 1.6F, 1.0F);
                    }
                } else if (this.didTurnVolatileOnPlayer && this.isVolatile() && !this.canPossess(this.getAttackTarget())) {
                    this.setVolatile(false);
                    this.didTurnVolatileOnPlayer = false;
                }
            }
        }

        if (this.isVolatile()) {
        	if(!this.level.isClientSide()) {
	        	if (this.volatileTicks < this.getAttribute(VOLATILE_LENGTH_ATTRIB).getValue()) {
	            	this.volatileTicks++;
	
	                if (this.volatileTicks >= 20) {
	                    this.noClip = true;
	                }
	            } else {
	                if (!this.level.isClientSide()) {
	                    this.motionY -= 0.075D;
	
	                    this.fallDistance = 0;
	
	                    if (this.didTurnVolatileOnPlayer && this.onGround) {
	                        this.setVolatile(false);
	                        this.didTurnVolatileOnPlayer = false;
	                    }
	                }
	
	                this.noClip = false;
	            }
	
	            if (this.volatileTicks < 20) {
	                this.moveControl.setWantedPosition(this.getX(), this.getY() + 1.0D, this.getZ(), 0.15D);
	            }
	
	            if (this.getAttackTarget() != null) {
	                LivingEntity attackTarget = this.getAttackTarget();
	
	                if (this.getRidingEntity() == null && this.getDistance(attackTarget) < 1.75D && this.canPossess(attackTarget)) {
	                    this.startRiding(attackTarget, true);
	                    this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(attackTarget));
	                }
	
	                if (this.getRidingEntity() == null) {
	                    double dx = attackTarget.getX() - this.getX();
	                    double dz = attackTarget.getZ() - this.getZ();
	                    double dy;
	                    if (attackTarget instanceof LivingEntity) {
	                        LivingEntity entitylivingbase = attackTarget;
	                        dy = entitylivingbase.getY() + (double) entitylivingbase.getEyeHeight() - (this.getY() + (double) this.getEyeHeight());
	                    } else {
	                        dy = (attackTarget.getBoundingBox().minY + attackTarget.getBoundingBox().maxY) / 2.0D - (this.getY() + (double) this.getEyeHeight());
	                    }
	                    double dist = (double) MathHelper.sqrt(dx * dx + dz * dz);
	                    float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
	                    float pitch = (float) (-(Math.atan2(dy, dist) * 180.0D / Math.PI));
	                    this.setRotation(yaw, pitch);
	                    this.setRotationYawHead(yaw);
	                } else {
	                    this.setRotation(0, 0);
	                    this.setRotationYawHead(0);
	
	                    if (this.tickCount % 5 == 0 && this.canSee(this.getAttackTarget()) && !this.isWearingSkullMask(this.getAttackTarget())) {
	                        List<EntityVolatileSoul> existingSouls = this.world.getEntitiesOfClass(EntityVolatileSoul.class, this.getBoundingBox().inflate(16.0D, 16.0D, 16.0D));
	                        if (existingSouls.size() < 16) {
	                            EntityVolatileSoul soul = new EntityVolatileSoul(this.world);
	                            float mx = this.level.random.nextFloat() - 0.5F;
	                            float my = this.level.random.nextFloat() / 2.0F;
	                            float mz = this.level.random.nextFloat() - 0.5F;
	                            Vector3d dir = new Vector3d(mx, my, mz).normalize();
	                            soul.setOwner(this.getUUID());
	                            soul.moveTo(this.getX() + dir.x * 0.5D, this.getY() + dir.y * 1.5D, this.getZ() + dir.z * 0.5D, 0, 0);
	                            soul.shoot(mx * 2.0D, my * 2.0D, mz * 2.0D, 1.0F, 1.0F);
	                            this.world.addFreshEntity(soul);
	                        }
	                    }
	                }
	            }
	            
	            this.moveControl = this.flightMoveHelper;
        	}

            if (this.level.isClientSide() && (this.getRidingEntity() == null || this.tickCount % 4 == 0)) {
                this.spawnVolatileParticles(false);
            }

            this.setSize(0.7F, 0.7F);
        } else {
        	if(!this.level.isClientSide()) {
	            this.noClip = false;
	            this.moveControl = this.groundMoveHelper;
        	}
            
            this.setSize(0.7F, 2.2F);
        }

        this.lastHidingAnimationTicks = this.hidingAnimationTicks;
        if (this.isHiding()) {
            if (this.hidingAnimationTicks < 12)
                this.hidingAnimationTicks++;
        } else {
            if (this.hidingAnimationTicks > 0)
                this.hidingAnimationTicks--;
        }

        super.tick();
    }

	public boolean isInTar() {
		//System.out.println("IN TAR!");
		return this.world.isMaterialInBB(this.getBoundingBox().inflate(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), BLMaterialRegistry.TAR);
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || isInTar() || getGrowTimer() < 40;
	}

	@Override
    public void fall(float distance, float damageMultiplier) {
        if (!this.isVolatile()) {
            super.fall(distance, damageMultiplier);
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (!this.isVolatile()) {
            super.updateFallState(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void travel(float strafe, float up, float forward) {
        if (this.isVolatile()) {
            //Use flight movement

            if (this.isInWater()) {
                this.moveRelative(strafe, up, forward, 0.02F);
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.800000011920929D;
                this.motionY *= 0.800000011920929D;
                this.motionZ *= 0.800000011920929D;
            } else if (this.isInLava()) {
                this.moveRelative(strafe,up, forward, 0.02F);
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            } else {
                float f = 0.91F;

                if (this.onGround) {
                    f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()))).getBlock().slipperiness * 0.91F;
                }

                float f1 = 0.16277136F / (f * f * f);
                this.moveRelative(strafe, up,  forward, this.onGround ? 0.1F * f1 : 0.02F);
                f = 0.91F;

                if (this.onGround) {
                    f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()))).getBlock().slipperiness * 0.91F;
                }

                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= (double) f;
                this.motionY *= (double) f;
                this.motionZ *= (double) f;
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.getX() - this.xOld;
            double d0 = this.getZ() - this.zOld;
            float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        } else {
            //Use normal movement

            super.travel(strafe,up, forward);
        }
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        boolean isCreative = source instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect) source).getTrueSource() instanceof PlayerEntity && ((PlayerEntity) ((EntityDamageSourceIndirect) source).getTrueSource()).isCreative();
        return this.isHiding() && isCreative;
    }

    @Override
    public boolean canBePushed() {
        return !this.isHiding();
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.WIGHT;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (this.isVolatile() && source == DamageSource.IN_WALL) {
            return false;
        }
        float prevHealth = this.getHealth();
        boolean ret = super.hurt(source, damage);
        float dealtDamage = prevHealth - this.getHealth();
        if (this.didTurnVolatileOnPlayer && this.isVolatile() && this.getRidingEntity() != null) {
            this.volatileReceivedDamage += dealtDamage;
            if (this.volatileReceivedDamage >= this.getAttribute(VOLATILE_MAX_DAMAGE_ATTRIB).getValue()) {
                this.setVolatile(false);
                this.didTurnVolatileOnPlayer = false;
            }
        }
        if (this.getAttackTarget() != null && source instanceof EntityDamageSource && source.getTrueSource() == this.getAttackTarget()) {
            this.canTurnVolatileOnTarget = true;
        }
        return ret;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (this.isVolatile()) {
            return false;
        }
        if (super.attackEntityAsMob(entity)) {
            if (entity == this.getAttackTarget()) {
                this.canTurnVolatileOnTarget = true;
            }
            return true;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.WIGHT_MOAN;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundRegistry.WIGHT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.WIGHT_DEATH;
    }

    @Override
    public void writeEntityToNBT(CompoundNBT nbt) {
        super.writeEntityToNBT(nbt);
        nbt.putBoolean("volatileState", this.isVolatile());
        nbt.putInt("volatileCooldown", this.volatileCooldownTicks);
        nbt.putInt("volatileTicks", this.volatileTicks);
        nbt.putFloat("volatileReceivedDamage", this.volatileReceivedDamage);
        nbt.putBoolean("canTurnVolatileOnTarget", this.canTurnVolatileOnTarget);
        nbt.putBoolean("canTurnVolatile", this.canTurnVolatile);
        nbt.putBoolean("turnVolatileOnPlayer", this.didTurnVolatileOnPlayer);
        nbt.putInt("grow_timer", getGrowTimer());
    }

    @Override
    public void readEntityFromNBT(CompoundNBT nbt) {
        super.readEntityFromNBT(nbt);

        if (nbt.contains("volatileState")) {
            this.setVolatile(nbt.getBoolean("volatileState"));
        }
        if (nbt.contains("turnVolatileOnPlayer")) {
            this.didTurnVolatileOnPlayer = nbt.getBoolean("turnVolatileOnPlayer");
        }
        if (nbt.contains("volatileCooldown")) {
            this.volatileCooldownTicks = nbt.getInt("volatileCooldown");
        }
        if (nbt.contains("volatileTicks")) {
            this.volatileTicks = nbt.getInt("volatileTicks");
        }
        if (nbt.contains("volatileReceivedDamage")) {
            this.volatileReceivedDamage = nbt.getFloat("volatileReceivedDamage");
        }
        if (nbt.contains("canTurnVolatileOnTarget")) {
            this.canTurnVolatileOnTarget = nbt.getBoolean("canTurnVolatileOnTarget");
        }
        if (nbt.contains("canTurnVolatile")) {
            this.canTurnVolatile = nbt.getBoolean("canTurnVolatile");
        }

		if(nbt.contains("grow_timer"))
			setGrowTimer(nbt.getInt("grow_timer")); 
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnVolatileParticles(boolean tarred) {
        final double radius = 0.3F;

        final double cx = this.getX();
        final double cy = this.getY() + 0.35D;
        final double cz = this.getZ();

        for (int i = 0; i < 8; i++) {
            double px = this.level.random.nextFloat() * 0.7F;
            double py = this.level.random.nextFloat() * 0.7F;
            double pz = this.level.random.nextFloat() * 0.7F;
            Vector3d vec = new Vector3d(px, py, pz).subtract(new Vector3d(0.35F, 0.35F, 0.35F)).normalize();
            px = cx + vec.x * radius;
            py = cy + vec.y * radius;
            pz = cz + vec.z * radius;
			if (tarred) {
				float tintChange = 1F / 40F * growCount;
				BLParticles.STEAM_PURIFIER.spawn(this.world, px, py + 0.25D, pz).setRBGColorF(tintChange, tintChange,
						tintChange);
			} else
				BLParticles.STEAM_PURIFIER.spawn(this.world, px, py, pz);
		}
    }

    public boolean isHiding() {
        return this.getEntityData().get(HIDING_STATE_DW);
    }

    public void setHiding(boolean hiding) {
        this.getEntityData().set(HIDING_STATE_DW, hiding);
    }

	public int getGrowTimer() {
		return entityData.get(GROW_TIMER);
	}

	public void setGrowTimer(int timer) {
		entityData.set(GROW_TIMER, timer);
	}

    public float getHidingAnimation(float partialTicks) {
        return (this.lastHidingAnimationTicks + (this.hidingAnimationTicks - this.lastHidingAnimationTicks) * partialTicks) / 12.0F;
    }

	public float getGrowthFactor(float partialTicks) {
		return prevGrowCount + (growCount - prevGrowCount) * partialTicks;
	}

    public boolean isVolatile() {
        return this.getEntityData().get(VOLATILE_STATE_DW);
    }

    public void setVolatile(boolean isVolatile) {
        this.entityData.set(VOLATILE_STATE_DW, isVolatile);

        if (!isVolatile) {
            Entity ridingEntity = this.getRidingEntity();
            if (ridingEntity != null) {
                this.dismountRidingEntity();
                if(!this.level.isClientSide()) {
                	this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(ridingEntity));
                }
            }
        }
    }

    public int getMaxVolatileCooldown() {
        return (int) this.getAttribute(VOLATILE_COOLDOWN_ATTRIB).getValue();
    }

    public boolean canPossess(LivingEntity entity) {
        if(entity instanceof EntitySwampHag) {
        	return true;
        }
        if(entity instanceof PlayerEntity) {
        	return !this.isWearingSkullMask(entity);
        }
        return false;
    }
    
    public boolean isWearingSkullMask(LivingEntity entity) {
    	if(entity instanceof PlayerEntity) {
        	ItemStack helmet = ((PlayerEntity)entity).getItemStackFromSlot(EquipmentSlotType.HEAD);
        	if(!helmet.isEmpty() && helmet.getItem() == ItemRegistry.SKULL_MASK) {
        		return true;
        	}
        }
    	return false;
    }

    public void setCanTurnVolatile(boolean canTurnVolatile) {
        this.canTurnVolatile = canTurnVolatile;
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
