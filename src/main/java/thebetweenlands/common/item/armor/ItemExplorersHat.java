package thebetweenlands.common.item.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.armor.ModelExplorersHat;
import thebetweenlands.common.lib.ModInfo;

public class ItemExplorersHat extends ItemBLArmor {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/armor/explorers_hat.png");

	@OnlyIn(Dist.CLIENT)
	private static BipedModel<?> model;

	public ItemExplorersHat(Properties properties) {
		super(BLArmorMaterial.BL_CLOTH, EquipmentSlotType.HEAD, properties);
		//super(BLMaterialRegistry.ARMOR_BL_CLOTH, 2, EquipmentSlotType.HEAD, "explorers_hat");
		//this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public boolean isRepairable(ItemStack itemStack) {
		return false;
	}

	//Think this can be removed safely.
	/*@Override
	public int getColor(ItemStack itemStack) {
		return 0xFFFFFFFF;
	}*/

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return TEXTURE.toString();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BipedModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel defaultModel) {
		if(model == null) {
			model = new ModelExplorersHat(0.5F);
		}
		return model;
	}
}
