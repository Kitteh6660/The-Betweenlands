package thebetweenlands.common.block.plant;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.IDungeonFogBlock;
import thebetweenlands.client.handler.ItemTooltipHandler;

public class BlockSludgeDungeonHangingPlant extends BlockHangingPlant {
	
	public BlockSludgeDungeonHangingPlant(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canGrowAt(World world, BlockPos pos, BlockState state) {
		if(super.canGrowAt(world, pos, state)) {
			for(BlockPos.Mutable checkPos : BlockPos.betweenClosed(pos.offset(-6, -4, -6), pos.offset(6, 0, 6))) {
				if(world.isLoaded(checkPos)) {
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
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.bl.sludge_dungeon_plant.mist"));
	}
}
