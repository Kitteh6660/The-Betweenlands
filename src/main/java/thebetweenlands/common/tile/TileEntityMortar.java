package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.inventory.container.ContainerMortar;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class TileEntityMortar extends TileEntityBasicInventory implements ITickableTileEntity {

    public int progress;
    public boolean hasPestle;
    public boolean hasCrystal;
    public boolean manualGrinding = false;
    public float crystalVelocity;
    public float crystalRotation;
    public int itemBob;
    public boolean countUp = true;

    public TileEntityMortar() {
        super(4, "container.bl.mortar");
    }


    @Override
    public void tick() {
        if (level.isClientSide()) {
            if (hasCrystal) {
                crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
                crystalRotation += this.crystalVelocity;
                if (crystalRotation >= 360.0F)
                    crystalRotation -= 360.0F;
                else if (this.crystalRotation <= 360.0F)
                    this.crystalRotation += 360.0F;
                if (Math.abs(crystalVelocity) <= 1.0F && this.getLevel().random.nextInt(15) == 0)
                    crystalVelocity = this.level.random.nextFloat() * 18.0F - 9.0F;
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
            }
            
            if(this.progress > 0 && this.progress < 84) {
            	this.progress++;
            }
            
            return;
        }
        
        boolean validRecipe = false;
        boolean outputFull = outputIsFull();
        
        if (pestleInstalled()) {
            IPestleAndMortarRecipe recipe = PestleAndMortarRecipe.getRecipe(inventory.get(0), inventory.get(2), false);
            
            if(recipe != null) {
	            ItemStack output = recipe.getOutput(inventory.get(0), inventory.get(2).copy());
	            boolean replacesOutput = recipe.replacesOutput();
	            
	            outputFull &= !replacesOutput;
	            
	            if ((isCrystalInstalled() && getItem(3).getDamageValue() < getItem(3).getMaxDamage()) || manualGrinding) {
	                if (!output.isEmpty() && (replacesOutput || inventory.get(2).isEmpty() || (inventory.get(2).equals(output) && inventory.get(2).getCount() + output.getCount() <= output.getMaxStackSize()))) {
	                	validRecipe = true;
	                	
	                	progress++;
	                    
	                    if (progress == 1) {
	                        level.playSound(null, getBlockPos().getX() + 0.5F, getBlockPos().getY() + 0.5F, getBlockPos().getZ() + 0.5F, SoundRegistry.GRIND, SoundCategory.BLOCKS, 1F, 1F);
	                    
	                        //Makes sure client knows that new grinding cycle has started
	                        level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
	                    }
	                    
	                    if (progress == 64 || progress == 84) {
	                        level.playSound(null, getBlockPos().getX() + 0.5F, getBlockPos().getY() + 0.5F, getBlockPos().getZ() + 0.5F, SoundEvents.GRASS_BREAK, SoundCategory.BLOCKS, 0.3F, 1F);
	                        level.playSound(null, getBlockPos().getX() + 0.5F, getBlockPos().getY() + 0.5F, getBlockPos().getZ() + 0.5F, SoundEvents.STONE_BREAK, SoundCategory.BLOCKS, 0.3F, 1F);
	                    }
	                    
	                    if (!inventory.get(1).isEmpty())
	                    	NBTHelper.getStackNBTSafe(getItem(1)).putBoolean("active", true);
	                    
	                    if (progress > 84) {
	                        if (!inventory.get(0).isEmpty())
	                            if (inventory.get(0).getCount() - 1 <= 0)
	                                inventory.set(0, ItemStack.EMPTY);
	                            else
	                                inventory.get(0).shrink(1);
	                        
	                        if (replacesOutput || inventory.get(2).isEmpty())
	                            inventory.set(2, output.copy());
	                        else if (inventory.get(2).equals(output))
	                            inventory.get(2).inflate(output.getCount());
	                        
	                        inventory.get(1).setDamageValue(inventory.get(1).getDamageValue() + 1);
	                        
	                        if (!manualGrinding)
	                            inventory.get(3).setDamageValue(inventory.get(3).getDamageValue() + 1);
	                        
	                        progress = 0;
	                        manualGrinding = false;
	                        
	                        if (inventory.get(1).getDamageValue() >= inventory.get(1).getMaxDamage()) {
	                            inventory.set(1, ItemStack.EMPTY);
	                            hasPestle = false;
	                        }
	                        
	                        if (!inventory.get(1).isEmpty())
	                            NBTHelper.getStackNBTSafe(getItem(1)).putBoolean("active", false);
	                        
	                        setChanged();
	                    }
	                }
	            }
            }
        }
        if (progress > 0) {
        	setChanged();
        }
        if (pestleInstalled()) {
        	if(!hasPestle) {
        		hasPestle = true;
        		level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        	}
        } else {
        	if(hasPestle) {
        		hasPestle = false;
        		level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        	}
        }
        if (!validRecipe || getItem(0).isEmpty() || getItem(1).isEmpty() || outputFull) {
            if (!inventory.get(1).isEmpty())
                NBTHelper.getStackNBTSafe(getItem(1)).putBoolean("active", false);
            
            if(progress > 0) {
            	progress = 0;
            	level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            	setChanged();
            }
        }
        if (getItem(3).isEmpty() && progress > 0 && !manualGrinding) {
            if (!inventory.get(1).isEmpty())
                NBTHelper.getStackNBTSafe(getItem(1)).putBoolean("active", false);
            progress = 0;
            setChanged();
            level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        }
        if (isCrystalInstalled()) {
        	if(!hasCrystal) {
        		hasCrystal = true;
        		level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        	}
        } else {
        	if(hasCrystal) {
        		hasCrystal = false;
        		level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        	}
        }
    }

    public boolean pestleInstalled() {
        return !getItem(1).isEmpty() && getItem(1).getItem() == ItemRegistry.PESTLE.get();
    }

    public boolean isCrystalInstalled() {
        return !getItem(3).isEmpty() && getItem(3).getItem() instanceof ItemLifeCrystal && getItem(3).getDamageValue() <= getItem(3).getMaxDamage();
    }

    private boolean outputIsFull() {
        return !getItem(2).isEmpty() && getItem(2).getCount() >= getMaxStackSize();
    }

    public void sendGUIData(ContainerMortar mortar, IContainerListener containerListener) {
        containerListener.setContainerData(mortar, 0, progress);
    }

    public void getGUIData(int id, int value) {
        switch (id) {
            case 0:
                progress = value;
                break;
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("progress", progress);
        nbt.putBoolean("hasPestle", hasPestle);
        nbt.putBoolean("hasCrystal", hasCrystal);
        nbt.putBoolean("manualGrinding", manualGrinding);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        progress = nbt.getInt("progress");
        hasPestle = nbt.getBoolean("hasPestle");
        hasCrystal = nbt.getBoolean("hasCrystal");
        manualGrinding = nbt.getBoolean("manualGrinding");
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("progress", progress);
        nbt.putBoolean("hasPestle", hasPestle);
        nbt.putBoolean("hasCrystal", hasCrystal);
        nbt.putBoolean("manualGrinding", manualGrinding);
        this.writeInventoryNBT(nbt);
        return new SUpdateTileEntityPacket(getBlockPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        progress = packet.getTag().getInt("progress");
        hasPestle = packet.getTag().getBoolean("hasPestle");
        hasCrystal = packet.getTag().getBoolean("hasCrystal");
        manualGrinding = packet.getTag().getBoolean("manualGrinding");
        this.readInventoryNBT(packet.getTag());
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        switch(side) {
            case DOWN:
                return new int[]{2};
            case UP:
                return new int[]{0};
            case NORTH:
            case EAST:
            case SOUTH:
            case WEST:
                return new int[]{0, 1, 3};
        }
        return new int[]{};
    }


    @Override
    public boolean canPlaceItem(int slot, ItemStack itemstack) {
        return slot == 1 && itemstack.getItem() == ItemRegistry.PESTLE.get() || slot == 3 && itemstack.getItem() instanceof ItemLifeCrystal || slot != 1 && itemstack.getItem() != ItemRegistry.PESTLE.get() && slot != 3 && itemstack.getItem() != ItemRegistry.LIFE_CRYSTAL.get();
    }

}