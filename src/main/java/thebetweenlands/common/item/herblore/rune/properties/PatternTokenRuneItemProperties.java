package thebetweenlands.common.item.herblore.rune.properties;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.rune.TokenRunePattern;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class PatternTokenRuneItemProperties extends RuneItemProperties {
	private static final ResourceLocation TEXTURE_MARK = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/runes/pattern_rune_mark.png");
	private static final ResourceLocation TEXTURE_CENTER = new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/runes/pattern_rune_center.png");

	private static final String NBT_PATTERN_CENTER_X = "thebetweenlands.pattern_rune.pattern_center_x";
	private static final String NBT_PATTERN_CENTER_Y = "thebetweenlands.pattern_rune.pattern_center_y";
	private static final String NBT_PATTERN_CENTER_Z = "thebetweenlands.pattern_rune.pattern_center_z";
	private static final String NBT_PATTERN_BLOCKS = "thebetweenlands.pattern_rune.pattern_blocks";

	private final ResourceLocation regName;

	public PatternTokenRuneItemProperties(ResourceLocation regName) {
		this.regName = regName;
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		return new DefaultRuneContainerFactory(this.regName, () -> {
			List<BlockPos> pattern = new ArrayList<>();

			if(stack.hasTag()) {
				ListNBT blocks = stack.getTag().getList(NBT_PATTERN_BLOCKS, Constants.NBT.TAG_LONG);

				for(int i = 0; i < blocks.size(); i++) {
					pattern.add(BlockPos.of(((LongNBT)blocks.get(i)).getLong()));
				}
			}

			return new TokenRunePattern.Blueprint(
					RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build(),
					pattern);
		});
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand,
			Direction facing, BlockRayTraceResult hitResult) {

		ItemStack stack = player.getItemInHand(hand);

		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);

		boolean hasPattern = this.hasPattern(stack);
		BlockPos center = this.getCenter(stack);

		boolean changed = false;

		if(player.isCrouching() || !hasPattern) {
			if(!player.isCrouching()) {
				if(!worldIn.isClientSide()) {
					player.displayClientMessage(new TranslationTextComponent("chat.pattern_rune_set_origin"), true);
				}
			} else {
				if(center.equals(pos)) {
					nbt.remove(NBT_PATTERN_CENTER_X);
					nbt.remove(NBT_PATTERN_CENTER_Y);
					nbt.remove(NBT_PATTERN_CENTER_Z);
					nbt.remove(NBT_PATTERN_BLOCKS);
					worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_REMOVE, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
				} else {
					nbt.putInt(NBT_PATTERN_CENTER_X, pos.getX());
					nbt.putInt(NBT_PATTERN_CENTER_Y, pos.getY());
					nbt.putInt(NBT_PATTERN_CENTER_Z, pos.getZ());
					worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_ADD, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
				}

				changed = true;
			}
		} else {
			ListNBT blocks = nbt.getList(NBT_PATTERN_BLOCKS, Constants.NBT.TAG_LONG);

			boolean contained = false;

			for(int i = 0; i < blocks.size(); i++) {
				BlockPos block = BlockPos.of(((LongNBT)blocks.get(i)).getLong()).add(center);

				if(block.equals(pos)) {
					blocks.remove(i);
					changed = true;
					contained = true;
				}
			}

			if(!contained) {
				blocks.add(new LongNBT(pos.subtract(center).asLong()));
				changed = true;
				worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_ADD, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
			} else {
				worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_REMOVE, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
			}

			nbt.put(NBT_PATTERN_BLOCKS, blocks);
		}

		if(changed && player == TheBetweenlands.proxy.getClientPlayer()) {
			currentPatternCenter = null;
			currentPattern = null;
		}

		return ActionResultType.SUCCESS;
	}

	public boolean hasPattern(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		return nbt != null && nbt.contains(NBT_PATTERN_CENTER_X, Constants.NBT.TAG_INT) && nbt.contains(NBT_PATTERN_CENTER_Y, Constants.NBT.TAG_INT) && nbt.contains(NBT_PATTERN_CENTER_Z, Constants.NBT.TAG_INT);
	}

	public BlockPos getCenter(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		return nbt == null ? BlockPos.ZERO : new BlockPos(nbt.getInt(NBT_PATTERN_CENTER_X), nbt.getInt(NBT_PATTERN_CENTER_Y), nbt.getInt(NBT_PATTERN_CENTER_Z));
	}

	public List<BlockPos> getPattern(ItemStack stack) {
		List<BlockPos> pattern = new ArrayList<>();

		CompoundNBT nbt = stack.getTag();

		if(nbt != null) {
			ListNBT blocks = nbt.getList(NBT_PATTERN_BLOCKS, Constants.NBT.TAG_LONG);

			for(int i = 0; i < blocks.size(); i++) {
				pattern.add(BlockPos.of(((LongNBT)blocks.get(i)).getLong()));
			}
		}

		return pattern;
	}

	private static BlockPos currentPatternCenter = null;
	private static List<BlockPos> currentPattern = null;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		boolean holdingPattern = false;

		for(ItemStack stack : Minecraft.getInstance().player.getHeldEquipment()) {
			if(!stack.isEmpty() && stack.getItem() instanceof ItemRune) {
				RuneItemProperties properties = ((ItemRune) stack.getItem()).getProperties(stack);

				if(properties instanceof PatternTokenRuneItemProperties) {
					PatternTokenRuneItemProperties rune = (PatternTokenRuneItemProperties) properties;

					if(rune.hasPattern(stack)) {
						holdingPattern = true;

						if(currentPatternCenter == null || currentPattern == null) {
							currentPatternCenter = rune.getCenter(stack);
							currentPattern = rune.getPattern(stack);
						}

						GlStateManager._pushMatrix();

						GlStateManager.enableTexture2D();
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
						GlStateManager.glLineWidth(1F);
						GlStateManager.depthMask(false);

						GlStateManager.doPolygonOffset(-0.1F, -10.0F);
						GlStateManager.enablePolygonOffset();

						float alpha = (MathHelper.cos((Minecraft.getInstance().player.tickCount + event.getPartialTicks()) * 0.2f) + 1) * 0.5f * 0.3f + 0.5f;

						GlStateManager.color(1, 1, 1, alpha);

						RenderManager rm = Minecraft.getInstance().getRenderManager();

						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder buffer = tessellator.getBuffer();

						Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_CENTER);

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

						buffer.setTranslation(currentPatternCenter.getX() - rm.viewerPosX, currentPatternCenter.getY() - rm.viewerPosY, currentPatternCenter.getZ() - rm.viewerPosZ);

						buffer.pos(0, 0, 1).tex(0, 0).normal(0, 0, 1).endVertex();
						buffer.pos(1, 0, 1).tex(1, 0).normal(0, 0, 1).endVertex();
						buffer.pos(1, 1, 1).tex(1, 1).normal(0, 0, 1).endVertex();
						buffer.pos(0, 1, 1).tex(0, 1).normal(0, 0, 1).endVertex();

						buffer.pos(0, 0, 0).tex(0, 0).normal(0, 0, -1).endVertex();
						buffer.pos(0, 1, 0).tex(0, 1).normal(0, 0, -1).endVertex();
						buffer.pos(1, 1, 0).tex(1, 1).normal(0, 0, -1).endVertex();
						buffer.pos(1, 0, 0).tex(1, 0).normal(0, 0, -1).endVertex();

						buffer.pos(0, 0, 0).tex(0, 0).normal(-1, 0, 0).endVertex();
						buffer.pos(0, 0, 1).tex(1, 0).normal(-1, 0, 0).endVertex();
						buffer.pos(0, 1, 1).tex(1, 1).normal(-1, 0, 0).endVertex();
						buffer.pos(0, 1, 0).tex(0, 1).normal(-1, 0, 0).endVertex();

						buffer.pos(1, 0, 0).tex(0, 0).normal(1, 0, 0).endVertex();
						buffer.pos(1, 1, 0).tex(0, 1).normal(1, 0, 0).endVertex();
						buffer.pos(1, 1, 1).tex(1, 1).normal(1, 0, 0).endVertex();
						buffer.pos(1, 0, 1).tex(1, 0).normal(1, 0, 0).endVertex();

						buffer.pos(0, 1, 0).tex(0, 0).normal(0, 1, 0).endVertex();
						buffer.pos(0, 1, 1).tex(0, 1).normal(0, 1, 0).endVertex();
						buffer.pos(1, 1, 1).tex(1, 1).normal(0, 1, 0).endVertex();
						buffer.pos(1, 1, 0).tex(1, 0).normal(0, 1, 0).endVertex();

						buffer.pos(0, 0, 0).tex(0, 0).normal(0, -1, 0).endVertex();
						buffer.pos(1, 0, 0).tex(1, 0).normal(0, -1, 0).endVertex();
						buffer.pos(1, 0, 1).tex(1, 1).normal(0, -1, 0).endVertex();
						buffer.pos(0, 0, 1).tex(0, 1).normal(0, -1, 0).endVertex();

						tessellator.draw();

						Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_MARK);

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

						for(BlockPos pos : currentPattern) {

							buffer.setTranslation(pos.getX() + currentPatternCenter.getX() - rm.viewerPosX, pos.getY() + currentPatternCenter.getY() - rm.viewerPosY, pos.getZ() + currentPatternCenter.getZ() - rm.viewerPosZ);

							buffer.pos(0, 0, 1).tex(0, 0).normal(0, 0, 1).endVertex();
							buffer.pos(1, 0, 1).tex(1, 0).normal(0, 0, 1).endVertex();
							buffer.pos(1, 1, 1).tex(1, 1).normal(0, 0, 1).endVertex();
							buffer.pos(0, 1, 1).tex(0, 1).normal(0, 0, 1).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(0, 0, -1).endVertex();
							buffer.pos(0, 1, 0).tex(0, 1).normal(0, 0, -1).endVertex();
							buffer.pos(1, 1, 0).tex(1, 1).normal(0, 0, -1).endVertex();
							buffer.pos(1, 0, 0).tex(1, 0).normal(0, 0, -1).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 0, 1).tex(1, 0).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 1, 1).tex(1, 1).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 1, 0).tex(0, 1).normal(-1, 0, 0).endVertex();

							buffer.pos(1, 0, 0).tex(0, 0).normal(1, 0, 0).endVertex();
							buffer.pos(1, 1, 0).tex(0, 1).normal(1, 0, 0).endVertex();
							buffer.pos(1, 1, 1).tex(1, 1).normal(1, 0, 0).endVertex();
							buffer.pos(1, 0, 1).tex(1, 0).normal(1, 0, 0).endVertex();

							buffer.pos(0, 1, 0).tex(0, 0).normal(0, 1, 0).endVertex();
							buffer.pos(0, 1, 1).tex(0, 1).normal(0, 1, 0).endVertex();
							buffer.pos(1, 1, 1).tex(1, 1).normal(0, 1, 0).endVertex();
							buffer.pos(1, 1, 0).tex(1, 0).normal(0, 1, 0).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(0, -1, 0).endVertex();
							buffer.pos(1, 0, 0).tex(1, 0).normal(0, -1, 0).endVertex();
							buffer.pos(1, 0, 1).tex(1, 1).normal(0, -1, 0).endVertex();
							buffer.pos(0, 0, 1).tex(0, 1).normal(0, -1, 0).endVertex();
						}

						tessellator.draw();

						buffer.setTranslation(0, 0, 0);

						GlStateManager.disablePolygonOffset();

						GlStateManager.color(1, 1, 1, 1);
						GlStateManager.depthMask(true);
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
						GlStateManager.enableTexture2D();
						GlStateManager.enableDepth();
						GlStateManager.disableBlend();

						GlStateManager.popMatrix();
					}
				}
			}
		}

		if(!holdingPattern) {
			currentPattern = null;
			currentPatternCenter = null;
		}
	}
}
