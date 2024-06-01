package thebetweenlands.api.runechain.io.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thebetweenlands.api.runechain.IRuneChainUser;

public interface IRuneEffect {
	public boolean apply(Level world, IRuneChainUser user);
	
	public boolean apply(Level world, Entity entity);
	
	public boolean apply(Level world, Vec3i position);
	
	public boolean apply(Level world, BlockPos pos);
}
