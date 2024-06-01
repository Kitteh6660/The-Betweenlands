package thebetweenlands.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ICenser {
	public Level getCenserWorld();

	public BlockPos getCenserPos();

	public int getCurrentMaxInputAmount();

	public int getCurrentRemainingInputAmount();

	public boolean isRecipeRunning();

	public float getEffectStrength(float partialTicks);

	public ItemStack getInputStack();
}
