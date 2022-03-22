package thebetweenlands.common.block.container;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntityItemShelf;

public class BlockItemShelf extends ContainerBlock 
{
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

	protected static final AxisAlignedBB WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D);
	protected static final AxisAlignedBB EAST_AABB = Block.box(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB SOUTH_AABB = Block.box(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);

	public BlockItemShelf(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);*/
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		switch ((Direction)state.getValue(FACING)) {
		default:
		case EAST:
			return EAST_AABB;
		case WEST:
			return WEST_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case NORTH:
			return NORTH_AABB;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getPlayer().getDirection());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlock(pos, state.setValue(FACING, placer.getDirection()), 2);
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, PlayerEntity player) {
		if(!world.isClientSide() && (!player.isSwingInProgress || player.prevSwingProgress != player.swingProgress)
				/*Ugly check so that it doesn't give 2 items when clicking with empty hand*/) {
			TileEntity te = world.getBlockEntity(pos);

			if(te instanceof TileEntityItemShelf) {
				BlockState state = world.getBlockState(pos);

				TileEntityItemShelf shelf = (TileEntityItemShelf) te;

				RayTraceResult ray = this.rayTrace(pos, player.getPositionEyes(1), player.getPositionEyes(1).add(player.getLookVec().scale(10)), this.getBoundingBox(state, world, pos));
				if(ray != null) {
					InvWrapper wrapper = new InvWrapper(shelf);

					int slot = this.getSlot(state.getValue(FACING), (float)(ray.hitVec.x - pos.getX()), (float)(ray.hitVec.y - pos.getY()), (float)(ray.hitVec.z - pos.getZ()));

					ItemStack result = wrapper.extractItem(slot, player.isCrouching() ? 64 : 1, true);
					if(!result.isEmpty() && result.getCount() > 0) {
						result = wrapper.extractItem(slot, result.getCount(), false);
						world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
						if(!player.inventory.addItemStackToInventory(result)) {
							player.entityDropItem(result, 0);
						}
						world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_PLACE, SoundCategory.BLOCKS, 1, 0.8f);
					}
				}
			}
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
		if(hand == Hand.MAIN_HAND) {
			if(!world.isClientSide()) {
				ItemStack heldItem = player.getItemInHand(hand);
				TileEntity te = world.getBlockEntity(pos);

				if(te instanceof TileEntityItemShelf) {
					TileEntityItemShelf shelf = (TileEntityItemShelf) te;

					InvWrapper wrapper = new InvWrapper(shelf);

					int slot = this.getSlot(state.getValue(FACING), hitResult);

					if(!heldItem.isEmpty()) {
						ItemStack result = wrapper.insertItem(slot, heldItem, true);
						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							result = wrapper.insertItem(slot, heldItem.copy(), false);
							world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
							if(!player.isCreative()) {
								player.setItemInHand(hand, result);
							}
							world.playSound(null, pos, SoundEvents.ITEM_FRAME_PLACE, SoundCategory.BLOCKS, 1, 1);
						}
					}
				}
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	protected int getSlot(Direction blockDir, BlockRayTraceResult hitResult) {
		float cx, cy;

		Vector3i up = new Vector3i(0, 1, 0);
		Vector3i dir = up.cross(blockDir.getNormal());

		cx = dir.getX() * hitResult.getBlockPos().getX() + dir.getZ() * hitResult.getBlockPos().getZ();
		cy = hitResult.getBlockPos().getY();
		
		if(cx <= 0.0D) {
			cx = cx + 1;
		}

		int slot = 0;

		if(cx >= 0.0D && cx <= 0.5D) {
			slot++;
		}

		if(cy >= 0.0D && cy <= 0.5D) {
			slot += 2;
		}

		return slot;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta);

		if (facing.getAxis() == Direction.Axis.Y) {
			facing = Direction.NORTH;
		}

		return this.defaultBlockState().setValue(FACING, facing);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityItemShelf();
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
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
}
