package thebetweenlands.common.block.plant;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockShelfFungus extends Block {
	
	public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");

	public BlockShelfFungus(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		this.setSoundType2(SoundType.CLOTH).setHardness(0.2F);*/
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { IS_TOP });
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		BlockState stateAbove = worldIn.getBlockState(pos.above());
		return state.setValue(IS_TOP, stateAbove.getBlock() != this && !stateAbove.isFullCube());
	}
}
