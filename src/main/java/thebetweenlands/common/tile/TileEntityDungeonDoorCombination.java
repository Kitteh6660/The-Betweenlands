package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityDungeonDoorCombination extends TileEntity implements ITickableTileEntity {

	public int top_code = 0, mid_code = 0, bottom_code = 0;
	public int renderTicks = 0;
	
	public TileEntityDungeonDoorCombination(TileEntityType<?> te) {
		super(te);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
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
		return new SUpdateTileEntityPacket(getBlockPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		load(this.getBlockState(), packet.getTag());
	}

	@Override
	public void tick() {
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
