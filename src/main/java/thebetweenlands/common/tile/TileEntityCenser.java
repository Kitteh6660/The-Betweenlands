package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.common.block.container.BlockCenser;
import thebetweenlands.common.inventory.container.ContainerCenser;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.censer.AbstractCenserRecipe;

public class TileEntityCenser extends TileEntityBasicInventory implements IFluidHandler, ITickableTileEntity, ICenser 
{
	private final FluidTank fluidTank;
	private final IFluidTankProperties[] properties = new IFluidTankProperties[1];

	private static final int INV_SIZE = 3;

	private int remainingItemAmount = 0;

	private float prevDungeonFogStrength = 0.0f;
	private float dungeonFogStrength = 0.0f;

	private float prevEffectStrength = 0.0f;
	private float effectStrength = 0.0f;

	private int maxConsumptionTicks;
	private int consumptionTicks;

	private boolean isFluidRecipe;
	private Object currentRecipeContext;
	private ICenserRecipe<Object> currentRecipe;

	private boolean internalSlotChanged = false;

	private int maxFuelTicks;
	private int fuelTicks; 

	private boolean checkInternalSlotForRecipes = true;
	private boolean checkInputSlotForTransfer = true;

	private boolean isRecipeRunning = false;

	public TileEntityCenser() {
		super("container.bl.censer", NonNullList.withSize(INV_SIZE, ItemStack.EMPTY), (te, inv) -> new ItemStackHandler(inv) {
			@Override
			public void setSize(int size) {
				this.stacks = te.inventory = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
			}

			@Override
			protected void onContentsChanged(int slot) {
				// Don't mark dirty while loading chunk!
				if(te.hasLevel()) {
					te.setChanged();

					//Sync internal slot. Required on client side
					//for rendering stuff
					if(slot == ContainerCenser.SLOT_INTERNAL) {
						((TileEntityCenser) te).internalSlotChanged = true;
					}
				}

				if(slot == ContainerCenser.SLOT_INTERNAL) {
					((TileEntityCenser) te).checkInternalSlotForRecipes = true;
					((TileEntityCenser) te).checkInputSlotForTransfer = true;
				} else if(slot == ContainerCenser.SLOT_INPUT) {
					((TileEntityCenser) te).checkInputSlotForTransfer = true;
				}
			}

			//Internal slot stores at most 1 item and can't be extracted

			@Override
			public int getSlotLimit(int slot) {
				return slot == ContainerCenser.SLOT_INTERNAL ? 1 : super.getSlotLimit(slot);
			}

			@Override
			protected int getStackLimit(int slot, ItemStack stack) {
				return slot == ContainerCenser.SLOT_INTERNAL ? 1 : super.getStackLimit(slot, stack);
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				return slot == ContainerCenser.SLOT_INTERNAL ? stack : super.insertItem(slot, stack, simulate);
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return slot == ContainerCenser.SLOT_INTERNAL ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
			}
		});
		this.fluidTank = new FluidTank(null, Fluid.BUCKET_VOLUME * 8) {
			@Override
			public boolean canFillFluidType(FluidStack fluid) {
				return super.canFillFluidType(fluid) && TileEntityCenser.this.getEffect(fluid) != null;
			}
		};
		this.fluidTank.setTileEntity(this);
		this.properties[0] = new FluidTankPropertiesWrapper(this.fluidTank);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(nbt);
		this.fluidTank.readFromNBT(nbt.getCompound("fluidTank"));
		this.remainingItemAmount = nbt.getInt("remainingItemAmount");
		this.consumptionTicks = nbt.getInt("consumptionTicks");
		this.maxConsumptionTicks = nbt.getInt("maxConsumptionTicks");
		this.fuelTicks = nbt.getInt("fuelTicks");
		this.maxFuelTicks = nbt.getInt("maxFuelTicks");
		this.readRecipeNbt(nbt, false);
	}

