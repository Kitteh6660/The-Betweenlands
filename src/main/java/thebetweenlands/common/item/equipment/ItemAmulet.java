package thebetweenlands.common.item.equipment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.ICircleGemCapability;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGem;
import thebetweenlands.common.capability.circlegem.CircleGem.CombatType;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;
import thebetweenlands.common.entity.mobs.EntityEmberling;
import thebetweenlands.common.entity.mobs.EntityGiantToad;
import thebetweenlands.common.entity.mobs.EntityTamedSpiritTreeFace;
import thebetweenlands.common.entity.mobs.EntityTarminion;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.util.LightingUtil;
import thebetweenlands.util.NBTHelper;

public class ItemAmulet extends Item implements IEquippable 
{
	public static final Set<Class<? extends LivingEntity>> SUPPORTED_ENTITIES = new HashSet<>();

	static {
		SUPPORTED_ENTITIES.add(EntityTarminion.class);
		SUPPORTED_ENTITIES.add(EntityGiantToad.class);
		SUPPORTED_ENTITIES.add(EntityTamedSpiritTreeFace.class);
		SUPPORTED_ENTITIES.add(EntityEmberling.class);
		SUPPORTED_ENTITIES.add(EntityChiromawTame.class);
	}

	public ItemAmulet(Item.Properties properties) {
		super(properties);

		CircleGemHelper.addGemPropertyOverrides(this);
		IEquippable.addEquippedPropertyOverrides(this);
	}

	public static boolean canPlayerAddAmulet(PlayerEntity player, Entity target) {
		IPuppetCapability cap = (IPuppetCapability) target.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
		return SUPPORTED_ENTITIES.contains(target.getClass()) || (cap != null && cap.hasPuppeteer() && cap.getPuppeteer() == player);
	}

	/**
	 * Adds an amulet to the specified entity
	 *
	 * @param gem
	 * @param entity
	 * @param canUnequip
	 * @param canDrop
	 * @return True if successful
	 */
	public static boolean addAmulet(CircleGemType gem, Entity entity, boolean canUnequip, boolean canDrop) {
		ItemStack amulet = createStack(gem);

		CompoundNBT nbt = NBTHelper.getStackNBTSafe(amulet);
		nbt.putBoolean("canUnequip", canUnequip);
		nbt.putBoolean("canDrop", canDrop);

		ItemStack result = EquipmentHelper.equipItem(null, entity, amulet, false);

		return result.isEmpty() || result.getCount() != amulet.getCount();

	}

