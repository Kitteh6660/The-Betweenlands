package thebetweenlands.common.block.structure;

import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockMudTiles extends Block {

    public static final BooleanProperty CONNECTED_DOWN = BooleanProperty.create("connected_down");
    public static final BooleanProperty CONNECTED_UP = BooleanProperty.create("connected_up");
    public static final BooleanProperty CONNECTED_NORTH = BooleanProperty.create("connected_north");
    public static final BooleanProperty CONNECTED_SOUTH = BooleanProperty.create("connected_south");
    public static final BooleanProperty CONNECTED_WEST = BooleanProperty.create("connected_west");
    public static final BooleanProperty CONNECTED_EAST = BooleanProperty.create("connected_east");

	public BlockMudTiles(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
		this.registerDefaultState(this.defaultBlockState().setValue(CONNECTED_DOWN, Boolean.FALSE).setValue(CONNECTED_EAST, Boolean.FALSE).setValue(CONNECTED_NORTH, Boolean.FALSE).setValue(CONNECTED_SOUTH, Boolean.FALSE).setValue(CONNECTED_UP, Boolean.FALSE).setValue(CONNECTED_WEST, Boolean.FALSE));
	}

    @Override
    public BlockState getActualState(BlockState state, IBlockReader world, BlockPos position) {
        return state.setValue(CONNECTED_DOWN, this.isSideConnectable(world, position, Direction.DOWN)).setValue(CONNECTED_EAST, this.isSideConnectable(world, position, Direction.EAST)).setValue(CONNECTED_NORTH, this.isSideConnectable(world, position, Direction.NORTH)).setValue(CONNECTED_SOUTH, this.isSideConnectable(world, position, Direction.SOUTH)).setValue(CONNECTED_UP, this.isSideConnectable(world, position, Direction.UP)).setValue(CONNECTED_WEST, this.isSideConnectable(world, position, Direction.WEST));
    }

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] {CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST});
	}

    private boolean isSideConnectable(IBlockReader world, BlockPos pos, Direction side) {
    	final BlockState state = world.getBlockState(pos);
    	final BlockState stateConnection = world.getBlockState(pos.relative(side));
    	if(stateConnection.getBlock() == this && state.getBlock() == this) {
    		if(state.getBlock() == BlockRegistry.MUD_TILES.get() || state.getBlock() == BlockRegistry.CRACKED_MUD_TILES.get())
    			if(stateConnection.getBlock() == BlockRegistry.MUD_TILES_DECAY.get() || stateConnection.getBlock() == BlockRegistry.CRACKED_MUD_TILES_DECAY.get() || stateConnection.getBlock() == BlockRegistry.MUD_TILES.get() || stateConnection.getBlock() == BlockRegistry.CRACKED_MUD_TILES.get())
    				return false;
    		if(state.getBlock() == BlockRegistry.MUD_TILES_DECAY.get() || state.getBlock() == BlockRegistry.CRACKED_MUD_TILES_DECAY.get())
    			if(stateConnection.getBlock() == BlockRegistry.MUD_TILES.get() || stateConnection.getBlock() == BlockRegistry.CRACKED_MUD_TILES.get())
    				return false; 
    	}
        return (stateConnection == null) ? false : stateConnection.getBlock() == this;
    }

	@Override
    public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
		if(entity instanceof IEntityBL == false && (state.getBlock() == BlockRegistry.MUD_TILES_DECAY.get() || state.getBlock() == BlockRegistry.CRACKED_MUD_TILES_DECAY.get())) {
			return 0.98F;
		}
		return 0.6F;
    }

}