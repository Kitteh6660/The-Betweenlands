package thebetweenlands.common.recipe.censer;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.item.herblore.ItemElixir;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class CenserRecipeElixir extends AbstractCenserRecipe<CenserRecipeElixirContext> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "elixir");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		if(stack.getItem() == ItemRegistry.ELIXIR.get()) {
			ElixirEffect effect = ((ItemElixir)ItemRegistry.ELIXIR.get()).getElixirFromItem(stack);
			return !effect.getPotionEffect().isInstant();
		}
		return false;
	}

	@Override
	public CenserRecipeElixirContext createContext(ItemStack stack) {
		return new CenserRecipeElixirContext(stack);
	}

	private List<LivingEntity> getAffectedEntities(World world, BlockPos pos, CenserRecipeElixirContext context) {
		int amplifier = ((ItemElixir)ItemRegistry.ELIXIR.get()).createPotionEffect(context.elixir, 1).getAmplifier();

		int xzRange = 35 + amplifier * 15;
		int yRange = 12 + amplifier * 4;

		return world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos).inflate(xzRange, 1, xzRange).expandTowards(0, yRange, 0));
	}

	@Override
	public int update(CenserRecipeElixirContext context, ICenser censer) {
		World world = censer.getCenserWorld();

		if(world.getGameTime() % 100 == 0) {
			Effect potion = ((ItemElixir)ItemRegistry.ELIXIR.get()).getElixirFromItem(context.elixir).getPotionEffect();

			int maxDuration = ((ItemElixir)ItemRegistry.ELIXIR.get()).createPotionEffect(context.elixir, 0.25D).getDuration();

			BlockPos pos = censer.getCenserPos();

			List<LivingEntity> affected = this.getAffectedEntities(world, pos, context);

			if(!world.isClientSide()) {
				for(LivingEntity living : affected) {
					living.addEffect(new EffectInstance(potion, Math.min(maxDuration, 300), 0, true, false));
				}
			}

			context.setConsuming(!affected.isEmpty());
		}

		return 0;
	}

	private int getEffectiveDuration(CenserRecipeElixirContext context) {
		EffectInstance effect = ((ItemElixir)ItemRegistry.ELIXIR.get()).createPotionEffect(context.elixir, 1.0D);
		return 20 + effect.getDuration() * 8;
	}

	@Override
	public int getConsumptionDuration(CenserRecipeElixirContext context, ICenser censer) {
		return Math.max(1, MathHelper.floor(this.getEffectiveDuration(context) / 1000.0f));
	}

	@Override
	public int getConsumptionAmount(CenserRecipeElixirContext context, ICenser censer) {
		if(!context.isConsuming()) {
			return 0;
		}
		return 1 + MathHelper.floor(1000.0f / this.getEffectiveDuration(context));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getEffectColor(CenserRecipeElixirContext context, ICenser censer, EffectColorType type) {
		return ((ItemElixir) context.elixir.getItem()).getColorMultiplier(context.elixir, 0);
	}
}
