package thebetweenlands.common.entity.projectiles;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.entity.EntitySplodeshroom;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawDroppings extends Entity {

	private static final DataParameter<Boolean> HAS_EXPLODED = EntityDataManager.define(EntitySplodeshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> AOE_SIZE_XZ = EntityDataManager.define(EntitySplodeshroom.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> AOE_SIZE_Y = EntityDataManager.define(EntitySplodeshroom.class, DataSerializers.FLOAT);

	public float prevRotationTicks = 0;
	public float rotationTicks = 0;
	protected LivingEntity thrower;
    public Entity ignoreEntity;
    private int ignoreTime;

	public EntityChiromawDroppings(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setEntityInvulnerable(true);
	}

	public EntityChiromawDroppings(World world, LivingEntity throwerIn, double x, double y, double z) {
		super(world);
		setSize(0.5F, 0.5F);
		setPosition(x, y, z);
		setEntityInvulnerable(true);
		thrower = throwerIn;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(HAS_EXPLODED, false);
		this.entityData.define(AOE_SIZE_XZ, 4F);
		this.entityData.define(AOE_SIZE_Y, 0.5F);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnDroppingsParticles() {
		double d0 = this.getX() - 0.075F;
		double d1 = this.getY() + this.motionY;
		double d2 = this.getZ() - 0.075F;
		double d3 = this.getX() + 0.075F;
		double d4 = this.getZ() + 0.075F;
		double d5 = this.getX();
		double d6 = this.getY() + this.motionY + 0.25F;
		double d7 = this.getZ();

		BLParticles.CHIROMAW_DROPPINGS.spawn(level, d0, d1, d4, ParticleArgs.get().withMotion(0.08f * (d1) * (random.nextFloat() - 0.5f), this.motionY + 0.1f * (random.nextFloat() - 0.5f), 0.08f * (d1) * (random.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		BLParticles.CHIROMAW_DROPPINGS.spawn(level, d3, d1, d2, ParticleArgs.get().withMotion(0.08f * (d1) * (random.nextFloat() - 0.5f), this.motionY + 0.1f * (random.nextFloat() - 0.5f), 0.08f * (d1) * (random.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		BLParticles.CHIROMAW_DROPPINGS.spawn(level, d3, d1, d4, ParticleArgs.get().withMotion(0.08f * (d1) * (random.nextFloat() - 0.5f), this.motionY + 0.1f * (random.nextFloat() - 0.5f), 0.08f * (d1) * (random.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		BLParticles.CHIROMAW_DROPPINGS.spawn(level, d0, d1, d2, ParticleArgs.get().withMotion(0.08f * (d1) * (random.nextFloat() - 0.5f), this.motionY + 0.1f * (random.nextFloat() - 0.5f), 0.08f * (d1) * (random.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		BLParticles.CHIROMAW_DROPPINGS.spawn(level, d5, d6, d7, ParticleArgs.get().withMotion(0.08f * (d1) * (random.nextFloat() - 0.5f), this.motionY + 0.1f * (random.nextFloat() - 0.5f), 0.08f * (d1) * (random.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		BLParticles.CHIROMAW_DROPPINGS.spawn(level, d0, d1, d2, ParticleArgs.get().withMotion(0.08f * (d1) * (random.nextFloat() - 0.5f), this.motionY + 0.1f * (random.nextFloat() - 0.5f), 0.08f * (d1) * (random.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
	}
	
	@Override
	public void tick() {
		super.tick();
		// TODO in case we want an animation grab smoothedAngle(float partialTicks) method for render class
		prevRotationTicks = rotationTicks;
		rotationTicks += 15;
		float wrap = MathHelper.wrapDegrees(rotationTicks) - rotationTicks;
		rotationTicks +=wrap;
		prevRotationTicks += wrap;

		if (!level.isClientSide()) {
			if(getHasExploded()) {
				if (level.getGameTime() % 5 == 0)
					checkAreaOfEffect();
				if (getAOESizeXZ() > 0.5F)
					setAOESizeXZ(getAOESizeXZ() - 0.01F);
				if (getAOESizeXZ() <= 0.5F)
					remove();
			}
		} else {
			if (!getHasExploded() && this.tickCount % 4 == 0) {
				this.spawnDroppingsParticles();
			}
		}

		if (getHasExploded())
			setBoundingBoxSize();

		if (level.isClientSide())
			if(getHasExploded())
				spawnCloudParticle();

		lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;

        Vector3d poopVector = new Vector3d(posX, posY, posZ);
        Vector3d poopMovementVector = new Vector3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult raytraceresult = world.rayTraceBlocks(poopVector, poopMovementVector);
        poopVector = new Vector3d(posX, posY, posZ);
        poopMovementVector = new Vector3d(posX + motionX, posY + motionY, posZ + motionZ);

        if (raytraceresult != null)
        	poopMovementVector = new Vector3d(raytraceresult.getLocation().x, raytraceresult.getLocation().y, raytraceresult.getLocation().z);

        Entity entityCollidedWith = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getBoundingBox().expand(motionX, motionY, motionZ).inflate(1.0D));
        double collisionDistance = 0D;
        boolean miss = false;

        for (int entityCount = 0; entityCount < list.size(); ++entityCount)  {
            Entity entityInList = list.get(entityCount);

            if (entityInList.canBeCollidedWith()) {
                if (entityInList == ignoreEntity)
                	miss = true;
                else if (thrower != null && tickCount < 2 && ignoreEntity == null) {
                    ignoreEntity = entityInList;
                    miss = true;
                }
                else {
                	miss = false;
                    AxisAlignedBB axisalignedbb = entityInList.getBoundingBox().inflate(0.30000001192092896D);
                    RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(poopVector, poopMovementVector);

                    if (raytraceresult1 != null) {
                        double inpactDistance = poopVector.squareDistanceTo(raytraceresult1.getLocation());

                        if (inpactDistance < collisionDistance || collisionDistance == 0.0D) {
                        	entityCollidedWith = entityInList;
                            collisionDistance = inpactDistance;
                        }
                    }
                }
            }
        }

        if (ignoreEntity != null)
            if (miss)
                ignoreTime = 2;
            else if (ignoreTime-- <= 0)
                ignoreEntity = null;

        if (entityCollidedWith != null)
            raytraceresult = new RayTraceResult(entityCollidedWith);

        if (raytraceresult != null)
            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && level.getBlockState(raytraceresult.getLocation()).getBlock() == Blocks.NETHER_PORTAL)
                setPortal(raytraceresult.getBlockPos());
            else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
                onImpact(raytraceresult);

        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        float fallAmount = 0.99F;
        float fallAmountWithGravity = getGravityVelocity();

        if (isInWater()) {
            for (int particleCount = 0; particleCount < 4; ++particleCount)
                world.addParticle(ParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D, posY - motionY * 0.25D, posZ - motionZ * 0.25D, motionX, motionY, motionZ);
            fallAmount = 0.8F;
        }

        if(getHasExploded())
        	fallAmount = 0.0F;

        motionX *= (double)fallAmount;
        motionY *= (double)fallAmount;
        motionZ *= (double)fallAmount;

        if (!hasNoGravity())
            motionY -= (double)fallAmountWithGravity;

        if (raytraceresult != null)
        	if(getHasExploded())
        		if(posY < raytraceresult.getLocation().y)
        			posY = raytraceresult.getLocation().y;

        setPosition(posX, posY, posZ);
	}

	@Nullable
	protected Entity checkAreaOfEffect() {
		Entity entity = null;
		if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
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

    protected float getGravityVelocity() {
    	if(!getHasExploded())
    		return 0.03F;
    	return 0F;
    }

    @OnlyIn(Dist.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return prevRotationTicks + (rotationTicks - prevRotationTicks) * partialTicks;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    protected boolean canBeRidden(Entity entityIn) {
        return false;
    }

	protected void onImpact(RayTraceResult result) {
		if (!getHasExploded() && result.hitInfo != null) {
			if(result.getType() == RayTraceResult.Type.BLOCK || result.getType() == RayTraceResult.Type.ENTITY && !(result.entityHit instanceof EntityChiromawDroppings) && result.entityHit != thrower)
			if (!level.isClientSide()) {
				setHasExploded(true);
				level.playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_SPLAT, SoundCategory.HOSTILE, 1F, 1F + (level.rand.nextFloat() - level.rand.nextFloat()) * 0.8F);
			}
		}
	}

	protected void setBoundingBoxSize() {
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(posX - getAOESizeXZ() * 0.5D, posY, posZ - getAOESizeXZ() * 0.5D, posX + getAOESizeXZ() * 0.5D, posY + getAOESizeY(), posZ + getAOESizeXZ() * 0.5D);
		setEntityBoundingBox(axisalignedbb);
	}

	@Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (AOE_SIZE_XZ.equals(key)) 
            setAOESizeXZ(getAOESizeXZ());
        if (AOE_SIZE_Y.equals(key)) 
            setAOESizeY(getAOESizeY());
        super.notifyDataManagerChange(key);
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

	@OnlyIn(Dist.CLIENT)
	private void spawnCloudParticle() {
		double x = posX + (world.rand.nextFloat() - 0.5F) / 2.0F;
		double y = posY + 0.1D;
		double z = posZ + (world.rand.nextFloat() - 0.5F) / 2.0F;
		double mx = (world.rand.nextFloat() - 0.5F) / 12.0F;
		double my = (world.rand.nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (world.rand.nextFloat() - 0.5F) / 12.0F;
		int[] color = {100, 100, 0, 255};

		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(8f));
		
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
		
		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(world, x, y, z, ParticleFactory.ParticleArgs.get()
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

	@Override
	public void load(CompoundNBT compound) {
	}

	@Override
	public void save(CompoundNBT compound) {
	}
}
