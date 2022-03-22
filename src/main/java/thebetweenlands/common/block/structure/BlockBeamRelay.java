package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityBeamRelay;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockBeamRelay extends BlockDirectional implements ITileEntityProvider, IStateMappedBlock {
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public BlockBeamRelay() {
		super(Material.ROCK);
		setDefaultState(this.getBlockState().getBaseState().setValue(POWERED, false));
		setHardness(10.0F);
		setResistance(2000.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
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
		return false;
	}

	@Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBeamRelay();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState().setValue(FACING, Direction.byIndex(meta)).setValue(POWERED, Boolean.valueOf((meta & 8) > 0));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		meta = meta | ((Direction) state.getValue(FACING)).getIndex();

		if (((Boolean) state.getValue(POWERED)).booleanValue())
			meta |= 8;

		return meta;
	}

	@Override
	 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return this.defaultBlockState().setValue(FACING, getFacingFromEntity(pos, placer)).setValue(POWERED, world.isBlockPowered(pos));
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, POWERED });
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, BlockRayTraceResult hitResult) {
		if (world.isClientSide())
			return true;
		if (world.getBlockEntity(pos) instanceof TileEntityBeamRelay) {
			TileEntityBeamRelay tile = (TileEntityBeamRelay) world.getBlockEntity(pos);
			tile.deactivateBlock();
		}
		state = state.cycleProperty(FACING);
		world.setBlockState(pos, state, 3);
		world.playSound((PlayerEntity)null, pos, SoundRegistry.BEAM_SWITCH, SoundCategory.BLOCKS, 0.5F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.9F);
		return true;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (world.getBlockEntity(pos) instanceof TileEntityBeamRelay) {
			TileEntityBeamRelay tile = (TileEntityBeamRelay) world.getBlockEntity(pos);
			tile.deactivateBlock();
		}
    }

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(new IProperty[] {POWERED}).build();
	}
}
