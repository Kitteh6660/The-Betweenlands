package thebetweenlands.api.loot;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.loot.shared.SharedLootPool;

public interface ISharedLootContainer extends Container {
	@Nullable
	public StorageID getSharedLootPoolStorageID();

	public boolean isSharedLootTable();

	public void removeLootTable();

	public boolean fillInventoryWithLoot(@Nullable Player player);
	
	/**
	 * Sets the shared loot table of this loot container and registers/links it to the specified
	 * shared loot pool storage ({@link SharedLootPoolStorage#registerSharedLootInventory(net.minecraft.util.math.BlockPos, ResourceLocation)}.
	 * @param storage
	 * @param lootTable
	 * @param lootTableSeed
	 */
	public void setSharedLootTable(SharedLootPool storage, ResourceLocation lootTable, long lootTableSeed);
}
