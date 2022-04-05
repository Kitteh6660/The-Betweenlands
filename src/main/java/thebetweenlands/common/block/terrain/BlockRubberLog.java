package thebetweenlands.common.block.terrain;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;
import net.minecraft.block.WallBlock;

public class BlockRubberLog extends RotatedPillarBlock implements IStateMappedBlock, IWaterLoggable 
{
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

	protected static final VoxelShape[] BOUNDING_BOXES = {
			//CENTER
			Block.box(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D),
			//NORTH
			Block.box(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 0.25D),
			//SOUTH
			Block.box(0.25D, 0.25D, 0.75D, 0.75D, 0.75D, 1.0D),
			//EAST
			Block.box(0.75D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D),
			//WEST
			Block.box(0.0D, 0.25D, 0.25D, 0.25D, 0.75D, 0.75D),
			//UP
			Block.box(0.25D, 0.75D, 0.25D, 0.75D, 1.0D, 0.75D),
			//DOWN
			Block.box(0.25D, 0.0D, 0.25D, 0.75D, 0.25D, 0.75D),
	};
	protected static final VoxelShape[] COMBINED_BOUNDING_BOXES = Block.box[64];

	static {
		List<VoxelShape> boxes = new ArrayList<VoxelShape>();
		for(int i = 0; i < 64; i++) {
			boolean north = (i & 1) == 1;
			boolean south = ((i >> 1) & 1) == 1;
			boolean east = ((i >> 2) & 1) == 1;
			boolean west = ((i >> 3) & 1) == 1;
			boolean up = ((i >> 4) & 1) == 1;
			boolean down = ((i >> 5) & 1) == 1;
			boxes.clear();
			boxes.add(BOUNDING_BOXES[0]);
			if(north)
				boxes.add(BOUNDING_BOXES[1]);
			if(south)
				boxes.add(BOUNDING_BOXES[2]);
			if(east)
				boxes.add(BOUNDING_BOXES[3]);
			if(west)
				boxes.add(BOUNDING_BOXES[4]);
			if(up)
				boxes.add(BOUNDING_BOXES[5]);
			if(down)
				boxes.add(BOUNDING_BOXES[6]);
			double minX = 1.0D;
			double minY = 1.0D;
			double minZ = 1.0D;
			double maxX = 0.0D;
			double maxY = 0.0D;
			double maxZ = 0.0D;
			for(VoxelShape box : boxes) {
				if(box.min(Axis.X) < minX)
					minX = box.min(Axis.X);
				if(box.min(Axis.Y) < minY)
					minY = box.min(Axis.Y);
				if(box.min(Axis.Z) < minZ)
					minZ = box.min(Axis.Z);
				if(box.max(Axis.X) > maxX)
					maxX = box.max(Axis.X);
				if(box.max(Axis.Y) > maxY)
					maxY = box.max(Axis.Y);
				if(box.max(Axis.Z) > maxZ)
					maxZ = box.max(Axis.Z);
			}
			COMBINED_BOUNDING_BOXES[i] = Block.box(minX, minY, minZ, maxX, maxY, maxZ);
		}
	}

	public static AxisAlignedBB getCombinedBoundingBoxForState(BlockState state) {
		int index = 0;
		if(state.getValue(NORTH))
			index |= 1;
		if(state.getValue(SOUTH))
			index |= 2;
		if(state.getValue(EAST))
			index |= 4;
		if(state.getValue(WEST))
			index |= 8;
		if(state.getValue(UP))
			index |= 16;
		if(state.getValue(DOWN))
			index |= 32;
		return COMBINED_BOUNDING_BOXES[index];
	}

	public BlockRubberLog(Properties properties) {
		super(properties);
		/*this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
		registerDefaultState(this.stateDefinition.any().setValue(NATURAL, false));
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(NATURAL, meta == 1);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(NATURAL) ? 1 : 0;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { LOG_AXIS, UP, DOWN, NORTH, SOUTH, EAST, WEST, NATURAL });
	}

	@Override
	public boolean rotateBlock(net.minecraft.world.World world, BlockPos pos, Direction axis) {
		return false;
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state
				.setValue(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north())))
				.setValue(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east())))
				.setValue(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south())))
				.setValue(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west())))
				.setValue(UP, Boolean.valueOf(this.canConnectTo(worldIn, pos.above())))
				.setValue(DOWN, Boolean.valueOf(worldIn.getBlockState(pos.below()).isSideSolid(worldIn, pos.below(), Direction.UP) || this.canConnectTo(worldIn, pos.below())));
	}

	public boolean canConnectTo(IBlockReader worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		return block == this || block == BlockRegistry.LEAVES_RUBBER_TREE;
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		switch (rot) {
		case CLOCKWISE_180:
			return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
		case COUNTERCLOCKWISE_90:
			return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
		case CLOCKWISE_90:
			return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
		default:
			return state;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		switch (mirrorIn) {
		case LEFT_RIGHT:
			return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
		case FRONT_BACK:
			return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
		default:
			return super.withMirror(state, mirrorIn);
		}
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
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<VoxelShape> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		state = state.getActualState(worldIn, pos);

		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[0]);

		if(state.getValue(NORTH))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[1]);

		if(state.getValue(SOUTH))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[2]);

		if(state.getValue(EAST))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[3]);

		if(state.getValue(WEST))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[4]);

		if(state.getValue(UP))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[5]);

		if(state.getValue(DOWN))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[6]);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		state = this.getActualState(state, level, pos);
		return getCombinedBoundingBoxForState(state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(AXIS).ignore(NATURAL).withPropertySuffixFalse(NATURAL, "cut");
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState().setValue(NATURAL, placer instanceof PlayerEntity && ((PlayerEntity)placer).isCreative());
	}
	
	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		if (toolType == ToolType.AXE) {
			Block block = state.getBlock() == BlockRegistry.RUBBER_LOG ? BlockRegistry.STRIPPED_RUBBER_LOG : BlockRegistry.STRIPPED_RUBBER_WOOD;
			return block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)) : null;
		}
		return super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
}
