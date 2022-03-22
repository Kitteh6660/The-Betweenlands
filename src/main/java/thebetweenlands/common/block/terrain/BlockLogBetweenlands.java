package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockLogBetweenlands extends RotatedPillarBlock {
	
	public BlockLogBetweenlands(Properties properties) {
		super(properties);
		/*setHarvestLevel("axe", 0);
		setCreativeTab(BLCreativeTabs.PLANTS);
		this.registerDefaultState(this.stateDefinition.any().setValue(LOG_AXIS, BlockLog.EnumAxis.Y));
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	
	//TODO: Add stripped logs. And gut old code.
	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		/*if (toolType == ToolType.AXE) {
			Block block = state.getBlock() == ModBlocks.NETHERWOOD_LOG.get() ? ModBlocks.STRIPPED_NETHERWOOD_LOG.get() : ModBlocks.STRIPPED_NETHERWOOD_WOOD.get();
			return block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)) : null;
		}*/
		return super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
	
}