package thebetweenlands.common.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.entity.mobs.EntityGasCloud;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO: Send resulting elixir recipe with the NBT
public class TileEntityInfuser extends TileEntityBasicInventory implements IFluidHandler, ITickableTileEntity {
	
	public static final int MAX_INGREDIENTS = 6;

	public final FluidTank waterTank;

	private final IFluidTankProperties[] properties = new IFluidTankProperties[1];

	private int infusionTime = 0;
	private int stirProgress = 90;
	private int temp = 0;
	private int evaporation = 0;
	private int itemBob = 0;
	private boolean countUp = true;
	private boolean hasInfusion = false;
	private boolean hasCrystal = false;
	private float crystalVelocity = 0.0F;
	private float crystalRotation = 0.0F;
	private ElixirRecipe infusingRecipe = null;
	private boolean updateRecipe = false;

	/**
	 * 0 = no progress, 1 = in progress, 2 = finished, 3 = failed
	 **/
	private int currentInfusionState = 0;
	private int prevInfusionState = 0;
	private int infusionColorGradientTicks = 0;

	public float[] prevInfusionColor = new float[4];
	public float[] currentInfusionColor = new float[4];

	public TileEntityInfuser() {
		super(MAX_INGREDIENTS + 2, "container.bl.infuser");
		this.waterTank = new FluidTank(FluidRegistry.SWAMP_WATER, 0, Fluid.BUCKET_VOLUME * 3);
		this.waterTank.setTileEntity(this);
		this.properties[0] = new FluidTankPropertiesWrapper(this.waterTank);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return this.properties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(this.hasInfusion) {
			return 0; //Don't allow refill when infusing has already started
		}
		int filled = this.waterTank.fill(resource, FluidAction.SIMULATE);
		if (filled == resource.getAmount() && doFill) {
			this.waterTank.fill(resource, FluidAction.EXECUTE);
			if (temp >= 3) {
				temp = temp - temp / 3;
				evaporation = 0;
			}

			if (doFill) {
				this.setChanged();
				BlockState stat = this.level.getBlockState(this.worldPosition);
				this.level.sendBlockUpdated(this.worldPosition, stat, stat, 3);
			}
		}
		return filled;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		return null;
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
	public void tick() {
		BlockPos pos = this.getBlockPos();
		
		if (this.updateRecipe) {
			this.updateInfusingRecipe();
			this.updateRecipe = false;
		}
		boolean updateBlock = false;
		if (this.hasInfusion && this.infusingRecipe != null) {
			if (!this.level.isClientSide()) {
				this.infusionTime++;
			} else {
				if (this.prevInfusionState != this.currentInfusionState) {
					this.prevInfusionColor = this.currentInfusionColor;
					this.currentInfusionColor = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
				} else {
					this.currentInfusionColor = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
				}
			}
			if(this.prevInfusionState != this.currentInfusionState && this.currentInfusionState == 2) {
				this.level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.INFUSER_FINISHED, SoundCategory.BLOCKS, 1, 1);
			}
			this.prevInfusionState = this.currentInfusionState;
			if (!this.level.isClientSide()) {
				if (this.infusionTime > this.infusingRecipe.idealInfusionTime + this.infusingRecipe.infusionTimeVariation) {
					//fail
					if (this.currentInfusionState != 3)
						updateBlock = true;
					this.currentInfusionState = 3;
				} else if (this.infusionTime > this.infusingRecipe.idealInfusionTime - this.infusingRecipe.infusionTimeVariation
						&& this.infusionTime < this.infusingRecipe.idealInfusionTime + this.infusingRecipe.infusionTimeVariation) {
					//finished
					if (this.currentInfusionState != 2)
						updateBlock = true;
					this.currentInfusionState = 2;
				} else {
					//progress
					if (this.currentInfusionState != 1)
						updateBlock = true;
					this.currentInfusionState = 1;
				}
			}
			if (this.infusionColorGradientTicks > 0) {
				this.infusionColorGradientTicks++;
			}
			if (!this.level.isClientSide() && this.currentInfusionState != prevInfusionState) {
				//start gradient animation
				this.infusionColorGradientTicks = 1;
				updateBlock = true;
			}
			if (!this.level.isClientSide() && this.infusionColorGradientTicks > 30) {
				this.infusionColorGradientTicks = 0;
				updateBlock = true;
			}
			if (this.level.isClientSide() && this.infusionColorGradientTicks > 0 && this.currentInfusionState == 2) {
				for (int i = 0; i < 10; i++) {
					double x = pos.getX() + 0.25F + this.level.random.nextFloat() * 0.5F;
					double z = pos.getZ() + 0.25F + this.level.random.nextFloat() * 0.5F;
					BLParticles.STEAM_PURIFIER.spawn(this.level, x, pos.getY() + 1.0D - this.level.random.nextFloat() * 0.2F, z);
				}
			}
		} else {
			if (this.currentInfusionState != 0)
				updateBlock = true;
			
			this.infusionTime = 0;
			
			if(this.hasIngredients() && this.temp >= 100) {
				if (this.infusionColorGradientTicks > 0) {
					this.infusionColorGradientTicks++;
				}
				
				if (!this.level.isClientSide() && this.infusionColorGradientTicks == 0 && this.currentInfusionState == 0 && this.stirProgress == 89) {
					//start gradient animation
					this.infusionColorGradientTicks = 1;
					this.currentInfusionState = 1;
					this.level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.INFUSER_FINISHED, SoundCategory.BLOCKS, 1, 1);
					updateBlock = true;
				}
				
				if (!this.level.isClientSide() && this.infusionColorGradientTicks > 30) {
					this.infusionColorGradientTicks = 0;
					this.currentInfusionState = 2;
					updateBlock = true;
				}
				
				if(this.level.isClientSide() && (this.infusionColorGradientTicks > 0 || this.currentInfusionState == 2)) {
					this.prevInfusionColor = new float[]{0.2F, 0.6F, 0.4F, 1.0F};
					this.currentInfusionColor = new float[]{0.8F, 0.0F, 0.8F, 1.0F};
				}
				
				if (this.level.isClientSide() && this.infusionColorGradientTicks > 0) {
					for (int i = 0; i < 10; i++) {
						double x = pos.getX() + 0.25F + this.level.random.nextFloat() * 0.5F;
						double z = pos.getZ() + 0.25F + this.level.random.nextFloat() * 0.5F;
						BLParticles.STEAM_PURIFIER.spawn(this.level, x, pos.getY() + 1.0D - this.level.random.nextFloat() * 0.2F, z);
					}
				}
			} else {
				this.currentInfusionState = 0;
				this.currentInfusionColor = new float[]{0.2F, 0.6F, 0.4F, 1.0F};
				this.prevInfusionColor = this.currentInfusionColor;
			}
		}
		if (!this.level.isClientSide() && updateBlock) {
			this.markForUpdate();
		}
		if (level.isClientSide()) {
			if (isValidCrystalInstalled()) {
				crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
				crystalRotation += this.crystalVelocity;
				if (crystalRotation >= 360.0F) {
					crystalRotation -= 360.0F;
				} else if (this.crystalRotation <= 360.0F) {
					this.crystalRotation += 360.0F;
				}
				if (Math.abs(crystalVelocity) <= 1.0F && this.getLevel().random.nextInt(15) == 0) {
					crystalVelocity = this.level.random.nextFloat() * 18.0F - 9.0F;
				}
			}
			if (countUp && itemBob <= 20) {
				itemBob++;
				if (itemBob == 20)
					countUp = false;
			}
			if (!countUp && itemBob >= 0) {
				itemBob--;
				if (itemBob == 0)
					countUp = true;
			}
			return;
		}

