package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
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
import thebetweenlands.util.AdvancedStateMap;

public class BlockDoublePlantBL extends BushBlock implements IForgeShearable, ISickleHarvestable, IFarmablePlant, ITintedBlock {
	
	protected static final VoxelShape PLANT_AABB = Block.box(0.1D, 0.0D, 0.1D, 0.9D, 1.0D, 0.9D);

	public static final EnumProperty<BlockDoublePlantBL.Half> HALF = EnumProperty.<BlockDoublePlantBL.Half>create("half", BlockDoublePlantBL.Half.class);
	public static final EnumProperty<Direction> FACING = HorizontalFaceBlock.FACING;

	protected ItemStack sickleHarvestableDrop;
	protected boolean replaceable;
	
	public BlockDoublePlantBL(Properties properties) {
		super(properties);
		/*super(Material.PLANTS);
		this.setHardness(0.0F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);*/
		this.registerDefaultState(this.defaultBlockState().setValue(HALF, BlockDoublePlantBL.Half.LOWER).setValue(FACING, Direction.NORTH));
	}

	public BlockDoublePlantBL setSickleDrop(ItemStack drop) {
		this.sickleHarvestableDrop = drop;
		return this;
	}
	
	public BlockDoublePlantBL setReplaceable(boolean replaceable) {
		this.replaceable = replaceable;
		return this;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return PLANT_AABB;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isEmptyBlock(pos.above());
	}

	@Override
	public boolean isReplaceable(IBlockReader worldIn, BlockPos pos) {
		return this.replaceable;
	}

	@Override
	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			boolean isUpperHalf = state.getValue(HALF) == BlockDoublePlantBL.Half.UPPER;
			BlockPos posAboveOrHere = isUpperHalf ? pos : pos.above();
			BlockPos posBelowOrHere = isUpperHalf ? pos.below() : pos;
			Block blockAboveOrHere = (Block)(isUpperHalf ? this : worldIn.getBlockState(posAboveOrHere).getBlock());
			Block blockBelowOrHere = (Block)(isUpperHalf ? worldIn.getBlockState(posBelowOrHere).getBlock() : this);

			if (!isUpperHalf && blockAboveOrHere == this) 
				this.dropBlockAsItem(worldIn, pos, state, 0); //Forge move above the setting to air.

			if (blockAboveOrHere == this) {
				worldIn.setBlockState(posAboveOrHere, Blocks.AIR.defaultBlockState(), 2);
			}

			if (blockBelowOrHere == this) {
				worldIn.setBlockState(posBelowOrHere, Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
		if (state.getBlock() != this) 
			return super.canBlockStay(worldIn, pos, state); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
		if (state.getValue(HALF) == BlockDoublePlantBL.Half.UPPER) {
			return worldIn.getBlockState(pos.below()).getBlock() == this;
		} else {
			BlockState stateAbove = worldIn.getBlockState(pos.above());
			return stateAbove.getBlock() == this && super.canBlockStay(worldIn, pos, stateAbove);
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

	public void placeAt(World worldIn, BlockPos lowerPos, int updateFlags) {
		worldIn.setBlockState(lowerPos, this.defaultBlockState().setValue(HALF, BlockDoublePlantBL.Half.LOWER), updateFlags);
		worldIn.setBlockState(lowerPos.above(), this.defaultBlockState().setValue(HALF, BlockDoublePlantBL.Half.UPPER), updateFlags);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		int rot = MathHelper.floor(placer.yRot * 4.0F / 360.0F + 0.5D) & 3;
		worldIn.setBlockState(pos.above(), this.defaultBlockState().setValue(HALF, BlockDoublePlantBL.Half.UPPER).setValue(FACING, Direction.byHorizontalIndex(rot)), 2);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (state.getValue(HALF) == BlockDoublePlantBL.Half.UPPER) {
			if (worldIn.getBlockState(pos.below()).getBlock() == this) {
				if (!player.isCreative()) {
					//Stupid workarounds...
					this.harvestBlock(worldIn, player, pos.below(), worldIn.getBlockState(pos.below()), worldIn.getBlockEntity(pos.below()), player.getMainHandItem());
				}
				worldIn.setBlockToAir(pos.below());
			}
		} else if (worldIn.getBlockState(pos.above()).getBlock() == this) {
			worldIn.setBlockState(pos.above(), Blocks.AIR.defaultBlockState(), 2);
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		int facing = (meta >> 1) & 4;
		boolean isUpper = (meta & 1) == 1;
		return this.defaultBlockState().setValue(HALF, isUpper ? Half.UPPER : Half.LOWER).setValue(FACING, Direction.byHorizontalIndex(facing));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int facing = state.getValue(FACING).getHorizontalIndex();
		boolean isUpper = state.getValue(HALF) == BlockDoublePlantBL.Half.UPPER;
		int meta = facing << 1;
		meta |= isUpper ? 1 : 0;
		return meta;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] {HALF, FACING});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OffsetType getOffsetType() {
		return OffsetType.XYZ;
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	public static enum Half implements IStringSerializable {
		UPPER,
		LOWER;

		@Override
		public String toString() {
			return this.getName();
		}

		@Override
		public ITextComponent getName() {
			return this == UPPER ? "upper" : "lower";
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(FACING);
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
	protected boolean canSustainBush(BlockState state) {
		return super.canSustainBush(state) || SoilHelper.canSustainPlant(state);
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		return world.isEmptyBlock(targetPos) && world.isEmptyBlock(targetPos.above()) && this.canPlaceBlockAt(world, targetPos);
	}
	
	@Override
	public float getSpreadChance(World world, BlockPos pos, BlockState state, BlockPos taretPos, Random rand) {
		return 0.25F;
	}

	@Override
	public void spreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		world.setBlockState(targetPos, this.defaultBlockState().setValue(BlockDoublePlantBL.HALF, BlockDoublePlantBL.Half.LOWER));
		world.setBlockState(targetPos.above(), this.defaultBlockState().setValue(BlockDoublePlantBL.HALF, BlockDoublePlantBL.Half.UPPER));
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, BlockState state, Random rand) {
		return 8;
	}

	@Override
	public void decayPlant(World world, BlockPos pos, BlockState state, Random rand) {
		world.setBlockToAir(pos.above());
		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, BlockState state) {
		return true;
	}
	
	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getAverageGrassColor(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}
}