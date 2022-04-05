package thebetweenlands.common.item.herblore.rune.properties;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.herblore.rune.TokenRuneItem;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemTokenRuneItemProperties extends RuneItemProperties {
	private static final String NBT_ITEM_DATA = "thebetweenlands.block_rune.block_data";

	private final ResourceLocation regName;

	public ItemTokenRuneItemProperties(ResourceLocation regName) {
		this.regName = regName;
	}

	public Item getItemType(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();

		if(nbt != null && nbt.contains(NBT_ITEM_DATA, Constants.NBT.TAG_COMPOUND)) {
			CompoundNBT itemNbt = nbt.getCompoundTag(NBT_ITEM_DATA);

			if(itemNbt.contains("id", Constants.NBT.TAG_STRING)) {
				return Item.REGISTRY.getObject(new ResourceLocation(itemNbt.getString("id")));
			}
		}

		return null;
	}

	public int getDamageValue(ItemStack stack, boolean requiredOnly) {
		CompoundNBT nbt = stack.getTag();

		if(nbt != null && nbt.contains(NBT_ITEM_DATA, Constants.NBT.TAG_COMPOUND)) {
			CompoundNBT itemNbt = nbt.getCompoundTag(NBT_ITEM_DATA);

			if(itemNbt.contains("meta", Constants.NBT.TAG_INT)) {
				if(!requiredOnly || itemNbt.getBoolean("metaRequired")) {
					return itemNbt.getInt("meta");
				}
			}
		}

		return -1;
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		Item item = this.getItemType(stack);
		int meta = this.getDamageValue(stack, true);
		return new DefaultRuneContainerFactory(this.regName, () -> new TokenRuneItem.Blueprint(
				RuneStats.builder()
				.aspect(AspectRegistry.ORDANIIS, 1)
				.duration(5.0f)
				.build(),
				new ItemStack(item, 1, meta == -1 ? 0 : meta),
				s -> s.getItem() == item && (meta == -1 || s.getMetadata() == meta)));
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);

		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);

		if(playerIn.isCrouching()) {
			if(nbt.contains(NBT_ITEM_DATA)) {
				nbt.removeTag(NBT_ITEM_DATA);

				playerIn.swing(handIn);

				return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
			}
		} else {
			Vector3d start = playerIn.getPositionEyes(1);
			Vector3d dir = playerIn.getLookVec();

			float reach = 6.0f;
			int steps = 20;
			float size = 0.2f;

			float scale = 1.0f / steps * reach;

			ItemEntity hit = null;

			for(int i = 0; i < 20; i++) {
				AxisAlignedBB aabb = new AxisAlignedBB(
						start.x + dir.x * scale * i - size, start.y + dir.y * scale * i - size, start.z + dir.z * scale * i - size,
						start.x + dir.x * scale * i + size, start.y + dir.y * scale * i + size, start.z + dir.z * scale * i + size);

				List<ItemEntity> entities = worldIn.getEntitiesOfClass(ItemEntity.class, aabb);

				ItemEntity closest = null;
				double closestDstSq = Double.MAX_VALUE;

				for(ItemEntity entity : entities) {
					double dstSq = entity.getDistanceSq(start.x, start.y, start.z);
					if(dstSq < closestDstSq) {
						closest = entity;
						closestDstSq = dstSq;
					}
				}

				if(closest != null) {
					hit = closest;
					break;
				}
			}

			if(hit != null) {
				ItemStack hitStack = hit.getItem();

				if(!hitStack.isEmpty()) {
					CompoundNBT itemNbt = new CompoundNBT();

					itemNbt.putString("id", hitStack.getItem().getRegistryName().toString());
					itemNbt.putInt("meta", hitStack.getMetadata());
					itemNbt.putBoolean("metaRequired", hitStack.getHasSubtypes());

					nbt.put(NBT_ITEM_DATA, itemNbt);
				}

				playerIn.swing(handIn);

				return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
			}
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Item itemType = this.getItemType(stack);
		int meta = this.getDamageValue(stack, false);

		if(itemType != null) {
			String itemName = I18n.get(itemType.getUnlocalizedNameInefficiently(new ItemStack(itemType, 1, meta == -1 ? 0 : meta)) + ".name").trim();
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.thebetweenlands.rune.token_item.bound", itemName), 0));
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.thebetweenlands.rune.token_item.unbound"), 0));
		}
	}
}
