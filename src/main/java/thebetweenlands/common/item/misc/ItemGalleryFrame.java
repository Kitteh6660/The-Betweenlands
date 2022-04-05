package thebetweenlands.common.item.misc;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityGalleryFrame;

public class ItemGalleryFrame extends Item {
	public final EntityGalleryFrame.Type type;

	public ItemGalleryFrame(EntityGalleryFrame.Type type) {
		this.setMaxDamage(0);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.type = type;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack is, ItemStack book) {
		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack itemstack = player.getItemInHand(hand);
		BlockPos offsetPos = pos.offset(facing);

		if (facing != Direction.DOWN && facing != Direction.UP && player.mayUseItemAt(offsetPos, facing, itemstack)) {
			EntityHanging entity = new EntityGalleryFrame(world, offsetPos, facing, this.type);

			if (entity != null && entity.onValidSurface()) {
				if (!level.isClientSide()) {
					entity.playPlaceSound();
					world.addFreshEntity(entity);
				}

				itemstack.shrink(1);
			}

			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}
}
