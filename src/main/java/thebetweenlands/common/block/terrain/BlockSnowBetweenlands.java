package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.EventWinter;

public class BlockSnowBetweenlands extends SnowBlock {
	
	public BlockSnowBetweenlands(Properties properties) {
		super(properties);
		/*this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(0.1F);
		this.setSoundType(SoundType.SNOW);
		this.setLightOpacity(0);
		this.setHarvestLevel("shovel", 0);*/
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if(!EventWinter.isFroooosty(worldIn) || worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
			int layers = state.getValue(LAYERS);
			if(layers > 1) {
				worldIn.setBlockState(pos, state.setValue(LAYERS, layers - 1));
			} else {
				worldIn.setBlockToAir(pos);
			}
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockState iblockstate = worldIn.getBlockState(pos.below());
		Block block = iblockstate.getBlock();

		if (block != BlockRegistry.BLACK_ICE && block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
			BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos.below(), Direction.UP);
			return blockfaceshape == BlockFaceShape.SOLID || iblockstate.getBlock().isLeaves(iblockstate, worldIn, pos.below()) || block == this && ((Integer)iblockstate.getValue(LAYERS)).intValue() == 8;
		} else {
			return false;
		}
	}
}
