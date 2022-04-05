package thebetweenlands.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.event.EquipmentChangedEvent;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.capability.equipment.EquipmentEntityCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryEquipment implements IInventory, ITickableTileEntity {
	
    protected final NonNullList<ItemStack> inventory;
    protected final NonNullList<ItemStack> prevTickStacks;
    protected final EquipmentEntityCapability capability;
    private int lastChangeCheck = 0;

    public InventoryEquipment(EquipmentEntityCapability capability, NonNullList<ItemStack> inventory) {
        this.capability = capability;
        this.inventory = inventory;
        this.prevTickStacks = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);

        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty())
                this.prevTickStacks.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
    }

    @Override
    public String getName() {
        return "container.bl.equipment";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return (ITextComponent) (this.hasCustomName() ? new TextComponentString(this.getName()) : new TranslationTextComponent(this.getName(), new Object[0]));
    }

    @Override
    @Nullable
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = ItemStack.EMPTY;
        if (index < this.getContainerSize()) {
            stack = ItemStackHelper.takeItem(inventory, index);
            this.setChanged();
        }
        return stack;
    }

    @Override
    @Nullable
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = ItemStack.EMPTY;
        if (index < this.getContainerSize()) {
            stack = ItemStackHelper.removeItem(inventory, index, count);
            this.setChanged();
        }
        return stack;
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        if (index < this.getContainerSize()) {
            this.inventory.set(index, stack);
            this.setChanged();
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        this.capability.setChanged();
        MinecraftForge.EVENT_BUS.post(new EquipmentChangedEvent(this.capability.getEntity(), this.capability));
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }


    @Override
    public void startOpen(PlayerEntity player) {

    }

    @Override
    public void stopOpen(PlayerEntity player) {

    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index < this.getContainerSize()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.inventory.size(); ++i) {
            this.inventory.set(i, ItemStack.EMPTY);
        }
        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.size() <= 0;
    }

    @Override
    @Nullable
    public ItemStack getItem(int index) {
        return index >= this.getContainerSize() ? ItemStack.EMPTY : this.inventory.get(index);
    }

    @Override
    public void tick() {
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.get(i);

            if (!stack.isEmpty() && stack.getItem() instanceof IEquippable) {
                ((IEquippable) stack.getItem()).onEquipmentTick(stack, this.capability.getEntity(), this);
            }
        }

        if(this.lastChangeCheck++ > 10) {
        	this.detectChangesAndMarkDirty();
        	this.lastChangeCheck = 0;
        }
    }

    protected void detectChangesAndMarkDirty() {
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack stack = this.inventory.get(i);
            ItemStack prevStack = this.prevTickStacks.get(i);

            if (!ItemStack.isSame(prevStack, stack)) {
                this.prevTickStacks.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
                this.setChanged();
            }
        }
    }
}

