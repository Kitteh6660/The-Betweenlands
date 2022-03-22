package thebetweenlands.common.tile;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.inventory.InventoryCustomCrafting;
import thebetweenlands.common.inventory.InventoryCustomCrafting.ICustomCraftingGridChangeHandler;

public class TileEntityWeedwoodWorkbench extends TileEntity implements ICustomCraftingGridChangeHandler {

	public NonNullList<ItemStack> craftingSlots = NonNullList.withSize(9, ItemStack.EMPTY);
	public byte rotation = 0;

	private Set<InventoryCustomCrafting> openInventories = new HashSet<>();

	public TileEntityWeedwoodWorkbench(TileEntityType<?> te) {
		super(te);
	}
	
	@Override
	public void startOpen(InventoryCustomCrafting inv) {
		this.openInventories.add(inv);
	}

	@Override
	public void stopOpen(InventoryCustomCrafting inv) {
		this.openInventories.remove(inv);
	}

	/**
	 * Notifies *all* open inventories of the changes, fixes dupe bug as in #532
	 */
	@Override
	public void onCraftMatrixChanged() {
		for(InventoryCustomCrafting inv : this.openInventories) {
			inv.eventHandler.onCraftMatrixChanged(inv);
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 0, this.writeNbt(new CompoundNBT()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.readNBT(packet.getTag());
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.readNBT(nbt);
	}

	private CompoundNBT readNBT(CompoundNBT nbt) {
		ListNBT items = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);

		int count = items.size();
		for (int i = 0; i < count; i++) {
			CompoundNBT nbtItem = items.getCompound(i);
			this.craftingSlots.set(nbtItem.getByte("Slot"), new ItemStack(nbtItem));
		}

		this.onCraftMatrixChanged();

		this.rotation = nbt.getByte("Rotation");

		return nbt;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt = super.save(nbt);
		return this.writeNbt(nbt);
	}

	private CompoundNBT writeNbt(CompoundNBT nbt) {
		ListNBT items = new ListNBT();

		for (int i = 0; i < craftingSlots.size(); i++) {
			if (!this.craftingSlots.get(i).isEmpty()) {
				CompoundNBT nbtItem = new CompoundNBT();
				nbtItem.putByte("Slot", (byte) i);
				this.craftingSlots.get(i).save(nbtItem);
				items.add(nbtItem);
			}
		}

		nbt.put("Items", items);

		nbt.putByte("Rotation", this.rotation);

		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.writeNbt(super.getUpdateTag());
	}

	public NonNullList<ItemStack> getCraftingGrid() {
		return this.craftingSlots;
	}
}
