package thebetweenlands.common.item.farming;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemAspectrusSeeds extends ItemPlantableSeeds 
{
	public ItemAspectrusSeeds(Properties properties) {
		super(properties);
		// super(() -> BlockRegistry.ASPECTRUS_CROP.defaultBlockState());
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = playerIn.getItemInHand(hand);
		BlockState state = worldIn.getBlockState(pos);
		if (facing == Direction.UP && playerIn.mayUseItemAt(pos.offset(facing), facing, stack) && 
				state.getBlock().canSustainPlant(state, worldIn, pos, Direction.UP, this) 
				&& worldIn.getBlockState(pos.above()).getBlock() == BlockRegistry.RUBBER_TREE_PLANK_FENCE
				&& (this.soilMatcher == null || this.soilMatcher.test(state))) {
			BlockState plantState = this.crops.get();
			worldIn.setBlockAndUpdate(pos.above(), plantState);
			this.onPlant(playerIn, worldIn, pos.above(), hand, facing, hitX, hitY, hitZ, plantState);
			stack.shrink(1);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.aspectrus_seeds.mist"), 0));
	}
}
