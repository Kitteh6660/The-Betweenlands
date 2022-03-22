package thebetweenlands.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.capability.base.AbstractCapability;

public class NBTHelper {
	/**
	 * Returns the ItemStack NBT and creates a new CompoundNBT if necessary
	 * @param stack
	 * @return
	 */
	public static CompoundNBT getStackNBTSafe(ItemStack stack) {
		if(stack.getTag() == null)
			stack.setTag(new CompoundNBT());
		return stack.getTag();
	}

	/**
	 * Returns <tt>true</tt> if the two specifies item stacks' NBT
	 * compound tags are <i>equal</i> to one another.
	 * 
	 * @param a one NBT compound tag to be tested for equality
	 * @param b the other NBT compound tag to be tested for equality
	 * @param exclusions a list of capabilities to be excluded in checking equality
	 * @return <tt>true</tt> if the two NBT compounds tags are equal
	 * @see #areNBTCompoundsEquals(CompoundNBT, CompoundNBT, List)
	 */
	public static boolean areItemStackTagsEqualWithoutCapabilities(ItemStack a, ItemStack b, AbstractCapability<?, ?, ?>... exclusions) {
		List<String> strExclusions = new ArrayList<String>();
		for(AbstractCapability<?, ?, ?> exclusion : exclusions) {
			strExclusions.add("ForgeCaps." + exclusion.getID().toString());
		}
		return areItemStackTagsEqual(a, b, strExclusions);
	}
	
	/**
	 * Returns <tt>true</tt> if the two specifies item stacks' NBT
	 * compound tags are <i>equal</i> to one another.
	 * 
	 * @param a one NBT compound tag to be tested for equality
	 * @param b the other NBT compound tag to be tested for equality
	 * @param exclusions a list of tags to be excluded in checking equality
	 * @return <tt>true</tt> if the two NBT compounds tags are equal
	 * @see #areNBTCompoundsEquals(CompoundNBT, CompoundNBT, List)
	 */
	public static boolean areItemStackTagsEqual(ItemStack a, ItemStack b, Collection<String> exclusions) {
		if (a == null && b == null) {
			return true;
		}
		if (a != null && b != null) {
			if (a.getTag() == null && b.getTag() == null) {
				return true;
			}
			if (a.getTag() == null ^ b.getTag() == null) {
				return false;
			}
			return areNBTCompoundsEquals(a.getTag(), b.getTag(), exclusions);
		}
		return false;
	}

	/**
	 * Returns <tt>true</tt> if the two specified NBT compound tags
	 * are <i>equal</i> to one another. Two NBT compound tags are
	 * considered equal if both NBT compounds tags contain all of
	 * the same keys with the same values, while ignoring tags
	 * whose keys are in the exclusions list.
	 * 
	 * @param a one NBT compound tag to be tested for equality
	 * @param b the other NBT compound tag to be tested for equality
	 * @param exclusions a list of tags to be excluded in checking equality
	 * @return <tt>true</tt> if the two NBT compounds tags are equal
	 */
	public static boolean areNBTCompoundsEquals(CompoundNBT a, CompoundNBT b, Collection<String> exclusions) {
		Stack<String> tagOwners = new Stack<String>();
		Stack<CompoundNBT> aTagCompounds = new Stack<CompoundNBT>();
		Stack<CompoundNBT> bTagCompounds = new Stack<CompoundNBT>();
		tagOwners.push("");
		aTagCompounds.push(a);
		bTagCompounds.push(b);
		while (!aTagCompounds.isEmpty()) {
			String tagOwner = tagOwners.pop();
			CompoundNBT aCurrentTagCompound = aTagCompounds.pop();
			CompoundNBT bCurrentTagCompound = bTagCompounds.pop();
			Set<String> aKeys = aCurrentTagCompound.getAllKeys();
			Set<String> bKeys = bCurrentTagCompound.getAllKeys();
			for (String key : bKeys) {
				if (exclusions.contains(key)) {
					continue;
				}
				if (!aKeys.contains(key)) {
					return false;
				}
			}
			for (String key : aKeys) {
				String totalKey = tagOwner == "" ? key : tagOwner + '.' + key;
				if (exclusions.contains(totalKey)) {
					continue;
				}
				INBT aTag = aCurrentTagCompound.get(key);
				INBT bTag = bCurrentTagCompound.get(key);
				if (aTag instanceof CompoundNBT && bTag instanceof CompoundNBT) {
					tagOwners.push(totalKey);
					aTagCompounds.push((CompoundNBT) aTag);
					bTagCompounds.push((CompoundNBT) bTag);
				} else {
					if (!aTag.equals(bTag)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static CompoundNBT saveAllItems(CompoundNBT tag, IInventory inventory)
    {
        return saveAllItems(tag, inventory, true);
    }

    public static CompoundNBT saveAllItems(CompoundNBT tag, IInventory inventory, boolean saveEmpty)
    {
        ListNBT nbttaglist = new ListNBT();

        for (int i = 0; i < inventory.getContainerSize(); ++i)
        {
            ItemStack itemstack = inventory.getItem(i);

            if (!itemstack.isEmpty())
            {
                CompoundNBT nbttagcompound = new CompoundNBT();
                nbttagcompound.putByte("Slot", (byte)i);
                itemstack.setTag(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        if (!nbttaglist.isEmpty() || saveEmpty)
        {
            tag.put("Items", nbttaglist);
        }

        return tag;
    }

    public static void loadAllItems(CompoundNBT tag, IInventory inventory)
    {
        ListNBT nbttaglist = tag.getList("Items", 10);

        for (int i = 0; i < nbttaglist.size(); ++i)
        {
            CompoundNBT nbttagcompound = nbttaglist.getCompound(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < inventory.getContainerSize())
            {
            	ItemStack stack = new ItemStack().of(nbttagcompound);
            	inventory.setItem(j, stack);
            }
        }
    }
}
