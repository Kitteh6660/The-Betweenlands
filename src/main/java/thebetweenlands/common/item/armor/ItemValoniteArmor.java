package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EquipmentSlotType;
import thebetweenlands.common.capability.circlegem.CircleGemType;

public class ItemValoniteArmor extends ItemBLArmor {
	
	public ItemValoniteArmor(EquipmentSlotType slot, Properties properties) {
		super(BLArmorMaterial.VALONITE, slot, properties);

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "valonite_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "valonite_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "valonite_green");
	}
}
