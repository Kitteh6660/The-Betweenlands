package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.TorchBlock;
import net.minecraft.particles.IParticleData;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.BlockRegistry;

public class SulfurTorchBlock extends TorchBlock {
	
	public SulfurTorchBlock(Properties properties, IParticleData flameParticle) {
		super(properties, flameParticle);
		/*this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.WOOD);
		this.setLightLevel(0.9375F);
		this.setTickRandomly(true);*/
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if(!worldIn.isClientSide() && worldIn.isRainingAt(pos)) {
			worldIn.setBlockAndUpdate(pos, BlockRegistry.SULFUR_TORCH_EXTINGUISHED.get().defaultBlockState());
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		double px = (double)pos.getX() + 0.5D;
		double py = (double)pos.getY() + 0.7D;
		double pz = (double)pos.getZ() + 0.5D;

		BLParticles.SULFUR_TORCH.spawn(world, px, py, pz);
	}
}
