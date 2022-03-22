package thebetweenlands.common.item.food;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;


public class ItemGertsDonut extends BLFoodItem {
	
    public ItemGertsDonut() {
        super(6, 0.6F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player) {
        super.onFoodEaten(stack, world, player);
        player.heal(8.0F);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
        ItemStack stack = playerIn.getItemInHand(hand);
        BlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() instanceof BlockWeedwoodJukebox && !iblockstate.getValue(JukeboxBlock.HAS_RECORD)) {
            if (!worldIn.isClientSide()) {
                ((JukeboxBlock) iblockstate.getBlock()).setRecord(worldIn, pos, iblockstate, stack);
                worldIn.playEvent(null, 1010, pos, Item.getIdFromItem(this));
                stack.shrink(stack.getCount());
                playerIn.awardStat(Stats.PLAY_RECORD);
            } else {
                playerIn.sendStatusMessage(new TextComponentString("DOH!"), true);
                worldIn.playSound(playerIn, pos, SoundEvents.GENERIC_EAT, SoundCategory.RECORDS, 1.0F, 1.0F);
            }

            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }
}
