package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityPurifier extends TileEntityBasicInventory implements IFluidHandler, ITickableTileEntity {
	
    private static final int MAX_TIME = 432;
    public final FluidTank waterTank;
    private final IFluidTankProperties[] properties = new IFluidTankProperties[1];
    public int time = 0;
    public boolean lightOn = false;
    private int prevStackSize = 0;
    private Item prevItem;
    private boolean isPurifyingClient = false;

    public TileEntityPurifier(TileEntityType<?> te) {
        super(te, 3, "container.bl.purifier");
        this.waterTank = new FluidTank(FluidRegistry.SWAMP_WATER, 0, Fluid.BUCKET_VOLUME * 4);
        this.waterTank.setTileEntity(this);
        this.properties[0] = new FluidTankPropertiesWrapper(this.waterTank);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        waterTank.readFromNBT(nbt.getCompound("waterTank"));
        lightOn = nbt.getBoolean("state");
        time = nbt.getInt("progress");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt = super.save(nbt);
        nbt.put("waterTank", waterTank.writeToNBT(new CompoundNBT()));
        nbt.putBoolean("state", lightOn);
        nbt.putInt("progress", time);
        return nbt;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.writePacketNbt(nbt);
        return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.readPacketNbt(packet.getTag());
    }

    protected CompoundNBT writePacketNbt(CompoundNBT nbt) {
        nbt.putBoolean("state", lightOn);
        nbt.put("waterTank", waterTank.writeToNBT(new CompoundNBT()));
        nbt.putBoolean("isPurifying", this.isPurifying());
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    protected void readPacketNbt(CompoundNBT nbt) {
        CompoundNBT compound = nbt;
        lightOn = compound.getBoolean("state");
        waterTank.readFromNBT(compound.getCompound("waterTank"));
        this.readInventoryNBT(nbt);
        isPurifyingClient = compound.getBoolean("isPurifying");
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        this.writePacketNbt(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        this.readPacketNbt(nbt);
    }

    public int getPurifyingProgress() {
        return time / 36;
    }

    public boolean isPurifying() {
        return time > 0 || this.isPurifyingClient;
    }

    public int getWaterAmount() {
        return waterTank.getFluidAmount();
    }

    @Override
	public int getTanks() {
		return waterTank.getTanks();
	}
    
    public int getTankCapacity(int tank) {
        return waterTank.getCapacity();
    }

    public int getScaledWaterAmount(int scale) {
        return waterTank.getFluid() != null ? (int) ((float) waterTank.getFluid().getAmount() / (float) waterTank.getCapacity() * scale) : 0;
    }

    public void getGUIData(int id, int value) {
        switch (id) {
            case 0:
                time = value;
                break;
            case 1:
                if (waterTank.getFluid() == null)
                    waterTank.setFluid(new FluidStack(FluidRegistry.SWAMP_WATER.get(), value));
                else
                    waterTank.getFluid().setAmount(value);
                break;
        }
    }

    public void sendGUIData(ContainerPurifier purifier, IContainerListener craft) {
        craft.setContainerData(purifier, 0, time);
        craft.setContainerData(purifier, 1, waterTank.getFluid() != null ? waterTank.getFluid().getAmount() : 0);
    }

    @Override
    public void tick() {
        if (level.isClientSide())
            return;
        ItemStack output = PurifierRecipe.getRecipeOutput(inventory.get(1));
        if (hasFuel() && !outputIsFull()) {
            if (!output.isEmpty() && getWaterAmount() > 0 && inventory.get(2).isEmpty() || !output.isEmpty() && getWaterAmount() > 0 && !inventory.get(2).isEmpty() && inventory.get(2).sameItem(output)) {
                time++;
                if (time % 108 == 0)
                    level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundRegistry.PURIFIER, SoundCategory.BLOCKS, 1.5F, 1.0F);
                if (!lightOn)
                    setIlluminated(true);
                if (time >= MAX_TIME) {
                    for (int i = 0; i < 2; i++)
                        if (!inventory.get(i).isEmpty())
                            if (inventory.get(i).getCount() - 1 <= 0)
                                inventory.set(i, ItemStack.EMPTY);
                            else
                                inventory.get(i).shrink(1);
                    extractFluids(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME / 4));
                    if (inventory.get(2).isEmpty()) {
                        inventory.set(2, output.copy());
                    } else if (inventory.get(2).sameItem(output)) {
                        inventory.get(2).inflate(output.getCount());
                    }
                    time = 0;
                    setChanged();
                    boolean canRun = !output.isEmpty() && getWaterAmount() > 0 && inventory.get(2).isEmpty() || !output.isEmpty() && getWaterAmount() > 0 && !inventory.get(2).isEmpty() && inventory.get(2).sameItem(output);
                    if (!canRun) setIlluminated(false);
                }
            }
        }
        if (time > 0) {
            setChanged();
        }
        if (getItem(0).isEmpty() || getItem(1).isEmpty() || outputIsFull()) {
            time = 0;
            setChanged();
            setIlluminated(false);
        }
        if (this.prevStackSize != (!inventory.get(2).isEmpty() ? inventory.get(2).getCount() : 0)) {
            setChanged();
        }
        if (this.prevItem != (!inventory.get(2).isEmpty() ? inventory.get(2).getItem() : null)) {
            setChanged();
        }
        this.prevItem = !inventory.get(2).isEmpty() ? inventory.get(2).getItem() : null;
        this.prevStackSize = !inventory.get(2).isEmpty() ? inventory.get(2).getCount() : 0;
    }

    private void extractFluids(FluidStack fluid) {
        if (fluid.isFluidEqual(waterTank.getFluid()))
            waterTank.drain(fluid.getAmount(), FluidAction.EXECUTE);
        setChanged();
    }

    public boolean hasFuel() {
        return !getItem(0).isEmpty() && getItem(0).getItem() == ItemRegistry.SULFUR.get() && getItem(0).getCount() >= 1;
    }

    private boolean outputIsFull() {
        return !getItem(2).isEmpty() && getItem(2).getCount() >= getMaxStackSize();
    }

    public void setIlluminated(boolean state) {
        lightOn = state;
        level.blockEvent(worldPosition, getBlockState().getBlock(), 0, lightOn ? 1 : 0);
    }

    @Override
    public boolean receiveClientEvent(int eventId, int eventData) {
        switch (eventId) {
            case 0:
                lightOn = eventData == 1;
                level.checkLight(worldPosition);
                return true;
            default:
                return false;
        }
    }

    @Override
    public int[] getSlotsForFace(Direction facing) {
        if (facing == Direction.DOWN)
            return new int[]{2};
        if (facing == Direction.UP)
            return new int[]{1};
        return new int[]{0};
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return this.properties;
    }

    @Override
    public int fill(FluidStack resource, FluidAction doFill) {
        if (doFill == FluidAction.EXECUTE) {
            this.setChanged();
            BlockState stat = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, stat, stat, 3);
        }
        return this.waterTank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction doDrain) {
        if (doDrain == FluidAction.EXECUTE) {
            this.setChanged();
            BlockState stat = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, stat, stat, 3);
        }
        return this.waterTank.drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction doDrain) {
        if (doDrain == FluidAction.EXECUTE) {
            this.setChanged();
            BlockState stat = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, stat, stat, 3);
        }
        return this.waterTank.drain(maxDrain, doDrain);
    }

    @Override
    public <T> LazyOptional<T> hasCapability(Capability<?> capability, Direction facing) {
    	if (CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
    		return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
    	}
    	else {
    		return super.getCapability(capability, facing);
    	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this;
        return super.getCapability(capability, facing);
    }

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		// TODO Auto-generated method stub
		return false;
	}
}
