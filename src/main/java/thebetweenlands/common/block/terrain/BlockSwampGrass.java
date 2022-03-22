package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.common.world.gen.biome.decorator.DecorationHelper;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorPositionProvider;

public class BlockSwampGrass extends BasicBlock implements IGrowable, ITintedBlock {
	public BlockSwampGrass() {
		super(Material.GRASS);
		this.setTickRandomly(true);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if (!worldIn.isClientSide()) {
			updateGrass(worldIn, pos, rand);
		}
	}

	public static boolean updateGrass(World world, BlockPos pos, Random rand) {
		if(world.getBlockState(pos.above()).getLightOpacity(world, pos.above()) > 2) {
			revertToDirt(world, pos);
			return true;
		} else {
			for (int i = 0; i < 4; ++i) {
				BlockPos blockPos = pos.offset(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

				if (blockPos.getY() >= 0 && blockPos.getY() < 256 && !world.isBlockLoaded(blockPos)) {
					return false;
				}

				BlockState blockStateAbove = world.getBlockState(blockPos.above());

				if(blockStateAbove.getLightOpacity(world, pos.above()) <= 2) {
					spreadGrassTo(world, blockPos);
					return true;
				}
			}
		}
		return false;
	}

	public static void revertToDirt(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(state.getBlock() == BlockRegistry.SWAMP_GRASS) {
			world.setBlockState(pos, BlockRegistry.SWAMP_DIRT.defaultBlockState());
		}

		TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, pos);
		if(te != null) {
			int compost = te.getCompost();
			int decay = te.getDecay();

			if(state.getBlock() == BlockRegistry.DUG_SWAMP_GRASS) {
				world.setBlockState(pos, BlockRegistry.DUG_SWAMP_DIRT.defaultBlockState());
			}

			if(state.getBlock() == BlockRegistry.DUG_PURIFIED_SWAMP_GRASS) {
				world.setBlockState(pos, BlockRegistry.DUG_PURIFIED_SWAMP_DIRT.defaultBlockState());
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

		if(state.getBlock() == BlockRegistry.SWAMP_DIRT) {
			world.setBlockState(pos, BlockRegistry.SWAMP_GRASS.defaultBlockState());
		}

		TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, pos);
		if(te != null) {
			int compost = te.getCompost();
			int decay = te.getDecay();

			if(state.getBlock() == BlockRegistry.DUG_SWAMP_DIRT) {
				world.setBlockState(pos, BlockRegistry.DUG_SWAMP_GRASS.defaultBlockState(), 2); //don't do block update yet
			}

			if(state.getBlock() == BlockRegistry.DUG_PURIFIED_SWAMP_DIRT) {
				world.setBlockState(pos, BlockRegistry.DUG_PURIFIED_SWAMP_GRASS.defaultBlockState(), 2); //don't do block update yet
			}

			te = BlockGenericDugSoil.getTile(world, pos);
			if(te != null) {
				te.setCompost(compost);
				te.setDecay(decay);
			}
			
			world.sendBlockUpdated(pos, state, world.getBlockState(pos), 1); //do block update now
		}
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	@Nullable
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return BlockRegistry.SWAMP_DIRT.getItemDropped(BlockRegistry.SWAMP_DIRT.defaultBlockState(), rand, fortune);
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
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
	public int getColorMultiplier(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
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