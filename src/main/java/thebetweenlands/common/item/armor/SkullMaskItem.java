package thebetweenlands.common.item.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class SkullMaskItem extends ItemBLArmor {
	
	private static final ResourceLocation SKULL_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/skull_mask.png");
	private static final ResourceLocation SKULL_SIDE_LEFT_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/skull_mask_side_left.png");
	private static final ResourceLocation SKULL_SIDE_RIGHT_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/skull_mask_side_right.png");

	public SkullMaskItem(Properties properties) {
		super(BLArmorMaterial.SLIMY_BONE, EquipmentSlotType.HEAD, properties);
		//super(BLMaterialRegistry.ARMOR_BONE, 2, EquipmentSlotType.HEAD, "skull_mask");
		//this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack is, ItemStack book) {
		return false;
	}

	@Override
	protected ResourceLocation getOverlaySideTexture(ItemStack stack, PlayerEntity player, float partialTicks,
			boolean left) {
		return left ? SKULL_SIDE_LEFT_TEXTURE : SKULL_SIDE_RIGHT_TEXTURE;
	}

	@Override
	protected ResourceLocation getOverlayTexture(ItemStack stack, PlayerEntity player, float partialTicks) {
		return SKULL_TEXTURE;
	}
}
