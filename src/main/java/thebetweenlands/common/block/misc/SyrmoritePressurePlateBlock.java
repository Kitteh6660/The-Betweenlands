package thebetweenlands.common.block.misc;

import java.util.List;

import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SyrmoritePressurePlateBlock extends PressurePlateBlock {

	public SyrmoritePressurePlateBlock(Properties properties) {
		super(Sensitivity.MOBS, properties);
	}
	
	@Override
	protected int getSignalStrength(World worldIn, BlockPos pos) {
		AxisAlignedBB axisalignedbb = TOUCH_AABB.move(pos);
		List <? extends Entity > list;

		list = worldIn.<Entity>getEntitiesOfClass(PlayerEntity.class, axisalignedbb);

		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.isIgnoringBlockTriggers()) {
					return 15;
				}
			}
		}

		return 0;
	}
}
