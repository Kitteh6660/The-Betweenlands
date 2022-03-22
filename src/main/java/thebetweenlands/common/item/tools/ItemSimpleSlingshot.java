package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.controller.MovementController.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityBetweenstonePebble;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemSimpleSlingshot extends Item implements ICorrodible, IAnimatorRepairable {
	
	public ItemSimpleSlingshot(Properties properties) {
		super(properties);
		/*maxStackSize = 1;
		setMaxDamage(64);
		setCreativeTab(BLCreativeTabs.GEARS);*/
		CorrosionHelper.addCorrosionPropertyOverrides(this);

		//TODO Add pulling sprites
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
				if (entityIn == null) {
					return 0.0F;
				} else {
					return entityIn.getActiveItemStack().getItem() != ItemRegistry.SIMPLE_SLINGSHOT ? 0.0F : (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
				}
			}
		});
		this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});
		BowItem
	}

	protected ItemStack findAmmo(PlayerEntity player) {
		if (isSlingShotAmmo(player.getItemInHand(Hand.OFF_HAND))) {
			return player.getItemInHand(Hand.OFF_HAND);
		} else if (isSlingShotAmmo(player.getItemInHand(Hand.MAIN_HAND))) {
			return player.getItemInHand(Hand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
				ItemStack stack = player.inventory.getItem(i);
				if (isSlingShotAmmo(stack)) {
					return stack;
				}
			}
			return ItemStack.EMPTY;
		}
	}

	protected boolean isSlingShotAmmo(ItemStack stack) {
		return !stack.isEmpty() && EnumItemMisc.BETWEENSTONE_PEBBLE.isItemOf(stack);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityLiving;
			boolean infinite = player.isCreative() || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
			ItemStack ammo = findAmmo(player);

			int usedTicks = getMaxItemUseDuration(stack) - timeLeft;
			usedTicks = ForgeEventFactory.onArrowLoose(stack, world, (PlayerEntity) entityLiving, usedTicks, !ammo.isEmpty() || infinite);

			if (usedTicks < 0)
				return;

			if (!ammo.isEmpty() || infinite)
				if (ammo.isEmpty())
					ammo = new ItemStack(EnumItemMisc.BETWEENSTONE_PEBBLE.getItem());

			float strength = getAmmoVelocity(usedTicks);

			strength *= CorrosionHelper.getModifier(stack);

			if (strength >= 0.1F) {
				if (!world.isClientSide()) {
					ItemMisc itemAmmo = (ItemMisc) ammo.getItem();
					EntityBetweenstonePebble pebble = createAmmo(world, ammo, player);
					pebble.shoot(player, player.xRot, player.yRot, 0.0F, strength * 3.0F, 1.0F);

					if (strength == 1.0F)
						pebble.setIsCritical(true);

					int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);

					if (j > 0)
						pebble.setDamage(pebble.getDamage() + (double) j * 0.5D + 0.5D);

					int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);

					if (k > 0)
						pebble.setKnockbackStrength(k);

					if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0)
						pebble.setSecondsOnFire(100);

					stack.hurtAndBreak(1, player, (entity) -> {
						entity.broadcastBreakEvent(player.getUsedItemHand());
					});
					fireAmmo(player, stack, pebble, strength);
				}

				world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.SLINGSHOT_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + strength * 0.5F);

				if (!infinite)
					ammo.shrink(1);

				if (ammo.getCount() == 0) {
					player.inventory.removeItem(ammo);
				}
				player.awardStat(StatList.getObjectUseStats(this));
			}
		}
	}

	public EntityBetweenstonePebble createAmmo(World world, ItemStack stack, LivingEntity shooter) {
		EntityBetweenstonePebble pebble = new EntityBetweenstonePebble(world, shooter);
		return pebble;
	}

	protected void fireAmmo(PlayerEntity player, ItemStack stack, EntityBetweenstonePebble ammo, float strength) {
		player.level.addFreshEntity(ammo);
	}

	public static float getAmmoVelocity(int charge) {
		float strength = (float) charge / 20.0F;
		strength = (strength * strength + strength * 2.0F) / 3.0F * 1.15F;
		if (strength > 1.0F)
			strength = 1.0F;
		return strength;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 100000;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack itemstack = playerIn.getItemInHand(hand);
		boolean flag = !findAmmo(playerIn).isEmpty();
		ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, hand, flag);
		if (ret != null)
			return ret;
		if (!playerIn.isCreative() && !flag) {
			return new ActionResult<>(ActionResultType.FAIL, itemstack);
		} else {
			playerIn.setActiveHand(hand);
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
		}
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return CorrosionHelper.getDestroySpeed(super.getDestroySpeed(stack, state), stack, state);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.YELLOW + new TranslationTextComponent("tooltip.bl.simple_slingshot").getFormattedText());
		CorrosionHelper.addCorrosionTooltips(stack, tooltip, flagIn.isAdvanced());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onUpdateFov(FOVUpdateEvent event) {
		ItemStack activeItem = event.getEntity().getActiveItemStack();
		if (!activeItem.isEmpty() && activeItem.getItem() instanceof ItemSimpleSlingshot) {
			int usedTicks = activeItem.getItem().getMaxItemUseDuration(activeItem) - event.getEntity().getItemInUseCount();
			float strength = (float) usedTicks / 20.0F;
			strength = (strength * strength + strength * 2.0F) / 3.0F * 1.15F;
			if (strength > 1.0F) {
				strength = 1.0F;
			}
			event.setNewfov(1.0F - strength * 0.25F);
		}
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(BLMaterialRegistry.TOOL_WEEDWOOD);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(BLMaterialRegistry.TOOL_WEEDWOOD);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(BLMaterialRegistry.TOOL_WEEDWOOD);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(BLMaterialRegistry.TOOL_WEEDWOOD);
	}
}
