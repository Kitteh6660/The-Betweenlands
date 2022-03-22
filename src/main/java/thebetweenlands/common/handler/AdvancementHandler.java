package thebetweenlands.common.handler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.api.distmarker.Dist;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.item.misc.ItemGem;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemShimmerStone;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

import java.util.List;

public class AdvancementHandler {

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() == LogicalSide.SERVER && event.getPlayer() instanceof ServerPlayerEntity) {
            ItemStack stack = event.getItemStack();
            BlockState state = event.getWorld().getBlockState(event.getPos());
            AdvancementCriterionRegistry.CLICK_BLOCK.trigger(stack, (ServerPlayerEntity) event.getPlayer(), event.getPos(), state, event.getFace());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if(player != null && !event.getWorld().isClientSide() && player instanceof ServerPlayerEntity) {
            AdvancementCriterionRegistry.BREAK_BLOCK.trigger((ServerPlayerEntity) player, event.getPos(), event.getState());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (event.phase == TickEvent.Phase.END && player instanceof ServerPlayerEntity && player.tickCount % 50 == 0) {
            AdvancementCriterionRegistry.HAS_ADVANCEMENT.trigger((ServerPlayerEntity) player);
        }
    }

    @SubscribeEvent
    public static void onItemCrafting(PlayerEvent.ItemCraftedEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().level.isClientSide() && event.getInventory() instanceof CraftingInventory) {
            if (CircleGemHelper.getGem(event.getCrafting()) != CircleGemType.NONE) {
                boolean hadGem = false;
                for (int i = 0; i < event.getInventory().getContainerSize(); ++i) {
                    ItemStack stack = event.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        if (stack.getItem() instanceof ItemGem) {
                            hadGem = true;
                            break;
                        }
                    }
                }
                if (hadGem) {
                    ServerPlayerEntity playerMP = getActivePlayer((CraftingInventory) event.getInventory());
                    if (playerMP != null) {
                        AdvancementCriterionRegistry.MIDDLE_GEM_UPGRADE.trigger(playerMP);
                    }
                }
            } else if (event.getCrafting().getItem() instanceof ICorrodible) {
                boolean hadScabyst = false;
                for (int i = 0; i < event.getInventory().getContainerSize(); ++i) {
                    ItemStack stack = event.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        if(ItemMisc.EnumItemMisc.SCABYST.isItemOf(stack)) {
                            hadScabyst = true;
                            break;
                        }
                    }
                }
                if (hadScabyst) {
                    ServerPlayerEntity playerMP = getActivePlayer((CraftingInventory) event.getInventory());
                    if (playerMP != null) {
                        AdvancementCriterionRegistry.COAT_TOOL.trigger(playerMP);
                    }
                }

            }
        }
    }

    private static ServerPlayerEntity getActivePlayer(CraftingInventory crafting) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(server != null) {
            PlayerList manager = server.getPlayerList();
            if(manager != null) {
                Container container = crafting.eventHandler;
                if(container == null) {
                    return null;
                }
                for (ServerPlayerEntity entityPlayerMP : manager.getPlayers()) {
                    if (entityPlayerMP.openContainer == container && container.canInteractWith(entityPlayerMP) && container.getCanCraft(entityPlayerMP)) {
                        return entityPlayerMP;
                    }
                }
            }
        }
        return null;
    }
}
