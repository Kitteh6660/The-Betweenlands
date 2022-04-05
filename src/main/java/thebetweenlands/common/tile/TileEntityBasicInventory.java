package thebetweenlands.common.tile;

import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityBasicInventory extends TileEntity implements ISidedInventory {
	
	protected static final BiFunction<TileEntityBasicInventory, NonNullList<ItemStack>, ItemStackHandler> DEFAULT_HANDLER = (te, inv) -> new ItemStackHandler(inv) {
		@Override
		public void setSize(int size) {
			if(size != inv.size()) {
				throw new UnsupportedOperationException("Can't resize this inventory");
			}
		}

		@Override
		protected void onContentsChanged(int slot) {
			// Don't mark dirty while loading chunk!
			if(te.hasLevel()) {
				te.setChanged();
			}
		}
	};
	
	private final String name;
	protected NonNullList<ItemStack> inventory;
	protected final ItemStackHandler inventoryHandler;

	public TileEntityBasicInventory(TileEntityType<?> te, int invSize, String name) {
		this(te, name, NonNullList.withSize(invSize, ItemStack.EMPTY), DEFAULT_HANDLER);
	}
	
	public TileEntityBasicInventory(TileEntityType<?> te, String name, NonNullList<ItemStack> inventory, BiFunction<TileEntityBasicInventory, NonNullList<ItemStack>, ItemStackHandler> handler) {
		super(te);
		this.inventoryHandler = handler.apply(this, inventory);
		this.inventory = inventory;
		this.name = name;
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.readInventoryNBT(nbt);
	}

	/**
	 * Reads the inventory from NBT
	 * @param nbt
	 */
	protected void readInventoryNBT(CompoundNBT nbt) {
		this.clearContent();
		if(nbt.contains("Inventory", Constants.NBT.TAG_COMPOUND)) {
			this.inventoryHandler.deserializeNBT(nbt.getCompound("Inventory"));
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		this.writeInventoryNBT(nbt);
		return nbt;
	}

	/**
	 * Writes the inventory to NBT
	 * @param nbt
	 */
	protected void writeInventoryNBT(CompoundNBT nbt) {
		CompoundNBT inventoryNbt = this.inventoryHandler.serializeNBT();
		nbt.put("Inventory", inventoryNbt);
	}

	@Override
	public int getContainerSize() {
		return this.inventoryHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	@MethodsReturnNonnullByDefault
	public ItemStack getItem(int slot) {
		this.accessSlot(slot);
		return this.inventoryHandler.getStackInSlot(slot);
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		if(this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
    
    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponent(this.getName()) : new TranslationTextComponent(this.getName());
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
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		this.accessSlot(slot);
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		this.accessSlot(slot);
		return true;
	}

	@Override
	public void startOpen(PlayerEntity player) {}

	@Override
	public void stopOpen(PlayerEntity player) {}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.accessSlot(index);
		return this.inventoryHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.accessSlot(index);
		return this.inventoryHandler.extractItem(index, !this.inventoryHandler.getStackInSlot(index).isEmpty() ? this.inventoryHandler.getStackInSlot(index).getCount() : 0, false);
	}

	@Override
	public void setItem(int index, @Nonnull ItemStack stack) {
		this.accessSlot(index);
		this.inventoryHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clearContent() {
		for(int i = 0; i < this.inventoryHandler.getSlots(); i++) {
			this.inventoryHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	/**
	 * Called before a slot is accessed
	 * @param slot
	 */
	protected void accessSlot(int slot) {

	}

	private IItemHandler handlerUp = new SidedInvWrapper(this, Direction.UP);
	private IItemHandler handlerDown = new SidedInvWrapper(this, Direction.DOWN);
	private IItemHandler handlerNorth = new SidedInvWrapper(this, Direction.NORTH);
	private IItemHandler handlerSouth = new SidedInvWrapper(this, Direction.SOUTH);
	private IItemHandler handlerEast = new SidedInvWrapper(this, Direction.EAST);
	private IItemHandler handlerWest = new SidedInvWrapper(this, Direction.WEST);
	private IItemHandler handlerNull = new InvWrapper(this);

	@Override
	public boolean getCapability(Capability<?> capability, Direction facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.getCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == null) {
				return (T) handlerNull;
			}
			switch (facing) {
				case DOWN:
					return (T) this.handlerDown;
				case UP:
					return (T) this.handlerUp;
				case NORTH:
					return (T) this.handlerNorth;
				case SOUTH:
					return (T) this.handlerSouth;
				case WEST:
					return (T) this.handlerWest;
				case EAST:
					return (T) this.handlerEast;
			}
		}
		return super.getCapability(capability, facing);
	}

}
