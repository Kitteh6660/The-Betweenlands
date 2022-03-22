package thebetweenlands.api.loot;

import java.util.Random;

import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;

public interface ISharedLootCondition extends ILootCondition {
	public default boolean testCondition(Random rand, LootContext context, ISharedLootPool pool) {
		return this.testCondition(rand, context, pool);
	}
}
