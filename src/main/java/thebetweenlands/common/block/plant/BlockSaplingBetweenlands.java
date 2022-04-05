package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.trees.Tree;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockSaplingBetweenlands extends SaplingBlock  {
	
	public final Tree treeGrower;

	public BlockSaplingBetweenlands(Tree tree, Properties properties) {
		super(tree, properties);
		/*setCreativeTab(BLCreativeTabs.PLANTS);
		setSoundType(SoundType.PLANT);*/
		this.treeGrower = tree;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isClientSide()) {
			this.checkAndDropBlock(world, pos, state);

			if (random.nextInt(7) == 0) {
				this.advanceTree(world, pos, state, random);
			}
		}
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return SurfaceType.PLANT_DECORATION_SOIL.matches(state) || super.canSustainBush(state);
	}

	@Override
	public void advanceTree(ServerWorld level, BlockPos pos, BlockState state, Random random) {
		if (!TerrainGen.saplingGrowTree(level, random, pos)) {
			return;
		}
		
		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

		if (!this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random)) {
			level.setBlockAndUpdate(pos, state);
		}
	}
}