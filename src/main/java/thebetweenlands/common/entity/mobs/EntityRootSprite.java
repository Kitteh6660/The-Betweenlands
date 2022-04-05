package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIFollowTarget;
import thebetweenlands.common.entity.ai.EntityAIJumpRandomly;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityRootSprite extends EntityCreature implements IEntityBL {
	private static final byte EVENT_STEP = 40;

	private float jumpHeightOverride = -1;

	public EntityRootSprite(World worldIn) {
		super(worldIn);
		this.experienceValue = 1;
		this.setSize(0.3F, 0.55F);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();

		this.goalSelector.addGoal(0, new EntityAISwimming(this));
		this.goalSelector.addGoal(1, new EntityAIPanic(this, 1.0D));
		this.goalSelector.addGoal(2, new EntityAIAvoidEntity<>(this, PlayerEntity.class, 5, 0.5F, 1.0F));
		this.goalSelector.addGoal(3, new EntityAIFollowTarget(this, new EntityAIFollowTarget.FollowClosest(this, EntitySporeling.class, 10), 0.65D, 0.5F, 10.0F, false));
		this.goalSelector.addGoal(4, new EntityAIJumpRandomly(this, 10, () -> !EntityRootSprite.this.world.getEntitiesOfClass(EntitySporeling.class, this.getBoundingBox().inflate(1)).isEmpty()) {
			@Override
			public void start() {
				EntityRootSprite.this.setJumpHeightOverride(0.2F);
				EntityRootSprite.this.getJumpHelper().setJumping();
			}
		});
		this.goalSelector.addGoal(5, new EntityAIWanderAvoidWater(this, 0.6D));
		this.goalSelector.addGoal(6, new EntityAIWatchClosest(this, EntitySporeling.class, 8));
		this.goalSelector.addGoal(7, new EntityAIWatchClosest(this, PlayerEntity.class, 10));
		this.goalSelector.addGoal(8, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(5.0D);
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.ROOT_SPRITE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.ROOT_SPRITE_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.ROOT_SPRITE_LIVING;
	}

	@Override
	protected float getSoundVolume() {
		return 0.5F;
	}

	@Override
	public int getTalkInterval() {
		return 5 * 20;
	}

	public void setJumpHeightOverride(float jumpHeightOverride) {
		this.jumpHeightOverride = jumpHeightOverride;
	}

	@Override
	protected float getJumpUpwardsMotion() {
		if(this.jumpHeightOverride > 0) {
			float height = this.jumpHeightOverride;
			this.jumpHeightOverride = -1;
			return height;
		}
		return super.getJumpUpwardsMotion();
	}

	@Override
	public void tick() {
		super.tick();

		if(this.level.isClientSide() && this.random.nextInt(20) == 0) {
			this.spawnLeafParticles();
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.ROOT_SPRITE;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		super.playStepSound(pos, blockIn);

		this.distanceWalkedOnStepModified += 0.7F;

		this.world.setEntityState(this, EVENT_STEP);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_STEP) {
			this.spawnLeafParticles();
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnLeafParticles() {
		for(int i = 0; i < 1 + this.random.nextInt(3); i++) {
			BLParticles.WEEDWOOD_LEAF.spawn(this.world, this.getX() + this.motionX, this.getY() + 0.1F + this.random.nextFloat() * 0.3F, this.getZ() + this.motionZ, ParticleArgs.get()
					.withMotion(this.motionX * 0.5F + this.random.nextFloat() * 0.1F - 0.05F, 0.05F, this.motionZ * 0.5F + this.random.nextFloat() * 0.1F - 0.05F)
					.withScale(0.5F));
		}
	}
}
