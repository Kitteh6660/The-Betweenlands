package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.structure.BlockWaystone;
import thebetweenlands.util.StatePropertyHelper;

public class TileEntityWaystone extends TileEntity {
	private float rotation;

	public TileEntityWaystone(TileEntityType<?> te) { 
		super(te);
	}

	/*public TileEntityWaystone(float rotation) {
		this.rotation = rotation;
	}*/

	public void setRotation(float rotation) {
		this.rotation = rotation;
		this.setChanged();
	}

	public float getRotation() {
		return this.rotation;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
		return oldState.getBlock() != newSate.getBlock(); //Sate ftw
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putFloat("rotation", this.rotation);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.rotation = nbt.getFloat("rotation");
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putFloat("rotation", this.rotation);
		return new SUpdateTileEntityPacket(this.getBlockPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.rotation = pkt.getTag().getFloat("rotation");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putFloat("rotation", this.rotation);
		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
		super.handleUpdateTag(state, nbt);
		this.rotation = nbt.getFloat("rotation");
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB aabb = super.getRenderBoundingBox();

		if(StatePropertyHelper.getStatePropertySafely(this, BlockWaystone.class, BlockWaystone.ACTIVE, false)) {
			aabb = aabb.inflate(5.5f);
		}

		aabb = aabb.expandTowards(0, 2, 0);

		return aabb;
	}
}
