package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.properties.DirectionProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.misc.ItemRuneDoorKey;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockDungeonDoorRunes extends BasicBlock implements ITileEntityProvider, IStateMappedBlock {
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");

	public final boolean mimic;
	public final boolean barrishee;
	
	public BlockDungeonDoorRunes(boolean mimic, boolean barrishee) {
		this(Material.ROCK, mimic, barrishee);
	}

	public BlockDungeonDoorRunes(Material material, boolean mimic, boolean barrishee) {
		super(material);
		setBlockUnbreakable();
		setResistance(2000.0F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(INVISIBLE, false));
		setLightOpacity(255);
		useNeighborBrightness = true;
		this.mimic = mimic;
		this.barrishee = barrishee;
	}
	
	@Nullable
	public static TileEntityDungeonDoorRunes getBlockEntity(IBlockReader world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof TileEntityDungeonDoorRunes) {
			return (TileEntityDungeonDoorRunes) tile;
		}
		return null;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
    public boolean isFullCube(BlockState state) {
        return false;
    }
	
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if(!getStateFromMeta(meta).getValue(INVISIBLE))
			return new TileEntityDungeonDoorRunes(this.mimic, barrishee);
		return null;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta & 0b111); // Using this instead of 'byHorizontalIndex' because the ids don't match and previous was release
		return defaultBlockState().setValue(FACING, facing.getAxis().isHorizontal() ? facing: Direction.NORTH).setValue(INVISIBLE, (meta & 8) > 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		meta = meta | state.getValue(FACING).getIndex();
		if (state.getValue(INVISIBLE))
			meta |= 8;
		return meta;
	}

	@Override
	 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite()).setValue(INVISIBLE, false);
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, INVISIBLE);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		if (!state.getValue(INVISIBLE)) {
			TileEntityDungeonDoorRunes tile = getBlockEntity(world, pos);
			if (tile != null) {
				tile.breakAllDoorBlocks(state, state.getValue(FACING), false, true);
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, BlockState state) {
		BlockState invisiBlock = defaultBlockState().setValue(INVISIBLE, true);
		if (!state.getValue(INVISIBLE)) {
			if (state.getValue(FACING) == Direction.WEST || state.getValue(FACING) == Direction.EAST) {
				for (int z = -1; z <= 1; z++)
					for (int y = -1; y <= 1; y++)
						if(pos.offset(0, y, z) != pos)
							world.setBlockState(pos.offset(0, y, z), invisiBlock.setValue(FACING, state.getValue(FACING)));
			}
			if (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH) {
				for (int x = -1; x <= 1; x++)
					for (int y = -1; y <= 1; y++) {
						if(pos.offset(x, y, 0) != pos)
							world.setBlockState(pos.offset(x, y, 0), invisiBlock.setValue(FACING, state.getValue(FACING)));
					}
			}
		}
		world.sendBlockUpdated(pos, state, state, 3);
	}

	@Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, Direction side) {
    	if (side == Direction.WEST || side == Direction.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = -1; y <= 1; y++)
					if(!world.getBlockState(pos.offset(0, y, z)).getBlock().isReplaceable(world, pos.offset(0, y, z)))
						return false;
		}
		if (side == Direction.NORTH || side == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = -1; y <= 1; y++) {
					if(!world.getBlockState(pos.offset(x, y, 0)).getBlock().isReplaceable(world, pos.offset(x, y, 0)))
						return false;
				}
		}
		if (side == Direction.UP || side == Direction.DOWN)
			return false;
        return canPlaceBlockAt(world, pos);
    }

	@Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) { // added because additional logic will be needed
        return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
    }

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		if (!state.getValue(INVISIBLE) && hand == Hand.MAIN_HAND) {
			if(!world.isClientSide()) {
				TileEntityDungeonDoorRunes tile = getBlockEntity(world, pos);
				if (tile != null && facing == state.getValue(FACING) && !tile.is_gate_entrance) {
					if (stack.getItem() instanceof ItemRuneDoorKey) {
						tile.top_state = tile.top_code;
						tile.mid_state = tile.mid_code;
						tile.bottom_state = tile.bottom_code;
						if(!player.isCreative())
							stack.shrink(1);
						world.sendBlockUpdated(pos, state, state, 3);
						return true;
					}
	
					if(player.isCreative() && player.isCrouching()) {
						tile.enterLockCode();
						player.sendStatusMessage(new TranslationTextComponent("chat.dungeon_door_runes.locked"), true);
					} else {
						if(hitY >= 0.0625F && hitY < 0.375F && tile.bottom_rotate == 0)
							tile.cycleBottomState();
						if(hitY >= 0.375F && hitY < 0.625F && tile.mid_rotate == 0)
							tile.cycleMidState();
						if(hitY >= 0.625F && hitY <= 0.9375F &&  tile.top_rotate == 0)
							tile.cycleTopState();
					}
					world.sendBlockUpdated(pos, state, state, 3);
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader world, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(INVISIBLE).build();
	}
}
