package thebetweenlands.common.block.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import thebetweenlands.common.tile.TileEntityDecayPitControl;

public class BlockDecayPitControl extends Block {
	
	public BlockDecayPitControl(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		setLightLevel(0.5F);
		this.setBlockUnbreakable();
		this.setResistance(2000.0F);
		this.setSoundType2(SoundType.STONE);*/
	}

	@Override
	public TileEntity newBlockEntity(BlockState state, IBlockReader level) {
		return new TileEntityDecayPitControl();
	}

}
