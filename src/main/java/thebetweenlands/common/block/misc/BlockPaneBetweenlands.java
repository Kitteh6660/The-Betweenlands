package thebetweenlands.common.block.misc;

import net.minecraft.block.Block;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockPaneBetweenlands extends PaneBlock 
{
	public BlockPaneBetweenlands(Block.Properties properties, Material materialIn) {
		super(properties, materialIn, false);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.GLASS);
	}
	
	public BlockPaneBetweenlands(Block.Properties properties, Material materialIn, boolean canDrop) {
		super(properties, materialIn, canDrop);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.GLASS);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
