package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlessingCapability {
	public boolean isBlessed();

	@Nullable
	public BlockPos getBlessingLocation();
	
	public World getBlessingDimension();

	public void setBlessed(int dimension, BlockPos location);
	
	public void clearBlessed();
}
