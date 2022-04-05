package thebetweenlands.api.loot;

import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.ILootFunction;

public abstract class LootTableView extends LootTable {
	
	public LootTableView(LootParameterSet params, LootPool[] pool, ILootFunction[] func) {
		super(params, pool, func);
	}

	@Override
	public final boolean isFrozen() {
		return true;
	}
}
