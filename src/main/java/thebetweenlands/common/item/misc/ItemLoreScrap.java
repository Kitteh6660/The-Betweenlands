package thebetweenlands.common.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ItemLoreScrap extends Item implements ItemRegistry.IMultipleItemModelDefinition {
	public static final String[] PAGE_NAMES = new String[]{"them", "mutants", "shadows", "ruins", "heads", "tar", "dungeon", "pitstone", "tower", "fort"};

	public ItemLoreScrap() {
		this.maxStackSize = 1;
		this.setCreativeTab(BLCreativeTabs.SPECIALS);

		this.addPropertyOverride(new ResourceLocation("page"), (stack, worldIn, entityIn) -> getPage(stack));
	}

	public static ItemStack createStack(int page) {
		ItemStack stack = new ItemStack(ItemRegistry.LORE_SCRAP);
		setPage(stack, page);
		return stack;
	}

	public static ItemStack createRandomStack(Random random) {
		ItemStack stack = new ItemStack(ItemRegistry.LORE_SCRAP);
		stack = setRandomPage(stack, random);
		return stack;
	}

	public static ItemStack setRandomPage(ItemStack stack, Random random) {
		setPage(stack, random.nextInt(PAGE_NAMES.length));
		return stack;
	}

	public static ItemStack setPage(ItemStack stack, int page) {
		if(page >= 0 && page < PAGE_NAMES.length) {
			stack.setItemDamage(page);
		}
		return stack;
	}

	public static int getPage(ItemStack stack) {
		int dmg = stack.getItemDamage();
		if(dmg >= 0 && dmg < PAGE_NAMES.length) {
			return dmg;
		}
		return -1;
	}

	@Nullable
	public static String getPageName(ItemStack stack) {
		int page = getPage(stack);
		if(page != -1) {
			return PAGE_NAMES[page];
		}
		return null;
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubItems( CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			for (int i = 0; i < PAGE_NAMES.length; i++) {
				ItemStack stack = new ItemStack(this);
				setPage(stack, i);
				subItems.add(stack);
			}
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		String pageName = getPageName(stack);
		if(pageName != null) {
			return super.getTranslationKey() + "." + pageName;
		}
		return super.getTranslationKey();
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack itemStackIn = playerIn.getItemInHand(hand);
		if (getPage(itemStackIn) != -1) {
			playerIn.openGui(TheBetweenlands.instance, CommonProxy.GUI_LORE, worldIn, hand == Hand.MAIN_HAND ? 0 : 1, 0, 0);
			return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);
		}
		return new ActionResult<>(ActionResultType.PASS, itemStackIn);
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		Map<Integer, ResourceLocation> map = new HashMap<Integer, ResourceLocation>();
		for(int i = 0; i < PAGE_NAMES.length; i++) {
			map.put(i, this.getRegistryName());
		}
		return map;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
