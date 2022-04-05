package thebetweenlands.common.item;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import thebetweenlands.common.item.armor.BLArmorMaterial;
import thebetweenlands.common.item.tools.BLItemTier;

public class BLMaterialRegistry {
	
	//TODO: Move those to a new file.
	/*public static final ToolMaterial TOOL_WEEDWOOD = EnumHelper.addToolMaterial("weedwood", 0, 80, 2.0F, 0.0F, 0);
	public static final ToolMaterial TOOL_BONE = EnumHelper.addToolMaterial("bone", 1, 320, 4.0F, 1.0F, 0);
	public static final ToolMaterial TOOL_LURKER_SKIN = EnumHelper.addToolMaterial("bone", 1, 600, 5.0F, 1.0F, 0);
	public static final ToolMaterial TOOL_DENTROTHYST = EnumHelper.addToolMaterial("dentrothyst", 1, 600, 7.0F, 1.0F, 0);
	public static final ToolMaterial TOOL_OCTINE = EnumHelper.addToolMaterial("octine", 2, 900, 6.0F, 2.0F, 0);
	public static final ToolMaterial TOOL_SYRMORITE = EnumHelper.addToolMaterial("syrmorite", 2, 900, 6.0F, 2.0F, 0);
	public static final ToolMaterial TOOL_VALONITE = EnumHelper.addToolMaterial("valonite", 3, 2500, 8.0F, 3.0F, 0);
	public static final ToolMaterial TOOL_LOOT = EnumHelper.addToolMaterial("loot", 3, 7500, 2.0F, 0.5F, 0);
	public static final ToolMaterial TOOL_LEGEND = EnumHelper.addToolMaterial("legend", 6, 10000, 16.0F, 6.0F, 0);*/

	//TODO add armor equip sounds maybe
	/*public static final ArmorMaterial ARMOR_BL_CLOTH = EnumHelper.addArmorMaterial("bl_cloth", "bl_cloth", 12, new int[]{1, 2, 3, 1}, 0, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f);
	public static final ArmorMaterial ARMOR_LURKER_SKIN = EnumHelper.addArmorMaterial("lurker_skin", "lurker_skin", 12, new int[]{1, 2, 3, 1}, 0, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f);
	public static final ArmorMaterial ARMOR_BONE = EnumHelper.addArmorMaterial("slimy_bone", "slimy_bone", 6, new int[]{1, 3, 5, 2}, 0, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0f);
	public static final ArmorMaterial ARMOR_SYRMORITE = EnumHelper.addArmorMaterial("syrmorite", "syrmorite", 16, new int[]{2, 5, 6, 2}, 0, SoundEvents.ARMOR_EQUIP_IRON, 0.0f);
	public static final ArmorMaterial ARMOR_VALONITE = EnumHelper.addArmorMaterial("valonite", "valonite", 35, new int[]{3, 6, 8, 3}, 0, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0f);
	public static final ArmorMaterial ARMOR_RUBBER = EnumHelper.addArmorMaterial("rubber", "rubber", 10, new int[]{1, 0, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0f);
	public static final ArmorMaterial ARMOR_LEGEND = EnumHelper.addArmorMaterial("legend", "legend", 66, new int[]{6, 12, 16, 6}, 0, SoundEvents.ARMOR_EQUIP_NETHERITE, 2.0f);
	public static final ArmorMaterial ARMOR_ANCIENT = EnumHelper.addArmorMaterial("ancient", "ancient", 35, new int[]{3, 6, 8, 3}, 0, SoundEvents.ARMOR_EQUIP_DIAMOND, 3.0f);*/

	public static final Material MUD = (new Material.Builder(MaterialColor.DIRT)).build();
	public static final Material WISP = (new Material.Builder(MaterialColor.NONE).nonSolid().noCollider().replaceable()).build(); //.notSolidBlocking().destroyOnPush()
	
	public static final Material TAR = (new Material.Builder(MaterialColor.COLOR_BLACK).nonSolid().liquid().replaceable()).build(); // new LiquidMaterial(MapColor.BLACK);
	public static final Material RUBBER = (new Material.Builder(MaterialColor.WATER).nonSolid().liquid().replaceable()).build(); // public static final Material RUBBER = new MaterialLiquid(MapColor.WATER);
	public static final Material SLUDGE = (new Material.Builder(MaterialColor.DIRT)).build(); // public static final Material SLUDGE = new BLMaterial(MapColor.DIRT).setRequiresTool();

	// Tool Repair Costs
	public static int getMinRepairFuelCost(IItemTier material) {
		if(material == BLItemTier.WEEDWOOD) {
			return 2;
		} else if(material == BLItemTier.BONE) {
			return 3;
		} else if(material == BLItemTier.LURKER_SKIN) {
			return 4;
		} else if(material == BLItemTier.DENTROTHYST) {
			return 5;
		} else if(material == BLItemTier.OCTINE) {
			return 5;
		} else if(material == BLItemTier.SYRMORITE) {
			return 5;
		} else if(material == BLItemTier.VALONITE) {
			return 6;
		} else if(material == BLItemTier.LOOT) {
			return 16;
		} else if(material == BLItemTier.LEGEND) {
			return 24;
		}
		return 4;
	}

