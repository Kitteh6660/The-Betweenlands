package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TileEntityGroundItem extends TileEntity {

    public TileEntityGroundItem(TileEntityType<?> te) {
		super(te);
	}

	private ItemStack stack = ItemStack.EMPTY;

    public boolean hasRandomOffset() {
    	return true;
    }
    
    public float getStepY() {
    	return 0.4f;
    }
    
    public boolean isItemUpsideDown() {
    	return true;
    }
    
    public float getYRotation(float randomRotation) {
    	return randomRotation;
    }
    
    public float getTiltRotation() {
    	return this.isItemUpsideDown() ? -120.0f : 15.0f;
    }
    
    public float getItemScale() {
    	return 0.75f;
    }
    
    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        setChanged();
    }

    @Override
    public void setChanged() {
        final BlockState state = getLevel().getBlockState(getBlockPos());
        getLevel().sendBlockUpdated(getBlockPos(), state, state, 2);
        super.setChanged();
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        stack = new ItemStack(compound.getCompound("Stack"));
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.put("Stack", stack.save(new CompoundNBT()));
        return super.save(compound);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        load(state, tag);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
