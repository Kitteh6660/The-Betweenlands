package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.tile.TileEntitySimulacrum;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockWisp extends ContainerBlock implements IStateMappedBlock {
	protected static final AxisAlignedBB WISP_AABB = Block.box(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);

	public static final PropertyInteger COLOR = PropertyInteger.create("color", 0, 3);
	public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");

	public BlockWisp() {
		super(BLMaterialRegistry.WISP);
		this.setDefaultState(this.getBlockState().getBaseState().setValue(COLOR, 0).setValue(VISIBLE, false));
		this.setSoundType(SoundType.STONE);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(0);
		this.setTickRandomly(true);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { COLOR, VISIBLE });
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return WISP_AABB;
	}

	@SuppressWarnings("deprecation")
	@Override
	public RayTraceResult collisionRayTrace(BlockState blockState, World world, BlockPos pos, Vector3d start, Vector3d end){
		if(blockState.getValue(VISIBLE)) {
			return super.collisionRayTrace(blockState, world, pos, start, end);
		}
		return null;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public void onPlayerDestroy(World world, BlockPos pos, BlockState state) {
		if(!world.isClientSide() && state.getValue(VISIBLE)) {
			ItemEntity wispItem = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(Item.getItemFromBlock(this), 1));
			world.spawnEntity(wispItem);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWisp();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState s) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, BlockState state) {
		state = this.defaultBlockState().setValue(COLOR, world.rand.nextInt(COLORS.length / 2));
		world.setBlockState(pos, state, 2);
		this.updateVisibility(world, pos, state);
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isReplaceable(IBlockReader worldIn, BlockPos pos) {
		return true;
	}

	// Colors can be added here, always add a pair of colors for outer color and inner color
	public static final int[] COLORS = new int[] {
			0xFF7F1659, 0xFFFFFFFF, // Pink/White
			0xFF0707C8, 0xFFC8077B, // Blue/Pink
			0xFF0E2E0B, 0xFFC8077B, // Green/Yellow/White
			0xFF9A6908, 0xFF4F0303 // Red/Yellow/White
	};

	/**
	 * Sets the block at the giving position to a wisp block with a random color
	 *
	 * @param world
	 * @param pos
	 */
	public void generateBlock(World world, BlockPos pos) {
		world.setBlockState(pos, this.defaultBlockState().setValue(COLOR, world.rand.nextInt(COLORS.length / 2)), 2);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(COLOR, (meta >> 1) & 0b111).setValue(VISIBLE, (meta & 0b1) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((state.getValue(COLOR) << 1) & 0b111) | (state.getValue(VISIBLE) ? 1 : 0);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(Builder builder) {
		builder.ignore(COLOR).ignore(VISIBLE);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public int tickRate(World worldIn) {
		return 40;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		this.updateVisibility(worldIn, pos, state);
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
	}

	protected boolean checkVisibility(World world, BlockPos pos) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);

		if(worldStorage.getEnvironmentEventRegistry().auroras.isActive()) {
			return true;
		}

		if(!worldStorage.getLocalStorageHandler().getLocalStorages(LocationCragrockTower.class, pos.getX(), pos.getZ(), location -> location.isInside(pos)).isEmpty()) {
			return true;
		}

		if(!worldStorage.getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, pos.getX(), pos.getZ(), location -> location.isInside(pos)).isEmpty()) {
			return true;
		}

		if(TileEntitySimulacrum.getClosestActiveTile(TileEntitySimulacrum.class, null, world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 16, TileEntitySimulacrum.Effect.WISP, null) != null) {
			return true;
		}

		return false;
	}

	protected void updateVisibility(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.setValue(VISIBLE, this.checkVisibility(world, pos)));
	}
}
