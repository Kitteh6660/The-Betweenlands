package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.common.registries.BlockRegistry;

public class CragrockBlock extends Block {

	public CragrockBlock(Properties properties) {
		super(properties);
		/*super(materialIn);
		this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, EnumCragrockType.DEFAULT));
		this.setTickRandomly(true);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isClientSide() && state.getBlock() != BlockRegistry.CRAGROCK.get()) {
			BlockPos newPos = pos.offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
			if(newPos.getY() >= 0 && newPos.getY() < 256 && world.isLoaded(newPos)) {
				Block block = world.getBlockState(newPos).getBlock();
				if (block == this && block == BlockRegistry.CRAGROCK.get()) {
					if (world.getBlockState(newPos.above()).getBlock() == this  && world.getBlockState(newPos.above(2)).getBlock() == Blocks.AIR && state.getBlock() != BlockRegistry.MOSSY_CRAGROCK_BOTTOM.get()) {
						world.setBlockAndUpdate(newPos, BlockRegistry.MOSSY_CRAGROCK_BOTTOM.get().defaultBlockState());
					} else if (world.getBlockState(newPos).getBlock() == this && world.getBlockState(newPos.above()).getBlock() == Blocks.AIR) {
						world.setBlockAndUpdate(newPos, BlockRegistry.MOSSY_CRAGROCK_TOP.get().defaultBlockState());
					}
				}
			}
		}
	}
	
	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
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
		return false;
	}
}
