package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.client.audio.AnimatorSound;
import thebetweenlands.common.inventory.container.ContainerAnimator;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityAnimator extends TileEntityBasicInventory implements ITickableTileEntity {
	
    public ItemStack itemToAnimate = ItemStack.EMPTY;
    public int fuelBurnProgress, lifeCrystalLife, fuelConsumed = 0, requiredFuelCount = 32, requiredLifeCount = 32;
    public boolean itemAnimated = false;
    //public static final WeightedRandomItem[] items = new WeightedRandomItem[] { new WeightedRandomItem(new ItemStack(BLItemRegistry.lifeCrystal), 10), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD), 20), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 30), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.SULFUR), 40) };
    private int prevStackSize = 0;
    private ItemStack prevItem = ItemStack.EMPTY;

    private boolean running = false;
    
    private boolean soundPlaying = false;

    public TileEntityAnimator() {
        super(3, "container.bl.animator");
    }

    @Override
    public void tick() {
        if (isSlotInUse(0) && isValidFocalItem()) {
            this.itemToAnimate = this.inventory.get(0);
            if(!this.level.isClientSide()) {
	            IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(this.itemToAnimate);
	            if (recipe != null) {
	                this.requiredFuelCount = recipe.getRequiredFuel(this.itemToAnimate);
	                this.requiredLifeCount = recipe.getRequiredLife(this.itemToAnimate);
	            }
            }
        } else {
            this.itemToAnimate = ItemStack.EMPTY;
        }
        if (!level.isClientSide()) {
            if (isCrystalInslot())
                lifeCrystalLife = getCrystalPower();
            if (!isSlotInUse(0) || !isSlotInUse(1) || !isSlotInUse(2)) {
                fuelBurnProgress = 0;
                fuelConsumed = 0;
            }

            if (!this.itemToAnimate.isEmpty() && isCrystalInslot() && isSulfurInSlot() && fuelConsumed < requiredFuelCount && isValidFocalItem()) {
                if (lifeCrystalLife >= this.requiredLifeCount) {
                    fuelBurnProgress++;
                    if (fuelBurnProgress >= 42) {
                        fuelBurnProgress = 0;
                        removeItem(2, 1);
                        fuelConsumed++;
                        setChanged();
                    }
                    this.itemAnimated = false;
                }
            }

            if (isSlotInUse(2) && !this.itemAnimated) {
                if (!isSlotInUse(0) || !isSlotInUse(1)) {
                    fuelBurnProgress = 0;
                    fuelConsumed = 0;
                }
            }

            if (fuelConsumed >= requiredFuelCount && isSlotInUse(0) && isSlotInUse(1) && !this.itemAnimated) {
                IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(inventory.get(0));
                if(recipe != null) {
                	ItemStack input = inventory.get(0).copy();
	                ItemStack result = recipe.onAnimated(this.world, getBlockPos(), inventory.get(0));
	                if (result.isEmpty()) result = recipe.getResult(inventory.get(0));
	                if (!result.isEmpty()) {
	                    setItem(0, result.copy());
	                    
	                    AxisAlignedBB aabb = new AxisAlignedBB(this.getBlockPos()).inflate(12);
	                    for(ServerPlayerEntity player : this.world.getEntitiesOfClass(ServerPlayerEntity.class, aabb, EntitySelectors.NOT_SPECTATING)) {
	                    	if(player.getDistanceSq(this.getBlockPos()) <= 144) {
	                    		AdvancementCriterionRegistry.ANIMATE.trigger(input, result.copy(), player);
	                    	}
	                    }
	                }
                }
                inventory.get(1).setDamageValue(inventory.get(1).getDamageValue() + this.requiredLifeCount);
                setChanged();
                this.itemAnimated = true;
            }
            if (prevStackSize != (isSlotInUse(0) ? inventory.get(0).getCount() : 0))
                setChanged();
            if (prevItem != (isSlotInUse(0) ? inventory.get(0) : ItemStack.EMPTY))
                setChanged();
            prevItem = isSlotInUse(0) ? inventory.get(0) : ItemStack.EMPTY;
            prevStackSize = isSlotInUse(0) ? inventory.get(0).getCount() : 0;
            
            boolean shouldBeRunning = this.isSlotInUse(0) && this.isCrystalInslot() && this.isSulfurInSlot() && this.fuelConsumed < this.requiredFuelCount && lifeCrystalLife >= this.requiredLifeCount && this.isValidFocalItem();
            if(this.running != shouldBeRunning) {
            	this.running = shouldBeRunning;
            	this.setChanged();
            }
            
            updateContainingBlockInfo();
        } else {
            if (this.isRunning() && !this.soundPlaying) {
                this.playAnimatorSound();
                this.soundPlaying = true;
            } else if (!this.isRunning()) {
                this.soundPlaying = false;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void playAnimatorSound() {
        Minecraft.getInstance().getSoundHandler().playSound(new AnimatorSound(SoundRegistry.ANIMATOR, SoundCategory.BLOCKS, this));
    }

    @Override
    public boolean isEmpty() {
        return inventory.size() <= 0;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (world != null)
            world.sendBlockUpdated(getBlockPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    public boolean isCrystalInslot() {
        return isSlotInUse(1) && inventory.get(1).getItem() instanceof ItemLifeCrystal && inventory.get(1).getDamageValue() < inventory.get(1).getMaxDamage();
    }

    public int getCrystalPower() {
        if (isCrystalInslot())
            return inventory.get(1).getMaxDamage() - inventory.get(1).getDamageValue();
        return 0;
    }

    public boolean isSulfurInSlot() {
        return isSlotInUse(2) && inventory.get(2).getItem() == ItemRegistry.ITEMS_MISC && inventory.get(2).getDamageValue() == ItemMisc.EnumItemMisc.SULFUR.getID();
    }

    public boolean isSlotInUse(int slot) {
        return !inventory.get(slot).isEmpty();
    }

    public boolean isValidFocalItem() {
        return !inventory.get(0).isEmpty() && AnimatorRecipe.getRecipe(inventory.get(0)) != null;
    }

    public void sendGUIData(ContainerAnimator animator, IContainerListener listener) {
        listener.setContainerData(animator, 0, fuelBurnProgress);
        listener.setContainerData(animator, 1, lifeCrystalLife);
        listener.setContainerData(animator, 2, itemAnimated ? 1 : 0);
        listener.setContainerData(animator, 3, fuelConsumed);
        listener.setContainerData(animator, 4, requiredFuelCount);
        listener.setContainerData(animator, 5, requiredLifeCount);
    }

    public void getGUIData(int id, int value) {
        switch (id) {
            case 0:
                fuelBurnProgress = value;
                break;
            case 1:
                lifeCrystalLife = value;
                break;
            case 2:
                itemAnimated = value == 1;
                break;
            case 3:
                fuelConsumed = value;
                break;
            case 4:
            	requiredFuelCount = value;
            	break;
            case 5:
            	requiredLifeCount = value;
            	break;
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        this.writeNBT(nbt);
        return nbt;
    }

    protected void writeNBT(CompoundNBT nbt) {
        nbt.putInt("life", lifeCrystalLife);
        nbt.putInt("progress", fuelBurnProgress);
        nbt.putInt("itemsConsumed", fuelConsumed);
        nbt.putBoolean("lifeDepleted", itemAnimated);
        CompoundNBT toAnimateCompound = new CompoundNBT();
        if (!this.itemToAnimate.isEmpty()) {
            this.itemToAnimate.save(toAnimateCompound);
        }
        nbt.put("toAnimate", toAnimateCompound);
        nbt.putBoolean("running", running);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.readNBT(nbt);
    }

    protected void readNBT(CompoundNBT nbt) {
        lifeCrystalLife = nbt.getInt("life");
        fuelBurnProgress = nbt.getInt("progress");
        fuelConsumed = nbt.getInt("itemsConsumed");
        itemAnimated = nbt.getBoolean("lifeDepleted");
        CompoundNBT toAnimateStackCompound = nbt.getCompoundTag("toAnimate");
        if (toAnimateStackCompound.contains("id", Constants.NBT.TAG_STRING))
            this.itemToAnimate = new ItemStack(toAnimateStackCompound);
        else
            this.itemToAnimate = ItemStack.EMPTY;
        running = nbt.getBoolean("running");
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.writeNBT(nbt);
        this.writeInventoryNBT(nbt);
        return new SUpdateTileEntityPacket(pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        CompoundNBT nbt = packet.getTag();
        this.readNBT(nbt);
        this.readInventoryNBT(nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        this.writeNBT(nbt);
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        this.readNBT(nbt);
        this.readInventoryNBT(nbt);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, Direction side) {
        if (slot == 1 && !stack.isEmpty() && stack.getItem() instanceof ItemLifeCrystal)
            return true;
        else if (slot == 2 && !stack.isEmpty() && stack.getItem().equals(ItemRegistry.SULFUR.get()))
            return true;
        else if (slot == 0 && inventory.get(0).isEmpty())
            return true;
        return false;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN || side == Direction.UP)
            return new int[]{0};
        return new int[]{1, 2};

    }
}