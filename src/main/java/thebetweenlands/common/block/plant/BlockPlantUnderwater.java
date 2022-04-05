package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.block.fluid.SwampWaterBlock;
import thebetweenlands.common.block.fluid.SwampWaterFluid;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockPlantUnderwater extends SwampWaterBlock implements IWaterLoggable, IPlantable, IForgeShearable, ISickleHarvestable, IFarmablePlant {
	
	protected static final VoxelShape PLANT_AABB = Block.box(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);

	protected ItemStack sickleHarvestableDrop;
	protected boolean isReplaceable = false;

	/*public BlockPlantUnderwater(Properties properties) {
		super(properties);
		this(FluidRegistry.SWAMP_WATER, Material.WATER);
		this.setHardness(0.5F);
	}*/

	public BlockPlantUnderwater(Properties properties) {
		super(properties);
		/*this.setSoundType(SoundType.PLANT);
		this.setHardness(1.5F);
		this.setResistance(10.0F);*/
		this.setUnderwaterBlock(true);
		this.registerDefaultState(this.defaultBlockState().setValue(LEVEL, 0));
		//this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	public BlockPlantUnderwater setSickleDrop(ItemStack drop) {
		this.sickleHarvestableDrop = drop;
		return this;
	}

	public BlockPlantUnderwater setReplaceable(boolean replaceable) {
		this.isReplaceable = replaceable;
		return this;
	}
	
	/**
	 * Removes the plant. Usually sets the block to water
	 * @param world
	 * @param pos
	 * @param player
	 * @param canHarvest
	 * @return
	 */
	protected boolean removePlant(World world, BlockPos pos, @Nullable PlayerEntity player, boolean canHarvest) {
		return world.setBlock(pos, BlockRegistry.SWAMP_WATER.get().defaultBlockState(), world.isClientSide() ? 11 : 3);
	}

	@Override
	public boolean isReplaceable(IBlockReader worldIn, BlockPos pos) {
		return this.isReplaceable;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return PLANT_AABB;
	}

	@Override
	public boolean isBlockNormalCube(BlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState blockState) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public PlantType getPlantType(net.minecraft.world.IBlockReader world, BlockPos pos) {
		return PlantType.PLAINS;
	}

	@Override
	public BlockState getPlant(net.minecraft.world.IBlockReader world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) return defaultBlockState();
		return state;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OffsetType getOffsetType() {
		return OffsetType.NONE;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(@Nonnull BlockState blockState, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
		return NULL_AABB;
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(LEVEL);
	}

	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighbourPos) {
		super.neighborChanged(state, world, pos, neighborBlock, neighbourPos);
		this.checkAndDropBlock(world, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			this.removePlant(worldIn, pos, null, false);
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
		if (state.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
		{
			BlockState soil = worldIn.getBlockState(pos.below());
			return soil.getBlock().canSustainPlant(soil, worldIn, pos.below(), net.minecraft.util.Direction.UP, this) || 
					this.canSustainPlant(soil, worldIn, pos.below(), net.minecraft.util.Direction.UP, this);
		}
		return this.canSustainPlant(worldIn.getBlockState(pos.below()));
	}

	protected boolean canSustainPlant(BlockState state) {
		return SoilHelper.canSustainUnderwaterPlant(state);
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return this.removePlant(world, pos, player, willHarvest);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		BlockState soil = worldIn.getBlockState(pos.below());
		BlockState state = worldIn.getBlockState(pos);
		if(state.getBlock() instanceof IFluidBlock && ((IFluidBlock)state.getBlock()).getFluid() == this.getFluid()) {
			return super.canPlaceBlockAt(worldIn, pos) && 
					(soil.getBlock().canSustainPlant(soil, worldIn, pos.below(), net.minecraft.util.Direction.UP, this) || this.canSustainPlant(soil, worldIn, pos.below(), net.minecraft.util.Direction.UP, this));
		}
		return false;
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, net.minecraftforge.common.IPlantable plantable) {
		return super.canSustainPlant(state, world, pos, direction, plantable) || (plantable instanceof BlockPlantUnderwater && ((BlockPlantUnderwater)plantable).canSustainPlant(state)) || SoilHelper.canSustainUnderwaterPlant(state);
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
	public List<ItemStack> getHarvestableDrops(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return this.sickleHarvestableDrop != null ? ImmutableList.of(this.sickleHarvestableDrop.copy()) : ImmutableList.of();
	}

	@Override
	@Nullable
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		if(tintIndex == 1) {
			return worldIn != null && pos != null ? BiomeColors.getAverageGrassColor(worldIn, pos) : -1;
		}
		return super.getColorMultiplier(state, worldIn, pos, tintIndex);
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		Block block = world.getBlockState(targetPos).getBlock();
		if(block instanceof SwampWaterFluid && ((SwampWaterFluid)block).isSourceBlock(world, targetPos)) {
			return this.canPlaceBlockAt(world, targetPos);
		}
		return false;
	}

	@Override
	public float getSpreadChance(World world, BlockPos pos, BlockState state, BlockPos taretPos, Random rand) {
		return 0.25F;
	}
	
	@Override
	public int getCompostCost(World world, BlockPos pos, BlockState state, Random rand) {
		return 4;
	}

	@Override
	public void decayPlant(World world, BlockPos pos, BlockState state, Random rand) {
		world.setBlockAndUpdate(pos, BlockRegistry.SWAMP_WATER.get().defaultBlockState());
	}

	@Override
	public void spreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		world.setBlockAndUpdate(targetPos, this.defaultBlockState());
	}
	
	@Override
	public BlockItem getItemBlock() {
		return ICustomItemBlock.getDefaultItemBlock(this);
	}
}
