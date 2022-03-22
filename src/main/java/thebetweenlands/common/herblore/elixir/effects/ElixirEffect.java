package thebetweenlands.common.herblore.elixir.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.TranslationHelper;

public class ElixirEffect {
	public static final int VIAL_INFUSION_MAX_POTENCY = 5;
	
	private final String effectName;
	private final int effectID;
	private final ResourceLocation icon;
	private final int color;
	private List<ElixirAttributeModifier> elixirAttributeModifiers = new ArrayList<>();
	private ElixirPotionEffect potionEffect;
	private ResourceLocation potionID;
	private boolean isAntiInfusion = false;
	private boolean showInBook = false;
	
	
	public ElixirEffect(int id, String name) {
		this(id, name, null, 0x00000000);
	}
	public ElixirEffect(int id, String name, ResourceLocation icon) {
		this(id, name, icon, 0x00000000);
	}
	public ElixirEffect(int id, String name, int color) {
		this(id, name, null, color);
	}

	public ElixirEffect(int id, String name, ResourceLocation icon, int color) {
		this.effectID = id;
		this.effectName = name;
		this.icon = icon;
		this.color = color;
	}

	public ElixirEffect setShowInBook() {
		this.showInBook = true;
		return this;
	}

	public boolean shouldShowInBook() {
		return this.showInBook;
	}
	
	public EffectInstance createEffect(int duration, int strength) {
		return new EffectInstance(Potion.byName(this.potionID.toString()), duration, strength);
	}

	public void registerPotion(String name) {
		this.potionEffect = (ElixirPotionEffect) new ElixirPotionEffect(this, this.effectName, this.color, this.icon).setRegistryName(ModInfo.ID, name);
		this.potionID = potionEffect.getRegistryName();
		for(ElixirAttributeModifier modifier : this.elixirAttributeModifiers) {
			this.potionEffect.addAttributeModifier(modifier.attribute, modifier.uuid, modifier.modifier, modifier.operation);
		}
	}

	public int getID() {
		return this.effectID;
	}

	public String getEffectName() {
		return this.effectName;
	}

	public ResourceLocation getIcon() {
		return this.potionEffect.icon;
	}

	/**
	 * Whether this effect should be applied this tick
	 */
	protected boolean isReady(int ticks, int strength) {
		return true;
	}

	/**
	 * Effect over time
	 */
	protected void performEffect(LivingEntity entity, int strength) { }

