package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleLifeEssence extends Particle implements IParticleSpriteReceiver {
	private static final int MAX_PARTICLES = 9;

	private double offsetX, offsetY, offsetZ, radius;
	private final LivingEntity entity;

	private TextureAtlasSprite glowSprite;
	private TextureAnimation[] animations;
	private int rotationTicks;
	private int particles = MAX_PARTICLES;

	public ParticleLifeEssence(World world, LivingEntity entity, double offsetX, double offsetY, double offsetZ, double radius, int rotationTicks) {
		super(world, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ);
		this.motionX = this.motionY = this.motionZ = 0;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.radius = radius;
		this.entity = entity;
		this.particles = MathHelper.clamp(MathHelper.ceil(entity.getHealth() / entity.getMaxHealth() * MAX_PARTICLES), 0, MAX_PARTICLES);
		this.rotationTicks = rotationTicks;
		this.lifetime = 60;
		this.canCollide = false;
		this.animations = new TextureAnimation[MAX_PARTICLES];
		for(int i = 0; i < MAX_PARTICLES; i++) {
			this.animations[i] = new TextureAnimation().setRandomStart(entity.getRandom());
		}
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		for(int i = 0; i < MAX_PARTICLES; i++) {
			this.animations[i].setFrames(frames[0]);
		}
		this.glowSprite = frames[1][0].getSprite();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void tick() {
		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();

		if(this.age++ >= this.lifetime) {
			this.setExpired();
		}

		this.particles = MathHelper.clamp(MathHelper.ceil(this.entity.getHealth() / this.entity.getMaxHealth() * MAX_PARTICLES), 0, MAX_PARTICLES);

		this.rotationTicks++;

		for(int i = 0; i < this.particles; i++) {
			this.animations[i].update();
		}

		this.setPosition(this.entity.getX() + this.offsetX, this.entity.getY() + this.offsetY, this.entity.getZ() + this.offsetZ);
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.age > this.lifetime - 10) {
			this.alpha = (this.lifetime - this.age) / 10.0F;
		} else if(this.age < 10) {
			this.alpha = this.age / 10.0F;
		} else {
			this.alpha = 1.0F;
		}

		double px = this.getX();
		double py = this.getY();
		double pz = this.getZ();
		double ppx = this.xOld;
		double ppy = this.yOld;
		double ppz = this.zOld;

		float interval = (float)(Math.PI) * 2.0F / this.particles;
		float prevAngle = (this.rotationTicks - 1) / 120.0F * (float)(Math.PI) * 2.0F;
		float angle = this.rotationTicks / 120.0F * (float)(Math.PI) * 2.0F;

		float scale = (float) (this.radius) * 2.0F * (0.25F + this.particles / (float)MAX_PARTICLES * 0.75F);

		for(int i = 0; i < this.particles; i++) {
			double prevXo = Math.cos(prevAngle + interval * i) * this.radius;
			double prevYo = Math.cos(prevAngle + interval * i * 5.678F) * 0.05F;
			double prevZo = Math.sin(prevAngle + interval * i) * this.radius;
			double xo = Math.cos(angle + interval * i) * this.radius;
			double yo = Math.cos(angle + interval * i * 5.678F) * 0.05F;
			double zo = Math.sin(angle + interval * i) * this.radius;

			this.xOld = ppx + prevXo;
			this.zOld = ppz + prevZo;
			this.getX() = px + xo;
			this.getZ() = pz + zo;

			this.yOld = ppy + prevYo - scale * 0.025f;
			this.getY() = py + yo - scale * 0.025f;

			this.particleRed = 0.1F;
			this.particleGravity = 0.1F;
			this.particleBlue = 1.0F;
			this.particleScale = scale * 2.5F;
			this.particleTexture = this.glowSprite;
			float prevAlpha = this.alpha;
			this.alpha *= 0.05F;

			super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

			this.alpha *= 2.0F;
			this.particleRed = 1.0F;
			this.particleGravity = 1.0F;
			this.particleBlue = 1.0F;
			this.particleScale = scale * 1.25F;

			super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

			this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
			this.alpha = prevAlpha;

			this.yOld = ppy + prevYo;
			this.getY() = py + yo;

			this.particleScale = scale;
			this.particleTexture = this.animations[i].getCurrentSprite();

			super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
		}

		this.getX() = px;
		this.getY() = py;
		this.getZ() = pz;
		this.xOld = ppx;
		this.yOld = ppy;
		this.zOld = ppz;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleLifeEssence> {
		public Factory() {
			super(ParticleLifeEssence.class, ParticleTextureStitcher.create(ParticleLifeEssence.class,
					new ResourceLocation("thebetweenlands:particle/life_essence"),
					new ResourceLocation("thebetweenlands:particle/wisp"))
					.setSplitAnimations(true));
		}

		@Override
		public ParticleLifeEssence createParticle(ImmutableParticleArgs args) {
			return new ParticleLifeEssence(args.world, args.data.getObject(LivingEntity.class, 0), args.x, args.y, args.z, args.scale, args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withDataBuilder().setData(1, 20).buildData();
		}
	}
}
