package thebetweenlands.common.block.structure;

import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;

public class BlockMudTiles extends Block implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {

	public static final PropertyEnum<EnumMudTileType> VARIANT = PropertyEnum.<EnumMudTileType>create("variant", EnumMudTileType.class);
    public static final BooleanProperty CONNECTED_DOWN = BooleanProperty.create("connected_down");
    public static final BooleanProperty CONNECTED_UP = BooleanProperty.create("connected_up");
    public static final BooleanProperty CONNECTED_NORTH = BooleanProperty.create("connected_north");
    public static final BooleanProperty CONNECTED_SOUTH = BooleanProperty.create("connected_south");
    public static final BooleanProperty CONNECTED_WEST = BooleanProperty.create("connected_west");
    public static final BooleanProperty CONNECTED_EAST = BooleanProperty.create("connected_east");

	public BlockMudTiles() {
		super(Material.ROCK);
		this.setDefaultState(this.blockState.getBaseState().setValue(VARIANT, EnumMudTileType.MUD_TILES).setValue(CONNECTED_DOWN, Boolean.FALSE).setValue(CONNECTED_EAST, Boolean.FALSE).setValue(CONNECTED_NORTH, Boolean.FALSE).setValue(CONNECTED_SOUTH, Boolean.FALSE).setValue(CONNECTED_UP, Boolean.FALSE).setValue(CONNECTED_WEST, Boolean.FALSE));
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (EnumMudTileType type : EnumMudTileType.values())
			list.add(new ItemStack(this, 1, type.ordinal()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(this, 1, ((EnumMudTileType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(VARIANT, EnumMudTileType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((EnumMudTileType)state.getValue(VARIANT)).getMetadata();
	}

    @Override
    public BlockState getActualState (BlockState state, IBlockReader world, BlockPos position) {
        return state.setValue(CONNECTED_DOWN, this.isSideConnectable(world, position, Direction.DOWN)).setValue(CONNECTED_EAST, this.isSideConnectable(world, position, Direction.EAST)).setValue(CONNECTED_NORTH, this.isSideConnectable(world, position, Direction.NORTH)).setValue(CONNECTED_SOUTH, this.isSideConnectable(world, position, Direction.SOUTH)).setValue(CONNECTED_UP, this.isSideConnectable(world, position, Direction.UP)).setValue(CONNECTED_WEST, this.isSideConnectable(world, position, Direction.WEST));
    }

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT, CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST});
	}

	@Override
	public int damageDropped(BlockState state) {
		return ((EnumMudTileType)state.getValue(VARIANT)).getMetadata();
	}

    private boolean isSideConnectable (IBlockReader world, BlockPos pos, Direction side) {
    	final BlockState state = world.getBlockState(pos);
    	final BlockState stateConnection = world.getBlockState(pos.offset(side));
    	if(stateConnection.getBlock() == this && state.getBlock() == this) {
    		if(state.getValue(VARIANT) == EnumMudTileType.MUD_TILES || state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED)
    			if(stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES_DECAY || stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED_DECAY || stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES || stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED)
    				return false;
    		if(state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_DECAY || state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED_DECAY)
    			if(stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES || stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED)
    				return false; 
    	}
        return (stateConnection == null) ? false : stateConnection.getBlock() == this;
    }

	@Override
    public float getSlipperiness(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity entity) {
		if(entity instanceof IEntityBL == false && (state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_DECAY || state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED_DECAY)) {
			return 0.98F;
		}
		return 0.6F;
    }

	public static enum EnumMudTileType implements IStringSerializable {
		MUD_TILES,
		MUD_TILES_DECAY,
		MUD_TILES_CRACKED,
		MUD_TILES_CRACKED_DECAY;

		private final String name;

		private EnumMudTileType() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumMudTileType byMetadata(int metadata) {
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}
	}

	@Override
	public BlockItem getItemBlock() {
		return ItemBlockEnum.create(this, EnumMudTileType.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumMudTileType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumMudTileType.values()[meta].getName();
	}

}