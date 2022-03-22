package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockNettle extends BlockPlant {
	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if(rand.nextInt(240) == 0) {
			worldIn.setBlockState(pos, BlockRegistry.NETTLE_FLOWERED.defaultBlockState());
		}
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if(!worldIn.isClientSide() && entityIn instanceof IEntityBL == false && entityIn instanceof LivingEntity && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((LivingEntity)entityIn)) {
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
		}
	}
}
