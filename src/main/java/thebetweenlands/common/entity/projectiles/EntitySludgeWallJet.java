package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.BlockRegistry;

public class EntitySludgeWallJet extends EntityThrowable {

	private boolean playedSound = false;
	private static final byte EVENT_TRAIL_PARTICLES = 105;
	private static final byte EVENT_HIT_PARTICLES = 106;

	public EntitySludgeWallJet(World world) {
		super(world);
		setSize(0.2F, 0.2F);
	}

	public EntitySludgeWallJet(World world, MobEntity entity) {
		super(world, entity);
	}

	public EntitySludgeWallJet(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntitySludgeWallJet(World world, PlayerEntity player) {
		super(world, player);
	}

	@Override
	public void tick() {
		super.tick();
		if(!level.isClientSide())
			level.setEntityState(this, EVENT_TRAIL_PARTICLES);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_TRAIL_PARTICLES) {
			for(int i = 0; i < 8; ++i) {
				double velX = 0.0D;
				double velY = 0.0D;
				double velZ = 0.0D;
				int motionX = rand.nextInt(2) * 2 - 1;
				int motionZ = rand.nextInt(2) * 2 - 1;
				velY = (rand.nextFloat() - 0.5D) * 0.125D;
				velZ = rand.nextFloat() * 0.1F * motionZ;
				velX = rand.nextFloat() * 0.1F * motionX;
				BLParticles.ITEM_BREAKING.spawn(level, posX, posY, posZ, ParticleArgs.get().withMotion(velX, velY, velZ).withData(new ItemStack(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3)));
			}
		}

		if(id == EVENT_HIT_PARTICLES)
			for(int i = 0; i < 16; ++i)
				BLParticles.ITEM_BREAKING.spawn(level, posX + (level.rand.nextDouble() - 0.5D), posY + 2D + level.rand.nextDouble(), posZ + (level.rand.nextDouble() - 0.5D), ParticleArgs.get().withData(new ItemStack(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3)));
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_BOBBER_SPLASH;
	}

	@Override
	protected void onImpact(RayTraceResult mop) {
		if (!level.isClientSide()) {
			if (!playedSound) {
				level.playSound((PlayerEntity) null, getPosition(), getSplashSound(), SoundCategory.HOSTILE, 0.25F, 2.0F);
				playedSound = true;
			}
			if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.BLOCK)
				remove();
			if (mop.entityHit != null) {
				if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.ENTITY && mop.entityHit != thrower) {
					mop.entityHit.hurt(DamageSource.causeThrownDamage(this, getThrower()), 2F);
					level.setEntityState(this, EVENT_HIT_PARTICLES);
					remove();
				}
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	public boolean attackEntityFrom(DamageSource source, int amount) {
		return false;
	}
}