package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemRope extends Item {
	public ItemRope() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = playerIn.getItemInHand(hand);
		if(worldIn.getBlockState(pos).getBlock() == BlockRegistry.ROPE || worldIn.getBlockState(pos.below()).getBlock() == BlockRegistry.ROPE) {
			BlockPos offsetPos = pos.below();

			if(worldIn.getBlockState(pos).getBlock() != BlockRegistry.ROPE) {
				offsetPos = offsetPos.below();
			}

			while(worldIn.getBlockState(offsetPos).getBlock() == BlockRegistry.ROPE) {
				offsetPos = offsetPos.below();
			}

			if(playerIn.mayUseItemAt(offsetPos, facing, stack) && BlockRegistry.ROPE.canPlaceBlockAt(worldIn, offsetPos)) {
				if(!worldIn.isClientSide()) {
					worldIn.setBlockAndUpdate(offsetPos, BlockRegistry.ROPE.defaultBlockState());

					if(!playerIn.isCreative()) {
						stack.shrink(1);
					}
				}

				return ActionResultType.SUCCESS;
			}
		} else if(BlockRegistry.ROPE.canPlaceBlockAt(worldIn, pos.below())) {
			if(!worldIn.isClientSide()) {
				worldIn.setBlockAndUpdate(pos.below(), BlockRegistry.ROPE.defaultBlockState());

				if(!playerIn.isCreative()) {
					stack.shrink(1);
				}
			}

			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
}
