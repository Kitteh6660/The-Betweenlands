package thebetweenlands.common.recipe.censer;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class CenserRecipeSwampWater extends AbstractCenserRecipe<CenserRecipeSwampWaterContext> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "swamp_water");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(FluidStack stack) {
		return stack.getFluid() == FluidRegistry.SWAMP_WATER;
	}

	@Override
	public boolean matchesSecondaryInput(ItemStack stack) {
		return stack.getItem() == ItemRegistry.BARK_AMULET;
	}

	@Override
	public CenserRecipeSwampWaterContext createContext(FluidStack stack) {
		return new CenserRecipeSwampWaterContext();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getEffectColor(CenserRecipeSwampWaterContext context, ICenser censer, EffectColorType type) {
		return 0xFFEEEEEE;
	}

	private List<PlayerEntity> getAffectedEntities(World world, BlockPos pos) {
		return world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(pos).inflate(45, 1, 45).expandTowards(0, 16, 0));
	}

	@Override
	public int update(CenserRecipeSwampWaterContext context, ICenser censer) {
		ItemStack inputStack = censer.getInputStack();

		if(!inputStack.isEmpty() && inputStack.getItem() == ItemRegistry.BARK_AMULET) {
			World world = censer.getCenserWorld();

			if(world.getGameTime() % 100 == 0) {
				BlockPos pos = censer.getCenserPos();

				List<PlayerEntity> affected = this.getAffectedEntities(world, pos);

				if(!world.isClientSide()) {
					for(PlayerEntity player : affected) {
						player.addEffect(new EffectInstance(ElixirEffectRegistry.ENLIGHTENED, 200, 0, true, false));
					}
				}

				context.setConsuming(!affected.isEmpty());
			}
		} else {
			context.setConsuming(false);
		}

		return 0;
	}

	@Override
	public int getConsumptionDuration(CenserRecipeSwampWaterContext context, ICenser censer) {
		//7.5min / bucket
		return 9;
	}

	@Override
	public int getConsumptionAmount(CenserRecipeSwampWaterContext context, ICenser censer) {
		return context.isConsuming() ? 1 : 0;
	}
}
