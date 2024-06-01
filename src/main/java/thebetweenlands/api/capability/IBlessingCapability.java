package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBlessingCapability {
	public boolean isBlessed();

	@Nullable
	public BlockPos getBlessingLocation();
	
	public Level getBlessingDimension();

	public void setBlessed(int dimension, BlockPos location);
	
	public void clearBlessed();
}
