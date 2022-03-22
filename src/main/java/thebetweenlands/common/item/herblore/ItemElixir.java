package thebetweenlands.common.item.herblore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.entity.projectiles.EntityElixir;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

public class ItemElixir extends Item implements ITintedItem, ItemRegistry.IBlockStateItemModelDefinition {
	private final List<ElixirEffect> effects = new ArrayList<>();

	public ItemElixir() {
		this.effects.addAll(ElixirEffectRegistry.getEffects());

		this.setCreativeTab(BLCreativeTabs.HERBLORE);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);

	}

	private ElixirEffect getElixirByID(int id) {
		for(ElixirEffect effect : this.effects) {
			if(id == effect.getID()) return effect;
		}
		return null;
	}

	public ElixirEffect getElixirFromItem(ItemStack stack) {
		return this.getElixirByID(stack.getItemDamage() / 2);
	}

	@Override
	public int getColorMultiplier(ItemStack stack, int tintIndex) {
		if (tintIndex <= 0) {
			ElixirEffect effect = this.getElixirFromItem(stack);
			if (effect != null) {
				ElixirRecipe recipe = ElixirRecipes.getFromEffect(effect);
				if (recipe != null) {
					return recipe.infusionFinishedColor;
				}
			}
		}
		return -1;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (ElixirEffect effect : this.effects) {
				ElixirRecipe recipe = ElixirRecipes.getFromEffect(effect);
				if(recipe != null) {
					int baseDuration = effect.isAntiInfusion() ? recipe.negativeBaseDuration : recipe.baseDuration;
					int durationModifier = effect.isAntiInfusion() ? recipe.negativeDurationModifier : recipe.durationModifier;
					items.add(this.getElixirItem(effect, baseDuration, ElixirEffect.VIAL_INFUSION_MAX_POTENCY - 1, 0));
					items.add(this.getElixirItem(effect, baseDuration + MathHelper.floor(durationModifier / 6.0F * 5.0F), 0, 0));
					items.add(this.getElixirItem(effect, baseDuration, ElixirEffect.VIAL_INFUSION_MAX_POTENCY - 1, 1));
					items.add(this.getElixirItem(effect, baseDuration + MathHelper.floor(durationModifier / 6.0F * 5.0F), 0, 1));
				}
			}
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		try {
			return "item.thebetweenlands." + this.getElixirFromItem(stack).getEffectName();
		} catch (Exception e) {
			return "item.thebetweenlands.unknown";
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if(I18n.canTranslate(stack.getTranslationKey() + ".name")) {
			return I18n.translateToLocalFormatted(stack.getTranslationKey() + ".name", TranslationHelper.translateToLocal(this.getElixirFromItem(stack).getEffectName()));
		}
		return I18n.translateToLocalFormatted("item.thebetweenlands.bl.elixir.name", TranslationHelper.translateToLocal(this.getElixirFromItem(stack).getEffectName()));
	}

	@Override
	public UseAction getItemUseAction(ItemStack stack) {
		if(stack.getTag() != null && stack.getTag().contains("throwing") && stack.getTag().getBoolean("throwing")) {
			return UseAction.BOW;
		}
		return UseAction.DRINK;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {
		if(stack.getTag() != null && stack.getTag().contains("throwing") && stack.getTag().getBoolean("throwing")) {
			world.playSound((PlayerEntity)entityLiving, entityLiving.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!world.isClientSide()) {
				int useCount = this.getMaxItemUseDuration(stack) - timeLeft;
				EntityElixir elixir = new EntityElixir(world, entityLiving, stack);
				float strength = Math.min(0.2F + useCount / 20.0F, 1.0F);
				elixir.shoot(entityLiving, ((PlayerEntity)entityLiving).xRot, ((PlayerEntity)entityLiving).yRot, -20.0F, strength, 1.0F);
				world.spawnEntity(elixir);
				
				if (!((PlayerEntity)entityLiving).isCreative()) {
					stack.shrink(1);
					if(stack.isEmpty()) {
						((PlayerEntity) entityLiving).inventory.deleteStack(stack);
					}
				}
			}
		}
	}

	/**
	 * Creates an item stack with the specified effect, duration, strength and vial type.
	 * Vial types: 0 = green, 1 = orange
	 * @param effect
	 * @param duration
	 * @param strength
	 * @param vialType
	 * @return
	 */
	public ItemStack getElixirItem(ElixirEffect effect, int duration, int strength, int vialType) {
		ItemStack elixirStack = new ItemStack(this, 1, effect.getID() * 2 + vialType);
		CompoundNBT elixirData = new CompoundNBT();
		elixirData.putInt("duration", duration);
		elixirData.putInt("strength", strength);
		if(elixirStack.getTag() == null) elixirStack.setTag(new CompoundNBT());
		elixirStack.getTag().setTag("elixirData", elixirData);
		return elixirStack;
	}

	public EnumDentrothyst getDentrothystType(ItemStack stack) {
		return stack.getMetadata() % 2 == 0 ? EnumDentrothyst.GREEN : EnumDentrothyst.ORANGE;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		if(stack.getTag() != null && stack.getTag().contains("throwing") && stack.getTag().getBoolean("throwing")) {
			return 100000;
		}
		return 32;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		if(stack.getTag() == null) {
			stack.setTag(new CompoundNBT());
		}
		stack.getTag().putBoolean("throwing", playerIn.isCrouching());
		playerIn.setActiveHand(handIn);
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entityLiving) {
		PlayerEntity entityplayer = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
		if (entityplayer == null || !entityplayer.isCreative()) {
			stack.shrink(1);
		}

		if (entityplayer instanceof ServerPlayerEntity) {
			CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)entityplayer, stack);
		}

		if (!world.isClientSide()) {
			ElixirEffect effect = this.getElixirFromItem(stack);
			int duration = this.getElixirDuration(stack);
			int strength = this.getElixirStrength(stack);
			entityplayer.addEffect(effect.createEffect(duration == -1 ? 1200 : duration, strength == -1 ? 0 : strength));
		}

		if (entityplayer != null) {
			entityplayer.addStat(StatList.getObjectUseStats(this));
		}

		//Add empty dentrothyst vial
		if (entityplayer == null || !entityplayer.isCreative()) {
			if (stack.isEmpty()) {
				return ItemRegistry.DENTROTHYST_VIAL.createStack(stack.getItemDamage() % 2 == 0 ? 1 : 2);
			}
			if (entityplayer != null) {
				entityplayer.inventory.addItemStackToInventory(ItemRegistry.DENTROTHYST_VIAL.createStack(stack.getItemDamage() % 2 == 0 ? 1 : 2));
			}
		}

		return stack;
	}

	public void applyEffect(ItemStack stack, LivingEntity entity, double modifier) {
		ElixirEffect effect = this.getElixirFromItem(stack);
		int strength = this.getElixirStrength(stack);
		int duration = this.getElixirDuration(stack);
		entity.addEffect(effect.createEffect((int)(duration * modifier), strength));
	}

	public PotionEffect createPotionEffect(ItemStack stack, double modifier) {
		ElixirEffect effect = this.getElixirFromItem(stack);
		int strength = this.getElixirStrength(stack);
		int duration = this.getElixirDuration(stack);
		return effect.createEffect((int)(duration * modifier), strength);
	}

	public int getElixirDuration(ItemStack stack) {
		if(stack.getTag() != null && stack.getTag().contains("elixirData")) {
			CompoundNBT elixirData = stack.getTag().getCompoundTag("elixirData");
			return elixirData.getInt("duration");
		}
		return 1200;
	}

	public int getElixirStrength(ItemStack stack) {
		if(stack.getTag() != null && stack.getTag().contains("elixirData")) {
			CompoundNBT elixirData = stack.getTag().getCompoundTag("elixirData");
			return elixirData.getInt("strength");
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ElixirEffect elixirEffect = this.getElixirFromItem(stack);
		PotionEffect effect = this.createPotionEffect(stack, 1.0D);

		String potencyStr;
		if(I18n.canTranslate("bl.elixir.potency." + (effect.getAmplifier() + 1))) {
			potencyStr = I18n.translateToLocalFormatted("bl.elixir.potency." + (effect.getAmplifier() + 1));
		} else {
			potencyStr = I18n.translateToLocalFormatted("bl.elixir.potency.n", (effect.getAmplifier() + 1));
		}
		tooltip.add(I18n.translateToLocalFormatted("tooltip.bl.elixir.potency", potencyStr));

		int durationLevel;
		ElixirRecipe recipe = ElixirRecipes.getFromEffect(elixirEffect);
		if(recipe != null) {
			int baseDuration = elixirEffect.isAntiInfusion() ? recipe.negativeBaseDuration : recipe.baseDuration;
			int durationModifier = elixirEffect.isAntiInfusion() ? recipe.negativeDurationModifier : recipe.durationModifier;
			durationLevel = Math.max(0, MathHelper.floor((effect.getDuration() - baseDuration) / (float)durationModifier * ElixirEffect.VIAL_INFUSION_MAX_POTENCY));
		} else {
			durationLevel = MathHelper.floor(effect.getDuration() / 3600.0F);
		}
		String durationLevelStr;
		if(I18n.canTranslate("bl.elixir.duration." + (durationLevel + 1))) {
			durationLevelStr = I18n.translateToLocalFormatted("bl.elixir.duration." + (durationLevel + 1));
		} else {
			durationLevelStr = I18n.translateToLocalFormatted("bl.elixir.duration.n", (durationLevel + 1));
		}
		tooltip.add(I18n.translateToLocalFormatted("tooltip.bl.elixir.duration", durationLevelStr, StringUtils.ticksToElapsedTime(effect.getDuration()), effect.getDuration()));

		Potion potion = effect.getPotion();
		List<Tuple<String, AttributeModifier>> modifiers = Lists.<Tuple<String, AttributeModifier>>newArrayList();
		Map<IAttribute, AttributeModifier> modifersMap = potion.getAttributeModifierMap();
		if (!modifersMap.isEmpty()) {
			for (Entry<IAttribute, AttributeModifier> entry : modifersMap.entrySet()) {
				AttributeModifier modifier = entry.getValue();
				modifier = new AttributeModifier(modifier.getName(), potion.getAttributeModifierAmount(effect.getAmplifier(), modifier), modifier.getOperation());
				modifiers.add(new Tuple<>(((IAttribute)entry.getKey()).getName(), modifier));
			}
		}



		boolean hasEffectDescription = I18n.canTranslate("tooltip.bl." + elixirEffect.getEffectName() + ".effect");

		if (!modifiers.isEmpty() || hasEffectDescription) {
			tooltip.add("");
			tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocalFormatted("tooltip.bl.elixir.when_applied"));

			for (Tuple<String, AttributeModifier> tuple : modifiers) {
				AttributeModifier modifier = tuple.getSecond();
				double amount = modifier.getAmount();
				double adjustedAmount;

				if (modifier.getOperation() != 1 && modifier.getOperation() != 2) {
					adjustedAmount = modifier.getAmount();
				} else {
					adjustedAmount = modifier.getAmount() * 100.0D;
				}

				if (amount > 0.0D) {
					tooltip.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus." + modifier.getOperation(), ItemStack.DECIMALFORMAT.format(adjustedAmount), I18n.translateToLocalFormatted("attribute.name." + (String)tuple.getFirst())));
				} else if (amount < 0.0D) {
					adjustedAmount = adjustedAmount * -1.0D;
					tooltip.add(TextFormatting.RED + I18n.translateToLocalFormatted("attribute.modifier.take." + modifier.getOperation(), ItemStack.DECIMALFORMAT.format(adjustedAmount), I18n.translateToLocalFormatted("attribute.name." + (String)tuple.getFirst())));
				}
			}

			if(hasEffectDescription) {
				tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip." + elixirEffect.getEffectName() + ".effect"), 0));
			}
		}
	}

	@Override
	public Map<Integer, String> getVariants() {
		Map<Integer, String> variants = new HashMap<>();
		for (ElixirEffect effect : this.effects) {
			variants.put(effect.getID() * 2, "green");
			variants.put(effect.getID() * 2 + 1, "orange");
		}
		return variants;
	}
	
	@Override
	public boolean hasContainerItem() {
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return ItemRegistry.DENTROTHYST_VIAL.createStack(stack.getItemDamage() % 2 == 0 ? 1 : 2);
	}
}

