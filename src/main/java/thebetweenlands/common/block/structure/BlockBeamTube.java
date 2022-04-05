package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockBeamTube extends DirectionalBlock {

	public BlockBeamTube(Properties properties) {
		super(properties);
		//super(Material.ROCK);
		/*setHardness(10.0F);
		setResistance(2000.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return true;
	}


	@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState().setValue(FACING, Direction.byIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		meta = meta | ((Direction) state.getValue(FACING)).getIndex();
		return meta;
	}

	@Override
	 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return this.defaultBlockState().setValue(FACING, getFacingFromEntity(pos, placer));
	}

	public static Direction getFacingFromEntity(BlockPos pos, LivingEntity entity) {
		if (MathHelper.abs((float) entity.getX() - (float) pos.getX()) < 2.0F && MathHelper.abs((float) entity.getZ() - (float) pos.getZ()) < 2.0F) {
			double eyeHeight = entity.getY() + (double) entity.getEyeHeight();
			if (eyeHeight - (double) pos.getY() > 2.0D)
				return Direction.UP;
			if ((double) pos.getY() - eyeHeight > 0.0D)
				return Direction.DOWN;
		}
		return entity.getDirection().getOpposite();
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate((Direction) state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((Direction) state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}
}
