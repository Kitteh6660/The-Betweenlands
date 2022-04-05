package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleSimple extends Particle implements IParticleSpriteReceiver {
	private float startAlpha = 1.0F;
	private boolean fade = false;

	public ParticleSimple(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int maxAge, float scale, boolean fade, float gravity, boolean exactMotion) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.lifetime = maxAge;
		this.particleScale = scale;
		this.particleGravity = gravity;
		this.fade = fade;
		if(exactMotion) {
			this.motionX = xSpeedIn;
			this.motionY = ySpeedIn;
			this.motionZ = zSpeedIn;
		}
		if(fade) {
			this.alpha = 0;
		}
	}

	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		this.startAlpha = alpha;
		if(this.fade) {
			this.alpha = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();

		if(this.fade) {
			int fadeOutTime = Math.max(1, Math.min(40, (this.lifetime - 10) / 2));
			if(this.age > this.lifetime - fadeOutTime) {
				this.alpha = (this.startAlpha * (this.lifetime - this.age) / (float)fadeOutTime);
			} else if(this.age <= 10) {
				this.alpha = this.startAlpha * this.age / 10.0f;
			}
		}
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	public static final class GenericFactory extends ParticleFactory<GenericFactory, ParticleSimple> {
		public GenericFactory(ResourceLocation texture) {
			super(ParticleSimple.class, ParticleTextureStitcher.create(ParticleSimple.class, texture));
		}

		@Override
		public ParticleSimple createParticle(ImmutableParticleArgs args) {
			return new ParticleSimple(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale, args.data.getBool(1), args.data.getFloat(2), args.data.getBool(3));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(80, true, 0.0F, false);
		}
	}
}
