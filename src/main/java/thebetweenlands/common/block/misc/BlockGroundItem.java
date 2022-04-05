package thebetweenlands.common.block.misc;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;

public class BlockGroundItem extends Block {
	
	public static final VoxelShape BOUNDING_AABB = Block.box(0.15F, 0.0F, 0.15F, 0.85F, 0.45F, 0.85F);

	public BlockGroundItem(Properties properties) {
		super(properties);
		/*super(material);
		setSoundType(SoundType.GROUND);
		setHardness(0.1F);*/
		registerDefaultState(this.stateDefinition.any());
	}

	public static void create(World world, BlockPos pos, ItemStack stack) {
		Block block = BlockRegistry.GROUND_ITEM.get();
		if (!world.isClientSide() && block.canPlaceBlockAt(world, pos)) {
			world.setBlockAndUpdate(pos, block.defaultBlockState());
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof TileEntityGroundItem) {
				((TileEntityGroundItem) tileEntity).setStack(stack);
			}
		}
	}

	//TODO: Remove this code.
	/*@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState();
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return 0;
	}

	@Nullable
	@Override
	public BlockItem getItemBlock() {
		return null;
	}*/

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityGroundItem) {
			return ((TileEntityGroundItem) tileEntity).getStack();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
		TileEntity tileEntity = level.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityGroundItem && !level.isClientSide()) {
			ItemStack itemStack = ((TileEntityGroundItem) tileEntity).getStack();
			ItemHandlerHelper.giveItemToPlayer(player, itemStack);
			((TileEntityGroundItem) tileEntity).setStack(ItemStack.EMPTY);

			this.onItemTaken(level, pos, player, hand, itemStack);
		}
		return super.onBlockActivated(level, pos, state, player, hand, hitResult.getDirection(), hitResult.getBlockPos());
	}

	protected void onItemTaken(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}

	public boolean canStay(World world, BlockPos pos) {
		BlockState downState = world.getBlockState(pos);
		return downState.isSideSolid(world, pos, Direction.UP) || downState.getBlockFaceShape(world, pos, Direction.UP) == BlockFaceShape.SOLID;
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!canStay(level, pos.below())){
			TileEntity tileEntity = level.getBlockEntity(pos);
			if (tileEntity instanceof TileEntityGroundItem && !level.isClientSide() && !((TileEntityGroundItem) tileEntity).getStack().isEmpty()) {
				spawnAsEntity(level, pos, ((TileEntityGroundItem) tileEntity).getStack());
			}
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public void harvestBlock(World level, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		if (te instanceof TileEntityGroundItem && !level.isClientSide() && !((TileEntityGroundItem) te).getStack().isEmpty()) {
			spawnAsEntity(level, pos, ((TileEntityGroundItem) te).getStack());
		}

		super.harvestBlock(level, player, pos, state, te, stack);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return BOUNDING_AABB;
	}

	@Nullable
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return NULL_AABB;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this);
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
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader level, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isBlockNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World level, BlockPos pos) {
		return super.canPlaceBlockAt(level, pos) && canStay(level, pos.below());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity newBlockEntity(World world, BlockState state) {
		return new TileEntityGroundItem();
	}
}
