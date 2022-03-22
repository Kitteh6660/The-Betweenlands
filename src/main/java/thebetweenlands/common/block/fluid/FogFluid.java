package thebetweenlands.common.block.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class FogFluid extends Fluid
{

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
	protected Vector3d getFlow(IBlockReader p_215663_1_, BlockPos p_215663_2_, FluidState p_215663_3_) {
		// TODO Auto-generated method stub
		return null;
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
	public float getHeight(FluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getOwnHeight(FluidState p_223407_1_) {
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

	@Override
	public VoxelShape getShape(FluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_) {
		// TODO Auto-generated method stub
		return null;
	}

}
