package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntitySpikeTrap;

public class BlockSpikeTrap extends DirectionalBlock {

	public BlockSpikeTrap(Properties properties) {
		super(properties);
		//super(Material.ROCK);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
		/*setSoundType(SoundType.STONE);
		setHardness(10F);
		setResistance(2000.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public TileEntity newBlockEntity(BlockState state, IBlockReader world) {
		return new TileEntitySpikeTrap();
	}

	@Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
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