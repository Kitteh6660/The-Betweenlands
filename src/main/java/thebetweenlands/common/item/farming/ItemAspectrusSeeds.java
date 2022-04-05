package thebetweenlands.common.item.farming;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemAspectrusSeeds extends ItemPlantableSeeds
{
	public ItemAspectrusSeeds(Properties properties) {
		super(BlockRegistry.ASPECTRUS_CROP.get().defaultBlockState(), properties);
		// super(() -> BlockRegistry.ASPECTRUS_CROP.defaultBlockState());
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Hand hand = context.getHand();
		Direction facing = context.getClickedFace();		
		
		ItemStack stack = player.getItemInHand(hand);
		BlockState state = world.getBlockState(pos);
		if (facing == Direction.UP && player.mayUseItemAt(pos.relative(facing), facing, stack) && state.getBlock().canSustainPlant(state, world, pos, Direction.UP, this) && world.getBlockState(pos.above()).getBlock() == BlockRegistry.RUBBER_TREE_PLANK_FENCE.get() && (this.soilMatcher == null || this.soilMatcher.test(state))) {
			BlockState plantState = this.crops.get();
			world.setBlockAndUpdate(pos.above(), plantState);
			this.onPlant(context);
			stack.shrink(1);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.bl.aspectrus_seeds.mist"));
	}
}
