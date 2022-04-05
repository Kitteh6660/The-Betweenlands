package thebetweenlands.client.render.particle.entity;

import java.util.Random;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.ParticleFactory;

@OnlyIn(Dist.CLIENT)
public class ParticlePuzzleBeam2 extends ParticleBeam {
	public ParticlePuzzleBeam2(World worldIn, double x, double y, double z, double vx, double vy, double vz, float scale, int lifetime, Vector3d end) {
		super(worldIn, x, y, z, 0, 0, 0, end);
		this.lifetime = lifetime;
		this.particleScale = scale;
		this.texUScale = scale * 4;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.alpha = 0;
	}

	@Override
	public int getBrightnessForRender(float pTicks) {
		return 255;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.age > this.lifetime - 10) {
			this.alpha = (this.lifetime - this.age) / 10.0F;
		} else if(this.age < 10) {
			this.alpha = this.age / 10.0F;
		} else {
			this.alpha = 1.0F;
		}
		
		if(this.particleRed > 1) {
			this.particleRed /= 255.0f;
		}

		if(this.particleGreen > 1) {
			this.particleGreen /= 255.0f;
		}

		if(this.particleBlue > 1) {
			this.particleBlue /= 255.0f;
		}

		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	public static Random random = new Random();

	@Override
	public void tick() {
		super.tick();
		
		this.texUOffset = -this.age / (float)this.lifetime * 4;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticlePuzzleBeam2> {
		public Factory() {
			super(ParticlePuzzleBeam2.class);
		}

		@Override
		public ParticlePuzzleBeam2 createParticle(ImmutableParticleArgs args) {
			return new ParticlePuzzleBeam2(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getInt(0), args.data.getObject(Vector3d.class, 1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(20, new Vector3d(0, 0, 1));
		}
	}
}
