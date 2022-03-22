// Unfortunately, TMG is not ported to 1.16.5. This compat is disabled.
/*package thebetweenlands.compat.tmg;

import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.registries.CapabilityRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Copied from provided example
 * Original code by M4thG33k
 * /
public class TMGEquipmentInventory extends AbstractSpecialInventory {

    @Override
    public String getUniqueIdentifier() {
        return "TheBetweenlandsEquipment";
    }

    @Override
    public INBT getNbtData(PlayerEntity player) {
        IEquipmentCapability equipmentCapability = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
        if (equipmentCapability != null) {
            CompoundNBT compound = new CompoundNBT();
            boolean put = false;

            for (EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
                IInventory inv = equipmentCapability.getInventory(type);

                ListNBT tagList = SpecialInventoryHelper.getListFromIInventory(inv);
                if (tagList != null) {
                    compound.put(type.ordinal() + "", tagList);
                    put = true;
                }
            }

            if (put) {
                return compound;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public void insertInventory(PlayerEntity player, INBT compound, boolean shouldForce) {
        if (compound instanceof CompoundNBT) {
            IEquipmentCapability equipmentCapability = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

            if (equipmentCapability != null) {

                for (EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
                    if (((CompoundNBT) compound).contains(type.ordinal() + "")) {
                        ListNBT tagList = (ListNBT) ((CompoundNBT) compound).get(type.ordinal() + "");

                        TransitionInventory graveItems = new TransitionInventory(tagList);
                        IInventory currentInventory = equipmentCapability.getInventory(type);

                        for (int i = 0; i < graveItems.getContainerSize(); i++) {
                            ItemStack graveItem = graveItems.getItem(i);

                            if (type == EnumEquipmentInventory.AMULET && i >= equipmentCapability.getAmuletSlots()){
                                SpecialInventoryHelper.dropItem(player, graveItem);
                                continue;
                            }

                            if (! graveItem.isEmpty()) {
                                ItemStack playerItem = currentInventory.getItem(i).copy();

                                if (playerItem.isEmpty()) {
                                    currentInventory.setItem(i, graveItem);
                                } else if (shouldForce) {
                                    currentInventory.setItem(i, graveItem);
                                    SpecialInventoryHelper.dropItem(player, playerItem);
                                } else {
                                    SpecialInventoryHelper.dropItem(player, graveItem);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(INBT compound) {
        List<ItemStack> ret = new ArrayList<>();

        if (compound instanceof CompoundNBT) {
            for (EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
                if (((CompoundNBT) compound).contains(type.ordinal() + "")) {
                    ListNBT tagList = (ListNBT) ((CompoundNBT) compound).get(type.ordinal() + "");

                    ret.addAll((new TransitionInventory(tagList)).getListOfNonEmptyItemStacks());
                }
            }
        }

        return ret;
    }

    @Override
    public String getInventoryDisplayNameForGui() {
        return "Betweenlands Equipment";
    }

    @Override
    public int getInventoryDisplayNameColorForGui() {
        return 0x46AE46;
    }

}*/
