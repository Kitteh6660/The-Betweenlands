package thebetweenlands.common.entity;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.structure.BlockCompactedMudSlope;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EntityCCGroundSpawner extends EntityProximitySpawner {
	private static final byte EVENT_GOOP_PARTICLES = 100;
	
	private static final DataParameter<Boolean> IS_WORLD_SPANWED = EntityDataManager.createKey(EntityCCGroundSpawner.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SPAWN_COUNT = EntityDataManager.createKey(EntityCCGroundSpawner.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> CAN_BE_REMOVED_SAFELY = EntityDataManager.createKey(EntityCCGroundSpawner.class, DataSerializers.BOOLEAN);
	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper(null);

	public EntityCCGroundSpawner(World world) {
		super(world);
		setSize(3F, 0.5F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_WORLD_SPANWED, true);
		this.entityData.define(SPAWN_COUNT, 0);
		this.entityData.define(CAN_BE_REMOVED_SAFELY, false);
	}

	@Override
	public boolean getCanSpawnHere() {
		int solidCount = 0;
		BlockPos pos = new BlockPos(this);

		if(pos.getY() < WorldProviderBetweenlands.CAVE_START) {
			return false;
		}

		for(int xo = -1; xo <= 1; xo++) {
			for(int zo = -1; zo <= 1; zo++) {
				BlockPos offsetPos = pos.offset(xo, 0, zo);
				BlockState state = this.world.getBlockState(offsetPos);

				if(state.getMaterial().isLiquid()) {
					return false;
				}

				if(SurfaceType.MIXED_GROUND.apply(state)) {
					solidCount++;
				} else if(xo == 0 && zo == 0) {
					return false;
				}

				if(!this.world.isEmptyBlock(offsetPos.above())) {
					return false;
				}
			}
		}

		return solidCount >= 6;
	}

	@Override
	public boolean isNotColliding() {
		return true;
	}

	@Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }
/*
	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getBoundingBox();
	}
*/
	@Override
	public void tick() {
		super.tick();

		if (!level.isClientSide()) {
			if(isWorldSpawned() && !isSpawnEventActive(level))
				remove();

			if (level.getGameTime() % 60 == 0)
				checkArea();
			List<EntityFallingBlock> listPlug = level.getEntitiesOfClass(EntityFallingBlock.class, getBoundingBox());
			if (!listPlug.isEmpty()) {
				level.setBlockToAir(getPosition());
				remove();
			}
		}

		this.setPosition(MathHelper.floor(this.getX()) + 0.5D, MathHelper.floor(this.getY()), MathHelper.floor(this.getZ()) + 0.5D);
		this.xOld = this.lastTickPosX = this.getX();
		this.yOld = this.lastTickPosY = this.getY();
		this.zOld = this.lastTickPosZ = this.getZ();
	}

	public boolean isSpawnEventActive(World world) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
        if(worldStorage.getEnvironmentEventRegistry().bloodSky.isActive())
            return true;
        return false;
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		if (!level.isClientSide()) {
			if(getCanBeRemovedSafely() && canBeRemovedNow())
				remove();
			if (level.getDifficulty() != EnumDifficulty.PEACEFUL) {
				if(isWorldSpawned() && !isSpawnEventActive(level))
					return null;
				List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, proximityBox());
				if(list.stream().filter(e -> e instanceof EntityCryptCrawler).count() >= 4)
					return null;
				for (int entityCount = 0; entityCount < list.size(); entityCount++) {
					Entity entity = list.get(entityCount);
					if (entity != null)
						if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isSpectator() && !((PlayerEntity) entity).isCreative()) {
							if (canSneakPast() && entity.isCrouching())
								return null;
							else if (checkSight() && !canEntityBeSeen(entity) || getCanBeRemovedSafely())
								return null;
							else {
								for (int count = 0; count < getEntitySpawnCount(); count++) {
									Entity spawn = getEntitySpawned();
									if (spawn != null) {
										performPreSpawnaction(entity, spawn);
										if (!spawn.isDead) // just in case of pre-emptive removal
											level.spawnEntity(spawn);
										performPostSpawnaction(entity, spawn);
									}
								}
							}
						}
				}
			}
		}
		return null;
	}

    public boolean canBeRemovedNow() {
    	AxisAlignedBB dead_zone = getBoundingBox().grow(0D, 1D, 0D).offset(0D, -0.5D, 0D);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, dead_zone);
		if(list.stream().filter(e -> e instanceof EntityCryptCrawler).count() >= 1)
			return false;
        return true;
    }

	@Override
    public float getEyeHeight() {
        return height + 0.5F; // sort of needed so it can see a bit further
    }
