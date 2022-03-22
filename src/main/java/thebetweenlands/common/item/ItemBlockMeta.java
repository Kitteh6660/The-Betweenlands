package thebetweenlands.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class ItemBlockMeta extends BlockItem {
	public ItemBlockMeta(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}
}