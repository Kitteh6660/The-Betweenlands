package thebetweenlands.common.block.plant;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.util.AdvancedStateMap;

public class BlockStackablePlant extends BlockPlant {
	
	protected static final VoxelShape STACKABLE_PLANT_AABB = Block.box(0.1D, 0.0D, 0.1D, 0.9D, 1D, 0.9D);

	public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");
	public static final BooleanProperty IS_BOTTOM = BooleanProperty.create("is_bottom");
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);

	protected int maxHeight = 3;
	protected boolean harvestAll = false;
	protected boolean resetAge = true;

	protected final ThreadLocal<Boolean> harvesting = new ThreadLocal<>();

	public BlockStackablePlant(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(IS_TOP, true).setValue(IS_BOTTOM, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OffsetType getOffsetType() {
		return this.maxHeight != 1 ? OffsetType.NONE : OffsetType.XZ;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return STACKABLE_PLANT_AABB;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[]{AGE, IS_TOP, IS_BOTTOM});
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		boolean isTop = !this.isSamePlant(worldIn.getBlockState(pos.above()));
		boolean isBottom = !this.isSamePlant(worldIn.getBlockState(pos.below()));
		return state.setValue(IS_TOP, isTop).setValue(IS_BOTTOM, isBottom);
	}

	@Override
	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			this.removePlant(worldIn, pos, null, false);
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		boolean removed = false;
		Boolean harvesting = this.harvesting.get(); //I'm sorry
		if(harvesting == null || !harvesting) {
			this.harvesting.set(true);
			int height;
			for (height = 1; this.isSamePlant(world.getBlockState(pos.above(height))); ++height);
			for (int offset = height - 1; (this.harvestAll && this.isSamePlant(world.getBlockState(pos.above(offset)))) || (!this.harvestAll && offset >= 0); offset--) {
				if(offset != 0) {
					BlockPos offsetPos = pos.above(offset);
					BlockState blockState = world.getBlockState(offsetPos);
					boolean canHarvest = player.isCreative() ? false : blockState.getBlock().canHarvestBlock(world, offsetPos, player);
					boolean otherRemoved = this.removeOtherBlockAsPlayer(world, offsetPos, player, canHarvest);
					if(otherRemoved && canHarvest) {
						ItemStack stack = player.getMainHandItem() == null ? null : player.getMainHandItem().copy();
						blockState.getBlock().harvestBlock(world, player, offsetPos, blockState, world.getBlockEntity(offsetPos), stack);
					}
				} else {
					removed = this.removePlant(world, pos, null, false);
				}
			}
			this.harvesting.set(false);
		}
		if(removed) {
			return true;
		}
		return this.removePlant(world, pos, player, willHarvest);
	}

	protected boolean removeOtherBlockAsPlayer(World world, BlockPos pos, PlayerEntity player, boolean canHarvest) {
		BlockState blockState = world.getBlockState(pos);
		boolean removed = blockState.getBlock().removedByPlayer(blockState, world, pos, player, canHarvest);
		if (removed) {
			blockState.getBlock().playerWillDestroy(world, pos, blockState, player);
		}
		return removed;
	}

	/**
	 * Removes the plant. Usually sets the block to air
	 * @param world
	 * @param pos
	 * @param player
	 * @param canHarvest
	 * @return
	 */
	protected boolean removePlant(World world, BlockPos pos, @Nullable PlayerEntity player, boolean canHarvest) {
		return world.setBlockState(pos, Blocks.AIR.defaultBlockState(), level.isClientSide() ? 11 : 3);
	}

	/**
	 * Returns true if the specified block should be considered as the same plant
	 * @param blockState
	 * @return
	 */
	protected boolean isSamePlant(BlockState blockState) {
		return blockState.getBlock() == this;
	}

	/**
	 * Sets the maximum height this plant should naturally grow
	 * @param maxHeight
	 * @return
	 */
	public BlockStackablePlant setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	/**
	 * Returns the maximum height this plant should naturally grow
	 * @return
	 */
	public int getMaxHeight() {
		return this.maxHeight;
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return ((this.maxHeight == -1 || this.maxHeight > 1) && this.isSamePlant(state)) || SoilHelper.canSustainPlant(state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		this.checkAndDropBlock(worldIn, pos, state);

		if(this.canGrow(worldIn, pos, state)) {
			if(ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextFloat() <= this.getGrowthChance(worldIn, pos, state, rand))) {
				int currentAge = ((Integer)state.getValue(AGE)).intValue();

				worldIn.setBlockState(pos, state.setValue(AGE, Math.min(currentAge + this.getGrowthSpeed(worldIn, pos, state, rand), 15)));
				
				if (currentAge >= 15) {
					int height;
					for (height = 1; this.isSamePlant(worldIn.getBlockState(pos.below(height))); ++height);

					if (this.canGrowUp(worldIn, pos, state, height)) {
						this.growUp(worldIn, pos);
					}

					worldIn.setBlockState(pos, state.setValue(AGE, this.resetAge ? 0 : 15));
				}

				ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
			}
		}
	}
	
	/**
	 * Returns by how many steps it should age per growth chance
	 * @param world
	 * @param pos
	 * @param state
	 * @param rand
	 * @return
	 */
	protected int getGrowthSpeed(World world, BlockPos pos, BlockState state, Random rand) {
		return 1;
	}

	/**
	 * Returns the growth chance
	 * @param world
	 * @param pos
	 * @param state
	 * @param rand
	 * @return
	 */
	protected float getGrowthChance(World world, BlockPos pos, BlockState state, Random rand) {
		return 0.5F;
	}

	/**
	 * Returns whether the plant can grow
	 * @param world
	 * @param pos
	 * @param state
	 * @return
	 */
	protected boolean canGrow(World world, BlockPos pos, BlockState state) {
		return true;
	}

	/**
	 * Returns whether the plant can grow a block higher
	 * @param world
	 * @param pos
	 * @param state
	 * @param height
	 * @return
	 */
	protected boolean canGrowUp(World world, BlockPos pos, BlockState state, int height) {
		return world.isEmptyBlock(pos.above()) && (this.maxHeight == -1 || height < this.maxHeight);
	}

	/**
	 * Grows the plant one block higher
	 * @param world
	 * @param pos Position of the currently highest block of the plant
	 */
	protected void growUp(World world, BlockPos pos) {
		world.setBlockState(pos.above(), this.defaultBlockState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		int height;
		for (height = 1; this.isSamePlant(worldIn.getBlockState(pos.below(height))); ++height);
		return super.canPlaceBlockAt(worldIn, pos) && (this.maxHeight == -1 || height - 1 < this.maxHeight);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(AGE, Integer.valueOf(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((Integer)state.getValue(AGE)).intValue();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(AGE);
		if(this.maxHeight == 1) {
			builder.ignore(IS_TOP, IS_BOTTOM);
		}
	}
}
