package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.ai.attributes.Attributes.RangedAttribute;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleBatchTypeBuilder;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGasCloud extends EntityFlyingMob implements IEntityBL {
	public static final IAttribute GAS_CLOUD_COLOR_R = (new RangedAttribute(null, "bl.gasCloudColorRed", 104, 0, 255)).setDescription("Gas cloud color red component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_G = (new RangedAttribute(null, "bl.gasCloudColorGreen", 196, 0, 255)).setDescription("Gas cloud color green component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_B = (new RangedAttribute(null, "bl.gasCloudColorBlue", 179, 0, 255)).setDescription("Gas cloud color blue component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_A = (new RangedAttribute(null, "bl.gasCloudColorAlpha", 170, 0, 255)).setDescription("Gas cloud color alpha component").setShouldWatch(true);

	protected double aboveLayer = 6.0D;
	protected int targetBlockedTicks = 0;

	public static final DamageSource damageSourceSuffocation = (new DamageSource("suffocation")).setDamageBypassesArmor();

	@OnlyIn(Dist.CLIENT)
	private ParticleBatch particleBatch;

	public EntityGasCloud(World world) {
		super(world);
		this.setSize(1.75F, 1.75F);
		this.noClip = true;
		this.ignoreFrustumCheck = true;
		this.moveControl = new FlightMoveHelper(this) {
			@Override
			protected boolean isNotColliding(double x, double y, double z, double step) {
				double stepX = (x - this.entity.getX()) / step;
				double stepY = (y - this.entity.getY()) / step;
				double stepZ = (z - this.entity.getZ()) / step;

				double cx = this.entity.getX();
				double cy = this.entity.getY();
				double cz = this.entity.getZ();

				boolean canPassSolidBlocks = ((EntityGasCloud) this.entity).getAttackTarget() != null;

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int i = 1; (double)i < step; ++i) {
					cx += stepX;
					cy += stepY;
					cz += stepZ;

					checkPos.setPos(cx, cy, cz);

					if(this.entity.level.isBlockLoaded(checkPos)) {
						BlockState state = this.entity.level.getBlockState(checkPos);

						if ((!canPassSolidBlocks && state.canOcclude()) || state.getMaterial().isLiquid()) {
							return false;
						}
					} else {
						return false;
					}
				}

				checkPos.release();

				return true;
			}
		};

		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);

		if(this.level.isClientSide()) {
			this.initParticleBatch();
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void initParticleBatch() {
		this.particleBatch = BatchedParticleRenderer.INSTANCE.createBatchType(new ParticleBatchTypeBuilder().pass().depthMaskPass(true).lit(true).texture((ResourceLocation)null).setFog(false).end().build());
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new EntityAIFlyRandomly<EntityGasCloud>(this) {
			@Override
			protected double getTargetY(Random rand, double distanceMultiplier) {
				if(this.entity.getY() <= 0.0D) {
					return this.entity.getY() + 16.0F;
				}

				int worldHeight = 0;

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int yo = 0; yo < MathHelper.ceil(EntityGasCloud.this.aboveLayer); yo++) {
					checkPos.setPos(this.entity.getX(), this.entity.getY() - yo, this.entity.getZ());

					if(!this.entity.level.isBlockLoaded(checkPos))
						return this.entity.getY();

					if(!this.entity.level.isEmptyBlock(checkPos)) {
						worldHeight = checkPos.getY();
						break;
					}
				}

				checkPos.release();

				if(this.entity.getY() > worldHeight + EntityGasCloud.this.aboveLayer) {
					return this.entity.getY() + (-rand.nextFloat() * 2.0F) * 16.0F * distanceMultiplier;
				} else {
					float rndFloat = rand.nextFloat() * 2.0F - 1.0F;
					if(rndFloat > 0.0D) {
						double maxRange = worldHeight + EntityGasCloud.this.aboveLayer - this.entity.getY();
						return this.entity.getY() + (-rand.nextFloat() * 2.0F) * maxRange * distanceMultiplier;
					} else {
						return this.entity.getY() + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier;
					}
				}
			}

			@Override
			protected double getFlightSpeed() {
				return 0.3D;
			}
		});


		targetTasks.addGoal(1, new EntityAINearestAttackableTarget<PlayerEntity>(this, PlayerEntity.class, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.065D);
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(16.0D);
		getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_R);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_G);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_B);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_A);
	}

	/**
	 * Sets the gas color
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void setGasColor(int r, int g, int b, int a) {
		this.getAttribute(GAS_CLOUD_COLOR_R).setBaseValue(r);
		this.getAttribute(GAS_CLOUD_COLOR_G).setBaseValue(g);
		this.getAttribute(GAS_CLOUD_COLOR_B).setBaseValue(b);
		this.getAttribute(GAS_CLOUD_COLOR_A).setBaseValue(a);
	}

	/**
	 * Returns the gas color in an array [red, green, blue, alpha]
	 * @return
	 */
	public int[] getGasColor() {
		return new int[] { (int)this.getAttribute(GAS_CLOUD_COLOR_R).getValue(),
				(int)this.getAttribute(GAS_CLOUD_COLOR_G).getValue(),
				(int)this.getAttribute(GAS_CLOUD_COLOR_B).getValue(),
				(int)this.getAttribute(GAS_CLOUD_COLOR_A).getValue() };
	};

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level.isClientSide() && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			this.remove();
		}

		if (this.level.isClientSide()) {
			this.spawnCloudParticle(false);
			this.updateParticleBatch();
		}

		if (this.isInWater()) {
			this.moveControl.setWantedPosition(this.getX(), this.getY() + 1.0D, this.getZ(), 1.0D);
		} else {
			if(this.getAttackTarget() != null) {
				this.moveControl.setWantedPosition(this.getAttackTarget().getX(), this.getAttackTarget().getY() + this.getAttackTarget().getEyeHeight(), this.getAttackTarget().getZ(), 1.0D);
			}
		}

		if (!this.level.isClientSide() && this.isEntityAlive()) {
			List<LivingEntity> targets = this.world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D));
			for (LivingEntity target : targets) {
				if (!(target instanceof EntityGasCloud) && !(target instanceof IEntityBL)) {
					target.addEffect(new EffectInstance(Effects.POISON, 60, 0));
					if (target.tickCount % 10 == 0)
						target.hurt(damageSourceSuffocation, (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void updateParticleBatch() {
		BatchedParticleRenderer.INSTANCE.updateBatch(this.particleBatch);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnCloudParticle(boolean strongMotion) {
		if(strongMotion) {
			double x = this.getX() + this.motionX + (this.level.random.nextFloat() - 0.5F) / 2.0F;
			double y = this.getY() + this.height / 2.0D + this.motionY + (this.level.random.nextFloat() - 0.5F) / 2.0F;
			double z = this.getZ() + this.motionZ + (this.level.random.nextFloat() - 0.5F) / 2.0F;
			int[] color = this.getGasColor();

			ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
					.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
							.withData(this)
							.withMotion((this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F)
							.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F));

			BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, particle);
		} else {
			double x = this.getX() + this.motionX + (this.level.random.nextFloat() - 0.5F) / 2.0F;
			double y = this.getY() + this.height / 2.0D + this.motionY + (this.level.random.nextFloat() - 0.5F) / 2.0F;
			double z = this.getZ() + this.motionZ + (this.level.random.nextFloat() - 0.5F) / 2.0F;
			double mx = this.motionX + (this.level.random.nextFloat() - 0.5F) / 16.0F;
			double my = this.motionY + (this.level.random.nextFloat() - 0.5F) / 16.0F;
			double mz = this.motionZ + (this.level.random.nextFloat() - 0.5F) / 16.0F;
			int[] color = this.getGasColor();

			ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
					.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
							.withData(this)
							.withMotion(mx, my, mz)
							.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F));

			BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, particle);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public ParticleBatch getParticleBatch() {
		return this.particleBatch;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source != DamageSource.IN_WALL) {
			if(source instanceof EntityDamageSource) {
				Entity direct = ((EntityDamageSource) source).getImmediateSource();
				if(direct instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) direct;

					ItemStack held = player.getItemInHand(Hand.MAIN_HAND);

					if(!held.isEmpty() && held.getItem() == ItemRegistry.DENTROTHYST_VIAL && held.getDamageValue() != 1) {
						if(this.level.isClientSide()) {
							for(int i = 0; i < 10; i++) {
								this.world.addParticle(ParticleTypes.CRIT, this.getX() + this.motionX, this.getY() + this.motionY + this.height * 0.5f, this.getZ() + this.motionZ, -this.motionX + (this.random.nextFloat() - 0.5f), -this.motionY + 0.2D + (this.random.nextFloat() - 0.5f), -this.motionZ + (this.random.nextFloat() - 0.5f));
							}
						}

						if(super.hurt(source, damage * 3.0f)) {
							if(!this.level.isClientSide()) {
								if(!this.isEntityAlive()) {
									held.shrink(1);
									ItemHandlerHelper.giveItemToPlayer(player, ItemRegistry.DENTROTHYST_FLUID_VIAL.withFluid(held.getDamageValue() == 2 ? 1 : 0, FluidRegistry.SHALLOWBREATH));
								}

								this.world.playLocalSound(null, player.getX(), player.getY(), player.getZ(), FluidRegistry.SHALLOWBREATH.getFillSound(new FluidStack(FluidRegistry.SHALLOWBREATH, 1000)), SoundCategory.BLOCKS, 1.0F, 1.0F);
							}

							return true;
						}

						return false;
					}
				}
			}

			return super.hurt(source, damage);
		}
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GAS_CLOUD_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.GAS_CLOUD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GAS_CLOUD_DEATH;
	}

	@Override
	protected void onDeathUpdate() {
		++this.deathTime;

		if(this.level.isClientSide()) {
			for(int i = 0; i < 6; i++) {
				this.spawnCloudParticle(true);
			}
		}

		if (this.deathTime >= 80) {
			if (!this.level.isClientSide() && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot"))) {
				int i = this.getExperiencePoints(this.attackingPlayer);
				i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
				while (i > 0) {
					int j = EntityXPOrb.getXPSplit(i);
					i -= j;
					this.world.addFreshEntity(new EntityXPOrb(this.world, this.getX(), this.getY(), this.getZ(), j));
				}
			}

			this.remove();
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.GAS_CLOUD;
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
