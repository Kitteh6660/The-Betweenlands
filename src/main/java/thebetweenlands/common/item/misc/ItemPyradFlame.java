package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityPyradFlame;

public class ItemPyradFlame extends Item 
{
	public ItemPyradFlame(Properties properties) {
		super(properties);
		//this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(!world.isClientSide()) {
			world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

			Vector3d look = player.getLookAngle();

			float f = 0.05F;

			for (int i = 0; i < player.getRandom().nextInt(6) + 1; ++i) {
				EntityPyradFlame flame = new EntityPyradFlame(world, player, look.x + player.getRandom().nextGaussian() * (double)f, look.y, look.z + player.getRandom().nextGaussian() * (double)f);
				flame.yPos = player.getY() + (double)(player.getBbHeight() / 2.0F) + 0.5D;
				world.addFreshEntity(flame);
			}

			if (!player.isCreative()) {
				stack.shrink(1);
			}
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}
}