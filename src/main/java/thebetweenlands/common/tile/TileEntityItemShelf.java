package thebetweenlands.common.tile;

import javax.annotation.Nonnull;

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
        return new SUpdateTileEntityPacket(this.pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.readInventoryNBT(packet.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundNBT nbt) {
        super.handleUpdateTag(nbt);
        this.readInventoryNBT(nbt);
    }


    @Override
    public ItemStack decrStackSize(int index, int count) {
        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        return super.decrStackSize(index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        return super.removeStackFromSlot(index);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        super.setItem(index, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
    	//24 block render range for items
    	return 576;
    }
}
