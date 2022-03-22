package thebetweenlands.common.block.plant;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.world.gen.feature.WorldGenSmallSpiritTree;

public class BlockSaplingSpiritTree extends BlockSaplingBetweenlands implements ICustomItemBlock {
	public BlockSaplingSpiritTree() {
		super(new WorldGenSmallSpiritTree());
	}

	@Override
	public BlockItem getItemBlock() {
		return new BlockItem(this) {
			@Override
			public EnumRarity getRarity(ItemStack stack) {
				return EnumRarity.RARE;
			}
		};
	}
}
