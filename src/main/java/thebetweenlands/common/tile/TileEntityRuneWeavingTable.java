package thebetweenlands.common.tile;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thebetweenlands.api.runechain.container.IRuneChainContainerData;
import thebetweenlands.common.inventory.container.runeweavingtable.ContainerRuneWeavingTable;
import thebetweenlands.common.inventory.container.runeweavingtable.RuneChainContainerData;
import thebetweenlands.common.registries.CapabilityRegistry;

public class TileEntityRuneWeavingTable extends TileEntity implements ISidedInventory {
	private final String name;

	protected NonNullList<ItemStack> inventory;
	protected final ItemStackHandler inventoryHandler;

	protected IRuneChainContainerData containerData = new RuneChainContainerData();

	public static final int OUTPUT_SLOT = 0;
	public static final int NON_INPUT_SLOTS = 1;

	private ContainerRuneWeavingTable openContainer;

	public TileEntityRuneWeavingTable() {
		this(43 /*output + 3 full pages*/, "rune_weaving_table");
	}

	protected TileEntityRuneWeavingTable(int invtSize, String name) {
		this.inventoryHandler = new ItemStackHandler(NonNullList.withSize(0, ItemStack.EMPTY)) {
			@Override
			public void setSize(int size) {
				Preconditions.checkArgument(size >= 2, "Rune weaving table inventory must have at least one input and one output slot");
				this.stacks = TileEntityRuneWeavingTable.this.inventory = NonNullList.withSize(invtSize, ItemStack.EMPTY);
			}

			@Override
			public void setStackInSlot(int slot, ItemStack stack) {
				super.setStackInSlot(slot, stack);
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if (stack.isEmpty() || (!TileEntityRuneWeavingTable.this.isOutputItemAvailable() && slot >= NON_INPUT_SLOTS) || !TileEntityRuneWeavingTable.this.canPlaceItem(slot, stack)) {
					return stack;
				}

				return super.insertItem(slot, stack, simulate);
			}

			@Override
			protected void onContentsChanged(int slot) {
				TileEntityRuneWeavingTable.this.setChanged();

				if(openContainer != null) {
					openContainer.setChanged(slot);
				}
			}

			@Override
			public int getSlots() {
				return TileEntityRuneWeavingTable.this.getContainerSize();
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		};

		this.inventoryHandler.setSize(invtSize);

		this.name = name;
	}

	public void openContainer(ContainerRuneWeavingTable container) {
		this.openContainer = container;
	}

	public void closeContainer() {
		this.openContainer = null;
	}

	@Override
	public int getContainerSize() {
		return Math.min(this.getChainLength() + 1 + NON_INPUT_SLOTS, this.inventory.size());
	}

	@Override
	public boolean isEmpty() {
		return this.inventory.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.inventoryHandler.getStackInSlot(slot);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		if(this.openContainer != null && this.openContainer.getPlayer() != player) {
			return false;
		}
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return player.getDistanceSq((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public ITextComponent getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		int[] slots = new int[getContainerSize()];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}

		return slots;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if(slot == OUTPUT_SLOT) {
			return stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);
		} else {
			return this.isOutputItemAvailable() && slot < this.getContainerSize() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE, null);
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, Direction side) {
		return this.canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return true;
	}

	@Override
	public void startOpen(PlayerEntity player) {
	}

	@Override
	public void stopOpen(PlayerEntity player) {
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return this.inventoryHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return this.inventoryHandler.extractItem(index, !this.inventoryHandler.getStackInSlot(index).isEmpty() ? this.inventoryHandler.getStackInSlot(index).getCount() : 0, false);
	}

	@Override
	public void setItem(int index, @Nonnull ItemStack stack) {
		this.inventoryHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clear() {
		for(int i = 0; i < this.inventoryHandler.getSlots(); i++) {
			this.inventoryHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.Direction facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.Direction facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) this.inventoryHandler;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.readInventoryNBT(nbt);
		this.containerData = RuneChainContainerData.load(nbt.getCompound("chainInfo"));
	}

	protected void readInventoryNBT(CompoundNBT nbt) {
		this.clear();
		if(nbt.contains("Inventory", Constants.NBT.TAG_COMPOUND)) {
			this.inventoryHandler.deserializeNBT(nbt.getCompound("Inventory"));
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		this.writeInventoryNBT(nbt);
		nbt.put("chainInfo", RuneChainContainerData.save(this.containerData, new CompoundNBT()));
		return nbt;
	}

	protected void writeInventoryNBT(CompoundNBT nbt) {
		CompoundNBT inventoryNbt = this.inventoryHandler.serializeNBT();
		nbt.put("Inventory", inventoryNbt);
	}


	//TODO Remove all this and only sync when GUI is opened
	//##################################
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.put("chainInfo", RuneChainContainerData.save(this.containerData, new CompoundNBT()));
		return nbt;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("chainInfo", RuneChainContainerData.save(this.containerData, new CompoundNBT()));
		return new SUpdateTileEntityPacket(this.getBlockPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.containerData = RuneChainContainerData.readFromNBT(pkt.getNbtCompound().getCompoundTag("chainInfo"));
	}
	//##################################


	public int getChainStart() {
		return NON_INPUT_SLOTS;
	}

	public int getChainLength() {
		for(int i = this.inventory.size() - 1; i >= NON_INPUT_SLOTS; i--) {
			if(!this.inventory.get(i).isEmpty()) {
				return i + 1 - NON_INPUT_SLOTS;
			}
		}
		return 0;
	}

	public int getMaxChainLength() {
		return this.inventory.size() - NON_INPUT_SLOTS;
	}

	public void setContainerData(IRuneChainContainerData data) {
		this.containerData = data;
		this.setChanged();
	}
	
	public IRuneChainContainerData getContainerData() {
		return this.containerData;
	}
	
	public boolean isOutputItemAvailable() {
		return !this.inventory.get(OUTPUT_SLOT).isEmpty();
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		BlockPos pos = this.getBlockPos();
		return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 1, pos.getZ() + 2);
	}
}
