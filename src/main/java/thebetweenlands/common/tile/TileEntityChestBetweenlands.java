package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.loot.ISharedLootContainer;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;

public class TileEntityChestBetweenlands extends TileEntityChest implements ISharedLootContainer {
	protected StorageID storageId;
	protected boolean isSharedLootTable;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
		//Use vanilla behaviour to prevent inventory from resetting when creating double chest
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.offset(-1, 0, -1), pos.offset(2, 2, 2));
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.readFromNBT(compound);
	}

	@Override
	protected boolean checkLootAndRead(CompoundNBT compound) {
		this.isSharedLootTable = false;

		if(super.checkLootAndRead(compound)) {
			this.isSharedLootTable = compound.getBoolean("SharedLootTable");

			if(compound.contains("StorageID", Constants.NBT.TAG_COMPOUND)) {
				this.storageId = StorageID.readFromNBT(compound.getCompoundTag("StorageID"));
			}

			return true;
		}

		return false;
	}

	@Override
	protected boolean checkLootAndWrite(CompoundNBT compound) {
		if(super.checkLootAndWrite(compound)) {
			compound.putBoolean("SharedLootTable", this.isSharedLootTable);

			if(this.storageId != null) {
				compound.setTag("StorageID", this.storageId.save(new CompoundNBT()));
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean isSharedLootTable() {
		return this.isSharedLootTable;
	}

	@Override
	public void fillWithLoot(@Nullable PlayerEntity player) {
		this.fillInventoryWithLoot(player);
	}

	@Override
	public boolean fillInventoryWithLoot(@Nullable PlayerEntity player) {
		return TileEntityLootInventory.fillInventoryWithLoot(this.world, this, player, this.lootTableSeed);
	}

	@Override
	public void removeLootTable() {
		if(this.lootTable != null) {
			this.lootTable = null;
			this.setChanged();
		}
	}

	@Override
	public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
		super.setLootTable(lootTable, lootTableSeed);
		this.isSharedLootTable = false;
		this.setChanged();
	}

	@Override
	public void setSharedLootTable(SharedLootPoolStorage storage, ResourceLocation lootTable, long lootTableSeed) {
		if(!lootTable.equals(this.lootTable)) {
			storage.registerSharedLootInventory(this.pos, lootTable);
		}
		this.storageId = storage.getID();
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
		this.isSharedLootTable = true;
		this.setChanged();
	}

	@Override
	public StorageID getSharedLootPoolStorageID() {
		return this.storageId;
	}
}
