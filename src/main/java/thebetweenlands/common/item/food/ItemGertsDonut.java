package thebetweenlands.common.item.food;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;


public class ItemGertsDonut extends BLFoodItem {
	
    public ItemGertsDonut(Properties properties) {
    	super(false, 0, 0, properties);
        // super(6, 0.6F, false);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    	player.heal(8.0F);
        return super.use(world, player, hand);
        
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
    	PlayerEntity player = context.getPlayer();
    	World level = context.getLevel();
    	BlockPos pos = context.getClickedPos();
    	
        ItemStack stack = player.getItemInHand(context.getHand());
        BlockState iblockstate = level.getBlockState(pos);

        if (iblockstate.getBlock() instanceof BlockWeedwoodJukebox && !iblockstate.getValue(JukeboxBlock.HAS_RECORD)) {
            if (!level.isClientSide()) {
                ((JukeboxBlock) iblockstate.getBlock()).setRecord(level, pos, iblockstate, stack);
                level.levelEvent(null, 1010, pos, Item.getId(this));
                stack.shrink(stack.getCount());
                player.awardStat(Stats.PLAY_RECORD);
            } else {
                player.displayClientMessage(new TranslationTextComponent("DOH!"), true);
                level.playSound(player, pos, SoundEvents.GENERIC_EAT, SoundCategory.RECORDS, 1.0F, 1.0F);
            }

            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }
}
