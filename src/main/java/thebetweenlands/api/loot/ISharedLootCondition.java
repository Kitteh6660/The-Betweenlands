package thebetweenlands.api.loot;

import java.util.Random;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ISharedLootCondition extends LootItemCondition {
	
	public default boolean testCondition(Random rand, LootContext context, ISharedLootPool pool) {
		return this.testCondition(rand, context, pool);
	}
}
