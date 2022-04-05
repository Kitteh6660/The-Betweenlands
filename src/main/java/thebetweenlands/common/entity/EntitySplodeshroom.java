package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySplodeshroom extends EntityProximitySpawner {
	
	private static final byte EVENT_EXPLODE_PARTICLES = 100;
	
	public int MAX_SWELL = 40;
	public int MIN_SWELL = 0;
	private static final DataParameter<Boolean> IS_SWELLING = EntityDataManager.defineId(EntitySplodeshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SWELL_COUNT = EntityDataManager.defineId(EntitySplodeshroom.class, DataSerializers.INT);
	private static final DataParameter<Boolean> HAS_EXPLODED = EntityDataManager.defineId(EntitySplodeshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> AOE_SIZE_XZ = EntityDataManager.defineId(EntitySplodeshroom.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> AOE_SIZE_Y = EntityDataManager.defineId(EntitySplodeshroom.class, DataSerializers.FLOAT);

	public EntitySplodeshroom(World world) {
		super(world);
		setSize(0.5F, 1F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_SWELLING, false);
		this.entityData.define(SWELL_COUNT, 0);
		this.entityData.define(HAS_EXPLODED, false);
		this.entityData.define(AOE_SIZE_XZ, 4F);
		this.entityData.define(AOE_SIZE_Y, 0.5F);
	}

	@Override
	public void tick() {
		// super.tick();
		if (!level.isClientSide() && level.getGameTime() % 5 == 0) {
			if (!getHasExploded())
				checkArea();
			if (getHasExploded())
				checkAreaOfEffect();
		}

		if (!level.isClientSide()) {
			if (!getSwelling() && getSwellCount() > MIN_SWELL)
				setSwellCount(getSwellCount() - 1);

			if (getSwelling() && getSwellCount() < MAX_SWELL)
				setSwellCount(getSwellCount() + 1);
			
			if(getHasExploded()) {
				if (getSwellCount() < MAX_SWELL)
					setSwellCount(MAX_SWELL);
				if (getAOESizeXZ() > 0.5F)
					setAOESizeXZ(getAOESizeXZ() - 0.01F);
				if (getAOESizeXZ() <= 0.5F)
					remove();
			}
		}

		if (getHasExploded())
			setBoundingBoxSize();
		
		if (level.isClientSide())
			if(getHasExploded())
				spawnCloudParticle();
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		Entity entity = null;
		if (!level.isClientSide() && level.getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<PlayerEntity> list = level.getEntitiesOfClass(PlayerEntity.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);

				if (entity != null) {
					if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isSpectator() && !((PlayerEntity) entity).isCreative()) {
						if (canSneakPast() && entity.isCrouching())
							return null;
						else if (checkSight() && !canSee(entity))
							return null;
						else {
							if(!getSwelling())
								setSwelling(true);
						}
						if (!isDead && isSingleUse() && getSwellCount() >= MAX_SWELL) {
							explode();
						}
					}
				}
			}
			if (entity == null) {
				if (getSwelling())
					setSwelling(false);
			}
		}
		return entity;
	}

	@Nullable
	protected Entity checkAreaOfEffect() {
		Entity entity = null;
		if (!level.isClientSide() && level.getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<PlayerEntity> list = level.getEntitiesOfClass(PlayerEntity.class, getBoundingBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);
				if (entity != null)
					if (entity instanceof PlayerEntity) {
						PlayerEntity player = (PlayerEntity) entity;
						if(!player.isSpectator() && !player.isCreative()) {
							player.addEffect(new EffectInstance(Effects.BLINDNESS, 60));
							player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(40, 1));
						}
					}
				}
			}
		return entity;
	}

	private void explode() {
		this.world.setEntityState(this, EVENT_EXPLODE_PARTICLES);
		level.playSound((PlayerEntity)null, getPosition(), SoundRegistry.SPLODESHROOM_POP, SoundCategory.HOSTILE, 0.5F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		setHasExploded(true);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_EXPLODE_PARTICLES) {
			for (int count = 0; count <= 200; ++count) {
				Particle fx = new ParticleBreaking.SnowballFactory().createParticle(ParticleTypes.SNOWBALL.getParticleID(), world, this.getX() + (world.rand.nextDouble() - 0.5D), this.getY() + 0.25f + world.rand.nextDouble(), this.getZ() + (world.rand.nextDouble() - 0.5D), 0, 0, 0, 0);
				fx.setRBGColorF(128F, 203F, 175F);
				Minecraft.getInstance().effectRenderer.addEffect(fx);
			}
		}
	}
	
	@Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (AOE_SIZE_XZ.equals(key)) 
            setAOESizeXZ(getAOESizeXZ());
        if (AOE_SIZE_Y.equals(key)) 
            setAOESizeY(getAOESizeY());
        super.notifyDataManagerChange(key);
    }

	protected void setBoundingBoxSize() {
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(posX - getAOESizeXZ() * 0.5D, posY, posZ - getAOESizeXZ() * 0.5D, posX + getAOESizeXZ() * 0.5D, posY + getAOESizeY(), posZ + getAOESizeXZ() * 0.5D);
		setEntityBoundingBox(axisalignedbb);
	}

	private void setSwelling(boolean swell) {
		entityData.set(IS_SWELLING, swell);
		//probably doesn't work
		if(swell)
			level.playSound((PlayerEntity)null, getPosition(), SoundRegistry.SPLODESHROOM_WINDUP, SoundCategory.HOSTILE, 0.5F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		else
			level.playSound((PlayerEntity)null, getPosition(), SoundRegistry.SPLODESHROOM_WINDDOWN, SoundCategory.HOSTILE, 0.5F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
	}

    public boolean getSwelling() {
        return entityData.get(IS_SWELLING);
    }

	private void setSwellCount(int swellCountIn) {
		entityData.set(SWELL_COUNT, swellCountIn);
	}

	public int getSwellCount() {
		return entityData.get(SWELL_COUNT);
	}

	private void setHasExploded(boolean hasExploded) {
		entityData.set(HAS_EXPLODED, hasExploded);
	}

    public boolean getHasExploded() {
        return entityData.get(HAS_EXPLODED);
    }

	private void setAOESizeXZ(float aoeSizeXZ) {
		entityData.set(AOE_SIZE_XZ, aoeSizeXZ);
	}

	public float getAOESizeXZ() {
		return entityData.get(AOE_SIZE_XZ);
	}

	private void setAOESizeY(float aoeSizeY) {
		entityData.set(AOE_SIZE_Y, aoeSizeY);
	}

	public float getAOESizeY() {
		return entityData.get(AOE_SIZE_Y);
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return !this.getHasExploded();
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY += y;
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
		if(source == DamageSource.OUT_OF_WORLD) {
			return true;
		}
		if (source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if (sourceEntity instanceof PlayerEntity) {
				if (!level.isClientSide()) {
					if(!getHasExploded())
						explode();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return 3F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		return null;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	private void spawnCloudParticle() {
		double x = this.getX() + (this.level.random.nextFloat() - 0.5F) / 2.0F;
		double y = this.getY() + 0.1D;
		double z = this.getZ() + (this.level.random.nextFloat() - 0.5F) / 2.0F;
		double mx = (this.level.random.nextFloat() - 0.5F) / 12.0F;
		double my = (this.level.random.nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (this.level.random.nextFloat() - 0.5F) / 12.0F;
		int[] color = {100, 100, 0, 255};

		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(8f));
		
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
		
		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(4f));

		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
}