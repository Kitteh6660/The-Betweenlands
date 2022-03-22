package thebetweenlands.common.block.terrain;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;

public class BlockSwampDirt extends BasicBlock {
	public BlockSwampDirt(Material materialIn) {
		super(materialIn);
		this.setSoundType(SoundType.GROUND);
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, net.minecraftforge.common.IPlantable plantable) {
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
}
