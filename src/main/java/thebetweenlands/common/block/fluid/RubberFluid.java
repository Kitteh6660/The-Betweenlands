package thebetweenlands.common.block.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
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

public class RubberFluid extends FlowingFluid {
	
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
		builder.ignore(RubberFluid.LEVEL);
	}

	@Override
	public Fluid getFlowing() {
		return FluidRegistry.FLOWING_RUBBER.get();
	}

	@Override
	public Fluid getSource() {
		return FluidRegistry.RUBBER.get();
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(IWorld p_205580_1_, BlockPos p_205580_2_, BlockState p_205580_3_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getSlopeFindDistance(IWorldReader p_185698_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getDropOff(IWorldReader p_204528_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Item getBucket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTickDelay(IWorldReader p_205569_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected float getExplosionResistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState p_204527_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSource(FluidState p_207193_1_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAmount(FluidState p_207192_1_) {
		// TODO Auto-generated method stub
		return 0;
	}
}