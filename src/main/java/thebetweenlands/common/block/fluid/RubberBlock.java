package thebetweenlands.common.block.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;
import net.minecraft.fluid.WaterFluid;

public class RubberBlock extends FlowingFluidBlock {
	
	public RubberBlock(Properties properties) {
		super(() -> FluidRegistry.RUBBER.get(), properties);
	}

	@Override
	public boolean canDisplace(IBlockReader world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid())
			return false;
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid())
			return false;
		return super.displaceIfPossible(world, pos);
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
	public Vector3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vector3d motion) {
		BlockPos entityPos = new BlockPos(entityIn.getX(), entityIn.getY() + 0.5D, entityIn.getZ());
		if(pos.equals(entityPos) /*make sure it's only changed once*/) {
			entityIn.motionX *= 0.35D;
			entityIn.motionY *= 0.8D;
			entityIn.motionY -= 0.01D;
			entityIn.motionZ *= 0.35D;
		}
		return new Vector3d(0, 0, 0);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(RubberBlock.LEVEL);
	}


}