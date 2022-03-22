package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityPurifier extends TileEntityBasicInventory implements IFluidHandler, ITickable {
    private static final int MAX_TIME = 432;
    public final FluidTank waterTank;
    private final IFluidTankProperties[] properties = new IFluidTankProperties[1];
    public int time = 0;
    public boolean lightOn = false;
    private int prevStackSize = 0;
    private Item prevItem;
    private boolean isPurifyingClient = false;

    public TileEntityPurifier() {
        super(3, "container.bl.purifier");
        this.waterTank = new FluidTank(FluidRegistry.SWAMP_WATER, 0, Fluid.BUCKET_VOLUME * 4);
        this.waterTank.setTileEntity(this);
        this.properties[0] = new FluidTankPropertiesWrapper(this.waterTank);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.readFromNBT(nbt);
        waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
        lightOn = nbt.getBoolean("state");
        time = nbt.getInt("progress");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt = super.save(nbt);
        nbt.setTag("waterTank", waterTank.save(new CompoundNBT()));
        nbt.putBoolean("state", lightOn);
        nbt.putInt("progress", time);
        return nbt;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.writePacketNbt(nbt);
        return new SUpdateTileEntityPacket(pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.readPacketNbt(packet.getNbtCompound());
    }

    protected CompoundNBT writePacketNbt(CompoundNBT nbt) {
        nbt.putBoolean("state", lightOn);
        nbt.setTag("waterTank", waterTank.save(new CompoundNBT()));
        nbt.putBoolean("isPurifying", this.isPurifying());
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    protected void readPacketNbt(CompoundNBT nbt) {
        CompoundNBT compound = nbt;
        lightOn = compound.getBoolean("state");
        waterTank.readFromNBT(compound.getCompoundTag("waterTank"));
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
    public void handleUpdateTag(CompoundNBT nbt) {
        super.handleUpdateTag(nbt);
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

    public int getTanksFullValue() {
        return waterTank.getCapacity();
    }

    public int getScaledWaterAmount(int scale) {
        return waterTank.getFluid() != null ? (int) ((float) waterTank.getFluid().amount / (float) waterTank.getCapacity() * scale) : 0;
    }

    public void getGUIData(int id, int value) {
        switch (id) {
            case 0:
                time = value;
                break;
            case 1:
                if (waterTank.getFluid() == null)
                    waterTank.setFluid(new FluidStack(FluidRegistry.SWAMP_WATER, value));
                else
                    waterTank.getFluid().amount = value;
                break;
        }
    }

    public void sendGUIData(ContainerPurifier purifier, IContainerListener craft) {
        craft.sendWindowProperty(purifier, 0, time);
        craft.sendWindowProperty(purifier, 1, waterTank.getFluid() != null ? waterTank.getFluid().amount : 0);
    }

    @Override
    public void update() {
        if (world.isClientSide())
            return;
        ItemStack output = PurifierRecipe.getRecipeOutput(inventory.get(1));
        if (hasFuel() && !outputIsFull()) {
            if (!output.isEmpty() && getWaterAmount() > 0 && inventory.get(2).isEmpty() || !output.isEmpty() && getWaterAmount() > 0 && !inventory.get(2).isEmpty() && inventory.get(2).isItemEqual(output)) {
                time++;
                if (time % 108 == 0)
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundRegistry.PURIFIER, SoundCategory.BLOCKS, 1.5F, 1.0F);
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
                    } else if (inventory.get(2).isItemEqual(output)) {
                        inventory.get(2).grow(output.getCount());
                    }
                    time = 0;
                    setChanged();
                    boolean canRun = !output.isEmpty() && getWaterAmount() > 0 && inventory.get(2).isEmpty() || !output.isEmpty() && getWaterAmount() > 0 && !inventory.get(2).isEmpty() && inventory.get(2).isItemEqual(output);
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
            waterTank.drain(fluid.amount, true);
        setChanged();
    }

    public boolean hasFuel() {
        return !getItem(0).isEmpty() && EnumItemMisc.SULFUR.isItemOf(getItem(0)) && getItem(0).getCount() >= 1;
    }

    private boolean outputIsFull() {
        return !getItem(2).isEmpty() && getItem(2).getCount() >= getMaxStackSize();
    }

    public void setIlluminated(boolean state) {
        lightOn = state;
        world.addBlockEvent(pos, getBlockType(), 0, lightOn ? 1 : 0);
    }

    @Override
    public boolean receiveClientEvent(int eventId, int eventData) {
        switch (eventId) {
            case 0:
                lightOn = eventData == 1;
                world.checkLight(pos);
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
    public int fill(FluidStack resource, boolean doFill) {
        if (doFill) {
            this.setChanged();
            BlockState stat = this.world.getBlockState(this.pos);
            this.world.sendBlockUpdated(this.pos, stat, stat, 3);
        }
        return this.waterTank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (doDrain) {
            this.setChanged();
            BlockState stat = this.world.getBlockState(this.pos);
            this.world.sendBlockUpdated(this.pos, stat, stat, 3);
        }
        return this.waterTank.drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (doDrain) {
            this.setChanged();
            BlockState stat = this.world.getBlockState(this.pos);
            this.world.sendBlockUpdated(this.pos, stat, stat, 3);
        }
        return this.waterTank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, Direction facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this;
        return super.getCapability(capability, facing);
    }
}
