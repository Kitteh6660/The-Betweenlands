package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockState;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockStairsBetweenlands extends BlockStairs {
    public BlockStairsBetweenlands(BlockState modelState) {
        super(modelState);
        setLightOpacity(0);
        useNeighborBrightness = true;
        setCreativeTab(BLCreativeTabs.BLOCKS);
    }
}
