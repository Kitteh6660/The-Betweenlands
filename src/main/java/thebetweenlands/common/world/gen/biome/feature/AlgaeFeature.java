package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;

/**
 * Adds algae to water surfaces
 */
public class AlgaeFeature extends BiomeFeature {
	
	private PerlinNoiseGenerator algaeNoiseGen;
	private double[] algaeNoise = new double[256];

	@Override
	public void initializeGenerators(SharedSeedRandom seed, IntStream stream) {
		this.algaeNoiseGen = new PerlinNoiseGenerator(seed, stream);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, Biome biome) {
		this.algaeNoise = this.algaeNoiseGen.getRegion(this.algaeNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, BiomeWeights biomeWeights,
			EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.POST_GEN_CAVES) {
			float biomeWeight = biomeWeights.get(x, z);
			if(this.algaeNoise[x * 16 + z] / 1.6f * biomeWeight + 1.8f <= 0) {
				int y = WorldProviderBetweenlands.LAYER_HEIGHT;
				Block currentBlock = chunkPrimer.getBlockState(x, y, z).getBlock();
				Block blockAbove = chunkPrimer.getBlockState(x, y + 1, z).getBlock();
				if(currentBlock == chunkGenerator.layerBlock && (blockAbove == null || blockAbove == Blocks.AIR)) {
					chunkPrimer.setBlockState(x, y + 1, z, BlockRegistry.ALGAE.get().defaultBlockState());
				}
			}
		}
	}
}
