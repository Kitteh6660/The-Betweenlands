package thebetweenlands.common.item.armor;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;

public class ItemBLArmor extends ArmorItem implements IAnimatorRepairable {
	
	protected final String armorTexture1, armorTexture2;
	protected final String gemArmorTextures[][] = new String[CircleGemType.values().length][2];
	//protected final String armorName;

	public ItemBLArmor(IArmorMaterial materialIn, EquipmentSlotType equipmentSlotIn, Properties properties) {
		super(materialIn, equipmentSlotIn, properties);

		//this.setCreativeTab(BLCreativeTabs.GEARS);

		//this.armorName = armorName;

		this.armorTexture1 = ModInfo.ASSETS_PREFIX + "textures/armor/" + materialIn.getName() + "_1.png";
		this.armorTexture2 = ModInfo.ASSETS_PREFIX + "textures/armor/" + materialIn.getName() + "_2.png";

		CircleGemHelper.addGemPropertyOverrides(this);
	}

	/**
	 * Adds an armor texture override for the specified gem
	 * @param type
	 * @param topHalf
	 * @param bottomHalf
	 * @return
	 */
	public ItemBLArmor setGemArmorTextureOverride(CircleGemType type, String armorName) {
		this.gemArmorTextures[type.ordinal()][0] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_1.png";
		this.gemArmorTextures[type.ordinal()][1] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_2.png";
		return this;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		String texture1 = this.armorTexture1;
		String texture2 = this.armorTexture2;

		CircleGemType gem = CircleGemHelper.getGem(stack);

		if(this.gemArmorTextures[gem.ordinal()][0] != null) {
			texture1 = this.gemArmorTextures[gem.ordinal()][0];
		}
		if(this.gemArmorTextures[gem.ordinal()][1] != null) {
			texture2 = this.gemArmorTextures[gem.ordinal()][1];
		}

		if(slot == EquipmentSlotType.LEGS) {
			return texture2;
		} else {
			return texture1;
		}
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(this.getMaterial());
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(this.getMaterial());
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(this.getMaterial());
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(this.getMaterial());
	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	protected ResourceLocation getOverlayTexture(ItemStack stack, PlayerEntity player, float partialTicks) {
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	protected ResourceLocation getOverlaySideTexture(ItemStack stack, PlayerEntity player, float partialTicks, boolean left) {
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks) {
		ResourceLocation overlay = this.getOverlayTexture(stack, player, partialTicks);
		if(overlay != null) {
			GlStateManager._blendColor(1, 1, 1, 1);
			GlStateManager._disableDepthTest();
			GlStateManager.depthMask(false);
			GlStateManager._disableBlend();
			GlStateManager._disableAlphaTest();
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

			renderRepeatingOverlay((float)resolution.getScaledWidth_double(), (float)resolution.getScaledHeight_double(), overlay, this.getOverlaySideTexture(stack, player, partialTicks, true), this.getOverlaySideTexture(stack, player, partialTicks, false));

			GlStateManager.depthMask(true);
			GlStateManager._enableDepthTest();
			GlStateManager._enableAlphaTest();
			GlStateManager._blendColor(1, 1, 1, 1);
		}
	}

	public static void renderRepeatingOverlay(float width, float height, ResourceLocation overlay, @Nullable ResourceLocation sideOverlayLeft, @Nullable ResourceLocation sideOverlayRight) {
		if(overlay != null) {
			Minecraft.getInstance().getTextureManager().getTexture(overlay);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexBuffer = tessellator.getBuilder();

			if(sideOverlayLeft != null && sideOverlayRight != null) {
				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.vertex(width / 2 - height / 2, height, -90).uv(0, 1).endVertex();
				vertexBuffer.vertex(width / 2 + height / 2, height, -90).uv(1, 1).endVertex();
				vertexBuffer.vertex(width / 2 + height / 2, 0, -90).uv(1, 0).endVertex();
				vertexBuffer.vertex(width / 2 - height / 2, 0, -90).uv(0, 0).endVertex();
				tessellator.end();

				float texWidth = (width / 2 - height / 2) / height;

				Minecraft.getInstance().getTextureManager().getTexture(sideOverlayLeft);

				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.vertex(0, height, -90).uv(1 - texWidth, 1).endVertex();
				vertexBuffer.vertex(width / 2 - height / 2, height, -90).uv(1, 1).endVertex();
				vertexBuffer.vertex(width / 2 - height / 2, 0, -90).uv(1, 0).endVertex();
				vertexBuffer.vertex(0, 0, -90).uv(1 - texWidth, 0).endVertex();
				tessellator.end();

				Minecraft.getInstance().getTextureManager().getTexture(sideOverlayRight);

				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.vertex(width / 2 + height / 2, height, -90).uv(0, 1).endVertex();
				vertexBuffer.vertex(width, height, -90).uv(texWidth, 1).endVertex();
				vertexBuffer.vertex(width, 0, -90).uv(texWidth, 0).endVertex();
				vertexBuffer.vertex(width / 2 + height / 2, 0, -90).uv(0, 0).endVertex();
				tessellator.end();
			} else {
				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				float offset = 0.5F - width / height / 2;
				vertexBuffer.vertex(0, height, -90).uv(offset, 1).endVertex();
				vertexBuffer.vertex(width, height, -90).uv(1 - offset, 1).endVertex();
				vertexBuffer.vertex(width, 0, -90).uv(1 - offset, 0).endVertex();
				vertexBuffer.vertex(0, 0, -90).uv(offset, 0).endVertex();
				tessellator.end();
			}
		}
	}
}
