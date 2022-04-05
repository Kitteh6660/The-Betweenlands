package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.trees.Tree;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.DecorationHelper;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorPositionProvider;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class PodRoots extends Tree {
	
	@Override
	protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random pRandom, boolean pLargeHive) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		if(SurfaceType.PLANT_DECORATION_SOIL.matches(world, position.below())) {
			if(this.generateRootsStack(world, rand, position)) {
				DecoratorPositionProvider provider = new DecoratorPositionProvider();
				provider.init(world, world.getBiome(position), null, rand, position.getX(), position.getY(), position.getZ());
				provider.setOffsetXZ(-3, 3);
				provider.setOffsetY(-1, 1);

				for(int i = 0; i < 10; i++) {
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
				
				world.setBlockAndUpdate(position.below(), BlockRegistry.GIANT_ROOT.get().defaultBlockState());
				
				for(Direction facing : Direction.Plane.HORIZONTAL) {
					BlockPos offset = position.relative(facing).below();
					if(SurfaceType.PLANT_DECORATION_SOIL.apply(world.getBlockState(offset))) {
						world.setBlockAndUpdate(offset, BlockRegistry.GIANT_ROOT.get().defaultBlockState());
					}
				}
				
				for(int i = 0; i < 32; i++) {
					int rx = rand.nextInt(7) - 3;
					int rz = rand.nextInt(7) - 3;
					if(rx != 0 || rz != 0) {
						BlockPos offset = position.offset(rx, rand.nextInt(3) - 2, rz);
						if(SurfaceType.PLANT_DECORATION_SOIL.apply(world.getBlockState(offset))) {
							world.setBlockAndUpdate(offset, BlockRegistry.GIANT_ROOT.get().defaultBlockState());
						}
					}
				}
				
				return true;
			}
		}

		return false;
	}

	private boolean generateRootsStack(World world, Random rand, BlockPos pos) {
		int height = 6;
		BlockPos.Mutable checkPos = new BlockPos.Mutable();
		for(int yo = 0; yo < 6; yo++) {
			checkPos.set(pos.getX(), pos.getY() + yo, pos.getZ());
			if(!world.isEmptyBlock(checkPos)) {
				height = yo;
				break;
			}
		}
		if(height < 2) {
			return false;
		}
		height = rand.nextInt(height) + 1 + rand.nextInt(4);
		for(int yo = 0; yo < height; yo++) {
			BlockPos offsetPos = pos.offset(0, yo, 0);
			if(!world.isEmptyBlock(offsetPos)) {
				break;
			}
			world.setBlock(offsetPos, BlockRegistry.ROOT.get().defaultBlockState(), 2);
		}
		return true;
	}
}
