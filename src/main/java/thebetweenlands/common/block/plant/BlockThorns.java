package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;

public class BlockThorns extends BlockVineBL {
	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if (!worldIn.isClientSide() && !(entityIn instanceof IEntityBL) && entityIn instanceof LivingEntity && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((LivingEntity)entityIn)) {
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
		}
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemPlantDrop.THORNS_ITEM.create(1));
	}
}
