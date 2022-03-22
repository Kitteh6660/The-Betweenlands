package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;

public class ItemTarminion extends Item {
	public ItemTarminion() {
		this.maxStackSize = 16;
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (!world.isClientSide()) {
			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.NEUTRAL, 1, 1);

			EntityThrownTarminion tarminion = new EntityThrownTarminion(world, player);
			Vector3d lookVec = player.getLookVec();
			tarminion.setPosition(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
			tarminion.shoot(lookVec.x, lookVec.y, lookVec.z, 0.8F, 0.1F);
			world.spawnEntity(tarminion);

			if (!player.isCreative()) {
				itemStack.shrink(1);
			}
		}
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStack);
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}