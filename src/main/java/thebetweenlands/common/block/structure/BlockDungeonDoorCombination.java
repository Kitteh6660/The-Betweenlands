package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.tile.TileEntityDungeonDoorCombination;

public class BlockDungeonDoorCombination extends Block implements ITileEntityProvider {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

	public BlockDungeonDoorCombination(Properties properties) {
		super(properties);
		/*setHardness(0.4F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	@Nullable
	public static TileEntityDungeonDoorCombination getBlockEntity(IBlockReader world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof TileEntityDungeonDoorCombination) {
			return (TileEntityDungeonDoorCombination) tile;
		}
		return null;
	}

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new TileEntityDungeonDoorCombination();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta); // Using this instead of 'byHorizontalIndex' because the ids don't match and previous was release
		return defaultBlockState().setValue(FACING, facing.getAxis().isHorizontal() ? facing: Direction.NORTH);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		meta = meta | state.getValue(FACING).getIndex();
		return meta;
	}

	@Override
	 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
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
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void onPlace(World world, BlockPos pos, BlockState state) {
		world.sendBlockUpdated(pos, state, state, 3);
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader world, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if (player.isCreative() && hand == Hand.MAIN_HAND) {
			if(!level.isClientSide()) {
				TileEntityDungeonDoorCombination tile = getBlockEntity(world, pos);
				if (tile != null && facing == state.getValue(FACING)) {
					if(hitY >= 0.0625F && hitY < 0.375F)
						tile.cycleBottomState();
					if(hitY >= 0.375F && hitY < 0.625F)
						tile.cycleMidState();
					if(hitY >= 0.625F && hitY <= 0.9375F)
						tile.cycleTopState();
					world.playLocalSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1F, 1.0F);
					world.sendBlockUpdated(pos, state, state, 3);
					return ActionResultType.SUCCESS;
				}
			} else {
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}
}
