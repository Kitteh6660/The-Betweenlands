package thebetweenlands.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;

public class ItemRenamableBlockEnum<T extends Enum<T> & IStringSerializable> extends ItemBlockEnum<T> implements IRenamableItem {
	public static <T extends Enum<T> & IStringSerializable> ItemRenamableBlockEnum<T> create(Block block, Class<T> cls) {
		return new ItemRenamableBlockEnum<T>(block, cls.getEnumConstants(), '.', IGenericMetaSelector.class.isAssignableFrom(cls));
	}

	public static <T extends Enum<T> & IStringSerializable> ItemRenamableBlockEnum<T> create(Block block, Class<T> cls, char separator) {
		return new ItemRenamableBlockEnum<T>(block, cls.getEnumConstants(), separator, IGenericMetaSelector.class.isAssignableFrom(cls));
	}

	protected ItemRenamableBlockEnum(Block block, T[] values, char separator, boolean hasGenericMetaSelector) {
		super(block, values, separator, hasGenericMetaSelector);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if(player.isCrouching()) {
			ItemStack stack = player.getItemInHand(hand);

			if(!world.isClientSide()) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == Hand.MAIN_HAND ? 0 : 1, 0, 0);
			}

			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
		}

		return super.onItemRightClick(world, player, hand);
	}
}
