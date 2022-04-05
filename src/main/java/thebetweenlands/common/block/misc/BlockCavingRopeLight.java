package thebetweenlands.common.block.misc;

import java.util.List;
import java.util.Random;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockCavingRopeLight extends AirBlock {
	
	public BlockCavingRopeLight(Properties properties) {
		super(properties);
		/*this.setTickRandomly(true);
		this.lightValue = 6;*/
	}

	@Override
	public BlockItem getItemBlock() {
		return null;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		List<EntityRopeNode> ropes = worldIn.getEntitiesOfClass(EntityRopeNode.class, Block.box(pos));
		if(ropes.isEmpty()) {
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.empty(true);
	}
}
