package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockState;

public class BlockLichen extends BlockMoss {
	public BlockLichen(boolean spreading) {
		super(spreading);
	}

	@Override
	public int getColorMultiplier(BlockState state, net.minecraft.world.IBlockReader worldIn, net.minecraft.util.math.BlockPos pos, int tintIndex) {
		return 0xFFFFFF;
	}
}
