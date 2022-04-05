package thebetweenlands.common.block.misc;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.tile.TileEntityOfferingTable;

public class BlockOfferingTable extends BlockGroundItem {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);	

	public BlockOfferingTable(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.STONE);
		this.setHardness(0.5F);*/
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return BOUNDING_AABB;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
		TileEntity tileEntity = level.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityGroundItem) {
			TileEntityGroundItem tile = (TileEntityGroundItem) tileEntity;

			ItemStack stack = player.getItemInHand(hand);

			if(!stack.isEmpty() && tile.getStack().isEmpty()) {
				if(!level.isClientSide()) {
					tile.setStack(stack);
					player.setItemInHand(hand, ItemStack.EMPTY);
				}

				return true;
			}
		}

		return super.onBlockActivated(level, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.yRot * 4.0F / 360.0F + 0.5D + 2) & 3;
		state = state.setValue(FACING, Direction.byHorizontalIndex(rotation));
		level.setBlockState(pos, state, 3);
	}

	@Override
	public boolean canStay(World world, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean canHarvestBlock(IBlockReader world, BlockPos pos, PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(getItemBlock());
	}

	@Override
	protected void onItemTaken(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) { }

	@Override
	public BlockItem getItemBlock() {
		return BlockRegistry.ICustomItemBlock.getDefaultItemBlock(this);
	}

	@Override
	public TileEntity newBlockEntity(World world, BlockState state) {
		return new TileEntityOfferingTable();
	}
}
