package thebetweenlands.common.tile;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityItemShelf extends TileEntityBasicInventory {
    public TileEntityItemShelf() {
        super(4, "container.bl.item_shelf");
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.writeInventoryNBT(nbt);
        return new SUpdateTileEntityPacket(this.worldPosition, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.readInventoryNBT(packet.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        this.readInventoryNBT(nbt);
    }


    @Override
    public ItemStack removeItem(int index, int count) {
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
        return super.removeItem(index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
        return super.removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
        super.setItem(index, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getViewDistance() {
    	//24 block render range for items
    	return 576;
    }
}
