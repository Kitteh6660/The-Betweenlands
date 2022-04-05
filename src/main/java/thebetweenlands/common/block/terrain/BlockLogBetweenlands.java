package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockLogBetweenlands extends RotatedPillarBlock {
	
	public BlockLogBetweenlands(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
		/*setHarvestLevel("axe", 0);
		setCreativeTab(BLCreativeTabs.PLANTS);
		
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
    	return this == BlockRegistry.LOG_PORTAL.get() ? 0 : 5;
    }
	
	//TODO: Add stripped logs. And gut old code.
	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		if (toolType == ToolType.AXE) {
			Block oldBlock = state.getBlock();
			Block newBlock;
			if (oldBlock == BlockRegistry.WEEDWOOD_LOG.get() || oldBlock == BlockRegistry.WEEDWOOD.get()) {
				newBlock = oldBlock == BlockRegistry.WEEDWOOD_LOG.get() ? BlockRegistry.STRIPPED_WEEDWOOD_LOG.get() : BlockRegistry.STRIPPED_WEEDWOOD.get();
			}
			else if (oldBlock == BlockRegistry.ROTTEN_LOG.get() || oldBlock == BlockRegistry.ROTTEN_WOOD.get()) {
				newBlock = oldBlock == BlockRegistry.ROTTEN_LOG.get() ? BlockRegistry.STRIPPED_ROTTEN_LOG.get() : BlockRegistry.STRIPPED_ROTTEN_WOOD.get();
			}
			else {
				newBlock = BlockRegistry.STRIPPED_WEEDWOOD_LOG.get();
			}
			return newBlock != null ? newBlock.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)) : null;
		}
		return super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
	
}