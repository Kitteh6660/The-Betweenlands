package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalFaceBlock;
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
import thebetweenlands.common.item.ItemRuneWeavingTable;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.tile.TileEntityRuneWeavingTable;
import thebetweenlands.common.tile.TileEntityRuneWeavingTableFiller;

public class BlockRuneWeavingTable extends ContainerBlock implements ICustomItemBlock {
	
	public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

	public BlockRuneWeavingTable(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(PART, EnumPartType.MAIN));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		if(state.getValue(PART) == EnumPartType.MAIN) {
			facing = facing.rotateY();
		} else {
			facing = facing.rotateYCCW();
		}
		return Block.box(pos).expand(facing.getStepX(), 0, facing.getStepZ());
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRuneWeavingTable();
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return state.getValue(PART) == EnumPartType.MAIN ? new TileEntityRuneWeavingTable() : new TileEntityRuneWeavingTableFiller();
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand){
		return this.defaultBlockState().setValue(FACING, placer.getDirection());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlock(pos, state.setValue(FACING, placer.getDirection()), 2);
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult){
		TileEntity tile = world.getBlockEntity(pos);

		if(tile instanceof TileEntityRuneWeavingTable || tile instanceof TileEntityRuneWeavingTableFiller) {
			if(player.isCrouching()) {
				return false;
			}

			if(!world.isClientSide()) {
				if(state.getValue(PART) == EnumPartType.MAIN) {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_WEAVING_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					BlockPos offsetPos = pos.offset(state.getValue(FACING).rotateYCCW());
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_WEAVING_TABLE, world, offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		TileEntity tileEntity = world.getBlockEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileEntity);
			world.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(world, pos, state);
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
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		Direction offset = state.getValue(FACING).rotateYCCW();

		if(state.getValue(PART) == EnumPartType.FILLER) {
			if(worldIn.getBlockState(pos.offset(offset)).getBlock() != this) {
				worldIn.setBlockToAir(pos);
			}
		} else if(worldIn.getBlockState(pos.offset(offset.getOpposite())).getBlock() != this) {
			if(!worldIn.isClientSide()) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
			}

			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasCustomBreakingProgress(BlockState state) {
		return true;
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, PART);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getHorizontalIndex() << 1 | (state.getValue(PART) == EnumPartType.FILLER ? 0b1 : 0b0);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta >> 1)).setValue(PART, (meta & 0b1) != 0 ? EnumPartType.FILLER : EnumPartType.MAIN);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public BlockItem getItemBlock() {
		return new ItemRuneWeavingTable();
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
		public String getSerializedName() {
			return this.name;
		}
	}
}