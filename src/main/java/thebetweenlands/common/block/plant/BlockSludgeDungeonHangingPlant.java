package thebetweenlands.common.block.plant;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.IDungeonFogBlock;
import thebetweenlands.client.handler.ItemTooltipHandler;

public class BlockSludgeDungeonHangingPlant extends BlockHangingPlant {
	@Override
	protected boolean canGrowAt(World world, BlockPos pos, BlockState state) {
		if(super.canGrowAt(world, pos, state)) {
			for(BlockPos.Mutable checkPos : BlockPos.getAllInBoxMutable(pos.offset(-6, -4, -6), pos.offset(6, 0, 6))) {
				if(world.isBlockLoaded(checkPos)) {
					BlockState offsetState = world.getBlockState(checkPos);
					Block offsetBlock = offsetState.getBlock();
					if(offsetBlock instanceof IDungeonFogBlock && ((IDungeonFogBlock) offsetBlock).isCreatingDungeonFog(world, checkPos, offsetState)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.sludge_dungeon_plant.mist"), 0));
	}
}
