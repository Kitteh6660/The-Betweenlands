package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import thebetweenlands.common.registries.FluidRegistry;

public class TileEntityBarrel extends TileEntity implements IFluidHandler {
	
	private final FluidTank fluidTank;
	private final IFluidTankProperties[] properties = new IFluidTankProperties[1];

	public TileEntityBarrel() {
		this.fluidTank = new FluidTank(null, Fluid.BUCKET_VOLUME * 8);
		this.fluidTank.setTileEntity(this);
		this.properties[0] = new FluidTankPropertiesWrapper(this.fluidTank);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.fluidTank.readFromNBT(nbt.getCompound("fluidTank"));
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt = super.save(nbt);
		nbt.put("fluidTank", fluidTank.writeToNBT(new CompoundNBT()));
		return nbt;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("fluidTank", fluidTank.writeToNBT(new CompoundNBT()));
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.fluidTank.readFromNBT(packet.getTag().getCompound("fluidTank"));
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.put("fluidTank", fluidTank.writeToNBT(new CompoundNBT()));
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundNBT nbt) {
		super.handleUpdateTag(nbt);
		this.fluidTank.readFromNBT(nbt.getCompound("fluidTank"));
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(this.level != null) {
			BlockState state = this.level.getBlockState(this.worldPosition);

			boolean isFluidHot = resource.getFluid().getTemperature(resource) > 473.15F /*200°C*/ || resource.getFluid() == Fluids.LAVA;

			if(!isFluidHot || (state.getBlock() instanceof BlockBarrel && ((BlockBarrel) state.getBlock()).isHeatResistant(this.level, this.worldPosition, state))) {
				int filled = this.fluidTank.fill(resource, doFill);
				
				if(filled != 0 && doFill) {
					this.setChanged();
					BlockState stat = this.level.getBlockState(this.pos);
					this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);
				}
				
				return filled;
			}
		}

		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		if (doDrain == FluidAction.EXECUTE) {
			this.setChanged();
			BlockState stat = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);
		}
		return this.fluidTank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		if (doDrain == FluidAction.EXECUTE) {
			this.setChanged();
			BlockState stat = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);
		}
		return this.fluidTank.drain(maxDrain, doDrain);
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
