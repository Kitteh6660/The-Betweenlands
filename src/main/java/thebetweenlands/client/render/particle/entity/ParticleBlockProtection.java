package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleBlockProtection extends ParticleAnimated implements IParticleSpriteReceiver {
	protected final Direction face;

	protected ParticleBlockProtection(World world, double x, double y, double z, double mx, double my, double mz, Direction face, float scale, int maxAge) {
		super(world, x, y, z, 0, 0, 0, maxAge, scale, false);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.getX() = this.xOld = x;
		this.getY() = this.yOld = y;
		this.getZ() = this.zOld = z;
		this.face = face;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		float minU = (float)this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = (float)this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;
		float scale = 0.1F * this.particleScale * 2;

		if (this.particleTexture != null) {
			minU = this.particleTexture.getU0();
			maxU = this.particleTexture.getU1();
			minV = this.particleTexture.getV0();
			maxV = this.particleTexture.getV1();
		}

		float rpx = (float)(this.xOld + (this.getX() - this.xOld) * (double)partialTicks - interpPosX);
		float rpy = (float)(this.yOld + (this.getY() - this.yOld) * (double)partialTicks - interpPosY);
		float rpz = (float)(this.zOld + (this.getZ() - this.zOld) * (double)partialTicks - interpPosZ);
		int brightness = this.getBrightnessForRender(partialTicks);
		int lightmapX = brightness >> 16 & 65535;
		int lightmapY = brightness & 65535;

		Vector3d normal = new Vector3d(this.face.getDirectionVec());
		Vector3d perpendicular;
		switch(this.face) {
		case UP:
			perpendicular = new Vector3d(1, 0, 0);
			break;
		case DOWN:
			perpendicular = new Vector3d(-1, 0, 0);
			break;
		default:
			perpendicular = new Vector3d(0, 1, 0);
		}
		Vector3d perpendicular2 = perpendicular.cross(normal);

		double yOffset = 0.125D;
		Vector3d[] vertices = new Vector3d[] {perpendicular.add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale)};

		if (this.particleAngle != 0.0F) {
			float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float f9 = MathHelper.cos(f8 * 0.5F);
			float f10 = MathHelper.sin(f8 * 0.5F) * this.face.getStepX();
			float f11 = MathHelper.sin(f8 * 0.5F) * this.face.getStepY();
			float f12 = MathHelper.sin(f8 * 0.5F) * this.face.getStepZ();
			Vector3d vec3d = new Vector3d((double)f10, (double)f11, (double)f12);

			for (int l = 0; l < 4; ++l) {
				vertices[l] = vec3d.scale(2.0D * vertices[l].dotProduct(vec3d)).add(vertices[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.cross(vertices[l]).scale((double)(2.0F * f9)));
			}
		}

		buff.pos((double)rpx + vertices[0].x, (double)rpy + vertices[0].y, (double)rpz + vertices[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[1].x, (double)rpy + vertices[1].y, (double)rpz + vertices[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[2].x, (double)rpy + vertices[2].y, (double)rpz + vertices[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[3].x, (double)rpy + vertices[3].y, (double)rpz + vertices[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.alpha).lightmap(lightmapX, lightmapY).endVertex();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleBlockProtection> {
		public Factory() {
			super(ParticleBlockProtection.class, ParticleTextureStitcher.create(ParticleBlockProtection.class, new ResourceLocation("thebetweenlands:particle/block_protection")).setSplitAnimations(true));
		}

		@Override
		public ParticleBlockProtection createParticle(ImmutableParticleArgs args) {
			return new ParticleBlockProtection(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(Direction.class, 0), args.scale, args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Direction.UP, -1);
		}
	}
}

