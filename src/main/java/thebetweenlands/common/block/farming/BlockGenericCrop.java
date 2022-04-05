package thebetweenlands.common.block.farming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.IGrowable;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.block.plant.BlockStackablePlant;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.util.AdvancedStateMap;

public class BlockGenericCrop extends BlockStackablePlant implements IGrowable {
	
	public static final BooleanProperty DECAYED = BooleanProperty.create("decayed");

	private IntegerProperty stageProperty;

	public BlockGenericCrop(Properties properties) {
		super(properties);
		this.harvestAll = true;
		this.resetAge = false;
		this.setMaxHeight(1);
		this.setDefaultState(this.defaultBlockState().setValue(DECAYED, false));
	}

	/**
	 * Creates the growth stage property. Can be used to change the number of stages (max 15)
	 * @return
	 */
	protected IntegerProperty createStageProperty() {
		return IntegerProperty.create("stage", 0, 3);
	}

	/**
	 * Returns the growth stage property
	 * @return
	 */
	public IntegerProperty getStageProperty() {
		return this.stageProperty;
	}

	/**
	 * Returns whether the soil is decayed
	 * @param world
	 * @param pos
	 * @return
	 */
	public boolean isDecayed(IBlockReader world, BlockPos pos) {
		for(int i = 0; i < this.maxHeight + 1; i++) {
			BlockState blockState = world.getBlockState(pos);
			if(blockState.getBlock() instanceof BlockGenericDugSoil) {
				return blockState.getValue(BlockGenericDugSoil.DECAYED);
			}
			pos = pos.below();
		}
		return false;
	}

	/**
	 * Returns whether the soil is composted
	 * @param world
	 * @param pos
	 * @return
	 */
	public boolean isComposted(IBlockReader world, BlockPos pos) {
		for(int i = 0; i < this.maxHeight + 1; i++) {
			BlockState blockState = world.getBlockState(pos);
			if(blockState.getBlock() instanceof BlockGenericDugSoil) {
				return blockState.getValue(BlockGenericDugSoil.COMPOSTED);
			}
			pos = pos.below();
		}
		return false;
	}
	
	/**
	 * Returns whether the soil is fogged
	 * @param world
	 * @param pos
	 * @return
	 */
	public boolean isFogged(IBlockReader world, BlockPos pos) {
		for(int i = 0; i < this.maxHeight + 1; i++) {
			BlockState blockState = world.getBlockState(pos);
			if(blockState.getBlock() instanceof BlockGenericDugSoil) {
				return blockState.getValue(BlockGenericDugSoil.FOGGED);
			}
			pos = pos.below();
		}
		return false;
	}

	@Override
	protected float getGrowthChance(World world, BlockPos pos, BlockState state, Random rand) {
		return this.isFogged(world, pos) ? 1 : super.getGrowthChance(world, pos, state, rand);
	}
	
	@Override
	protected int getGrowthSpeed(World world, BlockPos pos, BlockState state, Random rand) {
		return super.getGrowthSpeed(world, pos, state, rand) + (this.isFogged(world, pos) ? rand.nextInt(3) + 2 : 0);
	}
	
	@Override
	public PlantType getPlantType(net.minecraft.world.IBlockReader world, BlockPos pos) {
		return PlantType.CROP;
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		player.awardStat(StatList.getBlockStats(this));
		player.causeFoodExhaustion(0.025F);
		//Dropping logic moved to #onBlockHarvested
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if(!player.isCreative()) {
			ItemStack stack = !player.getMainHandItem().isEmpty() ? player.getMainHandItem().copy() : ItemStack.EMPTY;
			if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
				List<ItemStack> items = new java.util.ArrayList<ItemStack>();
				ItemStack itemstack = this.getPickBlock(state, null, worldIn, pos, player);

				if (!itemstack.isEmpty()) {
					items.add(itemstack);
				}

				net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
				for (ItemStack item : items) {
					spawnAsEntity(worldIn, pos, item);
				}
			} else {
				this.harvesters.set(player);
				int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
				this.dropBlockAsItem(worldIn, pos, state, i);
				this.harvesters.set(null);
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		boolean removed = super.removedByPlayer(state, world, pos, player, willHarvest);
		if(removed && state.getValue(AGE) >= 15) {
			//Remove 10 compost after harvesting fully grown crop
			this.harvestAndUpdateSoil(world, pos, 10);
		}
		return removed;
	}

	/**
	 * Called when the crop is harvested. Updates the soil and e.g. consumes compost if the block below is dug soil
	 * @param world
	 * @param pos
	 * @param compost
	 */
	protected void harvestAndUpdateSoil(World world, BlockPos pos, int compost) {
		BlockState stateDown = world.getBlockState(pos.below());
		if(stateDown.getBlock() instanceof BlockGenericDugSoil) {
			TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, pos.below());
			if(te != null && te.isComposted()) {
				te.setCompost(Math.max(te.getCompost() - compost, 0));
				if(((BlockGenericDugSoil)stateDown.getBlock()).isPurified(world, pos.below(), stateDown)) {
					te.setPurifiedHarvests(te.getPurifiedHarvests() + 1);
				}
			}
		}
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		ItemStack seedDrop = this.getSeedDrop(world, pos, rand);
		ItemStack cropDrop = this.getCropDrop(world, pos, rand);

		if(!seedDrop.isEmpty()) {
			int  drops = this.getSeedDrops(world, pos, rand, fortune);
			for(int i = 0; i < drops; i++) {
				ret.add(seedDrop.copy());
			}
		}

		if(!cropDrop.isEmpty()) {
			int drops = this.getCropDrops(world, pos, rand, fortune);
			for(int i = 0; i < drops; i++) {
				ret.add(cropDrop.copy());
			}
		}

		return ret;
	}

