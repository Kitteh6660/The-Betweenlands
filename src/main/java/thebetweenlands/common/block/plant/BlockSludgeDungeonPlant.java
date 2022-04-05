package thebetweenlands.common.block.plant;

import java.util.List;

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
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;

public class BlockSludgeDungeonPlant extends BlockPlant {
	
	public BlockSludgeDungeonPlant(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, BlockState state) {
		BlockState soil = world.getBlockState(pos.below());
		if(soil.getBlock() instanceof BlockGenericDugSoil) {
			return soil.getValue(BlockGenericDugSoil.FOGGED);
		}
		return false;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.bl.sludge_dungeon_plant.mist"));
	}
}
