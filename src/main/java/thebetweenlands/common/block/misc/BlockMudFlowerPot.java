package thebetweenlands.common.block.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.plant.BlockStackablePlant;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.util.StatePropertyHelper;

public class BlockMudFlowerPot extends ContainerBlock {
	
	protected static final VoxelShape FLOWER_POT_AABB = Block.box(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);

	public static final PropertyBlockStateUnlisted FLOWER = new PropertyBlockStateUnlisted("flower");

	protected Map<Item, Function<ItemStack, BlockState>> plants = new HashMap<>();

	public BlockMudFlowerPot(Properties properties) {
		super(properties);
		/*super(Material.CIRCUITS);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(0.3F);
		this.setSoundType(SoundType.STONE);*/
		this.useNeighborBrightness = true;
	}

	/**
	 * Registers a plant so it can be placed in the pot
	 * @param item
	 * @param provider
	 */
	public void registerPlant(Item item, Function<ItemStack, BlockState> provider) {
		this.plants.put(item, provider);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return FLOWER_POT_AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{ FLOWER });
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
		state = ((IExtendedBlockState)state).setValue(FLOWER, Blocks.AIR.defaultBlockState());

		TileEntityMudFlowerPot te = StatePropertyHelper.getTileEntityThreadSafe(world, pos, TileEntityMudFlowerPot.class);

		if(te != null && !te.getFlowerItemStack().isEmpty()) {
			BlockState blockState = this.getPlantBlockStateFromItem(te.getFlowerItemStack());
			if(blockState != null) {
				state = ((IExtendedBlockState)state).setValue(FLOWER, blockState);
			}
		}

		return state;
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
		ItemStack heldItem = player.getItemInHand(hand);
		TileEntityMudFlowerPot te = this.getBlockEntity(level, pos);

		if (te == null) {
			return false;
		} else {
			ItemStack itemstack1 = te.getFlowerItemStack();

			if (itemstack1.isEmpty()) {
				if (this.getPlantBlockStateFromItem(heldItem) != null) {
					if(!level.isClientSide()) {
						te.setItemStack(heldItem);
						player.addStat(StatList.FLOWER_POTTED);
	
						if (!playerIn.isCreative()) {
							heldItem.shrink(1);
						}
					}
				} else if(Block.getBlockFromItem(heldItem.getItem()) == BlockRegistry.SULFUR_TORCH) {
					if(!level.isClientSide()) {
						level.setBlockState(pos, BlockRegistry.MUD_FLOWER_POT_CANDLE.defaultBlockState());
						
						level.playSound(null, pos, SoundType.WOOD.getPlaceSound(), SoundCategory.BLOCKS, (SoundType.WOOD.getVolume() + 1.0F) / 2.0F, SoundType.WOOD.getPitch() * 0.8F);
						
						if(!player.isCreative()) {
							heldItem.shrink(1);
						}
					}
					return true;
				} else {
					return false;
				}
			} else if(!level.isClientSide()) {
				if (heldItem.isEmpty()) {
					player.setItemInHand(hand, itemstack1);
				} else if (!player.addItem(itemstack1)) {
					player.drop(itemstack1, false);
				}

				te.setItemStack(ItemStack.EMPTY);
			}
			
			if(!level.isClientSide()) {
				te.setChanged();
				level.sendBlockUpdated(pos, state, state, 3);
			}
			
			return true;
		}
	}

	/**
	 * Returns the blockstate that should be used for the specified item.
	 * Return null if item can't be contained in pot
	 * @param itemStack
	 * @return
	 */
	@Nullable
	protected BlockState getPlantBlockStateFromItem(ItemStack itemStack) {
		if(!itemStack.isEmpty() && itemStack.getItem() instanceof BlockItem) {
			BlockItem item = (BlockItem)itemStack.getItem();

			Block block = item.getBlock();
			if(block != Blocks.AIR) {
				if(block.defaultBlockState().is(BlockTags.FLOWERS) || (block == Blocks.TALLGRASS && itemStack.getMetadata() == BlockTallGrass.EnumType.FERN.getMeta())
						|| (block instanceof BlockPlant && !(block instanceof BlockStackablePlant))) {

					return block.getStateFromMeta(itemStack.getMetadata());
				}
			} else if(item == ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM.get()) {
				return BlockRegistry.BULB_CAPPED_MUSHROOM.get().defaultBlockState();
			} else if(item == ItemRegistry.BLACK_HAT_MUSHROOM_ITEM.get()) {
				return BlockRegistry.BLACK_HAT_MUSHROOM.get().defaultBlockState();
			} else if(item == ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM.get()) {
				return BlockRegistry.FLAT_HEAD_MUSHROOM.get().defaultBlockState();
			}

			Function<ItemStack, BlockState> provider = this.plants.get(item);
			if(provider != null) {
				return provider.apply(itemStack);
			}
		}

		return null;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		TileEntityMudFlowerPot te = this.getBlockEntity(world, pos);

		if (te != null) {
			ItemStack itemstack = te.getFlowerItemStack();

			if (!itemstack.isEmpty()) {
				return itemstack;
			}
		}

		return new ItemStack(BlockRegistry.MUD_FLOWER_POT);
	}

	@Override
	public boolean canPlaceBlockAt(World level, BlockPos pos) {
		return super.canPlaceBlockAt(level, pos) && level.getBlockState(pos.below()).isSideSolid(level, pos.below(), Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!level.getBlockState(pos.below()).isSideSolid(level, pos.below(), Direction.UP)) {
			this.dropBlockAsItem(level, pos, state, 0);
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public void onBlockHarvested(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(level, pos, state, player);

		if (player.isCreative()) {
			TileEntityMudFlowerPot te = this.getBlockEntity(level, pos);

			if (te != null) {
				te.setItemStack(ItemStack.EMPTY);
			}
		}
	}

	@Nullable
	private TileEntityMudFlowerPot getBlockEntity(IBlockReader level, BlockPos pos) {
		TileEntity tileentity = level.getBlockEntity(pos);
		return tileentity instanceof TileEntityMudFlowerPot ? (TileEntityMudFlowerPot) tileentity : null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	/*============================FORGE START=====================================*/
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		TileEntityMudFlowerPot te = getBlockEntity(world, pos);
		if (te != null && te.getFlowerPotItem() != null)
			drops.add(new ItemStack(te.getFlowerPotItem(), 1, te.getFlowerPotData()));
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		if (willHarvest)
			return true; //If it will harvest, delay deletion of the block until after getDrops
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack tool) {
		super.harvestBlock(world, player, pos, state, te, tool);
		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}
	/*===========================FORGE END==========================================*/

	@Override
	public TileEntity createNewTileEntity(World level, int meta) {
		return new TileEntityMudFlowerPot();
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader level, BlockState state, BlockPos pos, Direction face) {
    	return face == Direction.DOWN ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
    }

	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		// TODO Auto-generated method stub
		return null;
	}
}