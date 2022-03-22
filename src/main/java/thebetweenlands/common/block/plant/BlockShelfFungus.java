package thebetweenlands.common.block.plant;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import thebetweenlands.common.block.BasicBlock;

public class BlockShelfFungus extends BasicBlock {
	public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");

	public BlockShelfFungus() {
		super(Material.WOOD);
		this.setSoundType2(SoundType.CLOTH).setHardness(0.2F);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { IS_TOP });
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return 0;
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		BlockState stateAbove = worldIn.getBlockState(pos.above());
		return state.setValue(IS_TOP, stateAbove.getBlock() != this && !stateAbove.isFullCube());
	}
}
