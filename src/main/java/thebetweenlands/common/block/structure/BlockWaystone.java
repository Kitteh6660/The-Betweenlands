package thebetweenlands.common.block.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntityWaystone;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class BlockWaystone extends ContainerBlock {
	
	public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public BlockWaystone(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		this.setHardness(25.0F);
		this.setResistance(10000.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setTickRandomly(true);*/
		this.registerDefaultState(this.defaultBlockState().setValue(PART, Part.BOTTOM).setValue(ACTIVE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, PART, ACTIVE);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(PART).getMeta() | (state.getValue(ACTIVE) ? 0b100 : 0);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(PART, Part.fromMeta(meta & 0b11)).setValue(ACTIVE, (meta & 0b100) != 0);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && super.canPlaceBlockAt(world, pos.above()) && super.canPlaceBlockAt(world, pos.above(2));
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState();
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		if(!worldIn.isClientSide()) {
			BlockState stateTop = this.defaultBlockState().setValue(PART, Part.TOP);
			BlockState stateMiddle = this.defaultBlockState().setValue(PART, Part.MIDDLE);

			worldIn.setBlockState(pos.above(2), stateTop, 3);
			worldIn.setBlockState(pos.above(), stateMiddle, 3);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		super.breakBlock(worldIn, pos, state);

		ILocalStorageHandler localStorageHandler = BetweenlandsWorldStorage.forWorld(worldIn).getLocalStorageHandler();
		List<LocationStorage> waystoneLocations = localStorageHandler.getLocalStorages(LocationStorage.class, Block.box(pos), storage -> storage.getType() == EnumLocationType.WAYSTONE);
		for(LocationStorage waystoneLocation : waystoneLocations) {
			localStorageHandler.removeLocalStorage(waystoneLocation);
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if(!this.isValidWaystone(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	public boolean isValidWaystone(World world, BlockPos pos, BlockState state) {
		switch(state.getValue(PART)) {
		case TOP: {
			BlockState down1 = world.getBlockState(pos.below());
			BlockState down2 = world.getBlockState(pos.below(2));
			return down1.getBlock() == this && down1.getValue(PART) == Part.MIDDLE && down2.getBlock() == this && down2.getValue(PART) == Part.BOTTOM;
		}
		case MIDDLE: {
			BlockState down1 = world.getBlockState(pos.below());
			BlockState up1 = world.getBlockState(pos.above());
			return down1.getBlock() == this && down1.getValue(PART) == Part.BOTTOM && up1.getBlock() == this && up1.getValue(PART) == Part.TOP;
		}
		default:
		case BOTTOM: {
			BlockState up1 = world.getBlockState(pos.above());
			BlockState up2 = world.getBlockState(pos.above(2));
			return up1.getBlock() == this && up1.getValue(PART) == Part.MIDDLE && up2.getBlock() == this && up2.getValue(PART) == Part.TOP;
		}
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		//Only drop once
		if(state.getValue(PART) == Part.BOTTOM) {
			super.getDrops(drops, world, pos, state, fortune);
		}
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
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean hasCustomBreakingProgress(BlockState state) {
		return true;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		if(!worldIn.isClientSide()) {
			return new TileEntityWaystone(worldIn.rand.nextFloat() * 360.0F);
		}
		return new TileEntityWaystone();
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public int getLightValue(BlockState state) {
		return state.getValue(ACTIVE) ? 8 : 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for(int i = 0; i < 16; i++) {
			worldIn.addParticle(ParticleTypes.SUSPENDED_DEPTH, pos.getX() + 0.5D + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 16, pos.getY() + 0.5D + rand.nextFloat() * 6 - 3, pos.getZ() + 0.5D + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 16, 0, 0.2D, 0);
		}
	}

	public static enum Part implements IStringSerializable {
		TOP("top", 0), MIDDLE("middle", 1), BOTTOM("bottom", 2);

		private final String name;
		private final int meta;

		private Part(String name, int meta) {
			this.name = name;
			this.meta = meta;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
		
		public int getMeta() {
			return this.meta;
		}

		public static Part fromMeta(int meta) {
			for(Part part : values()) {
				if(part.meta == meta) {
					return part;
				}
			}
			return null;
		}
	}
}
