package thebetweenlands.common.tile;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.loot.ISharedLootContainer;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;

public abstract class TileEntityLootInventory extends TileEntityBasicInventory implements ISharedLootContainer {
	
	protected StorageID storageId;
	protected ResourceLocation lootTable;
	protected long lootTableSeed;
	protected long sharedLootTableSeed;
	protected boolean isSharedLootTable;

	public TileEntityLootInventory(TileEntityType<?> te, int invtSize, String name) {
		super(te, invtSize, name);
	}

	@Override
	public boolean fillInventoryWithLoot(@Nullable PlayerEntity player) {
		return fillInventoryWithLoot(this.level, this, player, this.lootTableSeed);
	}

	public static boolean fillInventoryWithLoot(World world, ISharedLootContainer inventory, @Nullable PlayerEntity player, long seed) {
		ResourceLocation lootTableLocation = inventory.getLootTable();

		if(lootTableLocation != null && world instanceof ServerWorld) {
			LootTable lootTable = null;

			if(inventory.isSharedLootTable()) {
				StorageID storageId = inventory.getSharedLootPoolStorageID();

				if(storageId != null) {
					ILocalStorageHandler handler = BetweenlandsWorldStorage.forWorld(world).getLocalStorageHandler();

					ILocalStorage storage = handler.getLocalStorage(storageId);

					boolean foundLootTable = false;

					if(storage instanceof SharedLootPoolStorage) {
						SharedLootPoolStorage sharedStorage = (SharedLootPoolStorage) storage;

						ISharedLootPool sharedLootPool = sharedStorage.getOrCreateSharedLootPool(lootTableLocation);

						if(sharedLootPool != null) {
							lootTable = sharedLootPool.getLootTableView();
							foundLootTable = true;
						}
					}

					if(!foundLootTable) {
						TheBetweenlands.logger.info("Shared loot inventory could not find shared loot pool storage. Storage ID: " + storageId + "." + (inventory instanceof TileEntity ? " " + ((TileEntity) inventory).getBlockPos() : ""));
					}
				} else {
					TheBetweenlands.logger.info("Shared loot inventory has null storage ID.");
				}
			} else {
				lootTable = world.getLootTableManager().getLootTableFromLocation(lootTableLocation);
			}

			if(lootTable != null) {
				inventory.removeLootTable();

				Random random;

				if(seed == 0L) {
					random = new Random();
				} else {
					random = new Random(seed);
				}

				LootContext.Builder lootBuilder = new LootContext.Builder((ServerWorld) world);

				if(player != null) {
					lootBuilder.withLuck(player.getLuck());
				}

				List<ItemStack> loot = lootTable.generateLootForPools(random, lootBuilder.build());

				fillInventoryRandomly(random, loot, inventory);

				return true;
			}
		}

		return false;
	}

	public static boolean fillInventoryRandomly(Random random, List<ItemStack> loot, IInventory itemHandler)  {
		//Get empty slots
		List<Integer> emptySlots = Lists.<Integer>newArrayList();
		for (int i = 0; i < itemHandler.getContainerSize(); ++i) {
			if (itemHandler.getItem(i).isEmpty()) {
				emptySlots.add(Integer.valueOf(i));
			}
		}
		Collections.shuffle(emptySlots, random);

		//Split and shuffle items
		List<ItemStack> splittableStacks = Lists.<ItemStack>newArrayList();
		Iterator<ItemStack> iterator = loot.iterator();
		while (iterator.hasNext()) {
			ItemStack itemstack = (ItemStack)iterator.next();

			if (itemstack.getCount() <= 0) {
				iterator.remove();
			} else if (itemstack.getCount() > 1) {
				splittableStacks.add(itemstack);
				iterator.remove();
			}
		}
		int emptySlotCount = emptySlots.size();

		emptySlotCount = emptySlotCount - loot.size() - splittableStacks.size();

		while (emptySlotCount > 0 && ((List<ItemStack>)splittableStacks).size() > 0) {
			ItemStack itemstack2 = (ItemStack)splittableStacks.remove(MathHelper.getInt(random, 0, splittableStacks.size() - 1));
			int i = MathHelper.getInt(random, 1, itemstack2.getCount() / 2);
			itemstack2.shrink( i);
			ItemStack itemstack1 = itemstack2.copy();
			itemstack1.setCount(i);

			emptySlotCount--;

			if (emptySlotCount > 0 && itemstack2.getCount() > 1 && random.nextBoolean()) {
				splittableStacks.add(itemstack2);
			} else {
				loot.add(itemstack2);
			}

			if (emptySlotCount > 0 && itemstack1.getCount() > 1 && random.nextBoolean()) {
				splittableStacks.add(itemstack1);
			} else {
				loot.add(itemstack1);
			}
		}
		loot.addAll(splittableStacks);
		Collections.shuffle(loot, random);

		//Fill inventory
		for (ItemStack itemstack : loot) {
			if (!itemstack.isEmpty() && emptySlots.isEmpty()) {
				TheBetweenlands.logger.info("Tried to over-fill a container");
				return false;
			}

			if (itemstack.isEmpty()) {
				itemHandler.setItem(((Integer)emptySlots.remove(emptySlots.size() - 1)).intValue(), ItemStack.EMPTY);
			} else {
				itemHandler.setItem(((Integer)emptySlots.remove(emptySlots.size() - 1)).intValue(), itemstack);
			}
		}

		return true;
	}

