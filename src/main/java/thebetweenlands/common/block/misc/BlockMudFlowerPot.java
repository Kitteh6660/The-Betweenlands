package thebetweenlands.common.block.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.plant.BlockPlantUnderwater;
import thebetweenlands.common.block.plant.BlockStackablePlant;
import thebetweenlands.common.block.property.PropertyBlockStateUnlisted;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.util.StatePropertyHelper;

public class BlockMudFlowerPot extends ContainerBlock {
	protected static final AxisAlignedBB FLOWER_POT_AABB = Block.box(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);

	public static final PropertyBlockStateUnlisted FLOWER = new PropertyBlockStateUnlisted("flower");

	protected Map<Item, Function<ItemStack, BlockState>> plants = new HashMap<>();

	public BlockMudFlowerPot() {
		super(Material.CIRCUITS);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(0.3F);
		this.setSoundType(SoundType.STONE);
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
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
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
	protected BlockStateContainer createBlockState() {
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
	public ActionResultType use(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack heldItem = playerIn.getItemInHand(hand);
		TileEntityMudFlowerPot te = this.getBlockEntity(worldIn, pos);

		if (te == null) {
			return false;
		} else {
			ItemStack itemstack1 = te.getFlowerItemStack();

			if (itemstack1.isEmpty()) {
				if (this.getPlantBlockStateFromItem(heldItem) != null) {
					if(!worldIn.isClientSide()) {
						te.setItemStack(heldItem);
						playerIn.addStat(StatList.FLOWER_POTTED);
	
						if (!playerIn.isCreative()) {
							heldItem.shrink(1);
						}
					}
				} else if(Block.getBlockFromItem(heldItem.getItem()) == BlockRegistry.SULFUR_TORCH) {
					if(!worldIn.isClientSide()) {
						worldIn.setBlockState(pos, BlockRegistry.MUD_FLOWER_POT_CANDLE.defaultBlockState());
						
						worldIn.playSound(null, pos, SoundType.WOOD.getPlaceSound(), SoundCategory.BLOCKS, (SoundType.WOOD.getVolume() + 1.0F) / 2.0F, SoundType.WOOD.getPitch() * 0.8F);
						
						if(!playerIn.isCreative()) {
							heldItem.shrink(1);
						}
					}
					return true;
				} else {
					return false;
				}
			} else if(!worldIn.isClientSide()) {
				if (heldItem.isEmpty()) {
					playerIn.setItemInHand(hand, itemstack1);
				} else if (!playerIn.addItemStackToInventory(itemstack1)) {
					playerIn.dropItem(itemstack1, false);
				}

				te.setItemStack(ItemStack.EMPTY);
			}
			
			if(!worldIn.isClientSide()) {
				te.setChanged();
				worldIn.sendBlockUpdated(pos, state, state, 3);
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
		if(!itemStack.isEmpty()) {
			Item item = itemStack.getItem();

			Block block = Block.getBlockFromItem(itemStack.getItem());
			if(block != Blocks.AIR) {
				if(block == Blocks.YELLOW_FLOWER || block == Blocks.RED_FLOWER ||
						block == Blocks.CACTUS || block == Blocks.BROWN_MUSHROOM ||
						block == Blocks.RED_MUSHROOM || block == Blocks.SAPLING ||
						block == Blocks.DEADBUSH ||
						(block == Blocks.TALLGRASS && itemStack.getMetadata() == BlockTallGrass.EnumType.FERN.getMeta())
						|| (block instanceof BlockPlant && !(block instanceof BlockStackablePlant))) {

					return block.getStateFromMeta(itemStack.getMetadata());
				}
			} else if(item == ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM) {
				return BlockRegistry.BULB_CAPPED_MUSHROOM.defaultBlockState();
			} else if(item == ItemRegistry.BLACK_HAT_MUSHROOM_ITEM) {
				return BlockRegistry.BLACK_HAT_MUSHROOM.defaultBlockState();
			} else if(item == ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM) {
				return BlockRegistry.FLAT_HEAD_MUSHROOM.defaultBlockState();
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
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.below()).isSideSolid(worldIn, pos.below(), Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!worldIn.getBlockState(pos.below()).isSideSolid(worldIn, pos.below(), Direction.UP)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);

		if (player.isCreative()) {
			TileEntityMudFlowerPot te = this.getBlockEntity(worldIn, pos);

			if (te != null) {
				te.setItemStack(ItemStack.EMPTY);
			}
		}
	}

	@Nullable
	private TileEntityMudFlowerPot getBlockEntity(IBlockReader worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getBlockEntity(pos);
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
		world.setBlockToAir(pos);
	}
	/*===========================FORGE END==========================================*/

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMudFlowerPot();
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return face == Direction.DOWN ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
    }
}