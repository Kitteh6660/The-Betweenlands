package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleAnimated extends Particle implements IParticleSpriteReceiver {
	
	protected TextureAnimation animation;

	public ParticleAnimated(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale) {
		this(world, x, y, z, mx, my, mz, maxAge, scale, true);
	}

	public ParticleAnimated(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale, boolean randomStart) {
		super(world, x, y, z);
		this.x = this.xOld = x;
		this.y = this.yOld = y;
		this.x = this.zOld = z;
		this.xo = mx;
		this.yo = my;
		this.zo = mz;
		this.particleScale = scale;
		this.animation = new TextureAnimation();
		if(randomStart) {
			this.animation.setRandomStart(this.rand);
		}
		this.lifetime = maxAge;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		if (this.animation != null && frames != null) {
			this.animation.setFrames(frames[0]);
			if(this.lifetime < 0) {
				this.lifetime = this.animation.getTotalDuration() - 1;
			}
			if (this.particleTexture == null) {
				this.setParticleTexture(frames[0][0].getSprite());
			}
		}
	}

	@Override
	public void tick() {
		this.animation.update();
		this.setParticleTexture(this.animation.getCurrentSprite());

		super.tick();
	}

	public static final class GenericFactory extends ParticleFactory<GenericFactory, ParticleAnimated> {
		public GenericFactory(ResourceLocation texture) {
			super(ParticleAnimated.class, ParticleTextureStitcher.create(ParticleAnimated.class, texture).setSplitAnimations(true));
		}

		@Override
		public ParticleAnimated createParticle(ImmutableParticleArgs args) {
			return new ParticleAnimated(args.world, args.x, args.y, args.z, args.xo, args.yo, args.zo, args.data.getInt(0), args.scale, args.data.getBool(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(40, false);
		}
	}

	public static final class PortalFactory extends ParticleFactory<PortalFactory, ParticleAnimated> {
		public PortalFactory() {
			super(ParticleAnimated.class, ParticleTextureStitcher.create(ParticleAnimated.class, new ResourceLocation("thebetweenlands:particle/portal")).setSplitAnimations(true));
		}

		@Override
		public ParticleAnimated createParticle(ImmutableParticleArgs args) {
			return new ParticleAnimated(args.world, args.x, args.y, args.z, args.xo, args.yo, args.zo, args.data.getInt(0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(40);
		}
	}

	public static final class SpawnerFactory extends ParticleFactory<PortalFactory, ParticleAnimated> {
		public SpawnerFactory() {
			super(ParticleAnimated.class, ParticleTextureStitcher.create(ParticleAnimated.class, new ResourceLocation("thebetweenlands:particle/spawner")).setSplitAnimations(true));
		}

		@Override
		public ParticleAnimated createParticle(ImmutableParticleArgs args) {
			return new ParticleAnimated(args.world, args.x, args.y, args.z, args.xo, args.yo, args.zo, args.data.getInt(0), args.scale) {
				private float startAlpha;

				{
					this.startAlpha = this.alpha;
				}

				@Override
				public void setAlpha(float alpha) {
					super.setAlpha(alpha);
					this.startAlpha = alpha;
				}

				@Override
				public void tick() {
					super.tick();

					if(this.age > this.lifetime - 40) {
						this.alpha = (this.startAlpha * (this.lifetime - this.age) / 40.0F);
					}
				}
			};
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(80);
		}
	}
}