	/**
	 * Creates an amulet with the specified gem
	 *
	 * @param gem
	 * @return
	 */
	public static ItemStack createStack(CircleGemType gem) {
		ItemStack stack = new ItemStack(ItemRegistry.AMULET);
		CircleGemHelper.setGem(stack, gem);
		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderLiving(RenderLivingEvent.Specials.Post<LivingEntity> event) {
		if (event.getEntity() != null) {
			renderAmulet(event.getEntity(), event.getX(), event.getY(), event.getZ(), WorldRenderHandler.getPartialTicks());
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static void renderAmulet(LivingEntity entity, double x, double y, double z, float partialTicks) {
		if(entity instanceof PlayerEntity && ((PlayerEntity) entity).isSpectator()) {
			return;
		}

		IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if (cap != null) {
			IInventory inv = cap.getInventory(EnumEquipmentInventory.AMULET);
			List<ItemStack> items = new ArrayList<>(inv.getContainerSize());

			for (int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack stack = inv.getItem(i);
				if (!stack.isEmpty() && CircleGemHelper.getGem(stack) != CircleGemType.NONE) {
					items.add(stack);
				}
			}

			int amulets = items.size();

			if(amulets > 0) {
				float degOffset = 360.0F / amulets;
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);

				TextureManager textureManager = Minecraft.getInstance().getTextureManager();
				ItemRenderer ItemRenderer = Minecraft.getInstance().getRenderItem();

				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				ITextureObject texture = textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				texture.setBlurMipmap(false, false);

				int i = 0;
				for (ItemStack stack : items) {
					GlStateManager.rotate(degOffset, 0, 1, 0);

					CircleGemType gem = CircleGemHelper.getGem(stack);
					ItemStack gemItem = null;

					switch (gem) {
					case CRIMSON:
						gemItem = new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM);
						break;
					case AQUA:
						gemItem = new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM);
						break;
					case GREEN:
						gemItem = new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM);
						break;
					default:
					}

					if (gemItem != null) {
						IBakedModel model = ItemRenderer.getItemModelMesher().getItemModel(gemItem);

						GlStateManager.pushMatrix();
						GlStateManager.rotate((entity.tickCount + partialTicks) * 1.5F, 0, 1, 0);
						double eyeHeight = entity.getEyeHeight();
						GlStateManager.translate(0, eyeHeight / 1.5D + Math.sin((entity.tickCount + partialTicks) / 60.0D + (double) i / amulets * Math.PI * 2.0D) / 2.0D * entity.height / 4.0D, entity.width / 1.25D);
						GlStateManager.scale(0.25F * entity.height / 2.0D, 0.25F * entity.height / 2.0D, 0.25F * entity.height / 2.0D);
						GlStateManager.enableBlend();
						GlStateManager.color(1, 1, 1, 0.8F);
						GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

						LightingUtil.INSTANCE.setLighting(255);

						ItemRenderer.ItemRenderer(stack, model);

						LightingUtil.INSTANCE.revert();

						GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
						float scale = ((float) Math.cos(entity.tickCount / 5.0F) + 1.0F) / 15.0F + 1.05F;
						GlStateManager.scale(scale, scale, scale);
						GlStateManager.colorMask(false, false, false, false);

						ItemRenderer.ItemRenderer(stack, model);

						GlStateManager.colorMask(true, true, true, true);

						ItemRenderer.ItemRenderer(stack, model);

						GlStateManager.popMatrix();

						i++;
					}
				}

				GlStateManager.popMatrix();
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

				textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				texture.restoreLastBlurMipmap();
			}
		}
	}

	//TODO: Split this into different item registries to account for the Flattening.
	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubItems(ItemGroup tab, NonNullList<ItemStack> list) {
		if (this.isInCreativeTab(tab)) {
			list.add(createStack(CircleGemType.NONE));
			list.add(createStack(CircleGemType.AQUA));
			list.add(createStack(CircleGemType.CRIMSON));
			list.add(createStack(CircleGemType.GREEN));
		}
	}

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.AMULET;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, PlayerEntity player, Entity target) {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack, PlayerEntity player, Entity target) {
		if (CircleGemHelper.getGem(stack) == CircleGemType.NONE) {
			return false;
		}

		if(target instanceof PlayerEntity == false && player != null && !canPlayerAddAmulet(player, target)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean canUnequip(ItemStack stack, PlayerEntity player, Entity target, IInventory inventory) {
		return target == player || stack.getTag() == null || !stack.getTag().contains("canUnequip") || stack.getTag().getBoolean("canUnequip");
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
		return stack.getTag() == null || !stack.getTag().contains("canDrop") || stack.getTag().getBoolean("canDrop");
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
		ICircleGemCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null);
		if (cap != null) {
			cap.addGem(new CircleGem(CircleGemHelper.getGem(stack), CombatType.BOTH));
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		ICircleGemCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null);
		if (cap != null) {
			List<CircleGem> gems = cap.getGems();
			CircleGemType type = CircleGemHelper.getGem(stack);

			for (CircleGem gem : gems) {
				if (gem.getCombatType() == CombatType.BOTH && gem.getGemType() == type) {
					cap.removeGem(gem);
					break;
				}
			}
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.amulet." + CircleGemHelper.getGem(stack).name), 0));
		if(CircleGemHelper.getGem(stack) != CircleGemType.NONE) {
			if (Screen.hasShiftDown()) {
				list.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.amulet.usage", KeyBindRegistry.RADIAL_MENU.getDisplayName(), Minecraft.getInstance().gameSettings.keyBindUseItem.getDisplayName()), 1));
			} else {
				list.add(I18n.get("tooltip.bl.press.shift"));
			}
		}
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
