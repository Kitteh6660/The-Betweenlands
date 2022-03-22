package thebetweenlands.common.item.food;

import javax.annotation.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemTangledRoot extends Item {
	public ItemTangledRoot() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand hand) {
		if (player.canEat(true)) {
			player.setActiveHand(hand);
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, player.getItemInHand(hand));
		} else {
			return new ActionResult<ItemStack>(ActionResultType.FAIL, player.getItemInHand(hand));
		}
	}

	@Override
	public UseAction getItemUseAction(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	@Nullable
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		stack.shrink(1);

		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity entityplayer = (PlayerEntity)entityLiving;
			worldIn.playSound((PlayerEntity)null, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			if (!worldIn.isClientSide()) {
				entityplayer.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
			}
			entityplayer.addStat(StatList.getObjectUseStats(this));
		}

		return stack;
	}
}
