package thebetweenlands.common.item.herblore.rune;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.item.IRuneItem;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.api.runechain.rune.RuneCategory;
import thebetweenlands.api.runechain.rune.RuneTier;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRune extends Item implements ITintedItem, ItemRegistry.IMultipleItemModelDefinition, IRuneItem {
	
	private static final Map<Triple<Integer, Integer, IAspectType>, RuneItemProperties> REGISTRY = new HashMap<>();

	private static final String NBT_ASPECT_TYPE = "thebetweenlands.rune.aspect_type";

	public final ResourceLocation material;

	public static abstract class RuneItemProperties {
		public abstract IRuneContainerFactory getFactory(ItemStack stack);

		/**
		 * See {@link Item#onItemRightClick(World, PlayerEntity, Hand)}
		 * @param worldIn
		 * @param playerIn
		 * @param handIn
		 * @return
		 */
		public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
			return ActionResult.newResult(ActionResultType.PASS, playerIn.getItemInHand(handIn));
		}

		/**
		 * See {@link Item#onItemUse(PlayerEntity, World, BlockPos, Hand, Direction, float, float, float)}
		 * @param player
		 * @param worldIn
		 * @param pos
		 * @param hand
		 * @param facing
		 * @param hitX
		 * @param hitY
		 * @param hitZ
		 * @return
		 */
		public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
			return ActionResultType.PASS;
		}

		/**
		 * See {@link Item#getItemUseAction(ItemStack)}
		 * @param stack
		 * @return
		 */
		public UseAction getItemUseAction(ItemStack stack) {
			return UseAction.NONE;
		}

		/**
		 * See {@link Item#onUsingTick(ItemStack, LivingEntity, int)}
		 * @param stack
		 * @param player
		 * @param count
		 */
		public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
		}

		/**
		 * See {@link Item#onPlayerStoppedUsing(ItemStack, World, LivingEntity, int)}
		 * @param stack
		 * @param worldIn
		 * @param entityLiving
		 * @param timeLeft
		 */
		public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		}

		/**
		 * See {@link Item#getMaxItemUseDuration(ItemStack)}
		 * @param stack
		 * @return
		 */
		public int getMaxItemUseDuration(ItemStack stack) {
			return 0;
		}

		/**
		 * See {@link ItemRune#onUpdate(ItemStack, World, Entity, int, boolean)}
		 * @param stack
		 * @param worldIn
		 * @param entityIn
		 * @param itemSlot
		 * @param isSelected
		 */
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		}

		/**
		 * See {@link Item#itemInteractionForEntity(ItemStack, PlayerEntity, LivingEntity, Hand)}
		 * @param stack
		 * @param playerIn
		 * @param target
		 * @param hand
		 * @return
		 */
		public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
			return false;
		}

		/**
		 * See {@link Item#appendHoverText(ItemStack, World, List, ITooltipFlag)}
		 * @param stack
		 * @param worldIn
		 * @param tooltip
		 * @param flagIn
		 */
		public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		}

		/**
		 * See {@link Item#initCapabilities(ItemStack, CompoundNBT)}
		 * @param stack
		 * @param nbt
		 * @return
		 */
		public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
			return null;
		}
	}

	public ItemRune(ResourceLocation material) {
		this.material = material;
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(BLCreativeTabs.HERBLORE);

		this.addPropertyOverride(new ResourceLocation("infused"), (stack, worldIn, entityIn) -> {
			return this.getInfusedAspect(stack) != null ? 1.0f : 0.0f;
		});

		this.addPropertyOverride(new ResourceLocation("category"), (stack, worldIn, entityIn) -> {
			return this.getCategory(stack);
		});

		this.addPropertyOverride(new ResourceLocation("tier"), (stack, worldIn, entityIn) -> {
			return this.getTier(stack);
		});
	}

	public static void register(RuneCategory category, RuneTier tier, IAspectType type, RuneItemProperties properties) {
		REGISTRY.put(Triple.of(category.id, tier.id, type), properties);
	}

	@Nullable
	public static RuneItemProperties getProperties(RuneCategory category, RuneTier tier, IAspectType type) {
		return REGISTRY.get(Triple.of(category.id, tier.id, type));
	}

	@Nullable
	public static RuneItemProperties getProperties(int category, int tier, IAspectType type) {
		return REGISTRY.get(Triple.of(category, tier, type));
	}

	public RuneItemProperties getProperties(ItemStack stack) {
		return getProperties(this.getCategory(stack), this.getTier(stack), this.getInfusedAspect(stack));
	}

	@Override
	public ItemStack infuse(ItemStack stack, IAspectType type, RuneTier tier) {
		ItemStack infused = stack.copy();
		infused.setItemDamage(this.getCategory(stack) * RuneTier.COUNT + tier.id);
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(infused);
		nbt.setTag(NBT_ASPECT_TYPE, type.save(new CompoundNBT()));
		return infused;
	}

	public ItemStack carve(RuneCategory category) {
		return new ItemStack(this, 1, category.id * RuneTier.COUNT);
	}

	public int getCategory(ItemStack stack) {
		return stack.getItemDamage() / RuneTier.COUNT;
	}

	public int getTier(ItemStack stack) {
		return stack.getItemDamage() % RuneTier.COUNT;
	}

	@Override
	public IAspectType getInfusedAspect(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			if(nbt.contains(NBT_ASPECT_TYPE, Constants.NBT.TAG_COMPOUND)) {
				return IAspectType.readFromNBT(nbt.getCompoundTag(NBT_ASPECT_TYPE));
			}
		}
		return null;
	}

	@Override
	public void getSubItems(ItemGroup tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			//Testing
			/*for(RuneCategory category : RuneCategory.VALUES) {
				items.add(this.carve(category));

				for(RuneTier tier : RuneTier.VALUES) {
					for(IAspectType aspect : AspectRegistry.ASPECT_TYPES) {
						items.add(this.infuse(this.carve(category), aspect, tier));
					}
				}
			}*/

			Set<RuneCategory> usedCategories = new HashSet<>();
			for(Triple<Integer, Integer, IAspectType> key : REGISTRY.keySet()) {
				usedCategories.add(RuneCategory.fromId(key.getLeft()));
			}
			for(RuneCategory category : usedCategories) {
				items.add(this.carve(category));
			}

			for(Triple<Integer, Integer, IAspectType> key : REGISTRY.keySet()) {
				items.add(this.infuse(this.carve(RuneCategory.fromId(key.getLeft())), key.getRight(), RuneTier.fromId(key.getMiddle())));
			}
		}
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		ResourceLocation regName = this.getRegistryName();
		Map<Integer, ResourceLocation> models = new HashMap<>();
		for(RuneCategory category : RuneCategory.values()) {
			for(RuneTier tier : RuneTier.values()) {
				models.put(category.id * RuneTier.COUNT + tier.id, new ResourceLocation(regName.getNamespace(), String.format("%s_%s", regName.getPath(), category.name)));
			}
		}
		return models;
	}

	@Override
	public int getColorMultiplier(ItemStack stack, int tintIndex) {
		if(tintIndex == 1) {
			IAspectType aspect = this.getInfusedAspect(stack);
			return aspect != null ? aspect.getColor() : 0xFFFFFFFF;
		}
		return 0xFFFFFFFF;
	}

	@Override
	public String getTranslationKey() {
		return "item.thebetweenlands.unknown_rune";
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return String.format("item.%s.%s_rune.%s", this.getRegistryName().getNamespace(), RuneCategory.fromId(this.getCategory(stack)).name, this.getInfusedAspect(stack) != null ? "infused" : "carved");
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		IAspectType aspect = this.getInfusedAspect(stack);

		if(aspect != null) {
			String runeName;

			RuneItemProperties properties = this.getProperties(stack);

			if(properties != null) {
				IRuneContainerFactory factory = properties.getFactory(stack);
				runeName = I18n.get(String.format("rune.%s.%s.name", factory.getId().getNamespace(), factory.getId().getPath()));
			} else {
				runeName = I18n.get("rune.thebetweenlands.no_effect.name");
			}

			return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", runeName).trim();
		} else {
			return super.getItemStackDisplayName(stack);
		}
	}

	@Override
	public RuneCategory getRuneCategory(ItemStack stack) {
		return RuneCategory.fromId(this.getCategory(stack));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String runeMaterial = I18n.get(String.format("rune_material.%s.%s.name", this.material.getNamespace(), this.material.getPath()));

		IAspectType aspect = this.getInfusedAspect(stack);

		if(aspect != null) {
			String runeName;

			RuneItemProperties properties = this.getProperties(stack);

			if(properties != null) {
				IRuneContainerFactory factory = properties.getFactory(stack);
				runeName = I18n.get(String.format("rune.%s.%s.name", factory.getId().getNamespace(), factory.getId().getPath()));
			} else {
				runeName = I18n.get("rune.thebetweenlands.no_effect.name");
			}

			String aspectName = this.getInfusedAspect(stack).getName();
			String tierName = I18n.get(String.format("rune_tier.%s.name", RuneTier.fromId(this.getTier(stack)).name));

			tooltip.addAll(ItemTooltipHandler.splitTooltip(
					I18n.translateToLocalFormatted(
							String.format("tooltip.%s.%s_rune.infused", this.getRegistryName().getNamespace(), RuneCategory.fromId(this.getCategory(stack)).name),
							runeMaterial, aspectName, tierName, runeName
							).trim(), 0));

			if(properties != null) {
				properties.appendHoverText(stack, worldIn, tooltip, flagIn);
			}
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(
					I18n.translateToLocalFormatted(
							String.format("tooltip.%s.%s_rune.carved", this.getRegistryName().getNamespace(), RuneCategory.fromId(this.getCategory(stack)).name),
							runeMaterial
							).trim(), 0));
		}
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		RuneItemProperties properties = this.getProperties(stack);
		if(properties != null) {
			return properties.onItemRightClick(worldIn, playerIn, handIn);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		RuneItemProperties properties = this.getProperties(stack);
		if(properties != null) {
			return properties.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public UseAction getItemUseAction(ItemStack stack) {
		RuneItemProperties properties = this.getProperties(stack);
		if(properties != null) {
			return properties.getItemUseAction(stack);
		}
		return super.getItemUseAction(stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
		RuneItemProperties properties = this.getProperties(stack);
		if(properties != null) {
			properties.onUsingTick(stack, player, count);
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		RuneItemProperties properties = this.getProperties(stack);
		if(properties != null) {
			properties.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		RuneItemProperties properties = this.getProperties(stack);
		if(properties != null) {
			return properties.getMaxItemUseDuration(stack);
		}
		return super.getMaxItemUseDuration(stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		RuneItemProperties properties = this.getProperties(stack);
		if(properties != null) {
			properties.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		}
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
		RuneItemProperties properties = this.getProperties(stack);
		if(properties != null) {
			properties.itemInteractionForEntity(stack, playerIn, target, hand);
		}
		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		// TODO Auto-generated method stub
		return super.initCapabilities(stack, nbt);
	}
}
