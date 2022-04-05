package thebetweenlands.common.inventory.container;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotPestle;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMortar;

public class ContainerMortar  extends Container {

    private final int numRows = 2;
    private TileEntityMortar pestleAndMortar;

    public ContainerMortar(PlayerInventory playerInventory, TileEntityMortar tile) {
        super();
        int i = (numRows - 4) * 18;
        pestleAndMortar = tile;

        this.addSlot(new Slot(tile, 0, 35, 36));
        this.addSlot(new SlotPestle(tile, 1, 79, 36));
        this.addSlot(new SlotOutput(tile, 2, 123, 36, this) {
        	@Override
        	public boolean mayPlace(ItemStack stack) {
        		return !stack.isEmpty() && PestleAndMortarRecipe.isOutputUsedInAnyRecipe(stack);
        	}
        	
        	@Override
        	public int getMaxStackSize() {
        		//Only for the vials and recipes that also use the output slot
        		return 1;
        	}
        });
        this.addSlot(new ContainerAnimator.SlotLifeCrystal(tile, 3, 79, 8));

        for (int j = 0; j < 3; j++)
            for (int k = 0; k < 9; k++)
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 7 + k * 18, 119 + j * 18 + i));
        for (int j = 0; j < 9; j++)
            this.addSlot(new Slot(playerInventory, j, 7 + j * 18, 177 + i));
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            if (slotIndex == 1) {
                if (stack1.getItem() == ItemRegistry.PESTLE) {
                    if(stack1.getTag().getBoolean("active"))
                        stack1.getTag().putBoolean("active", false);
                }
            }
            if (slotIndex > 3) {
                if (stack1.getItem() == ItemRegistry.PESTLE)
                    if (!moveItemStackTo(stack1, 1, 2, true))
                        return ItemStack.EMPTY;
                if (stack1.getItem() != ItemRegistry.PESTLE && stack1.getItem() instanceof ItemLifeCrystal == false)
                    if (!moveItemStackTo(stack1, 2, 3, true) && !moveItemStackTo(stack1, 0, 1, true))
                        return ItemStack.EMPTY;
                if (stack1.getItem() instanceof ItemLifeCrystal)
                    if (!moveItemStackTo(stack1, 3, 4, true))
                        return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack1, 4, inventorySlots.size(), false))
                return ItemStack.EMPTY;
            if (stack1.getCount() == 0)
                slot.putStack(ItemStack.EMPTY);
            else

                slot.setChanged();
            if (stack1.getCount() != stack.getCount())
                slot.onTake(player, stack1);
            else
                return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        pestleAndMortar.sendGUIData(this, listener);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : listeners)
            pestleAndMortar.sendGUIData(this, (IContainerListener) crafter);
    }

    @Override
	@OnlyIn(Dist.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        super.updateProgressBar(id, value);
        pestleAndMortar.getGUIData(id, value);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.pestleAndMortar.stillValid(player);
    }
}
