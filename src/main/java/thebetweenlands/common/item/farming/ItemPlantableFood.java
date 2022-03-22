package thebetweenlands.common.item.farming;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.item.food.BLFoodItem;
import thebetweenlands.common.item.food.BLFoods;

public abstract class BLPlantableFoodItem extends BLFoodItem
{
	public ItemPlantableFood(Properties properties) {
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = playerIn.getItemInHand(hand);
		Block block = worldIn.getBlockState(pos).getBlock();
		boolean isReplacing = block.isReplaceable(worldIn, pos);
		BlockPos facingOffset = pos.offset(facing);
		if (isReplacing || (worldIn.isEmptyBlock(facingOffset) || worldIn.getBlockState(facingOffset).getBlock().isReplaceable(worldIn, facingOffset))) {
			BlockPos newPos = isReplacing ? pos : facingOffset;
			block = worldIn.getBlockState(newPos).getBlock();
			Block placeBlock = this.getBlock(stack, playerIn, worldIn, newPos);
			if (block != placeBlock && placeBlock.canPlaceBlockAt(worldIn, newPos)) {
				if (!worldIn.isClientSide()) {
					worldIn.setBlockAndUpdate(newPos, this.getBlockState(placeBlock, stack, playerIn, worldIn, newPos));
					worldIn.playSound((PlayerEntity) null, (float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, placeBlock.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (placeBlock.getSoundType().getVolume() + 1.0F)
							/ 2.0F, placeBlock.getSoundType().getPitch() * 0.8F);
					stack.shrink(1);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}

	protected abstract Block getBlock(ItemStack stack, PlayerEntity playerIn, World worldIn, BlockPos pos);

	protected BlockState getBlockState(Block block, ItemStack stack, PlayerEntity playerIn, World worldIn, BlockPos pos) {
		return block.defaultBlockState();
	}
}
