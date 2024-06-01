package thebetweenlands.api.item;

import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.runechain.rune.RuneCategory;

public interface IRuneletItem {
	public ItemStack carve(ItemStack stack, RuneCategory category);
}
