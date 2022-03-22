package thebetweenlands.common.block.container;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.tile.TileEntityDruidAltar;

public class BlockDruidAltar extends BasicBlock implements ITileEntityProvider {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public BlockDruidAltar() {
		super(Material.ROCK);
		setHardness(8.0F);
		setResistance(100.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().setValue(ACTIVE, false));
		setItemDropped(() -> null);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ACTIVE);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState().setValue(ACTIVE, meta != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(ACTIVE) ? 1 : 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityDruidAltar();
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult){
		TileEntity tile = world.getBlockEntity(pos);
		if (tile instanceof TileEntityDruidAltar) {
			TileEntityDruidAltar altar = (TileEntityDruidAltar) tile;
			if (altar.craftingProgress == 0) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_DRUID_ALTAR, world, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}

		return false;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
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
