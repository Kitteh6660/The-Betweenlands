package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.audio.GreeblingFallSound;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGreeblingVolarpadFloater extends EntityThrowable {
	
	protected static final byte EVENT_START_DISAPPEARING = 40;
	protected static final byte EVENT_DISAPPEAR = 41;
	public int disappearTimer = 0;
	protected float prevFloatingRotationTicks = 0;
	protected float floatingRotationTicks = 0;

	public EntityGreeblingVolarpadFloater(World world) {
		super(world);
		setSize(1F, 1F);
		setEntityInvulnerable(true);
	}

	public EntityGreeblingVolarpadFloater(World world, double x, double y, double z) {
		super(world);
		setSize(1F, 1F);
		setPosition(x, y, z);
		setEntityInvulnerable(true);
		motionX = 0.25D - (world.rand.nextDouble() * 0.5D);
		motionZ = 0.25D - (world.rand.nextDouble() * 0.5D);
	}

	@Override
	public void tick() {
		super.tick();
		if(tickCount == 1)
			if (level.isClientSide())
				playFallingSound(level, getPosition());

		if (motionY < 0.0D)
			motionY *= 0.5D;

		prevFloatingRotationTicks = floatingRotationTicks;
		floatingRotationTicks += 5;
		float wrap = MathHelper.wrapDegrees(floatingRotationTicks) - floatingRotationTicks;
		floatingRotationTicks +=wrap;
		prevFloatingRotationTicks += wrap;
		
		if (disappearTimer > 0 && disappearTimer < 8)
			disappearTimer++;
		
		if(!level.isClientSide()) {
			if (disappearTimer == 5)
				level.setEntityState(this, EVENT_DISAPPEAR);
			if (disappearTimer >= 8)
				remove();
			List<PlayerEntity> nearPlayers = level.getEntitiesOfClass(PlayerEntity.class, getBoundingBox().inflate(4.5, 5, 4.5));
			if (disappearTimer == 0 && (!nearPlayers.isEmpty() || tickCount > 80))
				startVanishEvent();
		}
	}

	public boolean isFloating() {
		return motionY < 0D;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void playFallingSound(World world, BlockPos pos) {
		ISound fall_sound = new GreeblingFallSound(this);
		Minecraft.getInstance().getSoundHandler().playSound(fall_sound);
	}

	public void startVanishEvent() {
		disappearTimer++;
		level.playSound(null, posX, posY, posZ, SoundRegistry.GREEBLING_VANISH, SoundCategory.NEUTRAL, 1, 1);
		level.setEntityState(this, EVENT_START_DISAPPEARING);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_START_DISAPPEARING)
			disappearTimer = 1;
		else if(id == EVENT_DISAPPEAR)
			doLeafEffects();
	}
	
	private void doLeafEffects() {
		if(level.isClientSide()) {
			int leafCount = 40;
			float x = (float) (posX);
			float y = (float) (posY) + 0.5F;
			float z = (float) (posZ);
			while (leafCount-- > 0) {
				float dx = level.rand.nextFloat() * 1 - 0.5f;
				float dy = level.rand.nextFloat() * 1f - 0.1F;
				float dz = level.rand.nextFloat() * 1 - 0.5f;
				float mag = 0.08F + level.rand.nextFloat() * 0.07F;
				BLParticles.WEEDWOOD_LEAF.spawn(level, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

    public float smoothedAngle(float partialTicks) {
        return prevFloatingRotationTicks + (floatingRotationTicks - prevFloatingRotationTicks) * partialTicks;
    }

	@Override
    protected float getGravityVelocity() {
        return 0.09F;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    protected boolean canBeRidden(Entity entityIn) {
        return false;
    }

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.typeOfHit != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
			if (!level.isClientSide())
				startVanishEvent();
	}
}