	/**
	 * Returns the number of seeds to drop
	 * @param world
	 * @param pos
	 * @param rand
	 * @param fortune
	 * @return
	 */
	public int getSeedDrops(IBlockReader world, BlockPos pos, Random rand, int fortune) {
		BlockState state = world.getBlockState(pos);
		return 1 + (state.getValue(AGE) >= 15 ? (rand.nextInt(Math.max(3 - fortune, 1)) == 0 ? 1 : 0) : 0);
	}

	/**
	 * Returns the number of crops to drop
	 * @param world
	 * @param pos
	 * @param rand
	 * @param fortune
	 * @return
	 */
	public int getCropDrops(IBlockReader world, BlockPos pos, Random rand, int fortune) {
		BlockState state = world.getBlockState(pos);
		if(state.getValue(AGE) >= 15) {
			return 2 + rand.nextInt(3 + fortune);
		}
		return 0;
	}

	/**
	 * Returns the seed item to drop
	 * @param world
	 * @param pos
	 * @param rand
	 * @return
	 */
	public ItemStack getSeedDrop(IBlockReader world, BlockPos pos, Random rand) {
		return ItemStack.EMPTY;
	}

	/**
	 * Returns the crop item to drop
	 * @param world
	 * @param pos
	 * @param rand
	 * @return
	 */
	public ItemStack getCropDrop(IBlockReader world, BlockPos pos, Random rand) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		state = super.getActualState(state, worldIn, pos);
		return state.setValue(DECAYED, this.isDecayed(worldIn, pos)).setValue(this.stageProperty, MathHelper.floor(state.getValue(AGE) / 15.0f * Collections.max(this.stageProperty.getAllowedValues())));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		this.stageProperty = this.createStageProperty();
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), DECAYED, this.stageProperty);
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return (this.maxHeight > 1 && this.isSamePlant(state)) || SoilHelper.canSustainCrop(state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		BlockState state = worldIn.getBlockState(pos.below());
		return super.canPlaceBlockAt(worldIn, pos) && 
				!(state.getBlock() instanceof BlockGenericDugSoil && state.getValue(BlockGenericDugSoil.DECAYED)) &&
				!(state.getBlock() instanceof BlockGenericCrop && state.getValue(AGE) < 15);
	}

	@Override
	protected boolean canGrow(World world, BlockPos pos, BlockState state) {
		return !state.getValue(DECAYED) && this.isComposted(world, pos);
	}

	@Override
	protected void growUp(World world, BlockPos pos) {
		world.setBlockState(pos.above(), this.defaultBlockState().setValue(DECAYED, world.getBlockState(pos).getValue(DECAYED)));
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, BlockState state, boolean isClient) {
		if(!this.canGrow(worldIn, pos, state)) {
			return false;
		}
		if(state.getValue(AGE) < 15) {
			return true;
		}
		int height;
		for (height = 1; worldIn.getBlockState(pos.below(height)).getBlock() == this; ++height);
		return this.canGrowUp(worldIn, pos, state, height);
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
		int age = state.getValue(AGE) + MathHelper.getInt(worldIn.rand, 2, 5);
		if(age > 15) {
			age = 15;
			int height;
			for (height = 1; worldIn.getBlockState(pos.below(height)).getBlock() == this; ++height);
			if(this.canGrowUp(worldIn, pos, state, height)) {
				this.growUp(worldIn, pos);
			}
		}
		worldIn.setBlockState(pos, state.setValue(AGE, age));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(DECAYED).withPropertySuffixTrue(DECAYED, "decayed");
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, BlockState state) {
		//Crops shouldn't spread
		return false;
	}
	
	@Override
	public boolean isHarvestable(ItemStack item, IBlockReader world, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean isShearable(ItemStack item, IBlockReader world, BlockPos pos) {
		return false;
	}
}
