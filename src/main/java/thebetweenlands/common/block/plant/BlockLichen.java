package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockLichen extends BlockMoss {
	
	public BlockLichen(boolean spreading, Properties properties) {
		super(spreading);
	}

	@Override
	public int getColorMultiplier(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
		return 0xFFFFFF;
	}
}