	/**
	 * Instant effect
	 */
	protected void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity target, int amplifier, double health) { }

	/**
	 * Whether this affect should be applied instantly
	 * @return
	 */
	protected boolean isInstant() {
		return false;
	}

	/**
	 * Calculates the modifier from the attribute and elixir strength
	 * @param attributeModifier
	 * @param strength
	 * @return
	 */
	protected double getAttributeModifier(AttributeModifier attributeModifier, int strength) {
		return attributeModifier.getAmount() * (double)(strength + 1);
	}

	/**
	 * Adds an entity attribute modifier that is applied when the potion is active.
	 * @param attribute
	 * @param uuid
	 * @param modifier
	 * @param operation
	 * @return
	 */
	public ElixirEffect addAttributeModifier(Attribute attribute, String uuid, double modifier, Operation operation) {
		if(this.potionEffect != null) {
			this.potionEffect.addAttributeModifier(attribute, uuid, modifier, operation);
		} else {
			this.elixirAttributeModifiers.add(new ElixirAttributeModifier(attribute, uuid, modifier, operation));
		}
		return this;
	}

	public ElixirEffect setAntiInfusion(){
		isAntiInfusion = true;
		return this;
	}

	public boolean isAntiInfusion(){
		return isAntiInfusion;
	}

	public boolean isActive(LivingEntity entity) {
		if(entity == null) return false;
		Collection<EffectInstance> activePotions = entity.getActiveEffects();
		for(EffectInstance effect : activePotions) {
			if(effect.getEffect().getRegistryName() == this.potionID) {
				return true;
			}
		}
		return false;
	}

	public int getDuration(LivingEntity entity) {
		if(entity == null) return -1;
		Collection<EffectInstance> activePotions = entity.getActiveEffects();
		for(EffectInstance effect : activePotions) {
			if(effect.getEffect().getRegistryName() == this.potionID) {
				return effect.getDuration();
			}
		}
		return -1;
	}

	public int getStrength(LivingEntity entity) {
		if(entity == null) return -1;
		Collection<EffectInstance> activePotions = entity.getActiveEffects();
		for(EffectInstance effect : activePotions) {
			if(effect.getEffect().getRegistryName() == this.potionID) {
				return effect.getAmplifier();
			}
		}
		return -1;
	}

	public EffectInstance getPotionEffect(LivingEntity entity) {
		if(entity.hasEffect(Potion.byName(this.potionID.toString()))) {
			return entity.getEffect(this.potionEffect);
		}
		return null;
	}

	public void removeElixir(LivingEntity entity) {
		entity.removeEffect(Potion.byName(this.potionID.toString()));
	}

	public ElixirPotionEffect getPotionEffect() {
		return this.potionEffect;
	}

	private static class ElixirAttributeModifier {
		private final Attribute attribute;
		private final String uuid;
		private final double modifier;
		private final Operation operation;
		private ElixirAttributeModifier(Attribute attribute, String uuid, double modifier, Operation operation) {
			this.attribute = attribute;
			this.uuid = uuid;
			this.modifier = modifier;
			this.operation = operation;
		}
	}

	public static class ElixirPotionEffect extends Effect {
		private final ElixirEffect effect;
		private final ResourceLocation icon;

		@OnlyIn(Dist.CLIENT)
		private String localizedElixirName;
		
		@OnlyIn(Dist.CLIENT)
		private TextContainer nameContainer;

		protected ElixirPotionEffect(ElixirEffect effect, String unlocalizedName, int color, ResourceLocation icon) {
			super(false, color);
			this.setPotionName(unlocalizedName);
			this.effect = effect;
			this.icon = icon;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public boolean hasStatusIcon() {
			return this.icon != null;
		}

		@Override
		public boolean isInstant() {
			return this.effect.isInstant();
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
			if(this.icon != null) {
				GlStateManager.enableTexture2D();
				GlStateManager.enableBlend();
				Minecraft.getInstance().renderEngine.bindTexture(this.icon);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder vertexBuffer = tessellator.getBuffer();

				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.pos(x+6, y+6, 0).tex(0, 0).endVertex();
				vertexBuffer.pos(x+6, y+6+20, 0).tex(0, 1).endVertex();
				vertexBuffer.pos(x+6+20, y+6+20, 0).tex(1, 1).endVertex();
				vertexBuffer.pos(x+6+20, y+6, 0).tex(1, 0).endVertex();
				tessellator.draw();
			}
			if(this.localizedElixirName == null) {
				this.localizedElixirName = TranslationHelper.translateToLocal(this.getName());
			}
			if(this.nameContainer == null) {
				this.nameContainer = new TextContainer(88, 100, this.localizedElixirName, Minecraft.getInstance().fontRenderer);
				int width = Minecraft.getInstance().fontRenderer.getStringWidth(this.localizedElixirName);
				float scale = 1.0F;
				if(width > 88) {
					scale = 88.0F / (float)width;
					scale -= scale % 0.25F;
				}
				if(scale < 0.5F) {
					scale = 0.5F;
				}
				this.nameContainer.setCurrentScale(scale);
				this.nameContainer.setCurrentColor(0xFFFFFFFF);
				try {
					this.nameContainer.parse();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(this.nameContainer != null && this.nameContainer.getPages().size() > 0) {
				this.setPotionName("");
				TextContainer.TextPage page0 = this.nameContainer.getPages().get(0);
				page0.render(x + 28, y + 6);
				String s = Potion.getPotionDurationString(effect, 1.0F);
				mc.fontRenderer.drawStringWithShadow(s, (float)(x + 10 + 18), (float)(y + 6 + 10), 8355711);
			}
		}

		@Override
		public boolean shouldRenderInvText(PotionEffect effect) {
			return false;
		}

		@Override
		public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
			if(this.icon != null) {
				GlStateManager.enableTexture2D();
				GlStateManager.enableBlend();
				Minecraft.getInstance().renderEngine.bindTexture(this.icon);

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder vertexBuffer = tessellator.getBuffer();

				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vertexBuffer.pos(x+2, y+2, 0).tex(0, 0).endVertex();
				vertexBuffer.pos(x+2, y+2+20, 0).tex(0, 1).endVertex();
				vertexBuffer.pos(x+2+20, y+2+20, 0).tex(1, 1).endVertex();
				vertexBuffer.pos(x+2+20, y+2, 0).tex(1, 0).endVertex();
				tessellator.draw();
			}
		}

		@Override
		public boolean isReady(int ticks, int strength) {
			return this.effect.isReady(ticks, strength);
		}

		@Override
		public void performEffect(LivingEntity entity, int strength) {
			this.effect.performEffect(entity, strength);
		}

		@Override
		public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity target, int amplifier, double health) {
			this.effect.affectEntity(source, indirectSource, target, amplifier, health);
		}

		@Override
		public double getAttributeModifierAmount(int strength, AttributeModifier attributeModifier) {
			return this.effect.getAttributeModifier(attributeModifier, strength);
		}
	}
}
