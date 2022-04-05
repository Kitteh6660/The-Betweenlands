package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockPlant extends BushBlock implements IForgeShearable, ISickleHarvestable, IFarmablePlant, ITintedBlock {
	
	protected static final VoxelShape PLANT_AABB = Block.box(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);

	protected ItemStack sickleHarvestableDrop;
	protected boolean isReplaceable = false;

	public BlockPlant(Properties properties) {
		super(properties);
		/*super(Material.PLANTS);
		this.setHardness(0.0F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);*/
	}

	public BlockPlant setSickleDrop(ItemStack drop) {
		this.sickleHarvestableDrop = drop;
		return this;
	}

	public BlockPlant setReplaceable(boolean replaceable) {
		this.isReplaceable = replaceable;
		return this;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return PLANT_AABB;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isEmptyBlock(pos.above()) && this.canBlockStay(worldIn, pos, worldIn.getBlockState(pos));
	}

	@Override
	public boolean isReplaceable(IBlockReader worldIn, BlockPos pos) {
		return this.isReplaceable;
	}

	@Override
	public boolean isPassable(IBlockReader worldIn, BlockPos pos) {
		return true;
	}

	@Override
	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	@Override
	@Nullable
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int damageDropped(BlockState state) {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return SoilHelper.canSustainPlant(state);
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
	public boolean isHarvestable(ItemStack item, IWorldReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return this.sickleHarvestableDrop != null ? ImmutableList.of(this.sickleHarvestableDrop.copy()) : ImmutableList.of();
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		return world.isEmptyBlock(targetPos) && this.canPlaceBlockAt(world, targetPos);
	}
	
	@Override
	public float getSpreadChance(World world, BlockPos pos, BlockState state, BlockPos taretPos, Random rand) {
		return 0.25F;
	}

	@Override
	public void spreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		world.setBlockAndUpdate(targetPos, this.defaultBlockState());
	}

	@Override
	public void decayPlant(World world, BlockPos pos, BlockState state, Random rand) {
		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, BlockState state, Random rand) {
		return 4;
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, BlockState state) {
		return true;
	}
	
	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getAverageGrassColor(worldIn, pos) : -1;
	}
}