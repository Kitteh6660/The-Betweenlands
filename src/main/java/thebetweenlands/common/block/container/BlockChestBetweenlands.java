package thebetweenlands.common.block.container;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import thebetweenlands.common.registries.TileEntityRegistry;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;

public class BlockChestBetweenlands extends ChestBlock {
	
	public BlockChestBetweenlands(Properties properties) {
		super(properties, TileEntityRegistry.BL_CHEST.get());
		/*super(chestTypeIn);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, ChestType.SINGLE).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public TileEntity newBlockEntity(BlockState state, IBlockReader world) {
		return new TileEntityChestBetweenlands();
	}
}
