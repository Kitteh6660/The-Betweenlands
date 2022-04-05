package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EquipmentSlotType;
import thebetweenlands.common.capability.circlegem.CircleGemType;

public class BoneArmorItem extends ItemBLArmor
{
	public BoneArmorItem(EquipmentSlotType equipmentSlotIn, Properties properties) {
		super(BLArmorMaterial.SLIMY_BONE, equipmentSlotIn, properties);
		//super(BLMaterialRegistry.ARMOR_BONE, 3, slot, "bone");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "bone_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "bone_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "bone_green");
	}
}
