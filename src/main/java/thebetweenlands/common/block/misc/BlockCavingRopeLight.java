package thebetweenlands.common.block.misc;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockCavingRopeLight extends BlockAir implements ICustomItemBlock, IStateMappedBlock {
	public BlockCavingRopeLight() {
		this.setTickRandomly(true);
		this.lightValue = 6;
	}

	@Override
	public BlockItem getItemBlock() {
		return null;
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, BlockState state, Random random) {
		List<EntityRopeNode> ropes = worldIn.getEntitiesOfClass(EntityRopeNode.class, Block.box(pos));
		if(ropes.isEmpty()) {
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.empty(true);
	}
}
