package thebetweenlands.common.item.misc;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import thebetweenlands.util.NBTHelper;

public class ItemGlue extends Item {
	
	public ItemGlue() {
		this.setCreativeTab(null);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemStack = playerIn.getItemInHand(handIn);
		if (playerIn.canEat(true)) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStack);
		} else {
			return new ActionResult<ItemStack>(ActionResultType.FAIL, itemStack);
		}
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	@Nullable
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		stack.shrink(1);

		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity entityPlayer = (PlayerEntity)entityLiving;
			worldIn.playSound((PlayerEntity)null, entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
			if (!worldIn.isClientSide()) {
				entityPlayer.addEffect(new EffectInstance(Effects.CONFUSION, 200, 1));
				entityPlayer.addEffect(new EffectInstance(Effects.NIGHT_VISION, 200, 1));
				entityPlayer.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, 2));
				entityPlayer.addEffect(new EffectInstance(Effects.LEVITATION, 100, 0));
			}
			entityPlayer.addStat(StatList.getObjectUseStats(this));
		}

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(entityIn instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityIn;
			String uuidStr = player.getUUID().toString();
			if("ea341fd9-27d1-4ffe-a1e0-5b05a5c8a234".equals(uuidStr)) {
				CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
				nbt.putBoolean("mmm", true);
			}
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if(stack.getTag() != null && stack.getTag().getBoolean("mmm")) {
			return "Sniff.. sniff... Hmm, I like this stuff";
		}
		return super.getTranslationKey();
	}
}
