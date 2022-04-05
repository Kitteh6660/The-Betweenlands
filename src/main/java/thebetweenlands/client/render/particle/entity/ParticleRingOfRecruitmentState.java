package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleRingOfRecruitmentState extends Particle implements IParticleSpriteReceiver {
	private double offsetX, offsetY, offsetZ, radius;
	private final LivingEntity entity;
	private float maxAlpha = 1.0f;

	public ParticleRingOfRecruitmentState(World world, LivingEntity entity, double offsetX, double offsetY, double offsetZ, float scale) {
		super(world, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ);
		this.motionX = this.motionY = this.motionZ = 0;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.entity = entity;
		this.lifetime = 60;
		this.particleScale = scale;
		this.canCollide = false;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setAlpha(float alpha) {
		this.maxAlpha = alpha;
	}

	@Override
	public void tick() {
		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();

		if(this.age++ >= this.lifetime) {
			this.setExpired();
		}

		this.setPosition(this.entity.getX() + this.offsetX, this.entity.getY() + this.offsetY, this.entity.getZ() + this.offsetZ);
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.age > this.lifetime - 10) {
			this.alpha = (this.lifetime - this.age) / 10.0F * this.maxAlpha;
		} else if(this.age < 10) {
			this.alpha = this.age / 10.0F * this.maxAlpha;
		} else {
			this.alpha = this.maxAlpha;
		}

		double yOld = this.getY();
		double prevPrevPosY = this.yOld;

		double floatOffset = Math.sin((entityIn.tickCount + partialTicks) * 0.05f) * 0.05f;

		this.getY() += floatOffset;
		this.yOld += floatOffset;

		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

		this.getY() = yOld;
		this.yOld = prevPrevPosY;
	}

	public static final class FactoryFollow extends ParticleFactory<FactoryFollow, ParticleRingOfRecruitmentState> {
		public FactoryFollow() {
			super(ParticleRingOfRecruitmentState.class, ParticleTextureStitcher.create(ParticleRingOfRecruitmentState.class, new ResourceLocation("thebetweenlands:particle/ring_of_recruitment_follow")));
		}

		@Override
		public ParticleRingOfRecruitmentState createParticle(ImmutableParticleArgs args) {
			return new ParticleRingOfRecruitmentState(args.world, args.data.getObject(LivingEntity.class, 0), args.x, args.y, args.z, args.scale);
		}
	}

	public static final class FactoryGuard extends ParticleFactory<FactoryGuard, ParticleRingOfRecruitmentState> {
		public FactoryGuard() {
			super(ParticleRingOfRecruitmentState.class, ParticleTextureStitcher.create(ParticleRingOfRecruitmentState.class, new ResourceLocation("thebetweenlands:particle/ring_of_recruitment_guard")));
		}

		@Override
		public ParticleRingOfRecruitmentState createParticle(ImmutableParticleArgs args) {
			return new ParticleRingOfRecruitmentState(args.world, args.data.getObject(LivingEntity.class, 0), args.x, args.y, args.z, args.scale);
		}
	}

	public static final class FactoryStay extends ParticleFactory<FactoryStay, ParticleRingOfRecruitmentState> {
		public FactoryStay() {
			super(ParticleRingOfRecruitmentState.class, ParticleTextureStitcher.create(ParticleRingOfRecruitmentState.class, new ResourceLocation("thebetweenlands:particle/ring_of_recruitment_stay")));
		}

		@Override
		public ParticleRingOfRecruitmentState createParticle(ImmutableParticleArgs args) {
			return new ParticleRingOfRecruitmentState(args.world, args.data.getObject(LivingEntity.class, 0), args.x, args.y, args.z, args.scale);
		}
	}
}
