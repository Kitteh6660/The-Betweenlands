package thebetweenlands.common.tile.spawner;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class MobSpawnerLogicBetweenlands {
    private final List<WeightedSpawnerEntity> entitySpawnList = new ArrayList<WeightedSpawnerEntity>();
    public double entityRotation;
    public double lastEntityRotation;
    private WeightedSpawnerEntity randomEntity = new WeightedSpawnerEntity();
    private Entity cachedEntity;
    private int spawnDelay = 20;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    private int maxNearbyEntities = 6;
    private int activatingRangeFromPlayer = 16;
    private int spawnRange = 4;
    private double checkRange = 8.0D;
    private boolean hasParticles = true;
    private boolean spawnInAir = true;

    /**
     * Gets the entity name that should be spawned.
     */
    @Nullable
    public ResourceLocation getEntityId() {
        String s = this.randomEntity.getTag().getString("id");
        return StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
    }

    /**
     * Sets the entity name. Does not override NBT
     *
     * @param name
     * @return
     */
    public MobSpawnerLogicBetweenlands setNextEntityName(String name) {
        this.randomEntity.getTag().putString("id", name);
        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isClientSide()) {
            this.cachedEntity = null;
        }
        return this;
    }

    /**
     * Sets the next entity to spawn
     *
     * @param entity
     */
    public MobSpawnerLogicBetweenlands setNextEntity(WeightedSpawnerEntity entity) {
        this.randomEntity = entity;
        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isClientSide()) {
            this.cachedEntity = null;
        }
        return this;
    }

    /**
     * Sets the next entity to spawn. Overrides NBT
     *
     * @param name
     */
    public MobSpawnerLogicBetweenlands setNextEntity(String name) {
        this.randomEntity = new WeightedSpawnerEntity();
        this.setNextEntityName(name);
        return this;
    }

    /**
     * Sets the entity spawn list
     *
     * @param entitySpawnList
     * @return
     */
    public MobSpawnerLogicBetweenlands setEntitySpawnList(List<WeightedSpawnerEntity> entitySpawnList) {
        this.entitySpawnList.clear();
        this.entitySpawnList.addAll(entitySpawnList);
        if (!this.entitySpawnList.isEmpty()) {
            this.setNextEntity((WeightedSpawnerEntity) WeightedRandom.getRandomItem(this.getSpawnerWorld().random, this.entitySpawnList));
        } else {
            this.setNextEntity(new WeightedSpawnerEntity());
        }
        return this;
    }

    /**
     * Sets whether entities can spawn in the air
     *
     * @param spawnInAir
     * @return
     */
    public MobSpawnerLogicBetweenlands setSpawnInAir(boolean spawnInAir) {
        this.spawnInAir = spawnInAir;
        return this;
    }

    /**
     * Returns whether entities can spawn in the air
     *
     * @return
     */
    public boolean canSpawnInAir() {
        return this.spawnInAir;
    }

    /**
     * Sets whether the spawner creates particles
     *
     * @param hasParticles
     * @return
     */
    public MobSpawnerLogicBetweenlands setParticles(boolean hasParticles) {
        this.hasParticles = hasParticles;
        return this;
    }

    /**
     * Returns whether the spawner creates particles
     *
     * @return
     */
    public boolean hasParticles() {
        return this.hasParticles;
    }

    /**
     * Returns the maximum allowed entities within the spawn radius
     *
     * @return
     */
    public int getMaxEntities() {
        return this.maxNearbyEntities;
    }

    /**
     * Sets the maximum allowed entities within the check radius
     *
     * @param maxEntities
     * @return
     */
    public MobSpawnerLogicBetweenlands setMaxEntities(int maxEntities) {
        this.maxNearbyEntities = maxEntities;
        return this;
    }

    /**
     * Sets the spawn delay range
     *
     * @param minDelay
     * @param maxDelay
     * @return
     */
    public MobSpawnerLogicBetweenlands setDelayRange(int minDelay, int maxDelay) {
        this.minSpawnDelay = minDelay;
        this.maxSpawnDelay = maxDelay;
        return this;
    }

    /**
     * Sets the current spawn delay
     *
     * @param delay
     * @return
     */
    public MobSpawnerLogicBetweenlands setDelay(int delay) {
        this.spawnDelay = delay;
        return this;
    }

    /**
     * Returns the mimumum spawn delay
     *
     * @return
     */
    public int getMinDelay() {
        return this.minSpawnDelay;
    }

    /**
     * Returns the maximum spawn delay
     *
     * @return
     */
    public int getMaxDelay() {
        return this.maxSpawnDelay;
    }

    /**
     * Returns the spawn range
     *
     * @return
     */
    public int getSpawnRange() {
        return this.spawnRange;
    }

    /**
     * Sets the spawn range
     *
     * @param range
     * @return
     */
    public MobSpawnerLogicBetweenlands setSpawnRange(int range) {
        this.spawnRange = range;
        return this;
    }

    /**
     * Returns the check range
     *
     * @return
     */
    public double getCheckRange() {
        return this.checkRange;
    }

    /**
     * Sets the check range
     *
     * @param range
     * @return
     */
    public MobSpawnerLogicBetweenlands setCheckRange(double range) {
        this.checkRange = range;
        return this;
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate it.
     */
    public boolean isActivated() {
        return this.getSpawnerWorld().getNearestPlayer((double) this.getSpawnerX() + 0.5D, (double) this.getSpawnerY() + 0.5D, (double) this.getSpawnerZ() + 0.5D, (double) this.activatingRangeFromPlayer, false) != null;
    }

    /**
     * Sets the maximum number of entities to be spawned
     * @param count
     */
    public void setMaxSpawnCount(int count) {
    	this.spawnCount = count;
    }
    
    /**
     * Returns the maximum number of entities to be spawned
     * @return
     */
    public int getMaxSpawnCount() {
    	return this.spawnCount;
    }
    
    /**
     * Updates the spawner logic
     */
    public void updateSpawner() {
        if (!this.isActivated()) {
            this.lastEntityRotation = this.entityRotation;
        } else {
            if (this.getSpawnerWorld().isClientSide()) {
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                }

                if (this.hasParticles()) {
                    this.spawnParticles();
                }

                this.lastEntityRotation = this.entityRotation;
                this.entityRotation = (this.entityRotation + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
            } else {
                if (this.spawnDelay == -1) {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    return;
                }

                int spawnCount = this.spawnCount > 1 ? this.getSpawnerWorld().random.nextInt(this.spawnCount) + 1 : this.spawnCount;

                boolean entitySpawned = false;

                for (int i = 0; i < 128; ++i) {
                    if (spawnCount <= 0) {
                        break;
                    }

                    double rx = 1.0D - this.getSpawnerWorld().random.nextDouble() * 2.0D;
                    double ry = 1.0D - this.getSpawnerWorld().random.nextDouble() * 2.0D;
                    double rz = 1.0D - this.getSpawnerWorld().random.nextDouble() * 2.0D;
                    double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
                    rx = this.getSpawnerX() + rx / len * this.spawnRange;
                    ry = this.getSpawnerY() + ry / len * this.spawnRange;
                    rz = this.getSpawnerZ() + rz / len * this.spawnRange;
                    CompoundNBT entityNbt = this.randomEntity.getTag();
                    ListNBT posNbt = entityNbt.getList("Pos", 6);
                    World world = this.getSpawnerWorld();
                    int tags = posNbt.size();
                    rx = tags >= 1 ? posNbt.getDouble(0) : rx;
                    ry = tags >= 2 ? posNbt.getDouble(1) : ry;
                    rz = tags >= 3 ? posNbt.getDouble(2) : rz;
                    Entity entity = AnvilChunkLoader.readWorldEntityPos(entityNbt, world, rx, ry, rz, false);

                    if (entity == null) {
                        return;
                    }

                    List<Entity> entitiesInReach = this.getSpawnerWorld().getEntitiesOfClass(entity.getClass(), new AxisAlignedBB((double) this.getSpawnerX(), (double) this.getSpawnerY(), (double) this.getSpawnerZ(), (double) (this.getSpawnerX() + 1), (double) (this.getSpawnerY() + 1), (double) (this.getSpawnerZ() + 1)).grow(this.checkRange, this.checkRange, this.checkRange));
                    int nearbyEntities = 0;
                    for (Entity e : entitiesInReach) {
                        if (e.distanceToSqr(this.getSpawnerX() + 0.5D, this.getSpawnerY() + 0.5D, this.getSpawnerZ() + 0.5D) <= this.checkRange) {
                            nearbyEntities++;
                        }
                    }

                    if (nearbyEntities >= this.maxNearbyEntities) {
                        this.resetTimer();
                        return;
                    }

                    entity.moveTo(rx, ry, rz, this.getSpawnerWorld().random.nextFloat() * 360.0F, 0.0F);

                    boolean canSpawn = this.canSpawnInAir() || entity.getBoundingBox() == null;

                    //Check if entity can stand on block below and set position
                    if (!canSpawn) {
                        BlockPos down = new BlockPos(rx, ry, rz).below();
                        BlockState blockState = this.getSpawnerWorld().getBlockState(down);
                        if (blockState.getBlock() != Blocks.AIR) {
                            AxisAlignedBB boundingBox = blockState.getCollisionShape(this.getSpawnerWorld(), down);
                            if (boundingBox != null) {
                                boundingBox = boundingBox.offset(down);
                                AxisAlignedBB entityBoundingBox = entity.getBoundingBox();
                                if (boundingBox.intersects(entityBoundingBox.minX, boundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, boundingBox.maxY, entityBoundingBox.maxZ)) {
                                    RayTraceResult intercept = boundingBox.calculateIntercept(entity.getPositionVector(), entity.getPositionVector().add(0, -2, 0));
                                    if (intercept != null) {
                                        canSpawn = true;
                                        entity.moveTo(entity.getX(), intercept.hitVec.y + 0.1D, entity.getZ(), entity.yRot, entity.xRot);
                                    }
                                }
                            }
                        }
                    }

                    if (canSpawn) {
                        MobEntity entityLiving = entity instanceof MobEntity ? (MobEntity) entity : null;

                        if (entityLiving == null || ForgeEventFactory.canEntitySpawnSpawner(entityLiving, getSpawnerWorld(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ())) {
                            if (entityLiving != null) {
                                if (!ForgeEventFactory.doSpecialSpawn(entityLiving, this.getSpawnerWorld(), (float) entity.getX(), (float) entity.getY(), (float) entity.getZ())) {
                                    ((MobEntity) entity).onInitialSpawn(this.getSpawnerWorld().getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
                                }
                            }

                            AnvilChunkLoader.addFreshEntity(entity, this.getSpawnerWorld());
                            this.getSpawnerWorld().playEvent(2004, entity.getPosition(), 0);

                            if (entityLiving != null) {
                                entityLiving.spawnExplosionParticle();
                            }

                            spawnCount--;

                            entitySpawned = true;
                        }
                    }
                }

                if (entitySpawned) {
                    this.resetTimer();
                }
            }
        }
    }

    /**
     * Spawns the particles
     */
    protected void spawnParticles() {
    	World world = this.getSpawnerWorld();
        double rx = (double) (world.random.nextFloat());
        double ry = (double) (world.random.nextFloat());
        double rz = (double) (world.random.nextFloat());
        double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
        BLParticles.SPAWNER.spawn(this.getSpawnerWorld(),
                (float) this.getSpawnerX() + rx, (float) this.getSpawnerY() + ry, (float) this.getSpawnerZ() + rz,
                ParticleFactory.ParticleArgs.get().withMotion((rx - 0.5D) / len * 0.05D, (ry - 0.5D) / len * 0.05D, (rz - 0.5D) / len * 0.05D));
    }

    /**
     * Spawns an entity in the world
     *
     * @param entity
     * @return
     */
    public Entity addFreshEntity(Entity entity) {
        ((MobEntity) entity).onInitialSpawn(this.getSpawnerWorld().getCurrentDifficultyAt(new BlockPos(entity)), (IEntityLivingData) null);
        this.getSpawnerWorld().addFreshEntity(entity);
        return entity;
    }

    /**
     * Resets the timer
     */
    public void resetTimer() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().random.nextInt(i);
        }

        if (!this.entitySpawnList.isEmpty()) {
            this.setNextEntity((WeightedSpawnerEntity) WeightedRandom.getRandomItem(this.getSpawnerWorld().random, this.entitySpawnList));
        }

        this.broadcastEvent(1);
    }

    public void load(BlockState state, CompoundNBT nbt) {
        CompoundNBT entityNbt = nbt.getCompound("SpawnData");
        if (!entityNbt.contains("id", 8)) {
            entityNbt.putString("id", "Pig");
        }
        this.setNextEntity(new WeightedSpawnerEntity(1, entityNbt));
        this.spawnDelay = nbt.getShort("Delay");
        this.entitySpawnList.clear();
        if (nbt.contains("SpawnPotentials", 9)) {
            ListNBT nbttaglist = nbt.getList("SpawnPotentials", 10);
            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.entitySpawnList.add(new WeightedSpawnerEntity(nbttaglist.getCompound(i)));
            }
        }
        if (nbt.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }
        if (nbt.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
        }
        if (nbt.contains("SpawnRange", 99)) {
            this.spawnRange = nbt.getShort("SpawnRange");
        }
        if (nbt.contains("CheckRange", 99)) {
            this.checkRange = nbt.getDouble("CheckRange");
        }
        if (nbt.contains("HasParticles")) {
            this.hasParticles = nbt.getBoolean("HasParticles");
        }
        if (nbt.contains("SpawnInAir")) {
            this.spawnInAir = nbt.getBoolean("SpawnInAir");
        }
        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isClientSide()) {
            this.cachedEntity = null;
        }
    }

    public void save(CompoundNBT nbt) {
        nbt.put("SpawnData", this.randomEntity.getTag().copy());
        ListNBT entityNbtList = new ListNBT();
        if (this.entitySpawnList.isEmpty()) {
            entityNbtList.appendTag(this.randomEntity.toCompoundTag());
        } else {
            for (WeightedSpawnerEntity weightedspawnerentity : this.entitySpawnList) {
                entityNbtList.appendTag(weightedspawnerentity.toCompoundTag());
            }
        }
        nbt.put("SpawnPotentials", entityNbtList);
        nbt.putShort("Delay", (short) this.spawnDelay);
        nbt.putShort("MinSpawnDelay", (short) this.minSpawnDelay);
        nbt.putShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
        nbt.putShort("SpawnCount", (short) this.spawnCount);
        nbt.putShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
        nbt.putShort("RequiredPlayerRange", (short) this.activatingRangeFromPlayer);
        nbt.putShort("SpawnRange", (short) this.spawnRange);
        nbt.putDouble("CheckRange", this.checkRange);
        nbt.putBoolean("HasParticles", this.hasParticles);
        nbt.putBoolean("SpawnInAir", this.spawnInAir);
    }

    /**
     * Sets the delay to minDelay if parameter given is 1, else return false.
     */
    public boolean setDelayToMin(int event) {
        if (event == 1 && this.getSpawnerWorld().isClientSide()) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Entity getCachedEntity() {

        if (this.cachedEntity == null) {
            this.cachedEntity = AnvilChunkLoader.readWorldEntity(this.randomEntity.getNbt(), this.getSpawnerWorld(), false);

            if (this.randomEntity.getTag().size() == 1 && this.randomEntity.getTag().contains("id", 8) && this.cachedEntity instanceof MobEntity) {
                ((MobEntity) this.cachedEntity).onInitialSpawn(this.getSpawnerWorld().getCurrentDifficultyAt(new BlockPos(this.cachedEntity)), (IEntityLivingData) null);
            }
        }

        return this.cachedEntity;
    }

    public abstract void broadcastEvent(int event);

    public abstract World getSpawnerWorld();

    public abstract int getSpawnerX();

    public abstract int getSpawnerY();

    public abstract int getSpawnerZ();
}
