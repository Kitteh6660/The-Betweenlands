package thebetweenlands.common.herblore.rune;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.runechain.chain.IRuneChainData;
import thebetweenlands.api.runechain.container.IRuneChainContainerData;
import thebetweenlands.common.inventory.container.runeweavingtable.RuneChainContainerData;

public class RuneChainData implements IRuneChainData {
	private final NonNullList<ItemStack> runes;
	private final RuneChainContainerData containerData;
	
	public RuneChainData(NonNullList<ItemStack> runes, IRuneChainContainerData containerData) {
		this.runes = NonNullList.withSize(runes.size(), ItemStack.EMPTY);
		for(int i = 0; i < runes.size(); i++) {
			this.runes.set(i, runes.get(i).copy());
		}

		this.containerData = RuneChainContainerData.load(RuneChainContainerData.save(containerData, new CompoundNBT()));
	}

	public static CompoundNBT save(IRuneChainData data, CompoundNBT nbt) {
		nbt.put("container", RuneChainContainerData.save(data.getContainerData(), new CompoundNBT()));

		ListNBT itemsNbtList = new ListNBT();
		NonNullList<ItemStack> runes = data.getRuneItems();
		for (int i = 0; i < runes.size(); i++) {
			if (!runes.get(i).isEmpty()) {
				CompoundNBT itemTag = new CompoundNBT();
				itemTag.putInt("Slot", i);
				runes.get(i).save(itemTag);
				itemsNbtList.add(itemTag);
			}
		}
		nbt.put("Items", itemsNbtList);
		nbt.putInt("Size", runes.size());

		return nbt;
	}

	public static RuneChainData load(CompoundNBT nbt) {
		int size = nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : 0;

		NonNullList<ItemStack> runes = NonNullList.withSize(size, ItemStack.EMPTY);

		ListNBT itemsNbtList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < itemsNbtList.size(); i++) {
			CompoundNBT itemTag = itemsNbtList.getCompound(i);
			int slot = itemTag.getInt("Slot");
			if (slot >= 0 && slot < runes.size()) {
				runes.set(slot, new ItemStack(itemTag));
			}
		}

		RuneChainContainerData containerData = RuneChainContainerData.load(nbt.getCompound("container"));

		return new RuneChainData(runes, containerData);
	}

	@Override
	public IRuneChainContainerData getContainerData() {
		return this.containerData;
	}

	@Override
	public NonNullList<ItemStack> getRuneItems() {
		return this.runes;
	}
}
