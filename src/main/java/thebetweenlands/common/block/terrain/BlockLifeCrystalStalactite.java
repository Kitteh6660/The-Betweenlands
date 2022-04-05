package thebetweenlands.common.block.terrain;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.fluid.SwampWaterBlock;
import thebetweenlands.common.block.fluid.SwampWaterFluid;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.misc.ItemGemSinger;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;
import thebetweenlands.util.AdvancedStateMap;

public class BlockLifeCrystalStalactite extends SwampWaterBlock implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition, IStateMappedBlock, IWaterLoggable {
	
	public static final EnumProperty<EnumLifeCrystalType> VARIANT = EnumProperty.<EnumLifeCrystalType>create("variant", EnumLifeCrystalType.class);
	public static final BooleanProperty NO_BOTTOM = BooleanProperty.create("no_bottom");
	public static final BooleanProperty NO_TOP = BooleanProperty.create("no_top");
	public static final IntegerProperty DIST_UP = IntegerProperty.create("dist_up", 0, 7);
	public static final IntegerProperty DIST_DOWN = IntegerProperty.create("dist_down", 0, 7);
	public static final IntegerProperty POS_X = IntegerProperty.create("pos_x");
	public static final IntegerProperty POS_Y = IntegerProperty.create("pos_x");
	public static final IntegerProperty POS_Z = IntegerProperty.create("pos_z");

	public BlockLifeCrystalStalactite(Properties properties) {
		super(properties);
		/*super(fluid, materialIn);
		this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, EnumLifeCrystalType.DEFAULT));
		this.setHardness(2.5F);
		this.setResistance(10.0F);
		this.setUnderwaterBlock(true);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHarvestLevel("pickaxe", 2);*/
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, EnumLifeCrystalType.DEFAULT.getMetadata()));
		items.add(new ItemStack(this, 1, EnumLifeCrystalType.ORE.getMetadata()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(this, 1, ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(VARIANT, EnumLifeCrystalType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return BlockStateContainerHelper.extendBlockstateContainer((ExtendedBlockState) super.createBlockState(), 
				new IProperty<?>[]{ VARIANT }, 
				new IntegerProperty[] {
					POS_X,
					POS_Y,
					POS_Z,
					NO_BOTTOM,
					NO_TOP,
					DIST_UP,
					DIST_DOWN
				});
	}

	@Override
	public int damageDropped(BlockState state) {
		return 0;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata());
	}
	
	public static enum EnumLifeCrystalType implements IStringSerializable {
		DEFAULT,
		ORE;

		private final String name;

		private EnumLifeCrystalType() {
			this.name = this.name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumLifeCrystalType byMetadata(int metadata) {
			if (metadata < 0 || metadata >= EnumLifeCrystalType.values().length) {
				metadata = 0;
			}
			return EnumLifeCrystalType.values()[metadata];
		}

		@Override
		public ITextComponent getName() {
			return this.name;
		}
	}

	@Override
	public BlockItem getItemBlock() {
		return ItemBlockEnum.create(this, EnumLifeCrystalType.class);
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
	public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) super.getExtendedState(oldState, worldIn, pos);

		final int maxLength = 32;
		int distUp = 0;
		int distDown = 0;
		boolean noTop = false;
		boolean noBottom = false;

		BlockState blockState;
		//Block block;
		for(distUp = 0; distUp < maxLength; distUp++) {
			blockState = worldIn.getBlockState(pos.offset(0, 1 + distUp, 0));
			if(blockState.getBlock() == this)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.canOcclude())
				noTop = true;
			break;
		}
		for(distDown = 0; distDown < maxLength; distDown++)
		{
			blockState = worldIn.getBlockState(pos.offset(0, -(1 + distDown), 0));
			if(blockState.getBlock() == this)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.canOcclude())
				noBottom = true;
			break;
		}

		return state.setValue(POS_X, pos.getX()).setValue(POS_Y, pos.getY()).setValue(POS_Z, pos.getZ()).setValue(DIST_UP, distUp).setValue(DIST_DOWN, distDown).setValue(NO_TOP, noTop).setValue(NO_BOTTOM, noBottom);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(VARIANT);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumLifeCrystalType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumLifeCrystalType.values()[meta].getName();
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return state.getValue(VARIANT) == EnumLifeCrystalType.ORE ? ItemRegistry.LIFE_CRYSTAL : null;
	}

	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return world.setBlockState(pos, BlockRegistry.SWAMP_WATER.defaultBlockState(), level.isClientSide() ? 11 : 3);
	}	
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return (int)((state.getValue(VARIANT) == EnumLifeCrystalType.ORE ? 0.4F : 0.0F) * 15.0F);
	}
	
	@Override
	public void onPlace(World worldIn, BlockPos pos, BlockState state) {
		super.onPlace(worldIn, pos, state);

		if(state.getValue(VARIANT) == EnumLifeCrystalType.ORE) {
			BetweenlandsChunkStorage.markGem(worldIn, pos, ItemGemSinger.GemSingerTarget.LIFE_CRYSTAL);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		super.breakBlock(worldIn, pos, state);

		if(state.getValue(VARIANT) == EnumLifeCrystalType.ORE) {
			BetweenlandsChunkStorage.unmarkGem(worldIn, pos, ItemGemSinger.GemSingerTarget.LIFE_CRYSTAL);
		}
	}
}
