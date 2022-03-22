package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.tree.WorldGenRubberTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSapTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenWeedwoodTree;
import thebetweenlands.util.AdvancedStateMap;

public class BlockSaplingBetweenlands extends BlockSapling implements IStateMappedBlock  {
	public final WorldGenerator gen;

	public BlockSaplingBetweenlands(WorldGenerator gen) {
		setCreativeTab(BLCreativeTabs.PLANTS);
		setSoundType(SoundType.PLANT);
		this.gen = gen;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this));
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClientSide()) {
			this.checkAndDropBlock(world, pos, state);

			if (rand.nextInt(7) == 0) {
				this.grow(world, pos, state, rand);
			}
		}
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return SurfaceType.PLANT_DECORATION_SOIL.matches(state) || super.canSustainBush(state);
	}

	@Override
	public void generateTree(World world, BlockPos pos, BlockState state, Random rand) {
		if (!TerrainGen.saplingGrowTree(world, rand, pos)) {
			return;
		}
		
		world.setBlockToAir(pos);

		if (!this.gen.generate(world, rand, pos)) {
			world.setBlockState(pos, state);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(TYPE);
	}
}