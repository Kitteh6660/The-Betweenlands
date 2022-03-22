package thebetweenlands.common.block.structure;

import java.util.EnumMap;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.property.PropertyBoolUnlisted;

public class BlockSlanted extends BlockStairs {
	public static final IUnlistedProperty<Boolean> CORNER_NORTH_WEST = new PropertyBoolUnlisted("corner_north_west");
	public static final IUnlistedProperty<Boolean> CORNER_NORTH_EAST = new PropertyBoolUnlisted("corner_north_east");
	public static final IUnlistedProperty<Boolean> CORNER_SOUTH_EAST = new PropertyBoolUnlisted("corner_south_east");
	public static final IUnlistedProperty<Boolean> CORNER_SOUTH_WEST = new PropertyBoolUnlisted("corner_south_west");

	public BlockSlanted(BlockState modelState) {
		super(modelState);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), new IProperty<?>[0], new IUnlistedProperty[]{CORNER_NORTH_WEST, CORNER_NORTH_EAST, CORNER_SOUTH_EAST, CORNER_SOUTH_WEST});
	}

	@Override
	public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;

		//x, z
		//0, 0
		boolean cornerNW = false;
		//1, 0
		boolean cornerNE = false;
		//1, 1
		boolean cornerSE = false;
		//0, 1
		boolean cornerSW = false;

		EnumMap<Direction, EnumHalf> halves = new EnumMap<Direction, EnumHalf>(Direction.class);
		EnumMap<Direction, Direction> facings = new EnumMap<Direction, Direction>(Direction.class);
		for(Direction side : Direction.HORIZONTALS) {
			BlockState offsetState = worldIn.getBlockState(pos.offset(side));
			if(isBlockStairs(offsetState)) {
				facings.put(side, offsetState.getValue(FACING));
				halves.put(side, offsetState.getValue(HALF));
			}
		}

		EnumHalf half = state.getValue(HALF);

		switch(state.getValue(FACING)) {
		default:
		case NORTH:
			cornerNW = true;
			cornerNE = true;
			if(halves.get(Direction.NORTH) == half && facings.get(Direction.NORTH) == Direction.WEST && facings.get(Direction.EAST) != Direction.NORTH) {
				cornerNE = false;
			}
			if(halves.get(Direction.NORTH) == half && facings.get(Direction.NORTH) == Direction.EAST && facings.get(Direction.WEST) != Direction.NORTH) {
				cornerNW = false;
			}
			if(halves.get(Direction.SOUTH) == half && facings.get(Direction.SOUTH) == Direction.WEST && facings.get(Direction.WEST) != Direction.NORTH) {
				cornerSW = true;
			}
			if(halves.get(Direction.SOUTH) == half && facings.get(Direction.SOUTH) == Direction.EAST && facings.get(Direction.EAST) != Direction.NORTH) {
				cornerSE = true;
			}
			break;
		case SOUTH:
			cornerSE = true;
			cornerSW = true;
			if(halves.get(Direction.SOUTH) == half && facings.get(Direction.SOUTH) == Direction.WEST && facings.get(Direction.EAST) != Direction.SOUTH) {
				cornerSE = false;
			}
			if(halves.get(Direction.SOUTH) == half && facings.get(Direction.SOUTH) == Direction.EAST && facings.get(Direction.WEST) != Direction.SOUTH) {
				cornerSW = false;
			}
			if(halves.get(Direction.NORTH) == half && facings.get(Direction.NORTH) == Direction.WEST && facings.get(Direction.WEST) != Direction.SOUTH) {
				cornerNW = true;
			}
			if(halves.get(Direction.NORTH) == half && facings.get(Direction.NORTH) == Direction.EAST && facings.get(Direction.EAST) != Direction.SOUTH) {
				cornerNE = true;
			}
			break;
		case EAST:
			cornerNE = true;
			cornerSE = true;
			if(halves.get(Direction.EAST) == half && facings.get(Direction.EAST) == Direction.SOUTH && facings.get(Direction.NORTH) != Direction.EAST) {
				cornerNE = false;
			}
			if(halves.get(Direction.EAST) == half && facings.get(Direction.EAST) == Direction.NORTH && facings.get(Direction.SOUTH) != Direction.EAST) {
				cornerSE = false;
			}
			if(halves.get(Direction.WEST) == half && facings.get(Direction.WEST) == Direction.SOUTH && facings.get(Direction.SOUTH) != Direction.EAST) {
				cornerSW = true;
			}
			if(halves.get(Direction.WEST) == half && facings.get(Direction.WEST) == Direction.NORTH && facings.get(Direction.NORTH) != Direction.EAST) {
				cornerNW = true;
			}
			break;
		case WEST:
			cornerSW = true;
			cornerNW = true;
			if(halves.get(Direction.WEST) == half && facings.get(Direction.WEST) == Direction.SOUTH && facings.get(Direction.NORTH) != Direction.WEST) {
				cornerNW = false;
			}
			if(halves.get(Direction.WEST) == half && facings.get(Direction.WEST) == Direction.NORTH && facings.get(Direction.SOUTH) != Direction.WEST) {
				cornerSW = false;
			}
			if(halves.get(Direction.EAST) == half && facings.get(Direction.EAST) == Direction.SOUTH && facings.get(Direction.SOUTH) != Direction.WEST) {
				cornerSE = true;
			}
			if(halves.get(Direction.EAST) == half && facings.get(Direction.EAST) == Direction.NORTH && facings.get(Direction.NORTH) != Direction.WEST) {
				cornerNE = true;
			}
			break;
		}

		return state.setValue(CORNER_NORTH_WEST, cornerNW).setValue(CORNER_NORTH_EAST, cornerNE).setValue(CORNER_SOUTH_EAST, cornerSE).setValue(CORNER_SOUTH_WEST, cornerSW);
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}
}
