package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockHangingPlant extends BlockBush implements ISickleHarvestable, IShearable, IStateMappedBlock {
	public static final BooleanProperty CAN_GROW = BooleanProperty.create("can_grow");
	public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");
	public static final BooleanProperty IS_BOTTOM = BooleanProperty.create("is_bottom");
	public static final AxisAlignedBB AABB = Block.box(0.25F, 0, 0.25F, 0.75F, 1, 0.75F);

	protected ItemStack sickleHarvestableDrop;

	public BlockHangingPlant(Material material) {
		super(material);
		setTickRandomly(true);
		setHardness(0);
		setCreativeTab(BLCreativeTabs.PLANTS);
		setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().setValue(IS_TOP, true).setValue(IS_BOTTOM, false).setValue(CAN_GROW, true));
	}

	public BlockHangingPlant() {
		this(Material.PLANTS);
	}

	public BlockHangingPlant setSickleDrop(ItemStack drop) {
		this.sickleHarvestableDrop = drop;
		return this;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{IS_TOP, IS_BOTTOM, CAN_GROW});
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		boolean isTop = worldIn.getBlockState(pos.above()).getBlock() != this;
		boolean isBottom = worldIn.getBlockState(pos.below()).getBlock() != this;
		return state.setValue(IS_TOP, isTop).setValue(IS_BOTTOM, isBottom);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isLadder(BlockState state, IBlockReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return isValidBlock(world, pos.above(), world.getBlockState(pos.above())) && canBlockStay(world, pos, world.getBlockState(pos));
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
		return isValidBlock(worldIn, pos.above(), worldIn.getBlockState(pos.above()));
	}

	@Override
	@Nullable
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!isValidBlock(worldIn, pos.above(), worldIn.getBlockState(pos.above()))) {
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(40) == 0) {
			float dripRange = 0.5F;
			float px = rand.nextFloat() - 0.5F;
			float py = rand.nextFloat();
			float pz = rand.nextFloat() - 0.5F;
			float u = Math.max(Math.abs(px), Math.abs(pz));
			px = px / u * dripRange + 0.5F;
			pz = pz / u * dripRange + 0.5F;
			BLParticles.CAVE_WATER_DRIP.spawn(worldIn, pos.getX() + px, pos.getY() + py, pos.getZ() + pz);
		}
	}

	protected boolean isValidBlock(World world, BlockPos pos, BlockState blockState) {
		return blockState.isSideSolid(world, pos, Direction.DOWN) || blockState.getBlock() == this;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockReader world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return this.sickleHarvestableDrop != null ? ImmutableList.of(this.sickleHarvestableDrop.copy()) : ImmutableList.of();
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(CAN_GROW) ? 1 : 0;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState().setValue(CAN_GROW, true);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(CAN_GROW, meta == 1);
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(CAN_GROW);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if(rand.nextInt(16) == 0 && worldIn.isEmptyBlock(pos.below()) && this.canGrowAt(worldIn, pos, state)) {
			worldIn.setBlockState(pos.below(), this.defaultBlockState());
		}
	}

	protected boolean canGrowAt(World world, BlockPos pos, BlockState state) {
		return state.getValue(CAN_GROW);
	}
}
