package thebetweenlands.common.tile;

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;

public class TileEntityLootPot extends TileEntityLootInventory {
	private int rotationOffset;

	public TileEntityLootPot() {
		super(3, "container.bl.loot_pot");
	}

	public void setModelRotationOffset(int rotation) {
		this.rotationOffset = rotation;
	}

	public int getModelRotationOffset() {
		return this.rotationOffset;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
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
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.rotationOffset = packet.getNbtCompound().getInt("rotationOffset");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("rotationOffset", this.rotationOffset);
		return nbt;
	}
}
