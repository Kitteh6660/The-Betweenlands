package thebetweenlands.common.herblore.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import thebetweenlands.api.aspect.AspectItem;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.api.item.IDiscoveryProvider;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.ItemRegistry;

public class ManualManager {

    public static List<String> findablePagesHL = new ArrayList<>();
    public static List<String> findablePagesAll = new ArrayList<>();

    /**
     * Adds the nbt for a page to the page manual
     *
     * @param player     a player with a manual
     * @param pageName   a valid page name (one found in the arrays above)
     * @param itemManual either manualGuideBook or manualHL
     * @return
     */
    public static boolean findPage(PlayerEntity player, String pageName, Item itemManual) {
        if (pageName != null && player != null) {
            for (int i = -1; i < player.inventory.getContainerSize(); i++) {
                ItemStack stack;
                if (i >= 0)
                    stack = player.inventory.getItem(i);
                else
                    stack = player.getItemInHand(Hand.OFF_HAND);
                if (!stack.isEmpty() && stack.getItem() == itemManual) {
                    CompoundNBT nbt = stack.getTag();
                    if (nbt == null)
                        nbt = new CompoundNBT();
                    ArrayList<String> foundPages = getFoundPages(player, itemManual);
                    if (foundPages != null && !foundPages.contains(pageName)) {
                        ListNBT pages = new ListNBT();
                        for (String string : foundPages) {
                            CompoundNBT data = new CompoundNBT();
                            data.putString("page", string);
                            pages.appendTag(data);
                        }
                        CompoundNBT data = new CompoundNBT();
                        data.putString("page", pageName);
                        pages.appendTag(data);
                        nbt.put("pages", pages);
                    } else {
                        ListNBT pages = new ListNBT();
                        CompoundNBT data = new CompoundNBT();
                        data.putString("page", pageName);
                        pages.appendTag(data);
                        nbt.put("pages", pages);
                    }
                    player.inventory.getItem(i).setTag(nbt);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Gets all the found pages on a manual
     *
     * @param player     a player with a manual
     * @param itemManual either manualGuideBook or manualHL
     * @return a list of found pages
     */
    public static ArrayList<String> getFoundPages(PlayerEntity player, Item itemManual) {
        if (player != null) {
            ArrayList<String> foundPages = new ArrayList<>();
            for (int i = -1; i < player.inventory.getContainerSize(); i++) {
                ItemStack stack;
                if (i >= 0)
                    stack = player.inventory.getItem(i);
                else
                    stack = player.getItemInHand(Hand.OFF_HAND);
                if (!stack.isEmpty() && stack.getItem() == itemManual) {
                    CompoundNBT nbt = stack.getTag();
                    if (nbt != null) {
                        ListNBT tag = nbt.getList("pages", 10);
                        for (int j = 0; j < tag.size(); j++) {
                            CompoundNBT data = tag.getCompound(j);
                            foundPages.add(data.getString("page"));
                        }
                        return foundPages;
                    }
                }
            }
        }
        return null;
    }

    /**
     * checks if the manual in the inventory has found a specific page
     *
     * @param player     a player with a manual
     * @param page       a valid page name (one found in the arrays above)
     * @param itemManual either manualGuideBook or manualHL
     * @return whether or not the player has found the specific page or not
     */
    public static boolean hasFoundPage(PlayerEntity player, String page, Item itemManual) {
        ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
        if (!stack.isEmpty() && stack.getItem() == ItemRegistry.MANUAL_HL) {
            IDiscoveryProvider<ItemStack> provider = (IDiscoveryProvider<ItemStack>) stack.getItem();
            DiscoveryContainer<?> container = provider.getContainer(stack);
            ItemStack ingredient = ItemStack.EMPTY;
            Map<AspectItem, List<AspectManager.AspectItemEntry>> matchedAspects = AspectManager.getRegisteredItems();
            for (Map.Entry<AspectItem, List<AspectManager.AspectItemEntry>> e : matchedAspects.entrySet()) {
                if (e.getKey() != null) {
                    ItemStack itemStack = new ItemStack(e.getKey().getOriginal().getItem(), 1, e.getKey().getOriginal().getDamageValue());
                    if (itemStack.getTranslationKey().toLowerCase().replace(" ", "").equals(page)) {
                        ingredient = new ItemStack(e.getKey().getOriginal().getItem(), 1, e.getKey().getOriginal().getDamageValue());
                        break;
                    }
                }
            }
            if(!ingredient.isEmpty()) {
            	ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(ingredient, AspectManager.get(Minecraft.getInstance().world));
                return aspectContainer.getAspects(container).size() > 0;
            }
            return false;
        }


        return player != null && page != null && getFoundPages(player, itemManual) != null && getFoundPages(player, itemManual).contains(page);
    }
    
    public static boolean isFullyDiscovered(PlayerEntity player, String page) {
        ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
        
        if (!stack.isEmpty() && stack.getItem() == ItemRegistry.MANUAL_HL) {
            IDiscoveryProvider<ItemStack> provider = (IDiscoveryProvider<ItemStack>) stack.getItem();
            DiscoveryContainer<?> container = provider.getContainer(stack);
            ItemStack ingredient = ItemStack.EMPTY;
            Map<AspectItem, List<AspectManager.AspectItemEntry>> matchedAspects = AspectManager.getRegisteredItems();
            for (Map.Entry<AspectItem, List<AspectManager.AspectItemEntry>> e : matchedAspects.entrySet()) {
                if (e.getKey() != null) {
                    ItemStack itemStack = new ItemStack(e.getKey().getOriginal().getItem(), 1, e.getKey().getOriginal().getDamageValue());
                    if (itemStack.getTranslationKey().toLowerCase().replace(" ", "").equals(page)) {
                        ingredient = new ItemStack(e.getKey().getOriginal().getItem(), 1, e.getKey().getOriginal().getDamageValue());
                        break;
                    }
                }
            }
            if(!ingredient.isEmpty()) {
            	AspectItem aspectItem = AspectManager.getAspectItem(ingredient);
                return container.getDiscoveredStaticAspects(AspectManager.get(Minecraft.getInstance().world), aspectItem).size() == AspectManager.get(Minecraft.getInstance().world).getStaticAspects(aspectItem).size();
            }
            return false;
        }

        return false;
    }

    /**
     * Adds NBT to the book to allow a page to be opened
     *
     * @param player     a player with a manual
     * @param name       a valid page name (one found in the arrays above)
     * @param itemManual either manualGuideBook or manualHL
     */
    public static void playerDiscoverPage(PlayerEntity player, String name, Item itemManual) {
        if (!ManualManager.hasFoundPage(player, name, itemManual) && player != null && player.inventory.hasItemStack(new ItemStack(itemManual)) && !player.level.isClientSide()) {
            if (ManualManager.findPage(player, name, itemManual))
                player.displayClientMessage(new TranslationTextComponent("chat.manual.discover_page", new TranslationTextComponent("manual." + name + ".title")), true);
        }
    }

    /**
     * Sets the page you will be on when you open the book
     *
     * @param category   the category the player is on
     * @param pageNumber the number of the page the player is on
     * @param itemManual either manualGuideBook or manualHL
     * @param player     a player with a manual
     */
    public static void setCurrentPage(String category, int pageNumber, Item itemManual, PlayerEntity player, Hand hand) {
        if (player != null && !player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() == itemManual && category != null) {
            if (player.getItemInHand(hand).getTag() == null)
                player.getItemInHand(hand).setTag(new CompoundNBT());
            CompoundNBT tagCompound = player.getItemInHand(hand).getTag();
            tagCompound.putInt("page_number", pageNumber);
            tagCompound.putString("category", category);
            player.getItemInHand(Hand.MAIN_HAND).setTag(tagCompound);
        }
    }

    /**
     * Gets the pagenumber you where on last time you closed the manual, or -1 if the player is opening
     * the manual for the first time
     * @param manual
     * @return
     */
    public static int getCurrentPageNumber(ItemStack manual) {
        if (!manual.isEmpty())
            if (manual.getTag() != null && manual.getTag().contains("page_number"))
                return manual.getTag().getInt("page_number");
        return -1;
    }

    /**
     * Gets the category you where on last time you closed the manual
     * @param manual
     * @return returns a category
     */
    @Nullable
    public static ManualCategory getCurrentCategory(ItemStack manual) {
        if (!manual.isEmpty()) {
            CompoundNBT nbt = manual.getTag();
            if (nbt != null && nbt.contains("category")) {
                return getCategoryFromString(nbt.getString("category"), manual.getItem());
            }
        }
        return null;
    }

    /**
     * gets a category from a string
     *
     * @param categoryName the name of a category
     * @param itemManual   either manualGuideBook or manualHL
     * @return category
     */
    public static ManualCategory getCategoryFromString(String categoryName, Item itemManual) {
        if (itemManual == ItemRegistry.MANUAL_HL && categoryName != null)
            for (ManualCategory category : HLEntryRegistry.CATEGORIES)
                if (category.getName().equals(categoryName))
                    return category;
        return null;
    }
}
