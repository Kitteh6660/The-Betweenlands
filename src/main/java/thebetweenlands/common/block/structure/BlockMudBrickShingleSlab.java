package thebetweenlands.common.block.structure;

import net.minecraft.block.material.Material;
import net.minecraft.state.properties.SlabType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockMudBrickShingleSlab extends SlabBlock {
	
	public BlockMudBrickShingleSlab(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
		if(state.getValue(TYPE) == SlabType.DOUBLE || (state.getValue(TYPE) == SlabType.TOP && direction == Direction.UP) || (state.getValue(TYPE) == SlabType.BOTTOM && direction == Direction.DOWN)) {
			if(super.canSustainPlant(state, world, pos, direction, plantable)) {
				return true;
			}
	
			if (state.getBlock() != BlockRegistry.CRAGROCK.get()) {
				PlantType plantType = plantable.getPlantType(world, pos.relative(direction));
				if (plantType == PlantType.BEACH) {
					boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER || world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
						world.getBlockState(pos.north()).getMaterial() == Material.WATER || world.getBlockState(pos.south()).getMaterial() == Material.WATER);
					return hasWater;
				}
				else if (plantType == PlantType.PLAINS) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		return false;
	}
}
