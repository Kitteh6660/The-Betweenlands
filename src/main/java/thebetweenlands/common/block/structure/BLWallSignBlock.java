package thebetweenlands.common.block.structure;

import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import thebetweenlands.common.tile.BLSignTileEntity;

public class BLWallSignBlock extends WallSignBlock {
	
	public BLWallSignBlock(Properties properties, WoodType wtype) {
		super(properties, wtype);
		/*this.setHardness(1.0F);
		this.setSoundType(SoundType.WOOD);*/
	}
	
	@Override
	public TileEntity newBlockEntity(BlockState state, IBlockReader world) {
		return new BLSignTileEntity();
	}

}
