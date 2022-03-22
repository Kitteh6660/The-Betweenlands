package thebetweenlands.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

//TODO: Rework or remove this file due to waterlogging added since 1.13+
public class ItemWaterPlaceable extends BlockItem {
	
	public ItemWaterPlaceable(Block block) {
		super(block);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		RayTraceResult rayTrace = this.rayTrace(world, player, true);

		if(rayTrace == null) {
			return new ActionResult<ItemStack>(ActionResultType.PASS, stack);
		} else {
			if(rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos rayTracePos = rayTrace.getLocation();

				if(!world.isBlockModifiable(player, rayTracePos) || !player.mayUseItemAt(rayTracePos.offset(rayTrace.sideHit), rayTrace.sideHit, stack)) {
					return new ActionResult<ItemStack>(ActionResultType.FAIL, stack);
				}

				BlockPos placePos = rayTracePos.above();
				BlockState iblockstate = world.getBlockState(rayTracePos);

				if(iblockstate.getMaterial() == Material.WATER && ((Integer) iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0 && world.isEmptyBlock(placePos)) {
					net.minecraftforge.common.util.BlockSnapshot snapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, placePos);

					world.setBlock(placePos, this.block.defaultBlockState());

					if(net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(player, snapshot, net.minecraft.util.Direction.UP, hand).isCanceled()) {
						snapshot.restore(true, false);
						return new ActionResult<ItemStack>(ActionResultType.FAIL, stack);
					}

					world.setBlock(placePos, this.block.defaultBlockState(), 11);

					if(player instanceof ServerPlayerEntity) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, placePos, stack);
					}

					if(!player.isCreative()) {
						stack.shrink(1);
					}

					player.awardStat(StatList.getObjectUseStats(this));

					BlockState state = world.getBlockState(placePos);
					SoundType soundtype = state.getBlock().getSoundType(state, world, placePos, player);
					world.playSound(player, rayTracePos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

					player.swingArm(hand);
					
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
				}
			}

			return new ActionResult<ItemStack>(ActionResultType.FAIL, stack);
		}
	}
}
