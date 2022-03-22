package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;

public class BlockPoisonIvy extends BlockVineBL implements ITintedBlock {

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if (!worldIn.isClientSide() && !(entityIn instanceof IEntityBL) && entityIn instanceof LivingEntity && worldIn.rand.nextInt(200) == 0 && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((LivingEntity)entityIn)) {
			((LivingEntity) entityIn).addEffect(new EffectInstance(Effects.POISON, 50, 25));
		}
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemPlantDrop.POISON_IVY_ITEM.create(1));
	}
	
	@Override
	public int getColorMultiplier(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	}
}
