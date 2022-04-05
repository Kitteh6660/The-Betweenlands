package thebetweenlands.common.item.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.armor.ModelSpiritTreeFaceMaskSmall;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.EntitySpiritTreeFaceMask;

public class SpiritTreeFaceMaskSmallItem extends SpiritTreeFaceMaskItem {
	
	public static final ResourceLocation TEXTURE = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/entity/spirit_tree_face_small.png");
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/gui/spirit_tree_face_small_mask_overlay.png");
	public static final ResourceLocation OVERLAY_SIDE_TEXTURE = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/gui/spirit_tree_face_small_mask_overlay_side.png");

	@OnlyIn(Dist.CLIENT)
	private static BipedModel<?> model;

	public SpiritTreeFaceMaskSmallItem(Properties properties) {
		super((world, pos, facing) -> new EntitySpiritTreeFaceMask(world, pos, facing, EntitySpiritTreeFaceMask.Type.SMALL), properties);
		//super("spirit_tree_face_mask_small", (world, pos, facing) -> new EntitySpiritTreeFaceMask(world, pos, facing, EntitySpiritTreeFaceMask.Type.SMALL));
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return TEXTURE.toString();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BipedModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel defaultModel) {
		if(model == null) {
			model = new ModelSpiritTreeFaceMaskSmall(1.0F, true);
		}
		return model;
	}

	@Override
	protected ResourceLocation getOverlayTexture(ItemStack stack, PlayerEntity player, float partialTicks) {
		return OVERLAY_TEXTURE;
	}

	@Override
	protected ResourceLocation getOverlaySideTexture(ItemStack stack, PlayerEntity player, float partialTicks, boolean left) {
		return OVERLAY_SIDE_TEXTURE;
	}
}