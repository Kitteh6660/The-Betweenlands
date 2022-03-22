package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;

public class WorldGenDruidCircle implements IWorldGenerator {
	private static final BlockState[] RUNE_STONES = {
			BlockRegistry.DRUID_STONE_1.defaultBlockState(),
			BlockRegistry.DRUID_STONE_2.defaultBlockState(),
			BlockRegistry.DRUID_STONE_3.defaultBlockState(),
			BlockRegistry.DRUID_STONE_4.defaultBlockState(),
			BlockRegistry.DRUID_STONE_5.defaultBlockState()
	};
	private final int height = 4;
	private final int baseRadius = 6;
	private final int checkRadius = 32;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			this.generate(world, random, chunkX * 16, chunkZ * 16);
		}
	}

	private void generate(World world, Random random, int startX, int startZ) {
		BlockPos genPos = null;
		BlockPos.Mutable pos = new BlockPos.Mutable();

		//Try to find a suitable location
		check:
		for (int xo = this.baseRadius + 1; xo <= this.checkRadius - (this.baseRadius + 1); xo++) {
			for (int zo = this.baseRadius + 1; zo <= this.checkRadius - (this.baseRadius + 1); zo++) {
				int x = startX + xo;
				int z = startZ + zo;
				if(world.isAreaLoaded(new BlockPos(x - baseRadius - 2, 64, z - baseRadius - 2), new BlockPos(x + baseRadius + 3, 64, z + baseRadius + 3))) {
					pos.setPos(x, 0, z);
					Biome biome = world.getBiome(pos);
					if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) {
						int newY = world.getHeight(pos).getY() - 1;
						pos.setY(newY);
						BlockState block = world.getBlockState(pos);
						if (block == biome.topBlock) {
							if(this.canGenerateAt(world, pos.above())) {
								genPos = pos.above();
								break check;
							}
						}
					}
				}
			}
		}

		if(genPos != null && random.nextInt(BetweenlandsConfig.WORLD_AND_DIMENSION.druidCircleFrequency) == 0) {
			generateStructure(world, random, genPos);
		}
	}

	public boolean canGenerateAt(World world, BlockPos altar) {
		for (BlockPos p : BlockPos.getAllInBox(altar.add(-this.baseRadius, 1, -this.baseRadius), altar.add(this.baseRadius, this.height, this.baseRadius))) {
			if (!world.isEmptyBlock(p) && !world.getBlockState(p).getBlock().isReplaceable(world, p)) {
				return false;
			}
		}
		return true;
	}

	public void generateStructure(World world, Random rand, BlockPos altar) {
		// circle
		BlockPos.Mutable pos = new BlockPos.Mutable();
		BlockState ground = world.getBiome(altar).topBlock;
		BlockState filler = world.getBiome(altar).fillerBlock;
		int altarX = altar.getX(), altarY = altar.getY(), altarZ = altar.getZ();
		for (int x = -this.baseRadius; x <= this.baseRadius; x++) {
			for (int z = -this.baseRadius; z <= this.baseRadius; z++) {
				pos.setPos(altarX + x, altarY, altarZ + z);
				int dSq = (int) Math.round(Math.sqrt(x * x + z * z));
				if (dSq == this.baseRadius) {
					if (x % 2 == 0 && z % 2 == 0) {
						placePillar(world, pos, rand);
					} else {
						placeAir(world, pos);
					}
				}
				if (dSq <= this.baseRadius) {
					for(int yo = 0; yo < 16; yo++) {
						Biome biome = world.getBiomeForCoordsBody(pos);
						BlockState blockState = world.getBlockState(pos);
						if(blockState == biome.fillerBlock || blockState == biome.topBlock || blockState.getMaterial() == Material.ROCK || blockState.getMaterial() == Material.GROUND) {
							world.setBlockToAir(pos.toImmutable());
						}
						pos.setY(pos.getY() + 1);
					}

					pos.setY(altarY - 1);
					world.setBlockState(pos.toImmutable(), ground);

					int offset = world.rand.nextInt(2);
					if(world.isEmptyBlock(pos.below(2)) || world.getBlockState(pos.below(2)).getMaterial().isLiquid()) {
						offset -= 1;
					}
					for(int yo = 0; yo < 10; yo++) {
						if (dSq <= this.baseRadius / 10.0F * (10 - yo) + offset) {
							pos.setY(altarY - 2 - yo);
							world.setBlockState(pos.toImmutable(), filler);
						}
					}
				}
			}
		}
		world.setBlockState(altar, BlockRegistry.DRUID_ALTAR.defaultBlockState());
		world.setBlockState(altar.below(), BlockRegistry.MOB_SPAWNER.defaultBlockState());
		TileEntity te = world.getBlockEntity(altar.below());
		if(te instanceof TileEntityMobSpawnerBetweenlands) {
			MobSpawnerLogicBetweenlands logic = ((TileEntityMobSpawnerBetweenlands)te).getSpawnerLogic();
			logic.setNextEntityName("thebetweenlands:dark_druid").setCheckRange(32.0D).setSpawnRange(6).setSpawnInAir(false).setMaxEntities(1 + world.rand.nextInt(3));
		}
	}

	private void placeAir(World world, BlockPos.Mutable pos) {
		Biome biome = world.getBiome(pos);
		for (int k = 0, y = pos.getY(); k <= this.height; k++, pos.setY(y + k)) {
			BlockState blockState = world.getBlockState(pos);
			if(blockState == biome.fillerBlock || blockState == biome.topBlock || blockState.getMaterial() == Material.ROCK || blockState.getMaterial() == Material.GROUND) {
				world.setBlockToAir(pos.toImmutable());
			}
		}
	}

	private void placePillar(World world, BlockPos.Mutable pos, Random rand) {
		int height = rand.nextInt(3) + 3;
		for (int k = 0, y = pos.getY(); k <= height; k++, pos.setY(y + k)) {
			Direction facing = Direction.HORIZONTALS[rand.nextInt(Direction.HORIZONTALS.length)];
			if (rand.nextBoolean()) {
				world.setBlockState(pos.toImmutable(), getRandomRuneBlock(rand).setValue(BlockDruidStone.FACING, facing), 3);
			} else {
				world.setBlockState(pos.toImmutable(), BlockRegistry.DRUID_STONE_6.defaultBlockState().setValue(BlockDruidStone.FACING, facing));
				for (int vineCount = 0; vineCount < 4; vineCount++) {
					setRandomFoliage(world, pos, rand);
				}
			}
		}
	}

	private void setRandomFoliage(World world, BlockPos pos, Random rand) {
		Direction facing = Direction.HORIZONTALS[rand.nextInt(Direction.HORIZONTALS.length)];
		BlockPos side = pos.toImmutable().offset(facing);
		if (world.isEmptyBlock(side)) {
			world.setBlockState(side, Blocks.VINE.defaultBlockState().setValue(BlockVine.getPropertyFor(facing.getOpposite()), true));
		}
	}

	private BlockState getRandomRuneBlock(Random rand) {
		return RUNE_STONES[rand.nextInt(RUNE_STONES.length)];
	}
}