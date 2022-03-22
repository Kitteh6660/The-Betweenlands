package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EquipmentSlotType;
import thebetweenlands.common.capability.circlegem.CircleGemType;

public class ItemSyrmoriteArmor extends ItemBLArmor {
	
	public ItemSyrmoriteArmor(EquipmentSlotType slot, Properties properties) {
		super(BLArmorMaterial.SYRMORITE, slot, properties);

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "syrmorite_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "syrmorite_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "syrmorite_green");
	}
}