		//To keep infusion time on client in sync
		if (this.infusionTime > 0 && this.infusionTime % 20 == 0) {
			this.markForUpdate();
		}

		if (stirProgress < 90) {
			stirProgress++;
			this.markForUpdate();
		}
		if (stirProgress == 89) {
			if (temp == 100 && !hasInfusion) {
				if (this.hasIngredients()) {
					hasInfusion = true;
					this.markForUpdate();
				}
			}
			evaporation = 0;
		}
		if (level.getBlockState(pos.below()).getBlock() == Blocks.FIRE && temp < 100 && getWaterAmount() > 0) {
			if (level.getGameTime() % 12 == 0) {
				temp++;
				this.markForUpdate();
			}
		}
		if (level.getBlockState(pos.below()).getBlock() != Blocks.FIRE && temp > 0) {
			if (level.getGameTime() % 6 == 0) {
				temp--;
				this.markForUpdate();
			}
		}
		if (temp == 100) {
			evaporation++;
			if (evaporation == 600 && getWaterAmount() >= Fluid.BUCKET_VOLUME) {
				extractFluids(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME));
			}
			this.markForUpdate();
		}
		if (temp < 100 && evaporation > 0) {
			evaporation--;
			this.markForUpdate();
		}
		if (isValidCrystalInstalled()) {
			if (temp >= 100 && evaporation >= 400 && stirProgress >= 90 && this.hasIngredients()) {
				inventory.get(MAX_INGREDIENTS + 1).setDamageValue(inventory.get(MAX_INGREDIENTS + 1).getDamageValue() + 1);
				stirProgress = 0;
			}
			if (!hasCrystal) {
				hasCrystal = true;
				this.markForUpdate();
			}
		} else {
			if (hasCrystal) {
				hasCrystal = false;
				this.markForUpdate();
			}
		}
	}

	/**
	 * Returns the current infusing state:
	 * 0 = no progress, 1 = in progress, 2 = finished, 3 = failed
	 */
	public int getInfusingState() {
		return this.currentInfusionState;
	}

	/**
	 * Returns the infusion color gradient ticks
	 *
	 * @return
	 */
	public int getInfusionColorGradientTicks() {
		return this.infusionColorGradientTicks;
	}

	public void extractFluids(FluidStack fluid) {
		if (fluid.isFluidEqual(waterTank.getFluid())) {
			waterTank.drain(fluid.getAmount(), FluidAction.EXECUTE);
		}
		if (getWaterAmount() == 0) {
			if (hasInfusion) {
				for (int i = 0; i <= TileEntityInfuser.MAX_INGREDIENTS; i++) {
					ItemStack stack = getItem(i);
					if (!stack.isEmpty() && stack.getItem() == ItemRegistry.ASPECT_VIAL.get()) {
						//Return empty vials
						ItemStack ret = ItemStack.EMPTY;
						switch (stack.getDamageValue()) {
						case 0:
						default:
							ret = new ItemStack(ItemRegistry.DENTROTHYST_VIAL.get(), 1, 0);
							break;
						case 1:
							ret = new ItemStack(ItemRegistry.DENTROTHYST_VIAL.get(), 1, 2);
							break;
						}
						ItemEntity entity = new ItemEntity(this.level, this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 1.0D, this.getBlockPos().getZ() + 0.5D, ret);
						this.level.addFreshEntity(entity);
					}
					setItem(i, ItemStack.EMPTY);
				}
				if (evaporation == 600) {
					EntityGasCloud gasCloud = new EntityGasCloud(this.level);
					if (this.infusingRecipe != null) {
						float[] color = ElixirRecipe.getInfusionColor(this.infusingRecipe, this.infusionTime);
						gasCloud.setGasColor((int)(color[0] * 255), (int)(color[1] * 255), (int)(color[2] * 255), 170);
					}
					gasCloud.moveTo(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 1D, this.worldPosition.getZ() + 0.5D, MathHelper.wrapDegrees(this.level.random.nextFloat() * 360.0F), 0.0F);
					this.level.addFreshEntity(gasCloud);
				}
				this.infusingRecipe = null;
			}
			hasInfusion = false;
			temp = 0;
			waterTank.setFluid(new FluidStack(FluidRegistry.SWAMP_WATER, 0));
		}
		evaporation = 0;
		this.markForUpdate();
	}

	public void markForUpdate() {
		BlockState state = this.level.getBlockState(this.getBlockPos());
		this.level.sendBlockUpdated(this.getBlockPos(), state, state, 2);
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

	public boolean isValidCrystalInstalled() {
		return !inventory.get(MAX_INGREDIENTS + 1).isEmpty() && inventory.get(MAX_INGREDIENTS + 1).getItem() instanceof ItemLifeCrystal && inventory.get(MAX_INGREDIENTS + 1).getDamageValue() < inventory.get(MAX_INGREDIENTS + 1).getMaxDamage();
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.put("waterTank", waterTank.save(new CompoundNBT()));
		nbt.putInt("stirProgress", stirProgress);
		nbt.putInt("evaporation", evaporation);
		nbt.putInt("temp", temp);
		nbt.putInt("infusionTime", infusionTime);
		nbt.putBoolean("hasInfusion", hasInfusion);
		nbt.putBoolean("hasCrystal", hasCrystal);
		nbt.putInt("infusionState", this.currentInfusionState);
		nbt.putInt("infusionColorGradientTicks", this.infusionColorGradientTicks);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
		stirProgress = nbt.getInt("stirProgress");
		evaporation = nbt.getInt("evaporation");
		temp = nbt.getInt("temp");
		infusionTime = nbt.getInt("infusionTime");
		hasInfusion = nbt.getBoolean("hasInfusion");
		hasCrystal = nbt.getBoolean("hasCrystal");
		currentInfusionState = nbt.getInt("infusionState");
		infusionColorGradientTicks = nbt.getInt("infusionColorGradientTicks");
		this.updateRecipe = true;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getBlockPos(), 1, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		this.readInventoryNBT(nbt);
		waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
		stirProgress = nbt.getInt("stirProgress");
		evaporation = nbt.getInt("evaporation");
		temp = nbt.getInt("temp");
		infusionTime = nbt.getInt("infusionTime");
		hasInfusion = nbt.getBoolean("hasInfusion");
		hasCrystal = nbt.getBoolean("hasCrystal");
		currentInfusionState = nbt.getInt("infusionState");
		infusionColorGradientTicks = nbt.getInt("infusionColorGradientTicks");
		this.updateInfusingRecipe();
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		this.writeInventoryNBT(nbt);
		nbt.put("waterTank", waterTank.save(new CompoundNBT()));
		nbt.putInt("stirProgress", stirProgress);
		nbt.putInt("evaporation", evaporation);
		nbt.putInt("temp", temp);
		nbt.putInt("infusionTime", infusionTime);
		nbt.putBoolean("hasInfusion", hasInfusion);
		nbt.putBoolean("hasCrystal", hasCrystal);
		nbt.putInt("infusionState", this.currentInfusionState);
		nbt.putInt("infusionColorGradientTicks", this.infusionColorGradientTicks);
		return nbt;
	}

	public boolean hasIngredients() {
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (!inventory.get(i).isEmpty()) return true;
		}
		return false;
	}

	public List<IAspectType> getInfusingAspects() {
		List<IAspectType> infusingAspects = new ArrayList<IAspectType>();
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (!inventory.get(i).isEmpty()) {
				ItemAspectContainer container = ItemAspectContainer.fromItem(inventory.get(i), AspectManager.get(this.level));
				for (Aspect aspect : container.getAspects()) {
					infusingAspects.add(aspect.type);
				}
			}
		}
		return infusingAspects;
	}

	public boolean hasFullIngredients() {
		for (int i = 0; i <= MAX_INGREDIENTS; i++) {
			if (inventory.get(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack itemstack) {
		return !hasInfusion() && getItem(slot).isEmpty() && ((slot <= MAX_INGREDIENTS && ItemAspectContainer.fromItem(itemstack, AspectManager.get(world)).getAspects().size() > 0) || (slot == MAX_INGREDIENTS + 1 && itemstack.getItem() instanceof ItemLifeCrystal));
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		BlockState state = world.getBlockState(pos);
		this.level.sendBlockUpdated(pos, state, state, 2);
	}

	public int getInfusionTime() {
		return this.infusionTime;
	}

	public float getCrystalRotation() {
		return this.crystalRotation;
	}

	public int getEvaporation() {
		return this.evaporation;
	}

	public boolean hasInfusion() {
		return this.hasInfusion;
	}

	public int getItemBob() {
		return this.itemBob;
	}

	public int getStirProgress() {
		return this.stirProgress;
	}

	public int getTemperature() {
		return this.temp;
	}

	public void setStirProgress(int progress) {
		this.stirProgress = progress;
	}

	public ElixirRecipe getInfusingRecipe() {
		return this.infusingRecipe;
	}

	public void updateInfusingRecipe() {
		if (this.level != null)
			this.infusingRecipe = ElixirRecipes.getFromAspects(this.getInfusingAspects());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTanks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTankCapacity(int tank) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		// TODO Auto-generated method stub
		return 0;
	}
}
