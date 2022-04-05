package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockPoisonIvy extends BlockVineBL implements ITintedBlock {

	public BlockPoisonIvy(Properties properties) {
		super(properties);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if (!worldIn.isClientSide() && !(entityIn instanceof IEntityBL) && entityIn instanceof LivingEntity && worldIn.random.nextInt(200) == 0 && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((LivingEntity)entityIn)) {
			((LivingEntity) entityIn).addEffect(new EffectInstance(Effects.POISON, 50, 25));
		}
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(ItemRegistry.POISON_IVY.get()));
	}
	
	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getAverageFoliageColor(worldIn, pos) : FoliageColors.getDefaultColor();
	}
}