	@SuppressWarnings("unchecked")
	protected void readRecipeNbt(CompoundNBT nbt, boolean packet) {
		this.isFluidRecipe = nbt.getBoolean("fluidRecipe");

		this.currentRecipe = null;
		this.currentRecipeContext = null;

		if(nbt.contains("recipeId", Constants.NBT.TAG_STRING)) {
			ResourceLocation id = new ResourceLocation(nbt.getString("recipeId"));

			ICenserRecipe<Object> recipe = null;
			Object recipeContext = null;

			if(this.isFluidRecipe && this.fluidTank.getFluid() != null) {
				recipe = (ICenserRecipe<Object>) this.getEffect(this.fluidTank.getFluid());
				if(recipe != null) {
					recipeContext = recipe.createContext(this.fluidTank.getFluid());
				}
			} else if(!this.isFluidRecipe) {
				recipe = (ICenserRecipe<Object>) this.getEffect(this.getItem(ContainerCenser.SLOT_INTERNAL));
				if(recipe != null) {
					recipeContext = recipe.createContext(this.getItem(ContainerCenser.SLOT_INTERNAL));
				}
			}

			if(recipe != null && this.canRecipeRun(this.isFluidRecipe, recipe)) {
				this.currentRecipe = recipe;
				this.currentRecipeContext = recipeContext;

				if(recipeContext != null && id.equals(recipe.getId()) && nbt.contains("recipeNbt", Constants.NBT.TAG_COMPOUND)) {
					CompoundNBT recipeNbt = nbt.getCompound("recipeNbt");
					recipe.read(recipeContext, recipeNbt, packet);
				}
			} else {
				this.consumptionTicks = -1;
				this.maxConsumptionTicks = -1;
			}
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt = super.save(nbt);
		nbt.put("fluidTank", this.fluidTank.writeToNBT(new CompoundNBT()));
		nbt.putInt("remainingItemAmount", this.remainingItemAmount);
		nbt.putInt("consumptionTicks", this.consumptionTicks);
		nbt.putInt("maxConsumptionTicks", this.maxConsumptionTicks);
		nbt.putInt("fuelTicks", this.fuelTicks);
		nbt.putInt("maxFuelTicks", this.maxFuelTicks);
		this.writeRecipeNbt(nbt, false);
		return nbt;
	}

	protected void writeRecipeNbt(CompoundNBT nbt, boolean packet) {
		nbt.putBoolean("fluidRecipe", this.isFluidRecipe);

		if(this.currentRecipe != null) {
			nbt.putString("recipeId", this.currentRecipe.getId().toString());

			if(this.currentRecipeContext != null) {
				CompoundNBT contextNbt = new CompoundNBT();
				this.currentRecipe.save(this.currentRecipeContext, contextNbt, packet);
				nbt.put("recipeNbt", contextNbt);
			}
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.writePacketNbt(nbt);
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.readPacketNbt(packet.getTag());
	}

	protected CompoundNBT writePacketNbt(CompoundNBT nbt) {
		this.writeInventoryNBT(nbt);
		nbt.put("fluidTank", fluidTank.writeToNBT(new CompoundNBT()));
		nbt.putInt("remainingItemAmount", this.remainingItemAmount);
		nbt.putInt("consumptionTicks", this.consumptionTicks);
		nbt.putInt("maxConsumptionTicks", this.maxConsumptionTicks);
		nbt.putInt("fuelTicks", this.fuelTicks);
		nbt.putInt("maxFuelTicks", this.maxFuelTicks);
		this.writeRecipeNbt(nbt, true);
		return nbt;
	}

	protected void readPacketNbt(CompoundNBT nbt) {
		this.readInventoryNBT(nbt);
		this.fluidTank.readFromNBT(nbt.getCompound("fluidTank"));
		this.remainingItemAmount = nbt.getInt("remainingItemAmount");
		this.consumptionTicks = nbt.getInt("consumptionTicks");
		this.maxConsumptionTicks = nbt.getInt("maxConsumptionTicks");
		this.fuelTicks = nbt.getInt("fuelTicks");
		this.maxFuelTicks = nbt.getInt("maxFuelTicks");
		this.readRecipeNbt(nbt, true);
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

	public void receiveGUIData(int id, int value) {
		switch (id) {
		case 0:
			this.remainingItemAmount = value;
			break;
		case 1:
			if(this.fluidTank.getFluid() != null) {
				this.fluidTank.getFluid().amount = value;
			}
			break;
		case 2:
			this.consumptionTicks = value;
			break;
		case 3:
			this.maxConsumptionTicks = value;
			break;
		case 4:
			this.fuelTicks = value;
			break;
		case 5:
			this.maxFuelTicks = value;
			break;
		}
	}

	public void sendGUIData(ContainerCenser censer, IContainerListener craft) {
		craft.sendWindowProperty(censer, 0, this.remainingItemAmount);
		craft.sendWindowProperty(censer, 1, this.fluidTank.getFluid() != null ? this.fluidTank.getFluid().amount : 0);
		craft.sendWindowProperty(censer, 2, this.consumptionTicks);
		craft.sendWindowProperty(censer, 3, this.maxConsumptionTicks);
		craft.sendWindowProperty(censer, 4, this.fuelTicks);
		craft.sendWindowProperty(censer, 5, this.maxFuelTicks);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		if(!this.level.isClientSide()) {
			if(this.fuelTicks > 0) {
				this.fuelTicks--;

				this.setChanged();
				if(this.fuelTicks <= 0) {
					BlockState stat = this.level.getBlockState(this.worldPosition);
					this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);
				}
			} else {
				this.fuelTicks = 0;
			}

			if(this.checkInternalSlotForRecipes) {
				this.checkInternalSlotForRecipes = false;

				FluidStack fluid = this.fluidTank.getFluid();
				ItemStack internalStack = this.getItem(ContainerCenser.SLOT_INTERNAL);

				if(fluid != null && fluid.amount > 0) {
					ICenserRecipe<?> recipe = this.getEffect(fluid);

					if(recipe != null && recipe.matchesInput(fluid)) {
						this.isFluidRecipe = true;
						this.currentRecipe = (ICenserRecipe<Object>)recipe;
						this.currentRecipeContext = recipe.createContext(fluid);
						this.currentRecipe.onStart(this.currentRecipeContext);
						this.maxConsumptionTicks = this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this);
						this.setChanged();
						BlockState stat = this.level.getBlockState(this.worldPosition);
						this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);
					}
				} else if(!internalStack.isEmpty()) {
					ICenserRecipe<?> recipe = this.getEffect(internalStack);

					if(recipe != null && recipe.matchesInput(internalStack)) {
						this.isFluidRecipe = false;
						this.currentRecipe = (ICenserRecipe<Object>)recipe;
						this.currentRecipeContext = recipe.createContext(internalStack);
						this.currentRecipe.onStart(this.currentRecipeContext);
						this.maxConsumptionTicks = this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this);
						this.setChanged();
						BlockState stat = this.level.getBlockState(this.worldPosition);
						this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);
					}
				}
			}
		}

		this.isRecipeRunning = false;

		if(this.currentRecipe != null) {
			BlockState state = this.level.getBlockState(this.worldPosition);
			boolean isDisabled = state.getBlock() instanceof BlockCenser && !state.getValue(BlockCenser.ENABLED);

			if(!this.level.isClientSide() && !isDisabled && this.fuelTicks <= 0 && this.isFilled()) {
				ItemStack fuelStack = this.getItem(ContainerCenser.SLOT_FUEL);

				if(!fuelStack.isEmpty() && AbstractFurnaceTileEntity.isFuel(fuelStack)) {
					this.maxFuelTicks = this.fuelTicks = ForgeHooks.getBurnTime(fuelStack) * 4;

					fuelStack.shrink(1);
					if(fuelStack.getCount() <= 0) {
						this.setItem(ContainerCenser.SLOT_FUEL, ItemStack.EMPTY);
					}

					this.setChanged();
					BlockState stat = this.level.getBlockState(this.worldPosition);
					this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);
				}
			}

			if(!this.canRecipeRun(this.isFluidRecipe, this.currentRecipe)) {
				if(!this.level.isClientSide()) {
					this.currentRecipe.onStop(this.currentRecipeContext);
					this.currentRecipe = null;
					this.currentRecipeContext = null;
					this.maxConsumptionTicks = this.consumptionTicks = -1;
					this.setChanged();
					BlockState stat = this.level.getBlockState(this.worldPosition);
					this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);
				}
			} else {
				if(this.fuelTicks > 0 && !isDisabled) {
					this.isRecipeRunning = true;

					int toRemove = this.currentRecipe.update(this.currentRecipeContext, this);

					if(!this.level.isClientSide()) {
						if(--this.consumptionTicks <= 0) {
							toRemove += this.currentRecipe.getConsumptionAmount(this.currentRecipeContext, this);
						}

						if(toRemove > 0) {
							if(this.isFluidRecipe) {
								this.fluidTank.drainInternal(toRemove, true);
								this.checkInputSlotForTransfer = true;
							} else {
								this.remainingItemAmount = Math.max(0, this.remainingItemAmount - toRemove);
								if(this.remainingItemAmount <= 0) {
									this.setItem(ContainerCenser.SLOT_INTERNAL, ItemStack.EMPTY);
								}
							}
							this.maxConsumptionTicks = this.consumptionTicks = this.currentRecipe.getConsumptionDuration(this.currentRecipeContext, this);
						}
					}
				}
			}
		}

		if(!this.level.isClientSide()) {
			if(this.checkInputSlotForTransfer) {
				this.checkInputSlotForTransfer = false;

				ItemStack inputStack = this.getItem(ContainerCenser.SLOT_INPUT);
				if(!inputStack.isEmpty()) {
					FluidActionResult fillResult;

					if(this.getItem(ContainerCenser.SLOT_INTERNAL).isEmpty() && (fillResult = FluidUtil.tryEmptyContainer(inputStack, this, Integer.MAX_VALUE, null, true)).isSuccess()) {
						this.setItem(ContainerCenser.SLOT_INPUT, fillResult.getResult());
					} else if(!this.isFilled()) {
						ICenserRecipe<?> recipe = this.getEffect(inputStack);

						if(recipe != null) {
							this.remainingItemAmount = Math.min(recipe.getInputAmount(inputStack), 1000);

							ItemStack internalStack = inputStack.copy();
							internalStack.setCount(1);
							this.setItem(ContainerCenser.SLOT_INTERNAL, internalStack);
							this.setItem(ContainerCenser.SLOT_INPUT, recipe.consumeInput(inputStack));
						}
					}
				}
			}

			if(this.currentRecipe == null) {
				ItemStack internalStack = this.getItem(ContainerCenser.SLOT_INTERNAL);
				if(!internalStack.isEmpty() && this.getEffect(internalStack) == null) {
					this.setItem(ContainerCenser.SLOT_INTERNAL, ItemStack.EMPTY);
				}
			}
		}

		if(!this.level.isClientSide()) {
			//Sync internal slot. Required on client side
			//for rendering stuff
			if(this.internalSlotChanged) {
				this.internalSlotChanged = false;
				BlockPos pos = this.getCenserPos();
				BlockState stat = this.getCenserWorld().getBlockState(pos);
				this.getCenserWorld().sendBlockUpdated(pos, stat, stat, 2);
			}
		}

		boolean isCreatingDungeonFog = this.isRecipeRunning && this.currentRecipe.isCreatingDungeonFog(this.currentRecipeContext, this);

		this.prevDungeonFogStrength = this.dungeonFogStrength;
		if(isCreatingDungeonFog && this.dungeonFogStrength < 1.0F) {
			this.dungeonFogStrength += 0.01F;
			if(this.dungeonFogStrength > 1.0F) {
				this.dungeonFogStrength = 1.0F;
			}
		} else if(!isCreatingDungeonFog && this.dungeonFogStrength > 0.0F) {
			this.dungeonFogStrength -= 0.01F;
			if(this.dungeonFogStrength < 0.0F) {
				this.dungeonFogStrength = 0.0F;
			}
		}

		this.prevEffectStrength = this.effectStrength;
		if(this.isRecipeRunning && this.effectStrength < 1.0F) {
			this.effectStrength += 0.01F;
			if(this.effectStrength > 1.0F) {
				this.effectStrength = 1.0F;
			}
		} else if(!this.isRecipeRunning && this.effectStrength > 0.0F) {
			this.effectStrength -= 0.01F;
			if(this.effectStrength < 0.0F) {
				this.effectStrength = 0.0F;
			}
		}
	}

	protected boolean canRecipeRun(boolean isFluidRecipe, ICenserRecipe<?> recipe) {
		return !((isFluidRecipe && (this.fluidTank.getFluidAmount() <= 0 || !recipe.matchesInput(this.fluidTank.getFluid()))) ||
				(!isFluidRecipe && (this.getItem(ContainerCenser.SLOT_INTERNAL).isEmpty() || this.remainingItemAmount <= 0 || !recipe.matchesInput(this.getItem(ContainerCenser.SLOT_INTERNAL)))));
	}

	protected ICenserRecipe<?> getEffect(FluidStack stack) {
		return AbstractCenserRecipe.getRecipe(stack);
	}

	protected ICenserRecipe<?> getEffect(ItemStack stack) {
		return AbstractCenserRecipe.getRecipe(stack);
	}

	protected boolean isFilled() {
		return this.fluidTank.getFluidAmount() > 0 || !this.getItem(ContainerCenser.SLOT_INTERNAL).isEmpty();
	}

	private void extractFluids(FluidStack fluid) {
		if(fluid.isFluidEqual(fluidTank.getFluid())) {
			fluidTank.drain(fluid.amount, true);
		}
		setChanged();
	}

	public boolean hasFuel() {
		return !getItem(ContainerCenser.SLOT_FUEL).isEmpty() && EnumItemMisc.SULFUR.isItemOf(getItem(ContainerCenser.SLOT_FUEL)) && getItem(ContainerCenser.SLOT_FUEL).getCount() >= 1;
	}

	@Nullable
	public ICenserRecipe<Object> getCurrentRecipe() {
		return this.currentRecipe;
	}

	@Nullable
	public Object getCurrentRecipeContext() {
		return this.currentRecipeContext;
	}

	@Override
	public int getCurrentRemainingInputAmount() {
		return this.isFluidRecipe ? this.fluidTank.getFluidAmount() : this.remainingItemAmount;
	}

	@Override
	public int getCurrentMaxInputAmount() {
		return this.isFluidRecipe ? this.fluidTank.getCapacity() : 1000;
	}

	public int getMaxFuelTicks() {
		return this.maxFuelTicks;
	}

	public int getFuelTicks() {
		return this.fuelTicks;
	}

	@Override
	public int[] getSlotsForFace(Direction facing) {
		switch(facing) {
		case DOWN:
		case UP:
			return new int[]{ ContainerCenser.SLOT_INPUT };
		default:
			return new int[]{ ContainerCenser.SLOT_FUEL };
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, Direction side) {
		if(super.canInsertItem(slot, stack, side)) {
			//Only allow automatic extraction from input slot if item has no censer recipe, i.e. was used
			if(slot == ContainerCenser.SLOT_INPUT) {
				FluidStack fluid = FluidUtil.getFluidContained(stack);

				if((fluid != null && this.getEffect(fluid) != null) || this.getEffect(stack) != null) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, FluidAction doFill) {
		if(this.remainingItemAmount > 0) {
			return 0;
		}
		//TODO Send custom packet that only updates fluid
		if (doFill == FluidAction.EXECUTE) {
			this.setChanged();
			BlockState stat = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		}
		return this.fluidTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		if (doDrain == FluidAction.EXECUTE) {
			this.setChanged();
			BlockState stat = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
		}
		return this.fluidTank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		if (doDrain == FluidAction.EXECUTE) {
			this.setChanged();
			BlockState stat = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, stat, stat, 2);

			this.checkInternalSlotForRecipes = true;
			this.checkInputSlotForTransfer = true;
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

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.getFogRenderArea();
	}

	public float getDungeonFogStrength(float partialTicks) {
		return this.prevDungeonFogStrength + (this.dungeonFogStrength - this.prevDungeonFogStrength) * partialTicks;
	}

	@Override
	public float getEffectStrength(float partialTicks) {
		return this.prevEffectStrength + (this.effectStrength - this.prevEffectStrength) * partialTicks;
	}

	@Override
	public boolean isRecipeRunning() {
		return this.isRecipeRunning;
	}

	public AxisAlignedBB getFogRenderArea() {
		float width = 13.0F;
		float height = 12.0F;
		BlockPos pos = this.getCenserPos();
		return new AxisAlignedBB(pos.getX() + 0.5D - width / 2, pos.getY() - 0.1D, pos.getZ() + 0.5D - width / 2, pos.getX() + 0.5D + width / 2, pos.getY() - 0.1D + height, pos.getZ() + 0.5D + width / 2);
	}

	@Override
	public ItemStack getInputStack() {
		return this.getItem(ContainerCenser.SLOT_INPUT);
	}

	@Override
	public World getCenserWorld() {
		return this.getLevel();
	}

	@Override
	public BlockPos getCenserPos() {
		return this.getBlockPos();
	}
}
