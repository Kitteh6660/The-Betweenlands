package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockVenusFlyTrap extends BlockPlant {
	public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

	public BlockVenusFlyTrap() {
		super();
		this.setDefaultState(this.blockState.getBaseState().setValue(BLOOMING, false));
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if(rand.nextInt(300) == 0) {
			if(!state.getValue(BLOOMING)) {
				if(rand.nextInt(3) == 0)
					worldIn.setBlockState(pos, this.defaultBlockState().setValue(BLOOMING, true));
			} else {
				worldIn.setBlockState(pos, this.defaultBlockState().setValue(BLOOMING, false));
			}
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {BLOOMING});
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		boolean blooming = meta == 1;
		return this.defaultBlockState().setValue(BLOOMING, blooming);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		if(state.getValue(BLOOMING))
			meta = 1;
		return meta;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}
}