/*
	@Override
	protected boolean isMovementBlocked() {
		return true;
	}
*/
	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void onKillCommand() {
		this.remove();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof PlayerEntity && ((PlayerEntity) sourceEntity).isCreative()) {
				setCanBeRemovedSafely(true);
			}
		}
		return false;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (entity instanceof EntityFallingBlock)
			if (!level.isClientSide())
				setCanBeRemovedSafely(true);
	}

	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(isWorldSpawned())
			setSpawnCount(getSpawnCount() + 1);
		level.playSound((PlayerEntity)null, getPosition(), getDigSound(), SoundCategory.HOSTILE, 0.5F, 1.0F);
		entitySpawned.setPosition(getPosition().getX() + 0.5F, getPosition().getY() - 1.5F, getPosition().getZ() + 0.5F);
	}

	protected SoundEvent getDigSound() {
		return SoundRegistry.CRYPT_CRAWLER_DIG;
	}

	@Override
	protected void performPostSpawnaction(Entity targetEntity, @Nullable Entity entitySpawned) {
		if(!level.isClientSide()) {
			this.world.setEntityState(this, EVENT_GOOP_PARTICLES);
			
			entitySpawned.motionY += 0.5D;
			if(isWorldSpawned() && getSpawnCount() >= maxUseCount())
				setCanBeRemovedSafely(true);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_GOOP_PARTICLES) {
			for (int count = 0; count <= 200; ++count) {
				Particle fx = new ParticleBreaking.SnowballFactory().createParticle(EnumParticleTypes.SNOWBALL.getParticleID(), world, this.getX() + (world.rand.nextDouble() - 0.5D) , this.getY() + world.rand.nextDouble() + 0.25F, this.getZ() + (world.rand.nextDouble() - 0.5D), 0, 0, 0, 0);
				fx.setRBGColorF(48F, 64F, 91F);
				Minecraft.getInstance().effectRenderer.addEffect(fx);
			}
		}
	}
	
	@Override
	protected float getProximityHorizontal() {
		return 8F;
	}

	@Override
	protected float getProximityVertical() {
		return 2F;
	}

	@Override
	protected boolean canSneakPast() {
		return false;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		EntityCryptCrawler crawler = new EntityCryptCrawler(level);
		crawler.onInitialSpawn(level.getDifficultyForLocation(getPosition()), null);
		return crawler;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return false;
	}

	@Override
	protected int maxUseCount() {
		return 5;
	}

	public void setIsWorldSpawned(boolean world_spawned) {
		dataManager.set(IS_WORLD_SPANWED, world_spawned);
	}

	public boolean isWorldSpawned() {
		return dataManager.get(IS_WORLD_SPANWED);
	}

	public void setSpawnCount(int spawn_count) {
		dataManager.set(SPAWN_COUNT, spawn_count);
	}

	public int getSpawnCount() {
		return dataManager.get(SPAWN_COUNT);
	}

	public void setCanBeRemovedSafely(boolean safe) {
		dataManager.set(CAN_BE_REMOVED_SAFELY, safe);
	}

	public boolean getCanBeRemovedSafely() {
		return dataManager.get(CAN_BE_REMOVED_SAFELY);
	}

	@Override
    public void remove() {
		if(!level.isClientSide()) {
			if(isWorldSpawned())
				if(getEntityData().contains("tempBlockTypes"))
					loadOriginBlocks(level, getEntityData());
		}
        super.remove();
    }

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!level.isClientSide()) {
			getOriginBlocks(level, getPosition());
			level.setBlockState(getPosition(), blockHelper.AIR);
			level.setBlockState(getPosition().add(0, -1, 0), blockHelper.COMPACTED_MUD);
			level.setBlockState(getPosition().add(-1, 0, -1), blockHelper.COMPACTED_MUD_SLOPE.setValue(BlockCompactedMudSlope.FACING, Direction.NORTH).setValue(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			level.setBlockState(getPosition().add(0, 0, -1), blockHelper.COMPACTED_MUD_SLOPE.setValue(BlockCompactedMudSlope.FACING, Direction.NORTH).setValue(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			level.setBlockState(getPosition().add(1, 0, -1), blockHelper.COMPACTED_MUD_SLOPE.setValue(BlockCompactedMudSlope.FACING, Direction.NORTH).setValue(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			level.setBlockState(getPosition().add(-1, 0, 1), blockHelper.COMPACTED_MUD_SLOPE.setValue(BlockCompactedMudSlope.FACING, Direction.SOUTH).setValue(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			level.setBlockState(getPosition().add(0, 0, 1), blockHelper.COMPACTED_MUD_SLOPE.setValue(BlockCompactedMudSlope.FACING, Direction.SOUTH).setValue(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			level.setBlockState(getPosition().add(1, 0, 1), blockHelper.COMPACTED_MUD_SLOPE.setValue(BlockCompactedMudSlope.FACING, Direction.SOUTH).setValue(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			level.setBlockState(getPosition().add(-1, 0, 0), blockHelper.COMPACTED_MUD_SLOPE.setValue(BlockCompactedMudSlope.FACING, Direction.WEST).setValue(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			level.setBlockState(getPosition().add(1, 0, 0), blockHelper.COMPACTED_MUD_SLOPE.setValue(BlockCompactedMudSlope.FACING, Direction.EAST).setValue(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
		}
		return livingdata;
	}

	private void getOriginBlocks(World world, BlockPos pos) {
		ListNBT tagList = new ListNBT();
		CompoundNBT entityNbt = getEntityData();
		for (int x = -1; x <= 1; x ++)
			for (int z = -1; z <= 1; z++) 
				for(int y = 0; y <= 1; y++) {
				BlockState state = world.getBlockState(pos.offset(x, -y, z));
				CompoundNBT tag = new CompoundNBT();
				NBTUtil.writeBlockState(tag, state);
				tagList.appendTag(tag);
			}

		if (!tagList.isEmpty()) {
			entityNbt.setTag("tempBlockTypes", tagList);
			CompoundNBT nbttagcompoundPos = NBTUtil.createPosTag(pos);
			entityNbt.setTag("originPos", nbttagcompoundPos);
		}
		writeEntityToNBT(entityNbt);
	}

	public void loadOriginBlocks(World world, CompoundNBT tag) {
		CompoundNBT entityNbt = getEntityData();
		CompoundNBT nbttagcompoundPos = entityNbt.getCompoundTag("originPos");
		BlockPos origin = NBTUtil.getPosFromTag(nbttagcompoundPos);
		List<BlockState> list = new ArrayList<BlockState>();
		ListNBT tagList = entityNbt.getList("tempBlockTypes", Constants.NBT.TAG_COMPOUND);
		for (int indexCount = 0; indexCount < tagList.size(); ++indexCount) {
			CompoundNBT nbttagcompound = tagList.getCompound(indexCount);
			BlockState state = NBTUtil.readBlockState(nbttagcompound);
			list.add(indexCount, state);
		}
		int a = 0;
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++)
				for(int y = 0; y <= 1; y++) {
				world.setBlockState(origin.add(x, -y, z), list.get(a++), 3);
			}
		level.playSound((PlayerEntity)null, origin, SoundRegistry.ROOF_COLLAPSE, SoundCategory.BLOCKS, 1F, 1.0F);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		if(nbt.contains("world_spawned", Constants.NBT.TAG_BYTE))
			setIsWorldSpawned(nbt.getBoolean("world_spawned"));
		if(nbt.contains("remove_safely", Constants.NBT.TAG_BYTE))
			setCanBeRemovedSafely(nbt.getBoolean("remove_safely"));
		setSpawnCount(nbt.getInt("spawn_count"));
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putBoolean("world_spawned", isWorldSpawned());
		nbt.putBoolean("remove_safely", getCanBeRemovedSafely());
		nbt.putInt("spawn_count", getSpawnCount());
	}
}