	public static int getFullRepairFuelCost(IItemTier material) {
		if(material == BLItemTier.WEEDWOOD) {
			return 6;
		} else if(material == BLItemTier.BONE) {
			return 8;
		} else if(material == BLItemTier.LURKER_SKIN) {
			return 10;
		} else if(material == BLItemTier.DENTROTHYST) {
			return 10;
		} else if(material == BLItemTier.OCTINE) {
			return 12;
		} else if(material == BLItemTier.SYRMORITE) {
			return 12;
		} else if(material == BLItemTier.VALONITE) {
			return 16;
		} else if(material == BLItemTier.LOOT) {
			return 32;
		} else if(material == BLItemTier.LEGEND) {
			return 48;
		}
		return 8;
	}

	public static int getMinRepairLifeCost(IItemTier material) {
		if(material == BLItemTier.WEEDWOOD) {
			return 4;
		} else if(material == BLItemTier.BONE) {
			return 4;
		} else if(material == BLItemTier.LURKER_SKIN) {
			return 4;
		} else if(material == BLItemTier.DENTROTHYST) {
			return 4;
		} else if(material == BLItemTier.OCTINE) {
			return 5;
		} else if(material == BLItemTier.SYRMORITE) {
			return 5;
		} else if(material == BLItemTier.VALONITE) {
			return 12;
		} else if(material == BLItemTier.LOOT) {
			return 32;
		} else if(material == BLItemTier.LEGEND) {
			return 48;
		}
		return 4;
	}

	public static int getFullRepairLifeCost(IItemTier material) {
		if(material == BLItemTier.WEEDWOOD) {
			return 16;
		} else if(material == BLItemTier.BONE) {
			return 16;
		} else if(material == BLItemTier.LURKER_SKIN) {
			return 16;
		} else if(material == BLItemTier.DENTROTHYST) {
			return 16;
		} else if(material == BLItemTier.OCTINE) {
			return 32;
		} else if(material == BLItemTier.SYRMORITE) {
			return 32;
		} else if(material == BLItemTier.VALONITE) {
			return 48;
		} else if(material == BLItemTier.LOOT) {
			return 64;
		} else if(material == BLItemTier.LEGEND) {
			return 110;
		}
		return 8;
	}

	// Armour Repair Costs
	public static int getMinRepairFuelCost(IArmorMaterial material) {
		if(material == BLArmorMaterial.BL_CLOTH) {
			return 2;
		} else if(material == BLArmorMaterial.SLIMY_BONE) {
			return 3;
		} else if(material == BLArmorMaterial.RUBBER) {
			return 3;
		} else if(material == BLArmorMaterial.LURKER_SKIN) {
			return 4;
		} else if(material == BLArmorMaterial.SYRMORITE) {
			return 5;
		} else if(material == BLArmorMaterial.VALONITE) {
			return 6;
		} else if(material == BLArmorMaterial.LEGEND) {
			return 24;
		} else if(material == BLArmorMaterial.ANCIENT) {
			return 6;
		} 
		return 4;
	}

	public static int getFullRepairFuelCost(IArmorMaterial material) {
		if(material == BLArmorMaterial.BL_CLOTH) {
			return 6;
		} else if(material == BLArmorMaterial.SLIMY_BONE) {
			return 8;
		} else if(material == BLArmorMaterial.RUBBER) {
			return 8;
		} else if(material == BLArmorMaterial.LURKER_SKIN) {
			return 10;
		} else if(material == BLArmorMaterial.SYRMORITE) {
			return 12;
		} else if(material == BLArmorMaterial.VALONITE) {
			return 16;
		} else if(material == BLArmorMaterial.LEGEND) {
			return 48;
		}else if(material == BLArmorMaterial.ANCIENT) {
			return 24;
		} 
		return 8;
	}

	public static int getMinRepairLifeCost(IArmorMaterial material) {
		if(material == BLArmorMaterial.BL_CLOTH) {
			return 4;
		} else if(material == BLArmorMaterial.SLIMY_BONE) {
			return 4;
		} else if(material == BLArmorMaterial.RUBBER) {
			return 4;
		} else if(material == BLArmorMaterial.LURKER_SKIN) {
			return 4;
		} else if(material == BLArmorMaterial.SYRMORITE) {
			return 5;
		} else if(material == BLArmorMaterial.VALONITE) {
			return 12;
		} else if(material == BLArmorMaterial.LEGEND) {
			return 48;
		}else if(material == BLArmorMaterial.ANCIENT) {
			return 16;
		} 
		return 4;
	}

	public static int getFullRepairLifeCost(IArmorMaterial material) {
		if(material == BLArmorMaterial.BL_CLOTH) {
			return 16;
		} else if(material == BLArmorMaterial.SLIMY_BONE) {
			return 16;
		} else if(material == BLArmorMaterial.RUBBER) {
			return 16;
		} else if(material == BLArmorMaterial.LURKER_SKIN) {
			return 16;
		} else if(material == BLArmorMaterial.SYRMORITE) {
			return 32;
		} else if(material == BLArmorMaterial.VALONITE) {
			return 48;
		} else if(material == BLArmorMaterial.LEGEND) {
			return 110;
		} else if(material == BLArmorMaterial.ANCIENT) {
			return 48;
		}
		return 8;
	}
}
