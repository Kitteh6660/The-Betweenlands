package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSulfurTorch extends BlockTorch {
	public BlockSulfurTorch() {
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.WOOD);
		this.setLightLevel(0.9375F);
		this.setTickRandomly(true);
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, BlockState state, Random random) {
		if(!worldIn.isClientSide() && worldIn.isRainingAt(pos)) {
			worldIn.setBlockState(pos, BlockRegistry.SULFUR_TORCH_EXTINGUISHED.defaultBlockState().setValue(FACING, worldIn.getBlockState(pos).getValue(FACING)));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
		Direction Direction = (Direction)state.getValue(FACING);
		double px = (double)pos.getX() + 0.5D;
		double py = (double)pos.getY() + 0.7D;
		double pz = (double)pos.getZ() + 0.5D;

		if (Direction.getAxis().isHorizontal()) {
			Direction Direction1 = Direction.getOpposite();
			BLParticles.SULFUR_TORCH.spawn(world, px + 0.27D * (double)Direction1.getStepX(), py + 0.22D, pz + 0.27D * (double)Direction1.getStepZ());
		} else {
			BLParticles.SULFUR_TORCH.spawn(world, px, py, pz);
		}
	}
}
