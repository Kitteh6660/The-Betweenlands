package thebetweenlands.api.loot;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;

public abstract class LootTableView extends LootTable {
	public LootTableView() {
		super(new LootPool[0]);
	}

	@Override
	public final boolean isFrozen() {
		return true;
	}
}
