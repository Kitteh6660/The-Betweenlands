package thebetweenlands.common.item.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.datafix.fixes.JukeboxRecordItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;
import thebetweenlands.common.sound.BLSoundEvent;

public class ItemBLRecord extends MusicDiscItem {
	
    private String name;

    public ItemBLRecord(BLSoundEvent soundIn) {
        super(soundIn.name, soundIn);
        name = soundIn.name;
        //this.setCreativeTab(BLCreativeTabs.SPECIALS);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
        ItemStack stack = playerIn.getItemInHand(hand);
        BlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() instanceof BlockWeedwoodJukebox && !iblockstate.getValue(JukeboxBlock.HAS_RECORD)) {
            if (!worldIn.isClientSide()) {
                ((JukeboxBlock) iblockstate.getBlock()).setRecord(worldIn, pos, iblockstate, stack);
                worldIn.playEvent(null, 1010, pos, Item.getIdFromItem(this));
                stack.shrink(1);
                playerIn.awardStat(Stats.PLAY_RECORD);
            }

            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getRecordNameLocal() {
        return I18n.get("item.thebetweenlands.record." + name + ".desc");
    }

}
