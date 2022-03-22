package thebetweenlands.common.item.farming;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public abstract class ItemPlantable extends Item {
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		World world = context.getLevel();
		ItemStack stack = player.getItemInHand(context.getHand());
		Block block = world.getBlockState(context.getClickedPos()).getBlock();
		boolean isReplacing = block.isReplaceable(world, context.getClickedPos());
		BlockPos facingOffset = context.getClickedPos().offset(context.getClickedFace());
		if (isReplacing || (world.isEmptyBlock(facingOffset) || world.getBlockState(facingOffset).getBlock().isReplaceable(world, facingOffset))) {
			BlockPos newPos = isReplacing ? context.getClickedPos() : facingOffset;
			block = world.getBlockState(newPos).getBlock();
			Block placeBlock = this.getBlock(stack, player, world, newPos);
			if (placeBlock != null && block != placeBlock && placeBlock.canPlaceBlockAt(world, newPos)) {
				if (!world.isClientSide()) {
					world.setBlockAndUpdate(newPos, this.getBlockState(placeBlock, stack, player, world, newPos));
					world.playSound((PlayerEntity)null, (float)context.getClickedPos().getX() + 0.5F, (float)context.getClickedPos().getY() + 0.5F, (float)context.getClickedPos().getZ() + 0.5F, placeBlock.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (placeBlock.getSoundType().getVolume() + 1.0F) / 2.0F, placeBlock.getSoundType().getPitch() * 0.8F);
					stack.shrink(1);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}

	@Nullable
	protected abstract Block getBlock(ItemStack stack, PlayerEntity playerIn, World world, BlockPos pos);

	protected BlockState getBlockState(Block block, ItemStack stack, PlayerEntity playerIn, World world, BlockPos pos) {
		return block.defaultBlockState();
	}
}
