package thebetweenlands.common.world.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.loot.shared.SharedLootPool;

public class SharedLootPoolStorage extends LocalStorageImpl {
	
	private Map<ResourceLocation, SharedLootPool> sharedLootPools = new HashMap<>();
	private TObjectIntMap<ResourceLocation> lootInventories = new TObjectIntHashMap<>();

	private long lootSeed;

	public SharedLootPoolStorage(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region);
	}

	public SharedLootPoolStorage(IWorldStorage worldStorage, StorageID id, LocalRegion region, long lootSeed) {
		super(worldStorage, id, region);
		this.lootSeed = lootSeed;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		//No bounding box required.
		//Loot inventories are directly linked to the storage by storage ID.
		return null;
	}

	@Override
	public IGenericDataManagerAccess getDataManager() {
		return null;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt = super.save(nbt);

		if(!this.sharedLootPools.isEmpty()) {
			ListNBT sharedLootPoolsNbt = new ListNBT();

			for(SharedLootPool sharedLootPool : this.sharedLootPools.values()) {
				sharedLootPoolsNbt.add(sharedLootPool.save(new CompoundNBT()));
			}

			nbt.put("sharedLootPools", sharedLootPoolsNbt);
		}

		if(!this.lootInventories.isEmpty()) {
			ListNBT lootInventoriesNbt = new ListNBT();
			this.lootInventories.forEachEntry((table, count) -> {
				CompoundNBT entryNbt = new CompoundNBT();
				entryNbt.putString("table", table.toString());
				entryNbt.putInt("count", count);
				lootInventoriesNbt.appendTag(entryNbt);
				return true;
			});
			nbt.put("lootInventories", lootInventoriesNbt);
		}

		return nbt;
	}

	@Override
	public void load(CompoundNBT nbt) {
		super.load(nbt);

		this.sharedLootPools.clear();
		ListNBT sharedLootPoolsNbt = nbt.getList("sharedLootPools", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < sharedLootPoolsNbt.size(); i++) {
			SharedLootPool sharedLootPool = new SharedLootPool(sharedLootPoolsNbt.getCompound(i), this);
			ResourceLocation lootTable = sharedLootPool.getLootTable();
			if(lootTable != null) {
				this.sharedLootPools.put(lootTable, sharedLootPool);
			}
		}

		this.lootInventories.clear();
		ListNBT lootInventoriesNbt = nbt.getList("lootInventories", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < lootInventoriesNbt.size(); i++) {
			CompoundNBT entryNbt = lootInventoriesNbt.getCompound(i);
			this.lootInventories.put(new ResourceLocation(entryNbt.getString("table")), entryNbt.getInt("count"));
		}
	}

	public int getSharedLootInventories(ResourceLocation lootTable) {
		return this.lootInventories.get(lootTable);
	}

	public void registerSharedLootInventory(BlockPos pos, ResourceLocation lootTable) {
		this.lootInventories.adjustOrPutValue(lootTable, 1, 1);

		//Make sure this storage is linked to the chunk the loot inventory is in
		this.linkChunkSafely(new ChunkPos(pos));

		this.setChanged();
	}

	@Nullable
	public ISharedLootPool getSharedLootPool(ResourceLocation lootTable) {
		return this.sharedLootPools.get(lootTable);
	}

	@Nullable
	public ISharedLootPool getOrCreateSharedLootPool(ResourceLocation lootTable) {
		ISharedLootPool pool = this.getSharedLootPool(lootTable);

		if(pool != null) {
			return pool;
		}

		SharedLootPool newPool = new SharedLootPool(lootTable, this.lootSeed, this);
		this.sharedLootPools.put(lootTable, newPool);
		this.setChanged();

		return newPool;
	}

	@Nullable
	public ISharedLootPool removeSharedLootPool(ResourceLocation lootTable) {
		ISharedLootPool pool = this.sharedLootPools.remove(lootTable);
		if(pool != null) {
			this.setChanged();
		}

		return pool;
	}

	public Set<ResourceLocation> getSharedLootPoolKeys() {
		return this.sharedLootPools.keySet();
	}
}
