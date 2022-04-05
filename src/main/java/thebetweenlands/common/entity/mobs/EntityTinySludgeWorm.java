package thebetweenlands.common.entity.mobs;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTinySludgeWorm extends EntitySludgeWorm {
	public static final byte EVENT_SQUASHED = 80;
	public static final byte EVENT_LEAP = 81;

	protected boolean isSquashed = false;
	
	public EntityTinySludgeWorm(World world) {
		this(world, true);
	}
	
	public EntityTinySludgeWorm(World world, boolean doSpawningAnimation) {
		super(world, doSpawningAnimation);
		setSize(0.3125F, 0.3125F);
		fireImmune = true;
		experienceValue = 1;
		this.parts = new MultiPartEntityPart[] {
				new MultiPartEntityPart(this, "part1", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part2", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part3", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part4", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part5", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part6", 0.1875F, 0.1875F),
		};
	}
	
	@Override
	protected void registerGoals() {
		tasks.addGoal(1, new EntityAILeapAtTarget(this, 0.3F) {
			@Override
			public void start() {
				super.start();
				EntityTinySludgeWorm.this.getWorld().setEntityState(EntityTinySludgeWorm.this, EVENT_LEAP);
			}
		});
		tasks.addGoal(2, new EntityAIAttackMelee(this, 1, false));
		tasks.addGoal(3, new EntityAIWander(this, 0.8D, 1));
		targetTasks.addGoal(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addGoal(1, new EntityAINearestAttackableTarget<>(this, PlayerEntity.class, true));
		targetTasks.addGoal(2, new EntityAINearestAttackableTarget<>(this, LivingEntity.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(4.0D);
		getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.21D);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5D);
	}

	@Override
	protected double getMaxPieceDistance() {
		return 0.2D;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if (canSee(entity) && entity.onGround)
			if (super.attackEntityAsMob(entity))
				return true;
		return false;
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity player) {
		if (!level.isClientSide()) {
			for (MultiPartEntityPart part : this.parts) {
				if (player.getBoundingBox().maxY >= part.getBoundingBox().minY
						&& player.getBoundingBox().minY <= part.getBoundingBox().maxY
						&& player.getBoundingBox().maxX >= part.getBoundingBox().minX
						&& player.getBoundingBox().minX <= part.getBoundingBox().maxX
						&& player.getBoundingBox().maxZ >= part.getBoundingBox().minZ
						&& player.getBoundingBox().minZ <= part.getBoundingBox().maxZ
						&& player.yOld > player.getY()) {
					
					player.addEffect(new EffectInstance(Effects.NAUSEA, 80, 0));
					
					if (level.getDifficulty() == EnumDifficulty.NORMAL) {
						player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(80, 1));
					} else if (level.getDifficulty() == EnumDifficulty.HARD) {
						player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(160, 1));
					}
					
					this.isSquashed = true;
				}
			}
			
			if (this.isSquashed) {
				this.world.setEntityState(this, EVENT_SQUASHED);
				
				this.world.playLocalSound(null, this.getX(), this.getY(), this.getZ(), getJumpedOnSound(), SoundCategory.NEUTRAL, 1.0F, 0.5F);
				this.world.playLocalSound(null, this.getX(), this.getY(), this.getZ(), getDeathSound(), SoundCategory.NEUTRAL, 1.0F, 0.5F);
				
				this.damageWorm(DamageSource.causePlayerDamage(player), this.getHealth());
			}
		}
	}

	public boolean isSquashed() {
		return this.isSquashed;
	}
	
	@Override
	public void onDeathUpdate() {
		if (this.isSquashed) {
			this.deathTime = 19;
		}
		
		super.onDeathUpdate();
	}
	
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_SQUASHED) {
			for(int i = 0; i < 100; i++) {
				Random rnd = this.world.rand;
				float rx = rnd.nextFloat() * 1.0F - 0.5F;
				float ry = rnd.nextFloat() * 1.0F - 0.5F;
				float rz = rnd.nextFloat() * 1.0F - 0.5F;
				Vector3d vec = new Vector3d(rx, ry, rz);
				vec = vec.normalize();
				BLParticles.SPLASH_TAR.spawn(level, this.getX() + rx + 0.1F, this.getY() + ry + 0.1F, this.getZ() + rz + 0.1F, ParticleArgs.get().withMotion(vec.x * 0.4F, vec.y * 0.4F, vec.z * 0.4F)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			}
		} else if(id == EVENT_LEAP) {
			for(Entity part : this.getParts()) {
				part.motionY += 0.3F;
			}
		}
	}

	protected SoundEvent getJumpedOnSound() {
		return SoundRegistry.WORM_SPLAT;
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() * 1.5F;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TINY_SLUDGE_WORM;
	}
}
