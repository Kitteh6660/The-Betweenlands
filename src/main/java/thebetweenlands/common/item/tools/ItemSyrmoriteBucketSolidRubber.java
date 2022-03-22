package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

public class ItemSyrmoriteBucketSolidRubber extends Item {
	
	public ItemSyrmoriteBucketSolidRubber(Properties properties) {
		super(properties);
		//this.setCreativeTab(BLCreativeTabs.GEARS);
		//this.setMaxStackSize(1);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.rubber_bucket"));
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if(!world.isClientSide()) {
			player.awardStat(Stats.getObjectUseStats(this));
			
			ItemStack bucket = new ItemStack(ItemRegistry.BL_BUCKET, 1, 1);
			bucket.getItem().onCreated(bucket, world, player);
			player.setItemInHand(hand, bucket);
			
			ItemStack rubber = EnumItemMisc.RUBBER_BALL.create(3);
			if(!player.addItem(rubber)) {
				player.drop(rubber, false);
			}
		}
		
		player.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
		
		return ActionResult.newResult(ActionResultType.SUCCESS, player.getItemInHand(hand));
	}
}
