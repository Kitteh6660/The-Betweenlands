package thebetweenlands.common.block.structure;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockMudBrickShingleSlab extends BlockSlabBetweenlands {
	public BlockMudBrickShingleSlab() {
		super(BlockRegistry.MUD_BRICK_SHINGLES);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
		EnumBlockHalfBL half = state.getValue(HALF);
		if(half == EnumBlockHalfBL.FULL || (half == EnumBlockHalfBL.TOP && direction == Direction.UP) || (half == EnumBlockHalfBL.BOTTOM && direction == Direction.DOWN)) {
			if(super.canSustainPlant(state, world, pos, direction, plantable)) {
				return true;
			}

			PlantType plantType = plantable.getPlantType(world, pos.offset(direction));

			switch(plantType) {
			case Beach:
				boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
				world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
				world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
				world.getBlockState(pos.south()).getMaterial() == Material.WATER);
				return hasWater;
			case Plains:
				return true;
			default:
				return false;
			}
		}
		return false;
	}
}
