package thebetweenlands.common.tile;

import net.minecraft.tileentity.TileEntityType;
import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntityMudBricksSpikeTrap extends TileEntitySpikeTrap {

	public int prevSpoopAnimationTicks;
	public int spoopAnimationTicks;
	public boolean activeSpoop;

	public TileEntityMudBricksSpikeTrap() {
		super(TileEntityRegistry.MUD_BRICK_SPIKE_TRAP.get());
	}
	
	@Override
	public void tick() {
		super.tick();
		prevSpoopAnimationTicks = spoopAnimationTicks;
		if(!activeSpoop && getLevel().random.nextInt(11) + getLevel().getGameTime()%10 == 0 && spoopAnimationTicks == 0)
			setActiveSpoop(true);
		if (activeSpoop) {
			if (spoopAnimationTicks <= 20)
				spoopAnimationTicks += 1;
			if (spoopAnimationTicks == 20)
				setActiveSpoop(false);
		}
		if (!activeSpoop)
			if (spoopAnimationTicks >= 1)
				spoopAnimationTicks--;
	}

	public void setActiveSpoop(boolean isActive) {
		activeSpoop = isActive;
		getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 2);
	}
}