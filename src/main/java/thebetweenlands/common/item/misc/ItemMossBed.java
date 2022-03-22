package thebetweenlands.common.item.misc;

import net.minecraft.block.Block;
import net.minecraft.item.BedItem;

public class ItemMossBed extends BedItem {
	
	public ItemMossBed(Block block, Properties properties) {
		super(block, properties);
		// this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	// Probably not needed with how bed item is overhauled.
	/*@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if (worldIn.isClientSide()) {
			return ActionResultType.SUCCESS;
		} else if (facing != Direction.UP) {
			return ActionResultType.FAIL;
		} else {
			BlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			boolean isReplaceable = block.isReplaceable(worldIn, pos);

			if (!isReplaceable) {
				pos = pos.above();
			}

			int rot = MathHelper.floor((double)(player.yRot * 4.0F / 360.0F) + 0.5D) & 3;
			Direction placementFacing = Direction.byHorizontalIndex(rot);
			BlockPos headPos = pos.offset(placementFacing);
			ItemStack stack = player.getItemInHand(hand);

			if (player.mayUseItemAt(pos, facing, stack) && player.mayUseItemAt(headPos, facing, stack)) {
				BlockState headState = worldIn.getBlockState(headPos);
				boolean isHeadPosReplaceable = headState.getBlock().isReplaceable(worldIn, headPos);
				boolean isFootPlaceable = isReplaceable || worldIn.isEmptyBlock(pos);
				boolean isHeadPlaceable = isHeadPosReplaceable || worldIn.isEmptyBlock(headPos);

				if (isFootPlaceable && isHeadPlaceable && worldIn.getBlockState(pos.below()).isTopSolid() && worldIn.getBlockState(headPos.below()).isTopSolid()) {
					BlockState bedState = BlockRegistry.MOSS_BED.defaultBlockState().setValue(BlockBed.OCCUPIED, Boolean.valueOf(false)).setValue(BlockBed.FACING, placementFacing).setValue(BlockBed.PART, BlockBed.EnumPartType.FOOT);

					if(worldIn.setBlockState(pos, bedState, 10)) {
						worldIn.setBlockState(headPos, bedState.setValue(BlockBed.PART, BlockBed.EnumPartType.HEAD), 10);
					}

					SoundType sound = bedState.getBlock().getSoundType(bedState, worldIn, pos, player);
					worldIn.playSound((PlayerEntity)null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

					worldIn.notifyNeighborsRespectDebug(pos, block, false);
					worldIn.notifyNeighborsRespectDebug(headPos, headState.getBlock(), false);

					if (player instanceof ServerPlayerEntity) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
					}

					stack.shrink(1);
					return ActionResultType.SUCCESS;
				} else {
					return ActionResultType.FAIL;
				}
			} else {
				return ActionResultType.FAIL;
			}
		}
	}*/
}