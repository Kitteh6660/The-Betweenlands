package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityFlameJet extends MobEntity {
	public LivingEntity shootingEntity;
	public EntityFlameJet(World world) {
		super(world);
		setSize(1F, 2.5F);
		setEntityInvulnerable(true);
	}

	public EntityFlameJet(World world, LivingEntity shooter) {
		super(world);
		setSize(1F, 2.5F);
		setEntityInvulnerable(true);
		this.shootingEntity = shooter;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide()) {
			if (tickCount > 20)
				remove();
		} else {
			if (tickCount == 1) {
				this.spawnFlameJetParticles();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnFlameJetParticles() {
		for (double yy = this.getY(); yy < this.getY() + 2D; yy += 0.5D) {
			double d0 = this.getX() - 0.075F;
			double d1 = yy;
			double d2 = this.getZ() - 0.075F;
			double d3 = this.getX() + 0.075F;
			double d4 = this.getZ() + 0.075F;
			double d5 = this.getX();
			double d6 = yy + 0.25F;
			double d7 = this.getZ();
			this.world.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.01D, 0.0D);
			this.world.addParticle(ParticleTypes.FLAME, d0, d1, d4, 0.0D, 0.01D, 0.0D);
			this.world.addParticle(ParticleTypes.FLAME, d3, d1, d2, 0.0D, 0.01D, 0.0D);
			this.world.addParticle(ParticleTypes.FLAME, d3, d1, d4, 0.0D, 0.01D, 0.0D);
			this.world.addParticle(ParticleTypes.FLAME, d5, d6, d7, 0.0D, 0.01D, 0.0D);
		}
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		if (!level.isClientSide()) {
			if (entity.getBoundingBox().maxY >= getBoundingBox().minY && entity.getBoundingBox().minY <= getBoundingBox().maxY)
				if (entity.getBoundingBox().maxX >= getBoundingBox().minX && entity.getBoundingBox().minX <= getBoundingBox().maxX)
					if (entity.getBoundingBox().maxZ >= getBoundingBox().minZ && entity.getBoundingBox().minZ <= getBoundingBox().maxZ)
						if (entity instanceof LivingEntity && !(entity instanceof EntityFlameJet))
							if (!entity.fireImmune()) {
								boolean catch_fire = entity.hurt(causeFlameJetDamage(this, shootingEntity), 5.0F);
								if (catch_fire)
									entity.setFire(5);
							}
							else {
								if (entity != shootingEntity)
									entity.hurt(DamageSource.causeIndirectDamage(this, shootingEntity).setProjectile(), 2.0F);
							}
			remove();
		}
	}

	public static DamageSource causeFlameJetDamage(EntityFlameJet flame_jet, @Nullable Entity indirectEntityIn) {
		return indirectEntityIn == null ? (new EntityDamageSourceIndirect("onFire", flame_jet, flame_jet)).setFireDamage().setProjectile() : (new EntityDamageSourceIndirect("flamejet", flame_jet, indirectEntityIn)).setFireDamage().setProjectile();
	}
}