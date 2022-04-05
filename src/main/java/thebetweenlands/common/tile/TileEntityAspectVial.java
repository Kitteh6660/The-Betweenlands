package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.herblore.Amounts;

import javax.annotation.Nullable;

public class TileEntityAspectVial extends TileEntity {
    public static final float MAX_AMOUNT = Amounts.VIAL;

    private Aspect aspect = null;

    /**
     * Tries to add an amount and returns the added amount
     * @param amount
     * @return
     */
    public int addAmount(int amount) {
        int canAdd = (int) (MAX_AMOUNT - this.aspect.amount);
        int added = 0;
        if(canAdd > 0) {
            added = Math.min(canAdd, amount);
            this.aspect = new Aspect(this.aspect.type, this.aspect.amount + added);
        }
        setChanged();
        return added;
    }

    /**
     * Tries to remove an amount and returns the removed amount
     * @param amount
     * @return
     */
    public int removeAmount(int amount) {
        int removed = Math.min(this.aspect.amount, amount);
        if(removed < this.aspect.amount) {
            this.aspect = new Aspect(this.aspect.type, this.aspect.amount - removed);
        } else {
            this.aspect = null;
        }
        setChanged();
        return removed;
    }

    @Override
    public void setChanged() {
        final BlockState state = getLevel().getBlockState(getBlockPos());
        getLevel().sendBlockUpdated(getBlockPos(), state, state, 2);
        super.setChanged();
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        if(this.aspect != null)
            this.aspect.save(compound);
        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if(nbt.contains("aspect")) {
            this.aspect = Aspect.load(state, nbt);
        } else {
            this.aspect = null;
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        super.handleUpdateTag(tag);
        readFromNBT(tag);
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
    	return oldState.getBlock() != newSate.getBlock();
    }
}