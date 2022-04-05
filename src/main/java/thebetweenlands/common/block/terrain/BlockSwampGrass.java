package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.common.world.gen.biome.decorator.DecorationHelper;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorPositionProvider;

public class BlockSwampGrass extends Block implements IGrowable, ITintedBlock {
	
	public BlockSwampGrass(Properties properties) {
		super(properties);
		/*super(Material.GRASS);
		this.setTickRandomly(true);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isClientSide()) {
			updateGrass(world, pos, random);
		}
	}

	public static boolean updateGrass(World world, BlockPos pos, Random rand) {
		if(world.getBlockState(pos.above()).getLightBlock(world, pos.above()) < 2) {
			revertToDirt(world, pos);
			return true;
		} else {
			for (int i = 0; i < 4; ++i) {
				BlockPos blockPos = pos.offset(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

				if (blockPos.getY() >= 0 && blockPos.getY() < 256 && !world.isLoaded(blockPos)) {
					return false;
				}

				BlockState blockStateAbove = world.getBlockState(blockPos.above());

				if(blockStateAbove.getLightBlock(world, pos.above()) >= 2) {
					spreadGrassTo(world, blockPos);
					return true;
				}
			}
		}
		return false;
	}

	public static void revertToDirt(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(state.getBlock() == BlockRegistry.SWAMP_GRASS.get()) {
			world.setBlockAndUpdate(pos, BlockRegistry.SWAMP_DIRT.get().defaultBlockState());
		}

		TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, pos);
		if(te != null) {
			int compost = te.getCompost();
			int decay = te.getDecay();

			if(state.getBlock() == BlockRegistry.DUG_SWAMP_GRASS.get()) {
				world.setBlockAndUpdate(pos, BlockRegistry.DUG_SWAMP_DIRT.get().defaultBlockState());
			}

			if(state.getBlock() == BlockRegistry.DUG_PURIFIED_SWAMP_GRASS.get()) {
				world.setBlockAndUpdate(pos, BlockRegistry.DUG_PURIFIED_SWAMP_DIRT.get().defaultBlockState());
			}

			te = BlockGenericDugSoil.getTile(world, pos);
			if(te != null) {
				te.setCompost(compost);
				te.setDecay(decay);
			}
		}
	}

	public static void spreadGrassTo(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(state.getBlock() == BlockRegistry.SWAMP_DIRT.get()) {
			world.setBlockAndUpdate(pos, BlockRegistry.SWAMP_GRASS.get().defaultBlockState());
		}

		TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, pos);
		if(te != null) {
			int compost = te.getCompost();
			int decay = te.getDecay();

			if(state.getBlock() == BlockRegistry.DUG_SWAMP_DIRT.get()) {
				world.setBlock(pos, BlockRegistry.DUG_SWAMP_GRASS.get().defaultBlockState(), 2); //don't do block update yet
			}

			if(state.getBlock() == BlockRegistry.DUG_PURIFIED_SWAMP_DIRT.get()) {
				world.setBlock(pos, BlockRegistry.DUG_PURIFIED_SWAMP_GRASS.get().defaultBlockState(), 2); //don't do block update yet
			}

			te = BlockGenericDugSoil.getTile(world, pos);
			if(te != null) {
				te.setCompost(compost);
				te.setDecay(decay);
			}
			
			world.sendBlockUpdated(pos, state, world.getBlockState(pos), 1); //do block update now
		}
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		DecoratorPositionProvider provider = new DecoratorPositionProvider();
		provider.init(worldIn, worldIn.getBiome(pos), null, rand, pos.getX(), pos.getY() + 1, pos.getZ());
		provider.setOffsetXZ(-4, 4);
		provider.setOffsetY(-2, 2);

		for(int i = 0; i < 4; i++) {
			DecorationHelper.generateSwampDoubleTallgrass(provider);
			DecorationHelper.generateTallCattail(provider);
			DecorationHelper.generateSwampTallgrassCluster(provider);
			if(rand.nextInt(5) == 0) {
				DecorationHelper.generateCattailCluster(provider);
			}
			if(rand.nextInt(3) == 0) {
				DecorationHelper.generateShootsCluster(provider);
			}
		}
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getAverageGrassColor(worldIn, pos) : GrassColors.get(0.5D, 1.0D);
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