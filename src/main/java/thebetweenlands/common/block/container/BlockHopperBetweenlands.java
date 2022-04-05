package thebetweenlands.common.block.container;

import net.minecraft.block.HopperBlock;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityHopperBetweenlands;
import thebetweenlands.util.AdvancedStateMap;

public class BlockHopperBetweenlands extends HopperBlock {
	
	public BlockHopperBetweenlands(Properties properties) {
		super(properties);
		/*this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(3.0F);
		this.setResistance(8.0F);
		this.setSoundType(SoundType.METAL);*/
	}
	
    @Override
	public TileEntity newBlockEntity(IBlockReader level) {
        return new TileEntityHopperBetweenlands();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setStateMapper(AdvancedStateMap.Builder builder) {
        builder.ignore(ENABLED);
    }
}
