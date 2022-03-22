package thebetweenlands.common.block.misc;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;

public class BlockGroundItem extends Block implements BlockRegistry.ICustomItemBlock {
	public static final AxisAlignedBB BOUNDING_AABB = Block.box(0.15F, 0.0F, 0.15F, 0.85F, 0.45F, 0.85F);

	protected BlockGroundItem(Material material) {
		super(material);
		setSoundType(SoundType.GROUND);
		setHardness(0.1F);
		setDefaultState(this.blockState.getBaseState());
	}

	public BlockGroundItem() {
		this(Material.GROUND);
	}

	public static void create(World world, BlockPos pos, ItemStack stack) {
		Block block = BlockRegistry.GROUND_ITEM;
		if (!world.isClientSide() && block.canPlaceBlockAt(world, pos)) {
			world.setBlockState(pos, block.defaultBlockState());
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof TileEntityGroundItem) {
				((TileEntityGroundItem) tileEntity).setStack(stack);
			}
		}
	}

	@Override
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
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityGroundItem) {
			return ((TileEntityGroundItem) tileEntity).getStack();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ActionResultType use(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityGroundItem && !worldIn.isClientSide()) {
			ItemStack itemStack = ((TileEntityGroundItem) tileEntity).getStack();
			ItemHandlerHelper.giveItemToPlayer(playerIn, itemStack);
			((TileEntityGroundItem) tileEntity).setStack(ItemStack.EMPTY);

			this.onItemTaken(worldIn, pos, playerIn, hand, itemStack);
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	protected void onItemTaken(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
		world.setBlockToAir(pos);
	}

	public boolean canStay(World world, BlockPos pos) {
		BlockState downState = world.getBlockState(pos);
		return downState.isSideSolid(world, pos, Direction.UP) || downState.getBlockFaceShape(world, pos, Direction.UP) == BlockFaceShape.SOLID;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!canStay(worldIn, pos.below())){
			TileEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof TileEntityGroundItem && !worldIn.isClientSide() && !((TileEntityGroundItem) tileEntity).getStack().isEmpty()) {
				spawnAsEntity(worldIn, pos, ((TileEntityGroundItem) tileEntity).getStack());
			}
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		if (te instanceof TileEntityGroundItem && !worldIn.isClientSide() && !((TileEntityGroundItem) te).getStack().isEmpty()) {
			spawnAsEntity(worldIn, pos, ((TileEntityGroundItem) te).getStack());
		}

		super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return BOUNDING_AABB;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	protected BlockStateContainer createBlockState() {
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
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
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
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && canStay(worldIn, pos.below());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileEntityGroundItem();
	}
}
