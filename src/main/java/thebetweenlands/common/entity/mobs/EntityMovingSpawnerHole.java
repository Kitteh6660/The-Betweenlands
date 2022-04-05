package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.LootTableRegistry;

public class EntityMovingSpawnerHole extends EntityMovingWallFace implements IMob {
	@OnlyIn(Dist.CLIENT)
	private TextureAtlasSprite wallSprite;

	protected int spawnCount = 3;
	protected int spawnCooldown = 0;

	protected double maxTargetRange = 6;
	protected double countCheckRange = 8;
	protected int maxCount = 5;

	public EntityMovingSpawnerHole(World world) {
		super(world);
		this.lookMoveSpeedMultiplier = 4.0F;
		this.experienceValue = 5;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.spawnCount = 2 + this.random.nextInt(3);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();

		this.targetSelector.addGoal(0, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, 0, true, false, null).setUnseenMemoryTicks(120));

		this.goalSelector.addGoal(0, new AITrackTarget<EntityMovingSpawnerHole>(this, true, 28.0D) {
			@Override
			protected boolean canMove() {
				return true;
			}
		});
		this.goalSelector.addGoal(1, new AISpawnMob(this, 7, 18));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.08D);
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.level.isClientSide()) {
			if(this.spawnCooldown > 0) {
				this.spawnCooldown--;
			}

			if(!this.isSpawningMob() && this.spawnCount <= 0) {
				this.remove();
			}
		} else {
			this.updateWallSprite();
		}
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putInt("spawnCount", this.spawnCount);
		nbt.putInt("spawnCooldown", this.spawnCooldown);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		if(nbt.contains("spawnCount", Constants.NBT.TAG_INT)) {
			this.spawnCount = nbt.getInt("spawnCount");
		}
		if(nbt.contains("spawnCooldown", Constants.NBT.TAG_INT)) {
			this.spawnCooldown = nbt.getInt("spawnCooldown");
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
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.MOVING_SPAWNER_HOLE;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, Direction facing, Direction facingUp) {
		return this.isValidBlockForMovement(pos, this.world.getBlockState(pos)) && facing != Direction.UP;
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, BlockState state) {
		return state.canOcclude() && state.isNormalCube() && state.isFullCube() && state.getBlockHardness(this.world, pos) > 0 && state.getMaterial() == Material.ROCK;
	}

	@Override
	public Vector3d getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	protected Entity createEntity(Vector3d holeBottom, double frontOffset, Direction facing) {
		Entity entity = new EntitySludgeWorm(this.world);
		entity.moveTo(holeBottom.x, holeBottom.y, holeBottom.z, facing.getHorizontalAngle(), 0);
		entity.move(MoverType.SELF, facing.getStepX() * 0.35D, facing == Direction.UP ? 1 : 0, facing.getStepZ() * 0.35D);
		return entity;
	}

	protected Predicate<Entity> getNearbySpawnedEntitiesPredicate() {
		return entity -> entity instanceof EntitySludgeWorm;
	}

	public void startSpawningMob() {
		BlockPos pos = this.getPosition();
		Entity entity = this.createEntity(new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D), 0.55D, this.getFacing());

		if(entity != null) {
			if(entity instanceof MobEntity) {
				((MobEntity) entity).onInitialSpawn(this.world.getCurrentDifficultyAt(new BlockPos(entity)), (IEntityLivingData) null);
			}
			this.world.addFreshEntity(entity);
			this.world.levelEvent(2004, this.getPosition(), 0);
		}

		this.spawnCount--;

		this.spawnCooldown = 40;
	}

	public boolean isSpawningMob() {
		return this.spawnCooldown > 0;
	}

	public boolean canSpawnMobs() {
		Entity target = this.getAttackTarget();
		if(target != null && !this.isSpawningMob() && this.getDistance(target) < this.maxTargetRange && this.canSee(target)) {
			Predicate<Entity> pred = this.getNearbySpawnedEntitiesPredicate();
			int others = this.world.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(this.countCheckRange), pred).size();
			return others < this.maxCount;
		}
		return false;
	}

	protected static class AISpawnMob extends EntityAIBase {
		protected final EntityMovingSpawnerHole entity;

		protected int minSpawnCooldown, maxSpawnCooldown;
		protected int spawnCooldown;

		public AISpawnMob(EntityMovingSpawnerHole entity, int minSpawnCooldown, int maxSpawnCooldown) {
			this.entity = entity;
			this.minSpawnCooldown = minSpawnCooldown;
			this.maxSpawnCooldown = maxSpawnCooldown;
			this.spawnCooldown = minSpawnCooldown + entity.rand.nextInt(maxSpawnCooldown - minSpawnCooldown);
		}

		@Override
		public boolean canUse() {
			if(--this.spawnCooldown <= 0) {
				this.spawnCooldown = this.minSpawnCooldown + this.entity.rand.nextInt(this.maxSpawnCooldown - this.minSpawnCooldown);
				return this.entity.isEntityAlive() && !this.entity.isMoving() && this.entity.isAnchored() && this.entity.canSpawnMobs();
			}
			return false;
		}

		@Override
		public void start() {
			this.entity.startSpawningMob();
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}
	}
}