	public void setLootTable(@Nullable ResourceLocation lootTable, long lootTableSeed) {
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
		this.isSharedLootTable = false;
		this.setChanged();
	}

	@Override
	public void setSharedLootTable(SharedLootPoolStorage storage, ResourceLocation lootTable, long lootTableSeed) {
		if(!lootTable.equals(this.lootTable)) {
			storage.registerSharedLootInventory(this.worldPosition, lootTable);
		}
		this.storageId = storage.getID();
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
		this.isSharedLootTable = true;
		this.setChanged();
	}

	@Override
	public void removeLootTable() {
		if(this.lootTable != null) {
			this.lootTable = null;
			this.setChanged();
		}
	}

	@Override
	public ResourceLocation getLootTable() {
		return this.lootTable;
	}

	@Override
	protected void writeInventoryNBT(CompoundNBT nbt) {
		if(!this.trySaveLootTable(nbt)) {
			super.writeInventoryNBT(nbt);
		}
	}

	@Override
	protected void readInventoryNBT(CompoundNBT nbt) {
		if(!this.tryLoadLootTable(nbt)) {
			super.readInventoryNBT(nbt);
		}
	}

	/**
	 * Tries to read a loot table from the NBT. Returns true if found loot table != null
	 * @param compound
	 * @return
	 */
	protected boolean tryLoadLootTable(CompoundNBT compound) {
		if (compound.contains("LootTable", 8)) {
			this.lootTable = new ResourceLocation(compound.getString("LootTable"));
			this.lootTableSeed = compound.getLong("LootTableSeed");
			this.isSharedLootTable = compound.getBoolean("SharedLootTable");

			if(compound.contains("StorageID", Constants.NBT.TAG_COMPOUND)) {
				this.storageId = StorageID.load(compound.getCompound("StorageID"));
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Writes the loot table to NBT. Returns true if loot table != null
	 * @param compound
	 * @return
	 */
	protected boolean trySaveLootTable(CompoundNBT compound) {
		if (this.lootTable != null) {
			compound.putString("LootTable", this.lootTable.toString());

			if (this.lootTableSeed != 0L) {
				compound.putLong("LootTableSeed", this.lootTableSeed);
			}

			compound.putBoolean("SharedLootTable", this.isSharedLootTable);

			if(this.storageId != null) {
				compound.put("StorageID", this.storageId.save(new CompoundNBT()));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void accessSlot(int slot) {
		if(fillInventoryWithLoot(this.level, this, null, this.lootTableSeed)) {
			this.setChanged();
		}
	}

	@Override
	public void clearContent() {
		if(fillInventoryWithLoot(this.level, this, null, this.lootTableSeed)) {
			this.setChanged();
		}
		super.clearContent();
	}

	@Override
	public boolean isSharedLootTable() {
		return this.isSharedLootTable;
	}

	@Override
	public StorageID getSharedLootPoolStorageID() {
		return this.storageId;
	}
}
