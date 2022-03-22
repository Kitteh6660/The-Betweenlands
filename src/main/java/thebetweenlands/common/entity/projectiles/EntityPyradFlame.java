package thebetweenlands.common.entity.projectiles;

import java.util.Random;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntityPyradFlame extends SmallFireballEntity 
{
	public EntityPyradFlame(EntityType<? extends EntityPyradFlame> entity, World world) {
		super(entity, world);
	}

	public EntityPyradFlame(World world, LivingEntity entity, double x, double y, double z) {
		super(world, entity, x, y, z);
		//this.setSize(0.3125F, 0.3125F);
	}

	public EntityPyradFlame(World world, double x, double y, double z, double targetX, double targetY, double targetZ) {
		super(world, x, y, z, targetX, targetY, targetZ);
		//this.setSize(0.3125F, 0.3125F);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level.isClientSide()) {
			if (this.tickCount >= 1200) {
				this.remove();
			}
		}
		if (this.level.isClientSide()) {
			this.trailParticles(this.level, this.xOld, this.yOld, this.zOld, this.random);
		}
		if (this.isOnFire()) {
			this.clearFire();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void trailParticles(World world, double x, double y, double z, Random rand) {
		double velX = 0.0D;
		double velY = 0.0D;
		double velZ = 0.0D;
		int motionX = rand.nextInt(2) * 2 - 1;
		int motionZ = rand.nextInt(2) * 2 - 1;
		velY = (rand.nextFloat() - 0.5D) * 0.125D;
		velZ = rand.nextFloat() * 0.1F * motionZ;
		velX = rand.nextFloat() * 0.1F * motionX;
		if(rand.nextInt(4) == 0) {
			BLParticles.FLAME.spawn(world, x, y, z, ParticleArgs.get().withMotion(velX, velY, velZ));
		}
		BLParticles.WEEDWOOD_LEAF.spawn(world, x, y + this.getBbHeight() / 2.0F, z, ParticleArgs.get().withMotion(velX, velY, velZ).withColor(1F, 0.25F, 0.0F, 1.0F).withData(40));
	}
}
