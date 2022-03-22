package thebetweenlands.common.item.food;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;

import javax.annotation.Nullable;

public class ItemTaintedPotion extends Item {
	public ItemTaintedPotion() {
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setCreativeTab(null);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAction getItemUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		ItemStack originalStack = this.getOriginalStack(stack);
		if(!originalStack.isEmpty() && originalStack.getItem() != Items.AIR) {
			return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", originalStack.getRarity().color + originalStack.getDisplayName() + TextFormatting.RESET).trim();
		}
		return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + "_empty.name").trim();
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
		playerIn.setActiveHand(hand);
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getItemInHand(hand));
	}

	@Override
	@Nullable
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
		PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;

		if (player == null || !player.isCreative()) {
			stack.shrink(1);
		}

		if (!world.isClientSide()) {
			player.addEffect(new PotionEffect(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect(), 180, 3));
			player.addEffect(new EffectInstance(Effects.POISON, 120, 2));
		}

		if (player != null) {
			player.awardStat(StatList.getObjectUseStats(this));
		}

		if (player == null || !player.isCreative()) {
			if (stack.getCount() <= 0) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}

			if (player != null) {
				player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return stack;
	}

	public void setOriginalStack(ItemStack stack, ItemStack originalStack) {
		stack.setTagInfo("originalStack", originalStack.save(new CompoundNBT()));
	}

	public ItemStack getOriginalStack(ItemStack stack) {
		return stack.getTag() != null ? new ItemStack(stack.getTag().getCompoundTag("originalStack")) : ItemStack.EMPTY;
	}
}
