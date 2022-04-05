package thebetweenlands.common.item.equipment;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;

public class ItemLurkerSkinPouch extends Item implements IEquippable, IRenamableItem {
	
    public ItemLurkerSkinPouch(Properties properties) {
    	super(properties);
        /*this.setMaxStackSize(1);
        this.setCreativeTab(BLCreativeTabs.GEARS);
        this.setMaxDamage(3);
        this.setNoRepair();*/

        this.addPropertyOverride(new ResourceLocation("pouch_size"), (stack, worldIn, entityIn) -> stack.getDamageValue());
        IEquippable.addEquippedPropertyOverrides(this);
    }

    /**
     * Returns the first accessible pouch of the players equipment (first priority) or hotbar
     *
     * @param player
     * @return
     */
    public static ItemStack getFirstPouch(PlayerEntity player) {
        IEquipmentCapability cap = (IEquipmentCapability) player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
        if (cap != null) {
            IInventory inv = cap.getInventory(EnumEquipmentInventory.MISC);

            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (!stack.isEmpty() && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH.get()) {
                    return stack;
                }
            }
        }

        PlayerInventory playerInventory = player.inventory;
        for (int i = 0; i < PlayerInventory.getSelectionSize(); i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH.get()) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
    
    @Override
    public boolean isValidRepairItem() {
    	return false;
    }
    
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    	return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        int slots = 9 + (stack.getDamageValue() * 9);
        list.add(TextFormatting.GRAY + I18n.get("tooltip.bl.lurker_skin_pouch.size", slots));
        list.add(I18n.get("tooltip.bl.lurker_skin_pouch.usage", KeyBindRegistry.OPEN_POUCH.getDisplayName()));
        if (stack.getDamageValue() < stack.getMaxDamage()) {
            list.add(I18n.get("tooltip.bl.lurker_skin_pouch.upgrade"));
        }
    }

    @Override
    public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, Direction side, BlockRayTraceResult hitResult, Hand hand) {
    	if(player.isCrouching()) {
    		ItemStack heldItem = player.getItemInHand(hand);
    		if(!heldItem.isEmpty() && heldItem.getItem() == this) {
    			if(!world.isClientSide()) {
	    			InventoryItem inventory = new InventoryItem(heldItem, 9 + (heldItem.getDamageValue() * 9), "Lurker Skin Pouch");
	    			TileEntity tile = world.getBlockEntity(pos);
	        		if(tile != null) {
	        			IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	        			if (itemHandler != null) {
                            for (int i = 0; i < inventory.getContainerSize(); i++) {
                                ItemStack stack = inventory.getItem(i);
                                if (!stack.isEmpty()) {
                                    stack = ItemHandlerHelper.insertItemStacked(itemHandler, stack, false);
                                    inventory.setItem(i, stack);
                                }
                            }
                        }
	        		}
    			}
        		return ActionResultType.SUCCESS;
    		}
    	}
    	return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            if (!player.isCrouching()) {
                player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH, world, 0, 0, 0);
            } else {
                player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == Hand.MAIN_HAND ? 0 : 1, 0, 0);
            }
        }

        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
    }

    @Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			ItemStack basePouch = new ItemStack(this);
            items.add(basePouch);
            items.add(new ItemStack(this, 1, basePouch.getMaxDamage()));
        }
	}
    
    private static boolean isRenderingWorld;
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors event) {
        isRenderingWorld = true;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        isRenderingWorld = false;
    }
    
	@OnlyIn(Dist.CLIENT)
    @SubscribeEvent
	public static void onPlayerRenderer(RenderLivingEvent.Specials.Post<LivingEntity> event) {
		if(event.getEntity() instanceof PlayerEntity) {
			renderPouch((PlayerEntity) event.getEntity(), event.getX(), event.getY(), event.getZ(), WorldRenderHandler.getPartialTicks());
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static void renderPouch(PlayerEntity player, double x, double y, double z, float partialTicks) {
        IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
        if(cap != null) {
			IInventory inv = cap.getInventory(EnumEquipmentInventory.MISC);
			ItemStack pouch = null;

			for(int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack stack = inv.getItem(i);
				if(stack != null && stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
					pouch = stack;
					break;
				}
			}

			if(pouch != null) {
				TextureManager textureManager = Minecraft.getInstance().getTextureManager();
				ItemRenderer ItemRenderer = Minecraft.getInstance().getRenderItem();

				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				ITextureObject texture = textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				texture.setBlurMipmap(false, false);

				IBakedModel model = ItemRenderer.getItemModelMesher().getItemModel(pouch);

				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + 1.0D, z);
				if(!isRenderingWorld) {
					GlStateManager.rotate(90 - player.renderYawOffset, 0, 1, 0);
				} else {
					GlStateManager.rotate(90 - (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks), 0, 1, 0);
				}
				GlStateManager.translate(player.isCrouching() ? 0.25D : 0.0D, (player.isCrouching() ? -0.15D : 0) - 0.2D, -0.25D);
				float limbSwingAmount = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialTicks;
				float swing = (float)Math.sin((player.limbSwing - limbSwingAmount * (1.0F - partialTicks)) / 1.4F) * limbSwingAmount;
				GlStateManager.rotate(swing * 25.0F, 0, 0, 1);
				GlStateManager.rotate(swing * 13.0F, 1, 0, 0);
				GlStateManager.rotate(swing * -10.0F, 0, 0, 1);
				GlStateManager.translate(0, -0.1D, 0);
				GlStateManager.enableBlend();
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 0.02D);
				GlStateManager.scale(0.4D, 0.4D, 0.5D);

				ItemRenderer.ItemRenderer(pouch, model);

				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.scale(0.37D, 0.37D, 0.5D);

				ItemRenderer.ItemRenderer(pouch, model);

				GlStateManager.popMatrix();

				GlStateManager.popMatrix();

				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				texture.restoreLastBlurMipmap();
			}
		}
	}

    @Override
    public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
        return EnumEquipmentInventory.MISC;
    }

    @Override
    public boolean canEquipOnRightClick(ItemStack stack, PlayerEntity player, Entity target) {
        return false;
    }

    @Override
    public boolean canEquip(ItemStack stack, PlayerEntity player, Entity target) {
        return target == player && EquipmentHelper.getEquipment(EnumEquipmentInventory.MISC, target, this).isEmpty();
    }

    @Override
    public boolean canUnequip(ItemStack stack, PlayerEntity player, Entity target, IInventory inventory) {
        return true;
    }

    @Override
    public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
        return true;
    }

    @Override
    public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
    }

    @Override
    public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
    }

    @Override
    public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
    }
}
