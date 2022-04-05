package thebetweenlands.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.container.BlockRuneCarvingTable;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemRuneCarvingTable extends BlockItem {
	
	public ItemRuneCarvingTable(Properties properties) {
		super(BlockRegistry.RUNE_CARVING_TABLE.get(), properties);
		//this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if (worldIn.isClientSide()) {
			return ActionResultType.SUCCESS;
		} else if (facing != Direction.UP) {
			return ActionResultType.FAIL;
		} else {
			BlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			boolean replaceable = block.isReplaceable(worldIn, pos);

			if(!replaceable) {
				pos = pos.above();
			}

			int rotation = MathHelper.floor((double)(player.yRot * 4.0F / 360.0F) + 0.5D) & 3;
			Direction placementFacing = Direction.byHorizontalIndex(rotation);
			BlockPos secondPosition = pos.above();
			ItemStack stack = player.getItemInHand(hand);

			if(player.mayUseItemAt(pos, facing, stack) && player.mayUseItemAt(secondPosition, facing, stack)) {
				BlockState secondState = worldIn.getBlockState(secondPosition);
				boolean secondReplaceable = secondState.getBlock().isReplaceable(worldIn, secondPosition);
				boolean placeable = replaceable || worldIn.isEmptyBlock(pos);
				boolean secondPlaceable = secondReplaceable || worldIn.isEmptyBlock(secondPosition);

				if(placeable && secondPlaceable && worldIn.getBlockState(pos.below()).isTopSolid()) {
					BlockState placedState = BlockRegistry.RUNE_CARVING_TABLE.get().defaultBlockState().setValue(BlockRuneCarvingTable.FACING, placementFacing).setValue(BlockRuneCarvingTable.PART, BlockRuneCarvingTable.EnumPartType.MAIN);

					worldIn.setBlock(pos, placedState, 10);
					worldIn.setBlock(secondPosition, placedState.setValue(BlockRuneCarvingTable.PART, BlockRuneCarvingTable.EnumPartType.FILLER), 10);

					SoundType soundtype = placedState.getBlock().getSoundType(placedState, worldIn, pos, player);
					worldIn.playSound((PlayerEntity)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

					worldIn.notifyNeighborsRespectDebug(pos, block, false);
					worldIn.notifyNeighborsRespectDebug(secondPosition, secondState.getBlock(), false);

					if(player instanceof ServerPlayerEntity) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
					}

					stack.shrink(1);
					return ActionResultType.SUCCESS;
				}
				else
				{
					return ActionResultType.FAIL;
				}
			}
			else
			{
				return ActionResultType.FAIL;
			}
		}
	}
}