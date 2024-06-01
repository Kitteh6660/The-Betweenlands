package thebetweenlands.api.item;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.DimensionRegistry;
import thebetweenlands.common.registries.GameruleRegistry;
import thebetweenlands.util.NBTHelper;

public class CorrosionHelper {
	/**
	 * The number of corrosion stages, used for the item model overrides
	 */
	public static final int CORROSION_STAGE_COUNT = 6;

	/**
	 * The default NBT tag for corrosion used by BL items
	 */
	public static final String ITEM_CORROSION_NBT_TAG = "Corrosion";

	/**
	 * The default NBT tag for coating used by BL items
	 */
	public static final String ITEM_COATING_NBT_TAG = "Coating";

	/**
	 * Adds the corrosion property overrides to the specified item
	 * @param item
	 */
	public static void addCorrosionPropertyOverrides(Item item) {
		ItemProperties.register(item, new ResourceLocation("corrosion"), (stack, worldIn, entityIn, id) -> getCorrosionStage(stack));
		ItemProperties.register(item, new ResourceLocation("coating"), (stack, worldIn, entityIn, id) -> getCoatingStage(stack));
	}

	/**
	 * Returns a general modifier at the amount corrosion of the specified item
	 * @param stack
	 * @return
	 */
	public static float getModifier(ItemStack stack) {
		if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
			ICorrodible corrodible = (ICorrodible) stack.getItem();
			int corrosion = corrodible.getCorrosion(stack);
			int maxCorrosion = corrodible.getMaxCorrosion(stack);
			int oneStage = maxCorrosion / 6;
			return (-0.7F * Math.max(0, ((corrosion - oneStage) / (float)(maxCorrosion - oneStage))) + 1);
		}
		return 1.0F;
	}

	/**
	 * Returns the dig speed of an item at the amount of corrosion of the specified item
	 * @param normalStrength
	 * @param itemStack
	 * @param blockState
	 * @return
	 */
	public static float getDestroySpeed(float normalStrength, ItemStack itemStack, BlockState blockState) {
		return normalStrength * getModifier(itemStack);
	}

	/**
	 * Returns whether the block breaking should reset, excludes corrosion changes.
	 * Only works for the default NBT tag {@link #ITEM_CORROSION_NBT_TAG}
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return !(newStack.getItem() == oldStack.getItem() && areItemStackTagsEqual(newStack, oldStack) && (newStack.isDamageableItem() || newStack.getTag().equals(oldStack.getTag())));
	}

	/**
	 * Returns whether the item reequip animation should be played.
	 * Only works for the default NBT tag {@link #ITEM_CORROSION_NBT_TAG}
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !(newStack.getItem() == oldStack.getItem() && areItemStackTagsEqual(newStack, oldStack) && (newStack.isDamageableItem() || newStack.getTag().equals(oldStack.getTag())));
	}

	/**
	 * Returns whether the item stack NBT is the same, excludes corrosion changes.
	 * Only works for the default NBT tag {@link #ITEM_CORROSION_NBT_TAG}
	 * @param oldStack
	 * @param newStack
	 * @return
	 */
	public static boolean areItemStackTagsEqual(ItemStack oldStack, ItemStack newStack) {
		return NBTHelper.areItemStackTagsEqual(newStack, oldStack, ImmutableList.of(ITEM_CORROSION_NBT_TAG, ITEM_COATING_NBT_TAG));
	}

	/**
	 * Returns a map with the attack damage attribute affected by the corrosion multiplier
	 * @param map
	 * @param slot
	 * @param stack
	 * @param uuid
	 * @param damageVsEntity
	 * @return
	 */
	public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(Multimap<Attribute, AttributeModifier> map, EquipmentSlot slot, ItemStack stack, UUID uuid, float damageVsEntity) {
		if(slot == EquipmentSlot.MAINHAND) {
			map.removeAll(Attributes.ATTACK_DAMAGE);
			map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Tool modifier", damageVsEntity * getModifier(stack), AttributeModifier.Operation.ADDITION));
		}
		return map;
	}

	/**
	 * Returns whether corrosion is enabled
	 * @return
	 */
	public static boolean isCorrosionEnabled() {
		return GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_CORROSION) && BetweenlandsConfig.GENERAL.useCorrosion;
	}
	
	/**
	 * Updates the corrosion on the specified item
	 * @param stack
	 * @param world
	 * @param holder
	 * @param slot
	 * @param isHeldItem
	 */
	public static void updateCorrosion(ItemStack stack, Level world, Entity holder, EquipmentSlot slot, boolean isHeldItem) {
		if (world.isClientSide()) {
			return;
		}
		if (!world.isClientSide() && holder.level().dimensionTypeId() == DimensionRegistry.BETWEENLANDS && !(holder instanceof Player && ((Player)holder).isCreative())) {
			if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
				ICorrodible corrodible = (ICorrodible) stack.getItem();
				int corrosion = corrodible.getCorrosion(stack);
				if(!isCorrosionEnabled()) {
					if(corrosion != 0) {
						corrodible.setCorrosion(stack, 0);
					}
				} else if (corrosion < corrodible.getMaxCorrosion(stack)) {
					float probability = holder.isInWater() ? 0.0014F : 0.0007F;
					if (holder instanceof Player) {
						Player player = (Player) holder;
						probability *= (isHeldItem && !player.getMainHandItem().isEmpty() ? 2.8F : 1.0F);
						IDecayCapability cap = (IDecayCapability) player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
						if(cap != null) {
							float playerCorruption = cap.getDecayStats().getDecayLevel() / 20.0F;
							probability *= (1 - Math.pow(playerCorruption, 2) * 0.9F);
						}
					}
					if (world.random.nextFloat() < probability) {
						int coating = corrodible.getCoating(stack);
						if(coating > 0) {
							corrodible.setCoating(stack, coating - 1);
						} else {
							corrodible.setCorrosion(stack, corrosion + 1);
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the corrosion tooltips
	 * @param stack
	 * @param lines
	 * @param advancedItemTooltips
	 */
	public static void addCorrosionTooltips(ItemStack stack, List<Component> lines, boolean advancedItemTooltips) {
		if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
			ICorrodible corrodible = (ICorrodible) stack.getItem();
			
			if(isCorrosionEnabled()) {
				StringBuilder corrosionInfo = new StringBuilder("tooltip.bl.corrosion.");
				corrosionInfo.append(getCorrosionStage(stack));
				corrosionInfo.replace(0, corrosionInfo.length(), I18n.get(corrosionInfo.toString()));
				lines.add(Component.translatable(corrosionInfo.toString()));
				if (advancedItemTooltips) {
					lines.add(Component.literal(" (" + corrodible + "/" + corrodible.getMaxCorrosion(stack) + ")"));
				}
			}

			int coating = corrodible.getCoating(stack);
			if(coating > 0 || advancedItemTooltips) {
				StringBuilder coatingInfo = new StringBuilder("tooltip.bl.coated.");
				coatingInfo.append(getCoatingStage(stack));
				coatingInfo.replace(0, coatingInfo.length(), I18n.get(coatingInfo.toString()));
				lines.add(Component.translatable(coatingInfo.toString()));
				if (advancedItemTooltips) {
					lines.add(Component.literal(" (" + coating + "/" + corrodible.getMaxCoating(stack) + ")"));
				}
			}
		}
	}

	/**
	 * Returns the corrosion stage of the specified item. Ranges from [0, 5]
	 * @param stack
	 * @return
	 */
	public static int getCorrosionStage(ItemStack stack) {
		if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
			ICorrodible corrodible = (ICorrodible) stack.getItem();
			int maxCorrosion = corrodible.getMaxCorrosion(stack);
			int corrosion = corrodible.getCorrosion(stack);
			return Math.min(5, Mth.floor(corrosion / (float)maxCorrosion * 6));
		}
		return 0;
	}
	
	/**
	 * Returns the coating stage of the specified item. Ranges from [0, 5]
	 * @param stack
	 * @return
	 */
	public static int getCoatingStage(ItemStack stack) {
		if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
			ICorrodible corrodible = (ICorrodible) stack.getItem();
			int maxCoating = corrodible.getMaxCoating(stack);
			int coating = corrodible.getCoating(stack);
			return Math.min(5, Mth.floor(coating / (float)maxCoating * 6));
		}
		return 0;
	}
}
