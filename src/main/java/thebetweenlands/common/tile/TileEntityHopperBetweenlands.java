package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class TileEntityHopperBetweenlands extends HopperTileEntity {
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
		return oldState.getBlock() != newSate.getBlock(); //Urgh why is this even a thing
	}
	
	@Override
    public ITextComponent getName() {
        return "container.bl.syrmorite_hopper";
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TranslationTextComponent(this.getName(), new Object[0]));
    }
}
