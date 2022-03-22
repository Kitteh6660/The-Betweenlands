package thebetweenlands.common.entity.mobs;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ai.EntityAIDruidTeleport;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetDruid;
import thebetweenlands.common.entity.ai.EntityAINearestAttackableTargetDruid;
import thebetweenlands.common.network.clientbound.MessageDruidTeleportParticles;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.MathUtils;

import java.util.UUID;

public class EntityDarkDruid extends EntityMob {
    private static final DataParameter<Boolean> CASTING = EntityDataManager.createKey(EntityDarkDruid.class, DataSerializers.BOOLEAN);

    private static final int MIN_ATTACK_DELAY = 40, MAX_ATTACK_DELAY = 120;
    private static final int MAX_ATTACK_TIME = 20;

    private static final int MAX_ATTACK_ANIMATION_TIME = 8;

    private EntityAIAttackMelee meleeAI;
    private EntityAIWander wanderAI;
    private EntityAIWatchClosest watchAI;

    private int attackDelayCounter;
    private int attackCounter;
    private int teleportCooldown;
    private boolean isWatching = true;

    private int prevAttackAnimationTime;
    private int attackAnimationTime;

    public EntityDarkDruid(World world) {
        super(world);
        this.experienceValue = 10;
        ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        setSize(0.9F, 1.9F);
    }

