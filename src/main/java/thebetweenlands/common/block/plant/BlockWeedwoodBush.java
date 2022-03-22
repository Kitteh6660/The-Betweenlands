package thebetweenlands.common.block.plant;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.api.capability.ICustomStepSoundCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;
import thebetweenlands.common.entity.WeedWoodBushUncollidableEntity;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BlockWeedwoodBush extends Block implements IShearable, ISickleHarvestable, ITintedBlock, IFarmablePlant {
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final PropertyIntegerUnlisted POS_X = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Y = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Z = new PropertyIntegerUnlisted("pos_z");

	public BlockWeedwoodBush() {
		super(Material.PLANTS);

		this.setHardness(0.35F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);

		this.setDefaultState(this.blockState.getBaseState()
				.setValue(NORTH, Boolean.valueOf(false))
				.setValue(EAST, Boolean.valueOf(false))
				.setValue(SOUTH, Boolean.valueOf(false))
				.setValue(WEST, Boolean.valueOf(false))
				.setValue(UP, Boolean.valueOf(false))
				.setValue(DOWN, Boolean.valueOf(false)));
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState();
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {NORTH, EAST, WEST, SOUTH, UP, DOWN}, new IUnlistedProperty[]{POS_X, POS_Y, POS_Z});
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemMisc.WEEDWOOD_STICK.create(1));
	}

	@Override
	@Nullable
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return ItemRegistry.ITEMS_MISC;
	}

	@Override
	public int damageDropped(BlockState state) {
		return EnumItemMisc.WEEDWOOD_STICK.getID();
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
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state
				.setValue(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north(), Direction.NORTH)))
				.setValue(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east(), Direction.EAST)))
				.setValue(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south(), Direction.SOUTH)))
				.setValue(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west(), Direction.WEST)))
				.setValue(UP, Boolean.valueOf(this.canConnectTo(worldIn, pos.above(), Direction.UP)))
				.setValue(DOWN, Boolean.valueOf(this.canConnectTo(worldIn, pos.below(), Direction.DOWN)));
	}

	public boolean canConnectTo(IBlockReader worldIn, BlockPos pos, Direction dir) {
		BlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return block == this;
	}

	@Override
	public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState)oldState;
		return state.setValue(POS_X, pos.getX()).setValue(POS_Y, pos.getY()).setValue(POS_Z, pos.getZ());
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
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return Blocks.LEAVES.getRenderLayer();
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		if (entityIn instanceof PlayerEntity || entityIn instanceof WeedWoodBushUncollidableEntity) {
			return;
		}
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity){
		if(entity instanceof PlayerEntity) {
			entity.motionX *= 0.06D;
			entity.motionZ *= 0.06D;
		}
		
		if(entity.world.isClientSide()) {
			ICustomStepSoundCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_CUSTOM_STEP_SOUND, null);
			if(cap != null) {
				if(entity.distanceWalkedOnStepModified > cap.getNextWeedwoodBushStep()) {
					AxisAlignedBB aabb = entity.getBoundingBox();
					Iterator<BlockPos.Mutable> it = BlockPos.getAllInBoxMutable(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)).iterator();
					while(it.hasNext()) {
						BlockPos.Mutable checkPos = it.next();
						if(entity.world.getBlockState(checkPos).getBlock() == BlockRegistry.WEEDWOOD_BUSH) {
							spawnLeafParticles(entity.world, checkPos, Math.min((entity.distanceWalkedOnStepModified - cap.getNextWeedwoodBushStep()) * 40, 1));
						}
					}
					
					entity.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundRegistry.GECKO_HIDE, SoundCategory.BLOCKS, 0.4F, entity.world.rand.nextFloat() * 0.3F + 0.7F, false);
					
					cap.setNextWeeedwoodBushStep(entity.distanceWalkedOnStepModified + 0.8F);
				}
			}
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		if(!worldIn.isClientSide() && !stack.isEmpty() && stack.getItem() instanceof ItemShears) {
			player.awardStat(StatList.getBlockStats(this));
			player.addExhaustion(0.025F);
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(BlockRegistry.WEEDWOOD_BUSH);
	}
	
	@Override
	public int getColorMultiplier(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	}

	@OnlyIn(Dist.CLIENT)
	protected static void spawnLeafParticles(World world, BlockPos pos, float strength) {
		int leafCount = (int)(60 * strength) + 1;
		float x = pos.getX() + 0.5F;
		float y = pos.getY() + 0.5F;
		float z = pos.getZ() + 0.5F;
		while (leafCount-- > 0) {
			float dx = world.rand.nextFloat() * 2 - 1;
			float dy = world.rand.nextFloat() * 2 - 0.5F;
			float dz = world.rand.nextFloat() * 2 - 1;
			float mag = 0.01F + world.rand.nextFloat() * 0.07F;
			BLParticles.WEEDWOOD_LEAF.spawn(world, x, y, z, ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
		}
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		return world.isEmptyBlock(targetPos) && this.canPlaceBlockAt(world, targetPos);
	}
	
	@Override
	public float getSpreadChance(World world, BlockPos pos, BlockState state, BlockPos taretPos, Random rand) {
		return 0.35F;
	}

	@Override
	public void spreadTo(World world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		world.setBlockState(targetPos, this.defaultBlockState());
	}

	@Override
	public void decayPlant(World world, BlockPos pos, BlockState state, Random rand) {
		world.setBlockToAir(pos);
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, BlockState state, Random rand) {
		return 2;
	}
}
