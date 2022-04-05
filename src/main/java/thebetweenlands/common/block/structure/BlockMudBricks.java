package thebetweenlands.common.block.structure;

import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.IPlantable;

public class BlockMudBricks extends Block {
	
	public BlockMudBricks(Properties properties) {
		super(properties);
		//super(Material.ROCK);
		//this.setSoundType2(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
		if(super.canSustainPlant(state, world, pos, direction, plantable)) {
			return true;
		}

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
