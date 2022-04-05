package thebetweenlands.client.render.particle.entity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.ParticleFactory;

@OnlyIn(Dist.CLIENT)
public class ParticleChiromawDroppings extends Particle {

	private int bobTimer;

	public ParticleChiromawDroppings(World world, double x, double y, double z, double velX, double velY, double velZ, float scale) {
		super(world, x, y, z, velX, velY, velZ);
		motionX += velX;
		motionY += velY;
		motionZ += velZ;
		particleScale = scale;
		setSize(0.01F, 0.01F);
		setParticleTextureIndex(112);
		particleGravity = 0.03F;
		lifetime = 40 + (int)(Math.random() * 40);
	}

	@Override
	public void tick() {
		xOld = posX;
		yOld = posY;
		zOld = posZ;

		motionY -= (double) particleGravity;
		motionX *= 0.1800000190734863D;
		motionZ *= 0.1800000190734863D;

		if (lifetime-- <= 0) {
			this.setExpired();
		}
		
		if (this.isExpired) {
			setParticleTextureIndex(114);
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
		}

		BlockState blockState = world.getBlockState(new BlockPos(this.getX(), this.getY(), this.getZ()));
		Material material = blockState.getMaterial();

		if (material.isLiquid() || material.isSolid()) {
			double d0 = (double) ((float) (MathHelper.floor(posY) + 1) - BlockLiquid.getLiquidHeightPercent(blockState.getBlock().getMetaFromState(blockState)));

			if (posY < d0) {
				this.setExpired();
			}
		}
		move(motionX, motionY, motionZ);
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleChiromawDroppings> {
		public Factory() {
			super(ParticleChiromawDroppings.class);
		}

		@Override
		public ParticleChiromawDroppings createParticle(ImmutableParticleArgs args) {
			return new ParticleChiromawDroppings(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}
	}
}
