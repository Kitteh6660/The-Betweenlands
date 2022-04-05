package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.loot.shared.SharedLootPool;
import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntityLootPot extends TileEntityLootInventory {
	
	//private int variant;
	private int rotationOffset;

	public TileEntityLootPot() {
		super(TileEntityRegistry.LOOT_POT.get());
	}

	public void setModelRotationOffset(int rotation) {
		this.rotationOffset = rotation;
	}

	public int getModelRotationOffset() {
		return this.rotationOffset;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.rotationOffset = nbt.getInt("rotationOffset");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("rotationOffset", this.rotationOffset);
		return nbt;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("rotationOffset", this.rotationOffset);
		return new SUpdateTileEntityPacket(this.worldPosition, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.rotationOffset = packet.getTag().getInt("rotationOffset");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("rotationOffset", this.rotationOffset);
		return nbt;
	}

	@Override
	public void setSharedLootTable(SharedLootPool storage, ResourceLocation lootTable, long lootTableSeed) {
		// TODO Auto-generated method stub
	}
}
