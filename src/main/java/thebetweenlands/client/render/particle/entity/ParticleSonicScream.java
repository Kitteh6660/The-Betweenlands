package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleSonicScream extends ParticleAnimated implements IParticleSpriteReceiver {
	protected final Vector3d dir;
	protected final Vector3d up;

	protected float initialAlpha;

	protected int initialFrame;

	protected ParticleSonicScream(World world, double x, double y, double z, double mx, double my, double mz, float scale, int maxAge, int initialFrame) {
		super(world, x, y, z, 0, 0, 0, maxAge, scale, false);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.getX() = this.xOld = x;
		this.getY() = this.yOld = y;
		this.getZ() = this.zOld = z;
		this.dir = new Vector3d(mx, my, mz).normalize();
		this.up = new Vector3d(1, 0, 0).cross(this.dir).normalize();
		this.initialFrame = initialFrame;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		super.setStitchedSprites(frames);
		this.animation.setFrameCounter(this.initialFrame);
	}

	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		this.initialAlpha = alpha;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		this.alpha = this.initialAlpha * (1.0f - this.age / (float)this.lifetime);
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		float minU = (float)this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = (float)this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;

		float scale = 0.1F * this.particleScale * 2 * (this.age / (float)this.lifetime);

		if (this.particleTexture != null) {
			minU = this.particleTexture.getU0();
			maxU = this.particleTexture.getU1();
			minV = this.particleTexture.getV0();
			maxV = this.particleTexture.getV1();
		}

		float tw = maxU - minU;
		float th = maxV - minV;
		float oneTexelU = tw / 128.0f;
		float oneTexelV = th / 128.0f;

		minU += oneTexelU;
		maxU -= oneTexelU;
		minV += oneTexelV;
		maxV -= oneTexelV;

		float rpx = (float)(this.xOld + (this.getX() - this.xOld) * (double)partialTicks - interpPosX);
		float rpy = (float)(this.yOld + (this.getY() - this.yOld) * (double)partialTicks - interpPosY);
		float rpz = (float)(this.zOld + (this.getZ() - this.zOld) * (double)partialTicks - interpPosZ);
		int brightness = this.getBrightnessForRender(partialTicks);
		int lightmapX = brightness >> 16 & 65535;
		int lightmapY = brightness & 65535;

		Vector3d normal = this.dir;
		Vector3d perpendicular = this.up;
		Vector3d perpendicular2 = perpendicular.cross(normal);

		double yOffset = 0.125D;
		Vector3d[] vertices = new Vector3d[] {perpendicular.add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale)};

		if (this.particleAngle != 0.0F) {
			float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float f9 = MathHelper.cos(f8 * 0.5F);
			float f10 = MathHelper.sin(f8 * 0.5F) * (float)this.dir.x;
			float f11 = MathHelper.sin(f8 * 0.5F) * (float)this.dir.y;
			float f12 = MathHelper.sin(f8 * 0.5F) * (float)this.dir.z;
			Vector3d vec3d = new Vector3d((double)f10, (double)f11, (double)f12);

			for (int l = 0; l < 4; ++l) {
				vertices[l] = vec3d.scale(2.0D * vertices[l].dotProduct(vec3d)).add(vertices[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.cross(vertices[l]).scale((double)(2.0F * f9)));
			}
		}

		buff.pos((double)rpx + vertices[0].x, (double)rpy + vertices[0].y, (double)rpz + vertices[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[1].x, (double)rpy + vertices[1].y, (double)rpz + vertices[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[2].x, (double)rpy + vertices[2].y, (double)rpz + vertices[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[3].x, (double)rpy + vertices[3].y, (double)rpz + vertices[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();

		buff.pos((double)rpx + vertices[1].x, (double)rpy + vertices[1].y, (double)rpz + vertices[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[0].x, (double)rpy + vertices[0].y, (double)rpz + vertices[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[3].x, (double)rpy + vertices[3].y, (double)rpz + vertices[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[2].x, (double)rpy + vertices[2].y, (double)rpz + vertices[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleSonicScream> {
		public Factory() {
			super(ParticleSonicScream.class, ParticleTextureStitcher.create(ParticleSonicScream.class, new ResourceLocation("thebetweenlands:particle/sonic_scream")).setSplitAnimations(true));
		}

		@Override
		public ParticleSonicScream createParticle(ImmutableParticleArgs args) {
			return new ParticleSonicScream(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getInt(0), args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(-1, 0);
		}
	}
}

