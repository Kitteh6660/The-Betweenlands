package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityPresent;

public class BlockPresent extends Block implements ITintedBlock {
	
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.<DyeColor>create("color", DyeColor.class);

	protected static final VoxelShape AABB = Block.box(0.06D, 0, 0.06D, 0.94D, 0.82D, 0.94D);

	public BlockPresent(Properties properties) {
		super(properties);
		/*super(Material.CLOTH);
		this.setHardness(0.8f);
		this.setSoundType(SoundType.CLOTH);
		this.setTickRandomly(true);
		this.setCreativeTab(null);*/
		this.registerDefaultState(this.defaultBlockState().setValue(COLOR, DyeColor.RED));
	}

	@Nullable
	public static TileEntityPresent getBlockEntity(IBlockReader world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof TileEntityPresent) {
			return (TileEntityPresent) tile;
		}
		return null;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new TileEntityPresent();
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(!worldIn.isClientSide()) {
			state = state.setValue(COLOR, DyeColor.values()[worldIn.rand.nextInt(DyeColor.values().length)]);
			worldIn.setBlockState(pos, state, 3);
			TileEntityPresent tile = getBlockEntity(worldIn, pos);
			if (tile != null) {
				tile.setLootTable(LootTableRegistry.PRESENT, worldIn.rand.nextLong());
				tile.setChanged();
			}
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		IInventory tile = (IInventory) worldIn.getBlockEntity(pos);
		if (tile != null) {
			((TileEntityLootInventory) tile).fillInventoryWithLoot(player);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		IInventory tile = (IInventory) worldIn.getBlockEntity(pos);
		if (tile != null) {
			InventoryHelper.dropInventoryItems(worldIn, pos, tile);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public MapColor getMapColor(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return MapColor.getBlockColor((DyeColor)state.getValue(COLOR));
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(COLOR, DyeColor.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((DyeColor)state.getValue(COLOR)).getMetadata();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] {COLOR});
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		if(tintIndex == 0) {
			return state.getValue(COLOR).getColorValue();
		}
		return 0xFFFFFF;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if(rand.nextInt(20) == 0 && worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32.0D, false) == null) {
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}
}
