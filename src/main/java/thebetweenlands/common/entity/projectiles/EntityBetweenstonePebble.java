package thebetweenlands.common.entity.projectiles;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityChiromawGreeblingRider;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBetweenstonePebble extends Entity implements IProjectile, IThrowableEntity {
	private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>() {
				public boolean apply(@Nullable Entity p_apply_1_) {
					return p_apply_1_.canBeCollidedWith();
				}
			});

	private static final DataParameter<Byte> CRITICAL = EntityDataManager .<Byte>createKey(EntityBetweenstonePebble.class, DataSerializers.BYTE);
	public Entity shootingEntity;
	private double damage;
	private int knockbackStrength;
	private int ticksInAir;

	public EntityBetweenstonePebble(World world) {
		super(world);
		damage = 0.5D;
		setSize(0.5F, 0.5F);
	}

	public EntityBetweenstonePebble(World world, double x, double y, double z) {
		this(world);
		setPosition(x, y, z);
	}

	public EntityBetweenstonePebble(World world, LivingEntity shooter) {
		this(world, shooter.getX(), shooter.getY() + (double) shooter.getEyeHeight() - 0.10000000149011612D, shooter.getZ());
		shootingEntity = shooter;
	}

	public void setDamage(double damageIn) {
		damage = damageIn;
	}

	public double getDamage() {
		return damage;
	}

	public void setKnockbackStrength(int knockbackStrengthIn) {
		knockbackStrength = knockbackStrengthIn;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		float f = MathHelper.sqrt(x * x + y * y + z * z);
		x = x / (double) f;
		y = y / (double) f;
		z = z / (double) f;
		x = x + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		y = y + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		z = z + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		x = x * (double) velocity;
		y = y * (double) velocity;
		z = z * (double) velocity;
		motionX = x;
		motionY = y;
		motionZ = z;
		float f1 = MathHelper.sqrt(x * x + z * z);
		yRot = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
		xRot = (float) (MathHelper.atan2(y, (double) f1) * (180D / Math.PI));
		prevRotationYaw = yRot;
		prevRotationPitch = xRot;
	}

	public void shoot(Entity shooter, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy) {
		float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		float f1 = -MathHelper.sin(pitch * 0.017453292F);
		float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		shoot((double) f, (double) f1, (double) f2, velocity, inaccuracy);
		motionX += shooter.motionX;
		motionZ += shooter.motionZ;

		if (!shooter.onGround) {
			motionY += shooter.motionY;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	@OnlyIn(Dist.CLIENT)
	public void setVelocity(double x, double y, double z) {
		motionX = x;
		motionY = y;
		motionZ = z;

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			xRot = (float) (MathHelper.atan2(y, (double) f) * (180D / Math.PI));
			yRot = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
			prevRotationPitch = xRot;
			prevRotationYaw = yRot;
			moveTo(posX, posY, posZ, yRot, xRot);
		}
	}

	public void tick() {
		super.tick();

		if (!level.isClientSide())
			if (tickCount >= 1200)
				remove();
		++this.ticksInAir;
		Vector3d vec3d1 = new Vector3d(posX, posY, posZ);
		Vector3d vec3d = new Vector3d(posX + motionX, posY + motionY, posZ + motionZ);
		RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
		vec3d1 = new Vector3d(posX, posY, posZ);
		vec3d = new Vector3d(posX + motionX, posY + motionY, posZ + motionZ);

		if (raytraceresult != null)
			vec3d = new Vector3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);

		Entity entity = findEntityOnPath(vec3d1, vec3d);

		if (entity != null)
			raytraceresult = new RayTraceResult(entity);

		if (raytraceresult != null && raytraceresult.entityHit instanceof PlayerEntity) {
			PlayerEntity entityplayer = (PlayerEntity) raytraceresult.entityHit;
			if (shootingEntity instanceof PlayerEntity && !((PlayerEntity) shootingEntity).canAttackPlayer(entityplayer))
				raytraceresult = null;
		}

		if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
			onHit(raytraceresult);

		if (getIsCritical())
			for (int k = 0; k < 4; ++k)
				world.addParticle(ParticleTypes.CRIT, posX + motionX * (double) k / 4.0D, posY + motionY * (double) k / 4.0D, posZ + motionZ * (double) k / 4.0D, -motionX, -motionY + 0.2D, -motionZ);

		posX += motionX;
		posY += motionY;
		posZ += motionZ;

		float f1 = 0.99F;

		if (isInWater()) {
			for (int i = 0; i < 4; ++i) {
				float f3 = 0.25F;
				world.addParticle(ParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D, posY - motionY * 0.25D, posZ - motionZ * 0.25D, motionX, motionY, motionZ);
			}
			f1 = 0.6F;
		}

		if (isWet())
			clearFire();

		motionX *= (double) f1;
		motionY *= (double) f1;
		motionZ *= (double) f1;

		if (!hasNoGravity())
			motionY -= 0.05000000074505806D;

		setPosition(posX, posY, posZ);
		doBlockCollisions();
	}

	protected void onHit(RayTraceResult raytraceResultIn) {
		Entity entity = raytraceResultIn.entityHit;

		if (entity != null) {
			float f = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
			int i = MathHelper.ceil((double) f * damage);

			if (getIsCritical())
				i += rand.nextInt(i / 2 + 2);

			DamageSource damagesource;

			if (shootingEntity == null)
				damagesource = DamageSource.causeThrownDamage(this, this);
			else
				damagesource = DamageSource.causeThrownDamage(this, shootingEntity);

			if (isOnFire() && !(entity instanceof EntityEnderman))
				entity.setFire(5);

			if (entity.hurt(damagesource, (float) i)) {
				if (entity instanceof LivingEntity) {
					LivingEntity entitylivingbase = (LivingEntity) entity;

					if (knockbackStrength > 0) {
						float f1 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);

						if (f1 > 0.0F) {
							entitylivingbase.addVelocity(motionX * (double) knockbackStrength * 0.6000000238418579D / (double) f1, 0.1D, motionZ * (double) knockbackStrength * 0.6000000238418579D / (double) f1);
						}
					}

					if (shootingEntity instanceof LivingEntity) {
						EnchantmentHelper.applyThornEnchantments(entitylivingbase, shootingEntity);
						EnchantmentHelper.applyArthropodEnchantments((LivingEntity) shootingEntity, entitylivingbase);
						if (shootingEntity instanceof EntityChiromawGreeblingRider) {
							level.playSound(null, shootingEntity.getPosition(), SoundRegistry.GREEBLING_GIGGLE, SoundCategory.HOSTILE, 1F, 1F);
						}
					}

					if (shootingEntity != null && entitylivingbase != shootingEntity && entitylivingbase instanceof PlayerEntity && shootingEntity instanceof ServerPlayerEntity) {
						((ServerPlayerEntity) shootingEntity).connection .sendPacket(new SPacketChangeGameState(6, 0.0F));
					}
				}

				
				if (!(entity instanceof EntityEnderman))
					remove();
				
			} else {
				motionX *= -0.10000000149011612D;
				motionY *= -0.10000000149011612D;
				motionZ *= -0.10000000149011612D;
				this.ticksInAir = 0;

				if (!level.isClientSide() && motionX * motionX + motionY * motionY + motionZ * motionZ < 0.0010000000474974513D)
					remove();
			}
		} else
			remove();
	
		level.playSound(null, getPosition(), SoundRegistry.SLINGSHOT_HIT, SoundCategory.HOSTILE, 1F, 1F + (level.rand.nextFloat() - level.rand.nextFloat()) * 0.8F);
	}

	public void move(MoverType type, double x, double y, double z) {
		super.move(type, x, y, z);
	}

	@Nullable
	protected Entity findEntityOnPath(Vector3d start, Vector3d end) {
		Entity entity = null;
		List<Entity> list = world.getEntitiesInAABBexcluding(this, getBoundingBox().expand(motionX, motionY, motionZ).inflate(1.0D), ARROW_TARGETS);
		double d0 = 0.0D;
		for (int i = 0; i < list.size(); ++i) {
			Entity entity1 = list.get(i);
			if (entity1 != shootingEntity || this.ticksInAir >= 5) {
				AxisAlignedBB axisalignedbb = entity1.getBoundingBox().inflate(0.30000001192092896D);
				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
				if (raytraceresult != null) {
					double d1 = start.squareDistanceTo(raytraceresult.hitVec);
					if (d1 < d0 || d0 == 0.0D) {
						entity = entity1;
						d0 = d1;
					}
				}
			}
		}
		return entity;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(CRITICAL, Byte.valueOf((byte) 0));
	}

	public void setIsCritical(boolean critical) {
		byte b0 = ((Byte) entityData.get(CRITICAL)).byteValue();
		if (critical)
			entityData.set(CRITICAL, Byte.valueOf((byte) (b0 | 1)));
		else
			entityData.set(CRITICAL, Byte.valueOf((byte) (b0 & -2)));
	}

	public boolean getIsCritical() {
		byte b0 = ((Byte) entityData.get(CRITICAL)).byteValue();
		return (b0 & 1) != 0;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return 0.0F;
	}

	@Override
	public void load(CompoundNBT compound) {
		if (compound.contains("damage", 99))
			damage = compound.getDouble("damage");
		setIsCritical(compound.getBoolean("crit"));
	}

	@Override
	public void save(CompoundNBT compound) {
		compound.setDouble("damage", damage);
		compound.putBoolean("crit", getIsCritical());
	}

	@Override
	public Entity getThrower() {
		return shootingEntity;
	}

	@Override
	public void setThrower(Entity entity) {
		shootingEntity = entity;
	}
}
