package thebetweenlands.common.block.structure;

import thebetweenlands.common.registries.BlockRegistry;

public class BlockCompactedMudSlope extends BlockSlanted {
	
	public BlockCompactedMudSlope(Properties properties) {
		super(BlockRegistry.COMPACTED_MUD.get().defaultBlockState(), properties);
		//this.setSoundType(SoundType.GRAVEL).setHardness(1F).setResistance(10.0F);
	}
}
