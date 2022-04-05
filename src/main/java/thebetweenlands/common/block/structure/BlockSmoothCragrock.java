package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import net.minecraft.block.SandBlock;

public class BlockSmoothCragrock extends FallingBlock {
	
	public BlockSmoothCragrock(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.STONE);
		this.setHardness(1.5F);
		this.setResistance(10.0F);*/
	}

	@Override
	public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean moving) { }

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) { }

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) { }

	@Override
	public int tickRate(World worldIn) {
		return 10;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) { }

	@Override
	public void onLand(World world, BlockPos pos, BlockState fallingState, BlockState hitState) {
		if(!world.isClientSide()) {
			world.playLocalSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), soundType.getStepSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
			world.levelEvent(null, 2001, pos.above(), Block.getIdFromBlock(world.getBlockState(pos).getBlock()));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}
}
