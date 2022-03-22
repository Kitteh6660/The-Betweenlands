package thebetweenlands.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemBlockRoot extends BlockItem {
	public ItemBlockRoot() {
		super(BlockRegistry.ROOT);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
        if(!world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
            pos = pos.offset(facing);
        }
        
        Block toPlace = this.block;
        
        if(world.getBlockState(pos).getMaterial() == Material.WATER) {
        	toPlace = BlockRegistry.ROOT_UNDERWATER;
        }

        ItemStack stack = player.getItemInHand(hand);

        if(!stack.isEmpty() && player.mayUseItemAt(pos, facing, stack) && world.mayPlace(toPlace, pos, false, facing, (Entity)null)) {
            int meta = this.getMetadata(stack.getMetadata());
            BlockState state = toPlace.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player, hand);

            if (placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, state)) {
                state = world.getBlockState(pos);
                SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                stack.shrink(1);
            }

            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.FAIL;
        }
    }
}
