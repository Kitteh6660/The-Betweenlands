package thebetweenlands.client.tab;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BLCreativeTabs
{
	public static final ItemGroup BLOCKS = new ItemGroup("thebetweenlands.block") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.SWAMP_GRASS.get());
		}
	};
	public static final ItemGroup ITEMS = new ItemGroup("thebetweenlands.item") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.SWAMP_TALISMAN_0.get());
		}
	};
	public static final ItemGroup GEARS = new ItemGroup("thebetweenlands.gear") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.VALONITE_PICKAXE.get());
		}
	};
	public static final ItemGroup SPECIALS = new ItemGroup("thebetweenlands.special") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.ASTATOS.get());
		}
	};
	public static final ItemGroup PLANTS = new ItemGroup("thebetweenlands.plants") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.MIRE_CORAL.get());
		}
	};
	public static final ItemGroup HERBLORE = new ItemGroup("thebetweenlands.herblore") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.GROUND_GENERIC_LEAF.get());
		}
	};
}