    @Override
    protected void initEntityAI() {
        this.meleeAI = new EntityAIAttackMelee(this, 0.6F, true);
        this.wanderAI = new EntityAIWander(this, 0.8F);
        this.watchAI = new EntityAIWatchClosest(this, PlayerEntity.class, 16);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(2, this.meleeAI);
        this.tasks.addTask(3, new EntityAIMoveTowardsRestriction(this, 0.23F));
        this.tasks.addTask(4, this.wanderAI);
        this.tasks.addTask(5, this.watchAI);
        this.tasks.addTask(6, new EntityAIDruidTeleport(this));

        this.targetTasks.addTask(0, new EntityAIHurtByTargetDruid(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTargetDruid(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CASTING, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45);
        getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(50);
        getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5);
        getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16);
        getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isClientSide()) {
            if (getAttackTarget() != null) {
                if (this.attackDelayCounter > 0 && !this.isCasting() && getAttackTarget().getDistance(this) < 10.0D) {
                    this.attackDelayCounter--;
                }
                if (this.attackDelayCounter <= 0 || this.attackCounter > 0) {
                    if (getEntitySenses().canSee(getAttackTarget())) {
                        if (attackCounter == 0) {
                            if (getAttackTarget().onGround && !getAttackTarget().isRiding()) {
                                attackCounter++;
                                if (!world.isClientSide()) {
                                    tasks.removeTask(meleeAI);
                                }
                            }
                        } else if (attackCounter < MAX_ATTACK_TIME) {
                            attackCounter++;
                            startCasting();
                            if (!world.isClientSide()) {
                                chargeSpell(getAttackTarget());
                            }
                        } else if (attackCounter >= MAX_ATTACK_TIME) {
                            this.attackDelayCounter = MIN_ATTACK_DELAY + this.random.nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
                            attackCounter = 0;
                            stopCasting();
                            if (!world.isClientSide()) {
                                if (!getAttackTarget().isRiding()) {
                                    castSpell(getAttackTarget());
                                }
                                tasks.addTask(2, meleeAI);
                            }
                        }
                    }
                }
            } else if (isCasting() || attackCounter != 0) {
                if (this.attackDelayCounter <= 0) {
                    this.attackDelayCounter = MIN_ATTACK_DELAY + this.random.nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
                }
                attackCounter = 0;
                stopCasting();
            }
        }
        if (world.isClientSide()) {
            prevRenderYawOffset = prevRotationYaw;
            renderYawOffset = yRot;
            prevAttackAnimationTime = attackAnimationTime;
            if (isCasting()) {
                if (attackAnimationTime < MAX_ATTACK_ANIMATION_TIME) {
                    attackAnimationTime++;
                }
                spawnParticles();
            } else {
                if (attackAnimationTime > 0) {
                    attackAnimationTime--;
                }
            }
        } else {
            if (getAttackTarget() != null) {
                faceEntity(getAttackTarget(), 100, 100);
            }
            if (teleportCooldown > 0) {
                teleportCooldown--;
            }
        }
    }

    private void enableWatch() {
        if (!isWatching) {
            isWatching = true;
            tasks.addTask(5, watchAI);
        }
    }

    private void disableWatch() {
        if (isWatching) {
            isWatching = false;
            tasks.removeTask(watchAI);
        }
    }

    public boolean teleportNearEntity(Entity entity) {
        double targetX = entity.getX() + (rand.nextDouble() - 0.5D) * 6.0D;
        double targetY = entity.getY() + (rand.nextInt(3) - 1);
        double targetZ = entity.getZ() + (rand.nextDouble() - 0.5D) * 6.0D;
        double x = posX;
        double y = posY;
        double z = posZ;
        boolean successful = false;
        BlockPos pos = this.getPosition();
        if (world.isBlockLoaded(pos)) {
            boolean validBlock = false;
            while (!validBlock && pos.getY() > 0) {
                BlockState block = world.getBlockState(pos.below());
                if (block.getMaterial().blocksMovement()) {
                    validBlock = true;
                } else {
                    pos = pos.below();
                }
            }
            if (validBlock) {
                teleportCooldown = rand.nextInt(20 * 2) + 20 * 2;
                EntityDarkDruid newDruid = new EntityDarkDruid(world);
                newDruid.copyDataFromOld(this);
                newDruid.putUUID(UUID.randomUUID());
                newDruid.setPosition(targetX, targetY, targetZ);
                newDruid.faceEntity(entity, 100, 100);
                newDruid.setAttackTarget(this.getAttackTarget());
                newDruid.attackDelayCounter = MIN_ATTACK_DELAY + this.random.nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
                if (world.getCollisionBoxes(newDruid, newDruid.getBoundingBox()).isEmpty() && !world.containsAnyLiquid(newDruid.getBoundingBox())) {
                    successful = true;
                    remove();
                    world.spawnEntity(newDruid);
                    druidParticlePacketOrigin();
                    druidParticlePacketTarget(newDruid);
                    
                    this.playSound(SoundRegistry.DRUID_TELEPORT, 1.0F, 1.0F);
                    newDruid.playSound(SoundRegistry.DRUID_TELEPORT, 1.0F, 1.0F);
                } else
                    newDruid.remove();
            }
        }

        if (successful) {
            return true;
        }
        setPosition(x, y, z);
        return false;
    }

    private void druidParticlePacketTarget(EntityDarkDruid newDruid) {
        World world = this.world;
        if (world instanceof ServerWorld) {
            int dim = ((ServerWorld) world).provider.getDimension();
            TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidTeleportParticles(newDruid), new NetworkRegistry.TargetPoint(dim, newDruid.getX() + 0.5D, newDruid.getY() + 1.0D, newDruid.getZ() + 0.5D, 64D));
        }
    }

    private void druidParticlePacketOrigin() {
        World world = this.world;
        if (world instanceof ServerWorld) {
            int dim = ((ServerWorld) world).provider.getDimension();
            TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidTeleportParticles(this), new NetworkRegistry.TargetPoint(dim, this.getX() + 0.5D, this.getY() + 1.0D, this.getZ() + 0.5D, 64D));
        }
    }

    public void spawnParticles() {
        double yaw = yRot * MathUtils.DEG_TO_RAD;
        double y = Math.cos(-xRot * MathUtils.DEG_TO_RAD);
        double offsetX = -Math.sin(yaw) * 0.5D * y;
        double offsetY = 1.2 - Math.sin(-xRot * MathUtils.DEG_TO_RAD) * 0.5D * y;
        double offsetZ = Math.cos(yaw) * 0.5D * y;
        double motionX = -Math.sin(yaw) * y * 0.2 * (rand.nextDouble() * 0.7 + 0.3) + rand.nextDouble() * 0.05 - 0.025;
        double motionY = Math.sin(-xRot * MathUtils.DEG_TO_RAD) + rand.nextDouble() * 0.25 - 0.125;
        double motionZ = Math.cos(yaw) * y * 0.2 * (rand.nextDouble() * 0.7 + 0.3) + rand.nextDouble() * 0.05 - 0.025;
        BLParticles.DRUID_CASTING.spawn(world, posX + offsetX, posY + offsetY, posZ + offsetZ, ParticleFactory.ParticleArgs.get().withMotion(motionX, motionY, motionZ).withScale(rand.nextFloat() + 0.5F));
    }

    public void chargeSpell(Entity entity) {
        if (entity.getDistance(this) <= 4) {
            double dx = entity.getX() - this.getX();
            double dz = entity.getZ() - this.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            entity.motionX = 1.5 * dx / len;
            entity.motionZ = 1.5 * dz / len;
        } else {
            entity.motionX = 0;
            entity.motionZ = 0;
        }
        entity.motionY = 0.1;
        entity.velocityChanged = true;
    }

    public void castSpell(Entity entity) {
        double dx = entity.getX() - this.getX();
        double dz = entity.getZ() - this.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        entity.motionX = 0.5 * dx / len;
        entity.motionZ = 0.5 * dz / len;
        entity.motionY = 1.05D;
        entity.velocityChanged = true;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.DARK_DRUID;
    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && !this.world.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.0F;
    }

    @Override
    protected boolean isValidLightLevel() {
        return true;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 5;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.DARK_DRUID_LIVING;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundRegistry.DARK_DRUID_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.DARK_DRUID_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1;
    }

    public int getAttackCounter() {
        return attackCounter;
    }

    private void copyDataFromOld(Entity entity) {
        CompoundNBT compound = entity.save(new CompoundNBT());
        compound.removeTag("Dimension");
        this.readFromNBT(compound);
    }

    public void startCasting() {
        dataManager.set(CASTING, true);
        enableWatch();
    }

    public void stopCasting() {
        dataManager.set(CASTING, false);
        disableWatch();
    }


    public boolean isCasting() {
        return dataManager.get(CASTING);
    }

    public boolean canTeleport() {
        return !isCasting() && teleportCooldown == 0;
    }

    public float getAttackAnimationTime(float partialRenderTicks) {
        return (prevAttackAnimationTime + (attackAnimationTime - prevAttackAnimationTime) * partialRenderTicks) / MAX_ATTACK_ANIMATION_TIME;
    }

    @Override
    public void writeEntityToNBT(CompoundNBT tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.putInt("Teleport", teleportCooldown);
    }

    @Override
    public void readEntityFromNBT(CompoundNBT tagCompound) {
        super.readEntityFromNBT(tagCompound);
        teleportCooldown = tagCompound.getInt("Teleport");
    }
}
