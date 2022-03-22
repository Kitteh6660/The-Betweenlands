package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemRandomSound extends Item {
    public ItemRandomSound() {
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {

        int randomSound = itemRand.nextInt(SoundRegistry.SOUNDS.size());
        worldIn.playSound(player, pos, SoundRegistry.SOUNDS.get(randomSound), SoundCategory.NEUTRAL, 1f, 1f);
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
