package thebetweenlands.common.item.herblore;

import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemScrivenerTool extends Item {
	public ItemScrivenerTool() {
		this.setMaxDamage(256);
		this.setMaxStackSize(1);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);

		if(facing == Direction.UP && player.mayUseItemAt(pos.above(), facing, stack) && BlockRegistry.SCRIVENER_SULFUR_MARK.canPlaceBlockAt(worldIn, pos.above())) {
			ItemStack sulfurStack = ItemStack.EMPTY;

			for(int i = 0; i < player.inventory.getContainerSize(); ++i) {
				ItemStack invStack = player.inventory.getItem(i);

				if(!invStack.isEmpty() && EnumItemMisc.SULFUR.isItemOf(invStack)) {
					sulfurStack = invStack;
					break;
				}
			}

			if(!sulfurStack.isEmpty()) {
				if(!worldIn.isClientSide() && !player.isCreative()) {
					sulfurStack.shrink(1);
					stack.damageItem(1, player);
				}

				BlockState markState = BlockRegistry.SCRIVENER_SULFUR_MARK.defaultBlockState();
				worldIn.setBlockState(pos.above(), markState);

				SoundType sound = markState.getBlock().getSoundType(markState, worldIn, pos, player);
				worldIn.playSound(player, pos, markState.getBlock().getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}
}
