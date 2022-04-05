package thebetweenlands.client.render.particle.entity;

import java.util.function.Supplier;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleEmissiveSwarm extends ParticleSwarm {
	private TextureAnimation emissiveAnimation;

	protected ParticleEmissiveSwarm(World world, double x, double y, double z, double mx, double my, double mz, Direction face, float scale, int maxAge, Vector3d start, Supplier<Vector3d> end) {
		super(world, x, y, z, mx, my, mz, face, scale, maxAge, start, end);
		this.emissiveAnimation = new TextureAnimation();
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		if (this.animation != null && frames != null) {
			if(frames.length % 2 != 0) {
				throw new IllegalStateException("Emissive particle requires a multiple of two number of sprites");
			}
			
			int variant = this.random.nextInt(frames.length / 2);

			this.animation.setFrames(frames[variant * 2]);
			this.emissiveAnimation.setFrames(frames[variant * 2 + 1]);

			ResourceLocation location = frames[variant * 2][0].getLocation();
			if(location instanceof ResourceLocationWithScale) {
				this.particleScale *= ((ResourceLocationWithScale) location).scale;
			}

			if(this.lifetime < 0) {
				this.lifetime = this.animation.getTotalDuration() - 1;
			}
			if (this.particleTexture == null) {
				this.setParticleTexture(frames[variant * 2][0].getSprite());
			}
		}
	}

	@Override
	public void tick() {
		super.tick();

		this.emissiveAnimation.update();
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		this.setParticleTexture(this.animation.getCurrentSprite());
		super.renderParticle(buff, entityIn, partialTicks, rx, rz, ryz, rxy, rxz);

		int prevLightmapX = this.lightmapX;
		int prevLightmapY = this.lightmapY;
		
		this.lightmapX = 15 << 4;
		this.lightmapY = 15 << 4;
		
		this.setParticleTexture(this.emissiveAnimation.getCurrentSprite());
		super.renderParticle(buff, entityIn, partialTicks, rx, rz, ryz, rxy, rxz);
		
		this.lightmapX = prevLightmapX;
		this.lightmapY = prevLightmapY;
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		if(this.particleTexture == this.emissiveAnimation.getCurrentSprite()) {
			return 15 << 20 | 15 << 4;
		}
		return super.getBrightnessForRender(partialTick);
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleEmissiveSwarm> {
		public Factory() {
			super(ParticleEmissiveSwarm.class, 
					ParticleTextureStitcher.create(ParticleEmissiveSwarm.class,
							new ResourceLocationWithScale("thebetweenlands:particle/swarm_4", 2), new ResourceLocationWithScale("thebetweenlands:particle/swarm_4_emissive", 2)
							).setSplitAnimations(true));
		}

		@SuppressWarnings("unchecked")
		@Override
		public ParticleEmissiveSwarm createParticle(ImmutableParticleArgs args) {
			return new ParticleEmissiveSwarm(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(Direction.class, 0), args.scale, args.data.getInt(1), args.data.getObject(Vector3d.class, 2), args.data.getObject(Supplier.class, 3));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Direction.UP, 40, Vector3d.ZERO, (Supplier<Vector3d>) () -> Vector3d.ZERO);
		}
	}
}
