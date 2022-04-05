package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public class ItemDraeton extends Item 
{
	public ItemDraeton(Properties properties) {
		super(properties);
		//this.setCreativeTab(BLCreativeTabs.ITEMS);
		//this.setMaxStackSize(1);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if(facing == Direction.UP) {
			if(!level.isClientSide()) {
				EntityDraeton draeton = new EntityDraeton(world);
				draeton.moveTo(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, player.yRot, 0);

				if(world.getBlockCollisions(draeton, draeton.getBoundingBox().inflate(1, 0, 1).expand(0, 3, 0)).isEmpty()) {
					world.addFreshEntity(draeton);
					player.getItemInHand(hand).shrink(1);
					player.awardStat(StatList.getObjectUseStats(this));
					return ActionResultType.SUCCESS;
				}

				return ActionResultType.FAIL;
			}

			return ActionResultType.SUCCESS;
		}

		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
}
