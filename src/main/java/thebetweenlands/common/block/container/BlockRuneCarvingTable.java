package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemRuneCarvingTable;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;
import thebetweenlands.common.tile.TileEntityRuneCarvingTableFiller;

public class BlockRuneCarvingTable extends BasicBlock implements ITileEntityProvider, ICustomItemBlock {
	
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;
	public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);
	public static final BooleanProperty FULL_GRID = BooleanProperty.create("full_grid");

	public BlockRuneCarvingTable(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		setDefaultState(this.blockState.any().setValue(FACING, Direction.NORTH).setValue(PART, EnumPartType.MAIN).setValue(FULL_GRID, false));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
		return state.getValue(PART) == EnumPartType.MAIN ? Block.box(pos).expand(0, 1, 0) : Block.box(pos).expand(0, -1, 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		return blockState.getValue(PART) == EnumPartType.MAIN ? super.getCollisionBoundingBox(blockState, worldIn, pos) : null;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState().setValue(FACING, placer.getDirection()).setValue(FULL_GRID, this.checkFullGridState(world, pos));
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.setValue(FACING, placer.getDirection()).setValue(FULL_GRID, this.checkFullGridState(world, pos)), 2);
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand,  Direction side, BlockRayTraceResult hitResult) {
		TileEntity tile = world.getBlockEntity(pos);

		if(tile instanceof TileEntityRuneCarvingTable || tile instanceof TileEntityRuneCarvingTableFiller) {
			if(player.isCrouching()) {
				return false;
			}

			if(!world.isClientSide()) {
				if(state.getValue(PART) == EnumPartType.MAIN) {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_CARVING_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_CARVING_TABLE, world, pos.getX(), pos.getY() - 1, pos.getZ());
				}
			}

			return true;
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

		if(!worldIn.isClientSide()) {
			boolean fullGridState = this.checkFullGridState(worldIn, pos);
			if(state.getValue(FULL_GRID) != fullGridState) {
				worldIn.setBlockState(pos, state.setValue(FULL_GRID, fullGridState));

				if(!fullGridState) {
					TileEntity tile = worldIn.getBlockEntity(pos);

					if(tile instanceof TileEntityRuneCarvingTable) {
						TileEntityRuneCarvingTable carvingTable = (TileEntityRuneCarvingTable) tile;

						for(int i = 1; i < 9; ++i) {
							ItemStack stack = carvingTable.getItem(i);

							if(!stack.isEmpty()) {
								InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 0.5f, pos.getZ(), stack);

								carvingTable.setItem(i, ItemStack.EMPTY);
							}
						}
					}
				}
			}
		}

		if(state.getValue(PART) == EnumPartType.FILLER) {
			if(worldIn.getBlockState(pos.below()).getBlock() != this) {
				worldIn.setBlockToAir(pos);
			}
		} else if(worldIn.getBlockState(pos.above()).getBlock() != this) {
			if(!worldIn.isClientSide()) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
			}

			worldIn.setBlockToAir(pos);
		}
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
	public int getMetaFromState(BlockState state) {
		return (state.getValue(FULL_GRID) ? 0b1000 : 0) | state.getValue(FACING).getHorizontalIndex() << 1 | (state.getValue(PART) == EnumPartType.FILLER ? 0b1 : 0b0);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta >> 1)).setValue(PART, (meta & 0b1) != 0 ? EnumPartType.FILLER : EnumPartType.MAIN).setValue(FULL_GRID, (meta & 0b1000) != 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRuneCarvingTable();
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return state.getValue(PART) == EnumPartType.MAIN ? new TileEntityRuneCarvingTable() : new TileEntityRuneCarvingTableFiller();
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, BlockState state, float chance, int fortune) {
		if(state.getValue(PART) == EnumPartType.MAIN) {
			super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
		}
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return state.getValue(PART) == EnumPartType.MAIN ? super.getItemDropped(state, rand, fortune) : Items.AIR;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public boolean hasCustomBreakingProgress(BlockState state) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, PART, FULL_GRID);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	protected boolean checkFullGridState(IBlockReader world, BlockPos pos) {
		for(Direction facing : Direction.VALUES) {
			if(facing != Direction.UP) {
				BlockState state = world.getBlockState(pos.offset(facing));
				if(state.getBlock() instanceof BlockWeedwoodWorkbench || state.getBlock() instanceof BlockWorkbench) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public BlockItem getItemBlock() {
		return new ItemRuneCarvingTable();
	}

	public static enum EnumPartType implements IStringSerializable {
		MAIN("main"),
		FILLER("filler");

		private final String name;

		private EnumPartType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getName() {
			return this.name;
		}
	}
}