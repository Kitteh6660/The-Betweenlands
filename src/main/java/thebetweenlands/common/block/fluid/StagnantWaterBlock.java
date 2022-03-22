package thebetweenlands.common.block.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class StagnantWaterBlock extends FlowingFluidBlock implements IStateMappedBlock, ICustomItemBlock {
	
	public StagnantWaterBlock(Properties properties) {
		super(() -> FluidRegistry.STAGNANT_WATER.get(), properties);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if (entityIn instanceof PlayerEntity && !worldIn.isClientSide() && !((PlayerEntity)entityIn).isPotionActive(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect())) {
			((PlayerEntity)entityIn).addEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(60, 3));
		}
		if(!worldIn.isClientSide() ) {
			IDecayCapability cap = entityIn.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if (cap != null) {
				cap.getDecayStats().addDecayAcceleration(0.1F);
			}
		}
	}

	@Override
	public boolean canDisplace(IBlockReader world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.displaceIfPossible(world, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(StagnantWaterBlock.LEVEL);
	}

	@Override
	public Boolean isEntityInsideMaterial(IBlockReader world, BlockPos blockpos, BlockState state, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
		if(materialIn == Material.WATER) {
			double liquidHeight = (double)((float)(blockpos.getY() + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)state.getValue(BlockLiquid.LEVEL)).intValue()));
			if(testingHead) {
				double liquidHeightBelow = 0;
				if(world.getBlockState(blockpos.above()).getBlock() == state.getBlock()) {
					liquidHeightBelow = (double)((float)(blockpos.getY() + 2) - BlockLiquid.getLiquidHeightPercent(((Integer)world.getBlockState(blockpos.above()).getValue(BlockLiquid.LEVEL)).intValue()));
				}
				return entity.getY() + entity.getEyeHeight() < 0.1D + liquidHeight || entity.getY() + entity.getEyeHeight() < 0.1D + liquidHeightBelow;
			} else {
				return entity.getBoundingBox().maxY >= liquidHeight && entity.getBoundingBox().minY < liquidHeight;
			}
		}
		return null;
	}
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return 100;
	}
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}
}
