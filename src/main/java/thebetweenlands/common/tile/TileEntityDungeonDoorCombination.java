package thebetweenlands.common.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityDungeonDoorCombination extends TileEntity implements ITickable {

	public int top_code = 0, mid_code = 0, bottom_code = 0;
	public int renderTicks = 0;
	
	public TileEntityDungeonDoorCombination() {
		super();
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		top_code = nbt.getInt("top_code");
		mid_code = nbt.getInt("mid_code");
		bottom_code = nbt.getInt("bottom_code");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("top_code", top_code);
		nbt.putInt("mid_code", mid_code);
		nbt.putInt("bottom_code", bottom_code);
		return nbt;
	}

	@Override
    public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
        return save(nbt);
    }

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		save(nbt);
		return new SUpdateTileEntityPacket(getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void update() {
		this.renderTicks++;
	}

	public void cycleTopState() {
		top_code++;
		if (top_code > 7)
			top_code = 0;
		this.setChanged();
	}

	public void cycleMidState() {
		mid_code++;
		if (mid_code > 7)
			mid_code = 0;
		this.setChanged();
	}

	public void cycleBottomState() {
		bottom_code++;
		if (bottom_code > 7)
			bottom_code = 0;
		this.setChanged();
	}
}
