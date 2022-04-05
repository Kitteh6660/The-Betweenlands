package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.TorchBlock;
import net.minecraft.particles.IParticleData;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class DampTorchBlock extends TorchBlock {
	
	public DampTorchBlock(Properties properties, IParticleData flameParticle) {
		super(properties, flameParticle);
		/*this.setHardness(0.0F);
		this.setLightLevel(0);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(4) == 0) {
			double x = (double)pos.getX() + 0.5D;
			double y = (double)pos.getY() + 0.7D;
			double z = (double)pos.getZ() + 0.5D;
			BLParticles.SMOKE.spawn(worldIn, x, y, z, ParticleArgs.get().withColor(0.2F, 0.2F, 0.2F, 1.0F));
		}
	}
}
