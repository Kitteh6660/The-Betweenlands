package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.ai.attributes.Attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityFrog extends EntityCreature implements IEntityBL {
	public static final IAttribute FROG_SKIN_ATTRIB = (new RangedAttribute(null, "bl.frogSkin", 0, 0, 5)).setDescription("Frog skin").setShouldWatch(true);

	private static final DataParameter<Byte> DW_SWIM_STROKE = EntityDataManager.defineId(EntityFrog.class, DataSerializers.BYTE);

	public int jumpAnimationTicks;
	public int prevJumpAnimationTicks;
	private int ticksOnGround = 0;
	private int strokeTicks = 0;

	public EntityFrog(World worldIn) {
		super(worldIn);
		this.setPathPriority(PathNodeType.WATER, 4.0F);
		this.getNavigation().getNodeProcessor().setCanSwim(true);
		setSize(0.7F, 0.5F);
		this.stepHeight = 1.0F;
		this.experienceValue = 3;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new EntityAIPanic(this, 1.0D));
		this.goalSelector.addGoal(1, new EntityAIWander(this, 1.0D, 40));
		this.goalSelector.addGoal(2, new EntityAIWatchClosest(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(3, new EntityAILookIdle(this));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DW_SWIM_STROKE, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(3.0D);
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.05D);
		getAttributeMap().registerAttribute(FROG_SKIN_ATTRIB);
		this.setSkin(this.random.nextInt(5));
	}

	@Override
	public void tick() {
		this.prevJumpAnimationTicks = this.jumpAnimationTicks;
		super.tick();
		if (this.onGround || (this.strokeTicks == 0 && this.isInWater())) {
			this.ticksOnGround++;
			if (this.jumpAnimationTicks > 0)
				this.jumpAnimationTicks = 0;
		} else {
			this.ticksOnGround = 0;
			this.jumpAnimationTicks++;
		}
		if (this.strokeTicks > 0)
			this.strokeTicks--;
		if (!this.level.isClientSide()) {
			if (this.strokeTicks > 0) {
				this.strokeTicks--;
				this.entityData.set(DW_SWIM_STROKE, (byte) 1);
			} else {
				this.entityData.set(DW_SWIM_STROKE, (byte) 0);
			}
		} else {
			if (this.entityData.get(DW_SWIM_STROKE) == 1) {
				if (this.strokeTicks < 20)
					this.strokeTicks++;
			} else {
				this.strokeTicks = 0;
			}
		}
		if (!this.level.isClientSide()) {
			this.setAir(20);

			Path path = getNavigation().getPath();
			if (path != null && !path.isFinished() && (onGround || this.isInWater()) && !this.isMovementBlocked()) {
				int index = path.getCurrentPathIndex();
				if (index < path.getCurrentPathLength()) {
					PathPoint nextHopSpot = path.getPathPointFromIndex(index);
					float x = (float) (nextHopSpot.x - posX);
					float z = (float) (nextHopSpot.z - posZ);
					float angle = (float) (Math.atan2(z, x));
					float distance = (float) Math.sqrt(x * x + z * z);
					double speedMultiplier = Math.min(distance / 2.0D, 1);
					if (distance > 0.5D) {
						if (!this.isInWater()) {
							if (this.ticksOnGround > 5) {
								this.motionY += 0.4;
								this.motionX += 0.4 * MathHelper.cos(angle) * speedMultiplier;
								this.motionZ += 0.4 * MathHelper.sin(angle) * speedMultiplier;
								this.velocityChanged = true;
							}
						} else {
							if (this.strokeTicks == 0) {
								this.motionY += (nextHopSpot.y < this.getY() ? -0.2D : 0.2D) * speedMultiplier;
								this.motionX += 0.45 * MathHelper.cos(angle) * speedMultiplier;
								this.motionZ += 0.45 * MathHelper.sin(angle) * speedMultiplier;
								this.velocityChanged = true;
								this.strokeTicks = 40;
								this.world.setEntityState(this, (byte) 8);
							} else if (this.collidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						}
					} else {
						path.incrementPathIndex();
					}
				}
			}

			if (!this.level.isClientSide()) {
				if (this.motionY < 0.0F && this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getY() + 0.4D), MathHelper.floor(this.getZ()))).getMaterial().isLiquid()) {
					this.motionY *= 0.1F;
					this.velocityChanged = true;
				}

				if ((path == null || path.isFinished()) && this.world.getBlockState(new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getY() + 0.5D), MathHelper.floor(this.getZ()))).getMaterial().isLiquid()) {
					this.motionY += 0.04F;
					this.velocityChanged = true;
				}
			}
		}

		if (level.isClientSide() && getSkin() == 4 && world.getGameTime() % 10 == 0) {
			BLParticles.DIRT_DECAY.spawn(world, posX, posY + 0.5D, posZ);
		}
	}

	@Override
	public void travel(float strafing, float up, float forward) {
		super.travel(0, 0, 0);
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity player) {
		super.onCollideWithPlayer(player);
		if (getSkin() == 4) {
			if (!level.isClientSide() && !player.isCreative() && player.getBoundingBox().maxY >= getBoundingBox().minY && player.getBoundingBox().minY <= getBoundingBox().maxY && player.getBoundingBox().maxX >= getBoundingBox().minX && player.getBoundingBox().minX <= getBoundingBox().maxX && player.getBoundingBox().maxZ >= getBoundingBox().minZ && player.getBoundingBox().minZ <= getBoundingBox().maxZ) {
				int duration = 0;
				switch(world.getDifficulty()) {
				default:
					duration = 0;
					break;
				case EASY:
					duration = 4;
					break;
				case NORMAL:
					duration = 7;
					break;
				case HARD:
					duration = 10;
					break;
				}
				if (duration > 0) {
					player.addEffect(new EffectInstance(Effects.POISON, duration * 20, 0));
				}
			}
		}
	}

	@Override
	public boolean isNotColliding() {
		return this.world.getBlockCollisions(this, this.getBoundingBox()).isEmpty() && this.world.checkNoEntityCollision(this.getBoundingBox(), this);
	}

	public int getSkin() {
		return (int) this.getAttribute(FROG_SKIN_ATTRIB).getValue();
	}

	public void setSkin(int skinType) {
		this.getAttribute(FROG_SKIN_ATTRIB).setBaseValue(skinType);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FROG_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.FROG_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FROG_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.FROG;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 8) {
			this.strokeTicks = 0;
		}
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}
	
	@Override
	public boolean canDespawn() {
		return false;
	}
}
