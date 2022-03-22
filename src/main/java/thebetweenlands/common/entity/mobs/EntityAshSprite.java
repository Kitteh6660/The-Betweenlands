package thebetweenlands.common.entity.mobs;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityAshSprite extends MobEntity implements IEntityBL {
	protected static final byte EVENT_ENABLE_NO_CLIP = 80;
	protected static final byte EVENT_DISABLE_NO_CLIP = 81;
	
	protected static final DataParameter<Byte> ASH_SPRITE_FLAGS = EntityDataManager.<Byte>createKey(EntityAshSprite.class, DataSerializers.BYTE);
	
	@Nullable
	private BlockPos boundOrigin;
	
	private boolean canNoClip = true;
	private int noClipTimeout = 0;

	public EntityAshSprite(World worldIn) {
		super(worldIn);
		fireImmune = true;
		moveHelper = new EntityAshSprite.AIMoveControl(this);
		setSize(0.4F, 0.8F);
		experienceValue = 3;
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		super.move(type, x, y, z);
		doBlockCollisions();
	}

	@Override
	public void tick() {
		if(!world.isClientSide()) {
			if(noClipTimeout > 0) {
				if(canNoClip) {
					canNoClip = false;
					world.setEntityState(this, EVENT_DISABLE_NO_CLIP);
				}
				noClipTimeout--;
			} else {
				if(!canNoClip) {
					canNoClip = true;
					world.setEntityState(this, EVENT_ENABLE_NO_CLIP);
				}
				noClipTimeout = 0;
			}
		}
		if(canNoClip || !this.isNotColliding()) {
			noClip = true;
		} else {
			noClip = false;
		}
		super.tick();
		noClip = false;
		setNoGravity(true);
		if (level.isClientSide())
			spawnParticles(level, getPosition(), rand);
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_ENABLE_NO_CLIP) {
			canNoClip = true;
		} else if(id == EVENT_DISABLE_NO_CLIP) {
			canNoClip = false;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void spawnParticles(World worldIn, BlockPos pos, Random rand) {
		for(int i = 0; i < 4; i++) {
			worldIn.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, pos.getX() + 0.5D + (rand.nextBoolean() ? -0.5F : 0.5F) * Math.pow(rand.nextFloat(), 1F), pos.getY() + 0.5D + rand.nextFloat() - 0.5F, pos.getZ() + 0.5D + (rand.nextBoolean() ? -0.5F : 0.5F) * Math.pow(rand.nextFloat(), 1F), 0, 0.2D, 0);
		}
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(4, new EntityAshSprite.AIChargeAttack(this));
		tasks.addTask(8, new EntityAshSprite.AIMoveRandom(this));
		tasks.addTask(9, new EntityAIWatchClosest(this, PlayerEntity.class, 3.0F, 1.0F));
		tasks.addTask(10, new EntityAIWatchClosest(this, MobEntity.class, 8.0F));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] { EntityAshSprite.class }));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(14.0D);
		getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ASH_SPRITE_FLAGS, Byte.valueOf((byte) 0));
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.ASH_SPRITE;
	}

		@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.ASHSPRITE_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.ASHSPRITE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.ASHSPRITE_DEATH;
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		super.readEntityFromNBT(compound);
		if (compound.contains("BoundX")) 
			boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		super.writeEntityToNBT(compound);
		if (boundOrigin != null) {
			compound.putInt("BoundX", boundOrigin.getX());
			compound.putInt("BoundY", boundOrigin.getY());
			compound.putInt("BoundZ", boundOrigin.getZ());
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN)) {
			return false;
		}
		if(super.attackEntityFrom(source, damage)) {
			this.noClipTimeout = 60;
			return true;
		}
		return false;
	}

	@Nullable
	public BlockPos getBoundOrigin() {
		return boundOrigin;
	}

	public void setBoundOrigin(@Nullable BlockPos boundOriginIn) {
		boundOrigin = boundOriginIn;
	}

	private boolean getAshSpriteFlag(int mask) {
		int i = ((Byte) dataManager.get(ASH_SPRITE_FLAGS)).byteValue();
		return (i & mask) != 0;
	}

	private void setAshSpriteFlag(int mask, boolean value) {
		int i = ((Byte) dataManager.get(ASH_SPRITE_FLAGS)).byteValue();
		if (value)
			i = i | mask;
		else
			i = i & ~mask;
		dataManager.set(ASH_SPRITE_FLAGS, Byte.valueOf((byte) (i & 255)));
	}

	public boolean isCharging() {
		return getAshSpriteFlag(1);
	}

	public void setCharging(boolean charging) {
		setAshSpriteFlag(1, charging);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getBrightnessForRender() {
		return 15728880;
	}

	@Override
	public float getBrightness() {
		return 1.0F;
	}

	class AIChargeAttack extends EntityAIBase {
		private final EntityAshSprite ashSprite;

		public AIChargeAttack(EntityAshSprite ash_sprite) {
			setMutexBits(1);
			this.ashSprite = ash_sprite;
		}

		@Override
		public boolean shouldExecute() {
			if (ashSprite.getAttackTarget() != null && !ashSprite.getMoveHelper().isUpdating() && ashSprite.rand.nextInt(7) == 0)
				return ashSprite.getDistanceSq(ashSprite.getAttackTarget()) > 4.0D;
			else
				return false;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return ashSprite.getMoveHelper().isUpdating() && ashSprite.isCharging() && ashSprite.getAttackTarget() != null && ashSprite.getAttackTarget().isEntityAlive();
		}

		@Override
		public void startExecuting() {
			LivingEntity target = ashSprite.getAttackTarget();
			if(target != null) {
				Vector3d vec3d = target.getPositionEyes(1.0F);
				ashSprite.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
				ashSprite.setCharging(true);
				//ash_sprite.playSound(SoundEvents.ASH_SPRITE_ATTACK, 1.0F, 1.0F);
			}
		}

		@Override
		public void resetTask() {
			ashSprite.setCharging(false);
		}

		@Override
		public void updateTask() {
			LivingEntity target = ashSprite.getAttackTarget();

			if(target != null) {
				if (ashSprite.getBoundingBox().intersects(target.getBoundingBox())) {
					ashSprite.attackEntityAsMob(target);
					ashSprite.setCharging(false);
				} else {
					double d0 = ashSprite.getDistanceSq(target);
					if (d0 < 9.0D) {
						Vector3d vec3d = target.getPositionEyes(1.0F);
						ashSprite.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
					}
				}
			}
		}
	}

	class AIMoveControl extends EntityMoveHelper {
		private final EntityAshSprite ashSprite;

		public AIMoveControl(EntityAshSprite ash_sprite) {
			super(ash_sprite);
			this.ashSprite = ash_sprite;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (action == EntityMoveHelper.Action.MOVE_TO) {
				double d0 = posX - ashSprite.getX();
				double d1 = posY - ashSprite.getY();
				double d2 = posZ - ashSprite.getZ();
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				d3 = (double) MathHelper.sqrt(d3);

				if (d3 < ashSprite.getBoundingBox().getAverageEdgeLength()) {
					action = EntityMoveHelper.Action.WAIT;
					ashSprite.motionX *= 0.5D;
					ashSprite.motionY *= 0.5D;
					ashSprite.motionZ *= 0.5D;
				} else {
					ashSprite.motionX += d0 / d3 * 0.05D * speed;
					ashSprite.motionY += d1 / d3 * 0.05D * speed;
					ashSprite.motionZ += d2 / d3 * 0.05D * speed;

					if (ashSprite.getAttackTarget() == null) {
						ashSprite.yRot = -((float) MathHelper.atan2(ashSprite.motionX, ashSprite.motionZ)) * (180F / (float) Math.PI);
						ashSprite.renderYawOffset = ashSprite.yRot;
					} else {
						double d4 = ashSprite.getAttackTarget().getX() - ashSprite.getX();
						double d5 = ashSprite.getAttackTarget().getZ() - ashSprite.getZ();
						ashSprite.yRot = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
						ashSprite.renderYawOffset = ashSprite.yRot;
					}
				}
			}
		}
	}

	class AIMoveRandom extends EntityAIBase {
		private final EntityAshSprite ashSprite;

		public AIMoveRandom(EntityAshSprite ash_sprite) {
			setMutexBits(1);
			this.ashSprite = ash_sprite;
		}

		@Override
		public boolean shouldExecute() {
			return !ashSprite.getMoveHelper().isUpdating() && ashSprite.rand.nextInt(7) == 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void updateTask() {
			BlockPos blockpos = ashSprite.getBoundOrigin();

			if (blockpos == null) {
				blockpos = new BlockPos(ashSprite);
			}

			for (int i = 0; i < 3; ++i) {
				BlockPos blockpos1 = blockpos.add(ashSprite.rand.nextInt(15) - 7, ashSprite.rand.nextInt(11) - 5, ashSprite.rand.nextInt(15) - 7);

				if (ashSprite.world.isEmptyBlock(blockpos1)) {
					ashSprite.moveHelper.setMoveTo((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);

					if (ashSprite.getAttackTarget() == null) {
						ashSprite.getLookHelper().setLookPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}
					break;
				}
			}
		}
	}
}