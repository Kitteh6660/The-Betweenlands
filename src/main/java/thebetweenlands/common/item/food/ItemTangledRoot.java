package thebetweenlands.common.item.food;

import javax.annotation.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemTangledRoot extends Item {
	
	public ItemTangledRoot(Properties properties) {
		super(properties);
		//this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
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
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	@Nullable
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		stack.shrink(1);

		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity entityplayer = (PlayerEntity)entityLiving;
			worldIn.playSound((PlayerEntity)null, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
			if (!worldIn.isClientSide()) {
				entityplayer.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
			}
			entityplayer.addStat(StatList.getObjectUseStats(this));
		}

		return stack;
	}
}
