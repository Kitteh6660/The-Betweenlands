package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.StorageID;

public class TileEntityChestBetweenlands extends ChestTileEntity {
	
	protected StorageID storageId;
	protected boolean isSharedLootTable;

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition.offset(-1, 0, -1), worldPosition.offset(2, 2, 2));
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
	}

	@Override
	protected boolean tryLoadLootTable(CompoundNBT compound) {
		this.isSharedLootTable = false;

		if (super.tryLoadLootTable(compound)) {
			this.isSharedLootTable = compound.getBoolean("SharedLootTable");

			if (compound.contains("StorageID", Constants.NBT.TAG_COMPOUND)) {
				this.storageId = StorageID.load(compound.getCompound("StorageID"));
			}

			return true;
		}

		return false;
	}

	@Override
	protected boolean trySaveLootTable(CompoundNBT compound) {
		if (super.trySaveLootTable(compound)) {
			compound.putBoolean("SharedLootTable", this.isSharedLootTable);

			if (this.storageId != null) {
				compound.put("StorageID", this.storageId.save(new CompoundNBT()));
			}

			return true;
		}

		return false;
	}

	@Override
	public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
		super.setLootTable(lootTable, lootTableSeed);
		this.isSharedLootTable = false;
		this.setChanged();
	}
}
