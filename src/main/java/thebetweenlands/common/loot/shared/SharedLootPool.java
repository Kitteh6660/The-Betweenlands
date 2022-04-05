package thebetweenlands.common.loot.shared;

import java.util.Random;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.api.loot.LootTableView;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class SharedLootPool implements ISharedLootPool {
	protected ResourceLocation lootTableLocation;

	protected SharedLootPoolStorage storage;

	protected LootTable view;

	protected final Object2IntMap<String> removedItems = new Object2IntOpenHashMap<>();
	protected final Object2LongMap<String> poolSeeds = new Object2LongOpenHashMap<>();
	protected final Object2LongMap<String> entrySeeds = new Object2LongOpenHashMap<>();

	protected long sharedLootSeed;

	protected int guaranteeCounter;

	private SharedLootPool(ResourceLocation lootTableLocation, long seed) {
		this.lootTableLocation = lootTableLocation;
		this.sharedLootSeed = seed;
	}

	public SharedLootPool(ResourceLocation lootTableLocation, long seed, @Nullable SharedLootPoolStorage storage) {
		this(lootTableLocation, seed);
		this.storage = storage;
	}

	public SharedLootPool(CompoundNBT nbt, @Nullable SharedLootPoolStorage storage) {
		this(null, 0);
		this.storage = storage;
		this.readFromNBT(nbt);
	}

	@Override
	public ResourceLocation getLootTable() {
		return this.lootTableLocation;
	}

	@Override
	public LootTableView getLootTableView() {
		return new SharedLootTableView(this);
	}

	@Override
	public void regenerate() {
		this.removedItems.clear();
		this.entrySeeds.clear();
		this.poolSeeds.clear();
		this.guaranteeCounter = 0;

		this.setLocationDirty();
	}

	@Override
	public void refill() {
		this.removedItems.clear();
		this.guaranteeCounter = 0;

		this.setLocationDirty();
	}

	@Override
	public int getRemovedItems(String pool, int poolRoll, String entry) {
		return this.removedItems.getInt(String.format("%s#%d#%s", pool, poolRoll, entry));
	}

	@Override
	public void setRemovedItems(String pool, int poolRoll, String entry, int count) {
		this.removedItems.put(String.format("%s#%d#%s", pool, poolRoll, entry), count);

		this.setLocationDirty();
	}

	@Override
	public long getLootPoolSeed(Random rand, String pool, int poolRoll) {
		String key = String.format("%s#%d", pool, poolRoll);
		long seed;
		if(this.poolSeeds.containsKey(key)) {
			seed = this.poolSeeds.get(key);
		} else {
			this.poolSeeds.put(key, seed = rand.nextLong());

			this.setLocationDirty();
		}
		return seed;
	}

	@Override
	public long getLootEntrySeed(Random rand, String pool, int poolRoll, String entry) {
		String key = String.format("%s#%d#%s", pool, poolRoll, entry);
		long seed;
		if(this.entrySeeds.containsKey(key)) {
			seed = this.entrySeeds.get(key);
		} else {
			this.entrySeeds.put(key, seed = rand.nextLong());

			this.setLocationDirty();
		}
		return seed;
	}

	@Override
	public int getGuaranteeCounter() {
		return this.guaranteeCounter;
	}

	void incrementGuaranteeCounter() {
		this.guaranteeCounter++;
		this.setLocationDirty();
	}

	@Override
	public float getGuaranteePercentage() {
		int lootInventories = this.storage != null ? this.storage.getSharedLootInventories(this.lootTableLocation) : 0;
		return lootInventories <= 0 ? 1.0F : Math.min((float)this.getGuaranteeCounter() / (float)lootInventories, 1.0F);
	}

	protected void setLocationDirty() {
		if(this.storage != null) {
			this.storage.setChanged();
		}
	}

	public CompoundNBT save(CompoundNBT nbt) {
		if(!this.removedItems.isEmpty()) {
			CompoundNBT removedItemsNbt = new CompoundNBT();
			for(Object2IntMap.Entry<String> entry : this.removedItems.object2IntEntrySet()) {
				removedItemsNbt.putInt(entry.getKey(), entry.getIntValue());
			}
			nbt.put("removedItems", removedItemsNbt);
		}

		if(!this.poolSeeds.isEmpty()) {
			CompoundNBT poolSeedsNbt = new CompoundNBT();
			for(Object2LongMap.Entry<String> entry : this.poolSeeds.object2LongEntrySet()) {
				poolSeedsNbt.setLong(entry.getKey(), entry.getLongValue());
			}
			nbt.put("poolSeeds", poolSeedsNbt);
		}

		if(!this.entrySeeds.isEmpty()) {
			CompoundNBT entrySeedsNbt = new CompoundNBT();
			for(Object2LongMap.Entry<String> entry : this.entrySeeds.object2LongEntrySet()) {
				entrySeedsNbt.setLong(entry.getKey(), entry.getLongValue());
			}
			nbt.put("entrySeeds", entrySeedsNbt);
		}

		if(this.lootTableLocation != null) {
			nbt.putString("lootTable", this.lootTableLocation.toString());
		}

		nbt.putInt("generatedLootTables", this.guaranteeCounter);

		nbt.setLong("sharedLootSeed", this.sharedLootSeed);

		return nbt;
	}

	public void load(BlockState state, CompoundNBT nbt) {
		this.removedItems.clear();
		CompoundNBT removedItemsNbt = nbt.getCompoundTag("removedItems");
		for(String key : removedItemsNbt.getKeySet()) {
			this.removedItems.put(key, removedItemsNbt.getInt(key));
		}

		this.poolSeeds.clear();
		CompoundNBT poolSeedsNbt = nbt.getCompoundTag("poolSeeds");
		for(String key : poolSeedsNbt.getKeySet()) {
			this.poolSeeds.put(key, poolSeedsNbt.getLong(key));
		}

		this.entrySeeds.clear();
		CompoundNBT entrySeedsNbt = nbt.getCompoundTag("entrySeeds");
		for(String key : entrySeedsNbt.getKeySet()) {
			this.entrySeeds.put(key, entrySeedsNbt.getLong(key));
		}

		this.lootTableLocation = null;
		if(nbt.contains("lootTable", Constants.NBT.TAG_STRING)) {
			this.lootTableLocation = new ResourceLocation(nbt.getString("lootTable"));
		}

		this.guaranteeCounter = nbt.getInt("generatedLootTables");

		this.sharedLootSeed = nbt.getLong("sharedLootSeed");
	}

	@Override
	public long getLootTableSeed() {
		return this.sharedLootSeed;
	}
}
