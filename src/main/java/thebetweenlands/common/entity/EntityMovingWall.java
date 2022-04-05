package thebetweenlands.common.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.audio.MovingWallSound;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityMovingWall extends Entity implements IEntityScreenShake, IEntityAdditionalSpawnData {
	
	private static final DataParameter<Boolean> IS_NEW_SPAWN = EntityDataManager.defineId(EntityMovingWall.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> HOLD_STILL = EntityDataManager.defineId(EntityMovingWall.class, DataSerializers.BOOLEAN);

	public Entity ignoreEntity;
	private int ignoreTime;
	private int holdCount;
	public boolean playSlideSound = true;
	private int prev_shake_timer;
	private int shake_timer;
	private boolean shaking = false;
	private int shakingTimerMax = 20;
	private int impacts;

	public static final Set<Block> UNBREAKABLE_BLOCKS = new HashSet<Block>();

	static {
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_ALCOVE.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.WORM_DUNGEON_PILLAR.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_TILES.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_TILES_WATER.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS_DECAY_1.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS_DECAY_2.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS_DECAY_4.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB_DECAY_1.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB_DECAY_2.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB_DECAY_3.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB_DECAY_4.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICKS.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICKS_CARVED.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SPIKE_TRAP.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_TILES_SPIKE_TRAP.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICKS_CLIMBABLE.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_COMBINATION.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_RUNES.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER.get());
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_WALL.get());
	}
	
	protected float speed = 0.05F;
	protected boolean isBlockAligned = true;
	protected boolean isDungeonWall = false;

	public EntityMovingWall(World world) {
		super(world);
		setSize(1F, 1F);
	}
	
	/**
	 * @param world
	 * @param isDungeonWall Whether this wall is from the sludge worm dungeon. If true the wall despawns once the dungeon is defeated.
	 */
	public EntityMovingWall(World world, boolean isDungeonWall) {
		this(world);
		this.isDungeonWall = isDungeonWall;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(IS_NEW_SPAWN, true);
		this.entityData.define(HOLD_STILL, false);
	}

	@Override
	public void onKillCommand() {
		this.remove();
	}

	@Override
	public void tick() {
		super.tick();
		
		if (!level.isClientSide()) {
			if(tickCount == 1 && isNewSpawn())
				checkSpawnArea();

			if(tickCount == 2) //needs to have moved 1 tick for direction to work
				doJankSafetyCheck();

			if(isHoldingStill()) {
				holdCount--;
				if (holdCount <= 0) {
					setHoldStill(false);
					holdCount = 20;
				}
			}
		}

		calculateAllCollisions(posX, posY + 0.5D, posZ);
		calculateAllCollisions(posX, posY - 0.5D, posZ);
		calculateAllCollisions(posX, posY + 1.5D, posZ);

		if (getDirection() == Direction.NORTH || getDirection() == Direction.SOUTH) {
			calculateAllCollisions(posX - 1D, posY + 0.5D, posZ);
			calculateAllCollisions(posX - 1D, posY - 0.5D, posZ);
			calculateAllCollisions(posX - 1D, posY + 1.5D, posZ);
			calculateAllCollisions(posX + 1D, posY + 0.5D, posZ);
			calculateAllCollisions(posX + 1D, posY - 0.5D, posZ);
			calculateAllCollisions(posX + 1D, posY + 1.5D, posZ);
		} else {
			calculateAllCollisions(posX, posY + 0.5D, posZ - 1D);
			calculateAllCollisions(posX, posY - 0.5D, posZ - 1D);
			calculateAllCollisions(posX, posY + 1.5D, posZ - 1D);
			calculateAllCollisions(posX, posY + 0.5D, posZ + 1D);
			calculateAllCollisions(posX, posY - 0.5D, posZ + 1D);
			calculateAllCollisions(posX, posY + 1.5D, posZ + 1D);
		}
		if (!this.level.isClientSide() && this.impacts > 0) {
			if (this.impacts-- > 2) {
				this.remove();
				return;
			}
		}

		Direction heading = Direction.getNearest((float)motionX, 0, (float)motionZ);
		
		if(this.isBlockAligned) {
			if(heading.getAxis() != Axis.Z) {
				this.getZ() = MathHelper.floor(this.getZ()) + 0.5D;
			}
			if(heading.getAxis() != Axis.X) {
				this.getX() = MathHelper.floor(this.getX()) + 0.5D;
			}
		}
		
		if(!isHoldingStill()) {
			motionY = 0;
			motionX = heading.getStepX() * speed;
			motionZ = heading.getStepZ() * speed;
			
			posX += motionX;
			posY += motionY;
			posZ += motionZ;

			this.pushEntitiesAway();
		}

		xRot = 0;
		yRot = (float) (MathHelper.atan2(-motionX, motionZ) * (180D / Math.PI));
		setPosition(posX, posY, posZ);
		setEntityBoundingBox(getCollisionBoundingBox());

		if (isShaking())
			shake(10);

		if (level.isClientSide()) {
			if (isHoldingStill())
				if (!playSlideSound)
					playSlideSound = true;

			if (!isHoldingStill())
				if (playSlideSound) {
					playSlidingSound(level, getPosition());
					playSlideSound = false;
				}
		}
		
		//Remove wall if it is a dungeon wall and the dungeon is defeated
		if(!this.level.isClientSide() && this.isDungeonWall) {
			List<LocationSludgeWormDungeon> dungeons = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSludgeWormDungeon.class, this.getBoundingBox(), l -> true);
			
			if(dungeons.isEmpty()) {
				this.remove();
			} else {
				for(LocationSludgeWormDungeon dungeon : dungeons) {
					if(dungeon.isDefeated()) {
						this.remove();
						break;
					}
				}
			}
		}
	}

	protected void pushEntitiesAway() {
		boolean collision = false;

		double maxReverseX = -1;
		double maxReverseZ = -1;

		AxisAlignedBB collisionAABB = this.getCollisionBoundingBox();
		if(collisionAABB != null) {
			List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, collisionAABB);

			for(Entity entity : entities) {
				if(entity.canBeCollidedWith()) {
					if(!entity.canBePushed() && entity instanceof EntityMovingWall == false) {
						collision = true;
					} else {
						AxisAlignedBB entityAABB = entity.getBoundingBox();
						boolean squished = false;
						double dx = Math.max(collisionAABB.minX - entityAABB.maxX, entityAABB.minX - collisionAABB.maxX);
						double dz = Math.max(collisionAABB.minZ - entityAABB.maxZ, entityAABB.minZ - collisionAABB.maxZ);

						if(Math.abs(dz) < Math.abs(dx)) {
							entity.move(MoverType.PISTON, 0, 0, (dz - 0.005D) * Math.signum(this.getZ() - entity.getZ()));
							entityAABB = entity.getBoundingBox();
							dz = Math.max(collisionAABB.minZ - entityAABB.maxZ, entityAABB.minZ - collisionAABB.maxZ);

							if(-dz > 0.025D) {
								squished = true;
								maxReverseZ = Math.max(-dz, maxReverseZ);
							}
						} else {
							entity.move(MoverType.PISTON, (dx - 0.005D) * Math.signum(this.getX() - entity.getX()), 0, 0);
							entityAABB = entity.getBoundingBox();
							dx = Math.max(collisionAABB.minX - entityAABB.maxX, entityAABB.minX - collisionAABB.maxX);

							if(-dx > 0.025D) {
								squished = true;
								maxReverseX = Math.max(-dx, maxReverseX);
							}
						}

						//Move slightly towards ground to update onGround state etc.
						entity.move(MoverType.PISTON, 0, -0.01D, 0);

						if(squished) {
							collision = true;

							if(!this.level.isClientSide()) {
								entity.hurt(DamageSource.IN_WALL, 10F);
								setHoldStill(true);
								holdCount = 20;
								level.playSound(null, getPosition(), SoundRegistry.WALL_SLAM, SoundCategory.HOSTILE, 0.5F, 0.75F);
							}
						}
					}
				}
			}
		}

		if(collision) {
			if(maxReverseZ > 0) {
				this.getZ() -= (maxReverseZ + 0.05D) * Math.signum(this.motionZ);
			}
			if(maxReverseX > 0) {
				this.getX() -= (maxReverseX + 0.05D) * Math.signum(this.motionX);
			}

			shaking = true;
			shake_timer = 0;
			
			if(!this.level.isClientSide()) {
				motionX *= -1D;
				motionZ *= -1D;
				velocityChanged = true;
			}
		}
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	private void checkSpawnArea() {
		BlockPos posEntity = getPosition();
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(posEntity.add(-1F, -1F, -1F), posEntity.add(1F, 1F, 1F));
		for (BlockPos pos : blocks) {
			if (isUnbreakableBlock(level.getBlockState(pos))) {
				remove();
			}
		}
		if(isUnbreakableBlock(level.getBlockState(posEntity.add(2, 0, 0))) && isUnbreakableBlock(level.getBlockState(posEntity.add(-2, 0, 0)))) {
			motionZ = this.speed;
			motionX = motionY = 0;
			setIsNewSpawn(false);
		}
		else if(isUnbreakableBlock(level.getBlockState(posEntity.add(0, 0, 2))) && isUnbreakableBlock(level.getBlockState(posEntity.add(0, 0, -2)))) {
			motionX = this.speed;
			motionY = motionZ = 0;
			setIsNewSpawn(false);
		}	
		else {
			remove();
		}
	}

	private void doJankSafetyCheck() {
		Direction facing = getDirection();
		Vector3d vec3d = new Vector3d(getPosition());
		Vector3d vec3d1 = new Vector3d(getPosition().offset(facing, 28)); //should be long enough
		RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1);
		if (raytraceresult != null) {
			vec3d1 = new Vector3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
			BlockPos hitpos = new BlockPos(vec3d1);
			AxisAlignedBB rayBox = new AxisAlignedBB(getPosition(), hitpos);
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, rayBox);
			for (int entityCount = 0; entityCount < list.size(); ++entityCount) {
				Entity entity = list.get(entityCount);
				if (entity != null && entity instanceof EntityMovingWall)
					entity.remove();
			}
		}
	}

	public void calculateAllCollisions(double posX, double posY, double posZ) {
		Vector3d vec3d = new Vector3d(posX, posY, posZ);
		Vector3d vec3d1 = new Vector3d(posX + motionX * 12D, posY + motionY, posZ + motionZ * 12D); //adjust multiplier higher for slower speeds
		RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1);
		vec3d = new Vector3d(posX, posY, posZ);
		vec3d1 = new Vector3d(posX + motionX, posY + motionY, posZ + motionZ);

		if (raytraceresult != null)
			vec3d1 = new Vector3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);

		Entity entity = null;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getCollisionBoundingBox().expand(motionX, motionY, motionZ).inflate(1.0D));
		double d0 = 0.0D;
		boolean ignore = false;

		for (int entityCount = 0; entityCount < list.size(); ++entityCount) {
			Entity entity1 = list.get(entityCount);

			if (entity1.canBeCollidedWith()) {
				if (entity1 == ignoreEntity)
					ignore = true;
				else if (tickCount < 2 && ignoreEntity == null) {
					ignoreEntity = entity1;
					ignore = true;
				} else {
					ignore = false;
					AxisAlignedBB axisalignedbb = entity1.getBoundingBox().inflate(0.30000001192092896D);
					RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

					if (raytraceresult1 != null) {
						double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

						if (d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}
		}

		if (ignoreEntity != null) {
			if (ignore)
				ignoreTime = 2;
			else if (ignoreTime-- <= 0)
				ignoreEntity = null;
		}

		if (entity != null)
			raytraceresult = new RayTraceResult(entity);

		if (raytraceresult != null)
			onImpact(raytraceresult);
	}

	protected void onImpact(RayTraceResult result) {
		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockState state = level.getBlockState(result.getBlockPos());
			if (isUnbreakableBlock(state)) { // not sure of all the different states so default will do
				if (result.sideHit.getIndex() == 2 || result.sideHit.getIndex() == 3) {
					shaking = true;
					shake_timer = 0;
					motionZ *= -1D;
					velocityChanged = true;
					if(!level.isClientSide()) {
						setHoldStill(true);
						holdCount = 20;
						level.playSound(null, getPosition(), SoundRegistry.WALL_SLAM, SoundCategory.HOSTILE, 0.5F, 0.75F);
					}
				} else if (result.sideHit.getIndex() == 4 || result.sideHit.getIndex() == 5) {
					shaking = true;
					shake_timer = 0;
					motionX *= -1D;
					velocityChanged = true;
					if(!level.isClientSide()) {
						setHoldStill(true);
						holdCount = 20;
						level.playSound(null, getPosition(), SoundRegistry.WALL_SLAM, SoundCategory.HOSTILE, 0.5F, 0.75F);
					}
				}
				this.impacts += 2;
			}
			else {
				if (state.getBlock() != Blocks.BEDROCK) {
					level.destroyBlock(result.getBlockPos(), false);
					level.notifyNeighborsOfStateChange(result.getBlockPos(), state.getBlock(), true);
				}
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void playSlidingSound(World world, BlockPos pos) {
		ISound wall_slide = new MovingWallSound(this);
		Minecraft.getInstance().getSoundHandler().playSound(wall_slide);
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		//No regular moving
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		if (isHoldingStill()) {
			motionX = 0;
			motionY = 0;
			motionZ = 0;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		if (getDirection() == Direction.NORTH || getDirection() == Direction.SOUTH)
			return new AxisAlignedBB(posX - 0.5D, posY - 0.5D, posZ - 0.5D, posX + 0.5D, posY + 0.5D, posZ + 0.5D).inflate(1D, 1D, 0D).offset(0D, 0.5D, 0D);
		return new AxisAlignedBB(posX - 0.5D, posY - 0.5D, posZ - 0.5D, posX + 0.5D, posY + 0.5D, posZ + 0.5D).inflate(0D, 1D, 1D).offset(0D, 0.5D, 0D);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return getBoundingBox();
	}

	public void setIsNewSpawn(boolean new_spawn) {
		entityData.set(IS_NEW_SPAWN, new_spawn);
	}

	public boolean isNewSpawn() {
		return entityData.get(IS_NEW_SPAWN);
	}

	private void setHoldStill(boolean hold_still) {
		entityData.set(HOLD_STILL, hold_still);
	}

	public boolean isHoldingStill() {
		return entityData.get(HOLD_STILL);
	}

	public boolean isMoving() {
		return !isHoldingStill();
	}

	public boolean isUnbreakableBlock(BlockState state) {
		return UNBREAKABLE_BLOCKS.contains(state.getBlock()) || BetweenlandsConfig.GENERAL.movingWallBlacklist.isListed(state);
	}

	@Override
	public void load(CompoundNBT nbt) {
		if(nbt.contains("new_spawn", Constants.NBT.TAG_BYTE)) {
			setIsNewSpawn(nbt.getBoolean("new_spawn"));
		}
		
		if(nbt.contains("isBlockAligned", Constants.NBT.TAG_BYTE)) {
			this.isBlockAligned = nbt.getBoolean("isBlockAligned");
		}
		
		if(nbt.contains("isDungeonWall", Constants.NBT.TAG_BYTE)) {
			this.isDungeonWall = nbt.getBoolean("isDungeonWall");
		}
	}

	@Override
	public boolean save(CompoundNBT nbt) {
		nbt.putBoolean("new_spawn", isNewSpawn());
		nbt.putBoolean("isBlockAligned", this.isBlockAligned);
		nbt.putBoolean("isDungeonWall", this.isDungeonWall);
		return true;
	}

	public void shake(int shakeTimerMax) {
		shakingTimerMax = shakeTimerMax;
		prev_shake_timer = shake_timer;
		if(shake_timer == 0) {
			shaking = true;
			shake_timer = 1;
		}
		if(shake_timer > 0)
			shake_timer++;

		if(shake_timer >= shakingTimerMax)
			shaking = false;
		else
			shaking = true;
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(isShaking()) {
			double dist = getDistance(viewer);
			float shakeMult = (float) (1.0F - dist / 16.0F);
			if(dist >= 16.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(getShakingProgress(partialTicks) * Math.PI) + 0.1F) * 0.075F * shakeMult);
		} else {
			return 0.0F;
		}
	}

	public float getShakeDistance(Entity entity) {
		float distX = (float)(getPosition().getX() - entity.getPosition().getX());
		float distY = (float)(getPosition().getY() - entity.getPosition().getY());
		float distZ = (float)(getPosition().getZ() - entity.getPosition().getZ());
		return MathHelper.sqrt(distX * distX + distY * distY + distZ * distZ);
	}

	public boolean isShaking() {
		return shaking;
	}

	public float getShakingProgress(float delta) {
		return 1.0F / shakingTimerMax * (prev_shake_timer + (shake_timer - prev_shake_timer) * delta);
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeBoolean(this.isBlockAligned);
		buffer.writeBoolean(this.isDungeonWall);
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		this.isBlockAligned = buffer.readBoolean();
		this.isDungeonWall = buffer.readBoolean();
	}
}
