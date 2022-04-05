package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class BlockLanternSiltGlass extends BetweenlanternBlock {
	
	public BlockLanternSiltGlass(Properties properties) {
		super(properties);
		//super(Material.WOOD, SoundType.GLASS);
	}

	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING, BLParticles.WISP.create(worldIn, pos.getX() + 0.5f, pos.getY() + 0.325f, pos.getZ() + 0.5f, ParticleArgs.get().withMotion(0, -0.0015f, 0).withScale(0.3F + rand.nextFloat() * 0.4f).withColor(1.0f, 0.5f + rand.nextFloat() * 0.2f, 0.1f + rand.nextFloat() * 0.2f, 0.6f).withData(255, false)));
	}
}
