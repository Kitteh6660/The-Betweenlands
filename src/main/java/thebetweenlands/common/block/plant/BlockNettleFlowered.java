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

public class BlockNettleFlowered extends BlockPlant {
	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if(rand.nextInt(350) == 0) {
			BlockPos randOffset = pos.offset(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
			if(worldIn.isAreaLoaded(randOffset, 0) && worldIn.isEmptyBlock(randOffset) && canBlockStay(worldIn, randOffset, worldIn.getBlockState(randOffset))) {
				worldIn.setBlockState(randOffset, BlockRegistry.NETTLE.defaultBlockState());
			}
		}
		if(rand.nextInt(220) == 0) {
			worldIn.setBlockState(pos, BlockRegistry.NETTLE.defaultBlockState());
		}
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if(!worldIn.isClientSide() && entityIn instanceof IEntityBL == false && entityIn instanceof LivingEntity && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((LivingEntity)entityIn)) {
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
		}
	}
}
